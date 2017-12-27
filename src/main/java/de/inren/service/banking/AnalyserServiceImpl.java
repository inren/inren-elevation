package de.inren.service.banking;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.banking.Token;
import de.inren.data.domain.banking.Transaction;
import de.inren.data.repositories.banking.TokenRepository;
import de.inren.data.repositories.banking.TransactionRepository;

@Service(value = "analyserService")
@Transactional(readOnly = true)
public class AnalyserServiceImpl implements AnalyserService {

	private static final Logger log = LoggerFactory.getLogger(AnalyserServiceImpl.class);

	private final Analyzer analyzer = new StandardAnalyzer(BANKING_STOP_WORDS_SET);

	@Resource
	private TransactionRepository transactionRepository;

	@Resource
	private TokenRepository tokenRepository;

	public static final CharArraySet BANKING_STOP_WORDS_SET;

	static {
		final List<String> stopWords = Arrays.asList("de", "folgenr", "verfalld", "sagt", "danke", "dank", "ihr", "aus",
				"s.c.a", "e.v", "r.l", "s.a", "a.g", "cie", "vielen", "und", "gmbh");
		final CharArraySet stopSet = new CharArraySet(stopWords, false);
		BANKING_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
	}

	@Override
	public void init() {
		log.info("AnalyserService service starting");
		tokenRepository.deleteAll();
		Iterable<Token> tokens = tokenRepository.findAll();
		if (!tokens.iterator().hasNext()) {
			log.info("AnalyserService service generating token");
			generateInitialTokensFromScratch();
		}
		updateStatistics();
		log.info("AnalyserService service initialized");
	}

	@Override
	public List<String> tokenizeString(String string) {
		List<String> result = new ArrayList<>();
		try (TokenStream stream = analyzer.tokenStream(null, new StringReader(string))) {
			stream.reset();
			while (stream.incrementToken()) {
				String term = stream.getAttribute(CharTermAttribute.class).toString();
				if (term.length() > 2 && !StringUtils.isNumericSpace(term)
						&& !StringUtils.containsAny(term, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')) {
					result.add(term);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Generates a table of token with reference to the transaction from which they
	 * were extracted.
	 */
	private void generateInitialTokensFromScratch() {
		Map<String, Token> tokenMap = new HashMap<>();
		Iterable<Transaction> transactions = transactionRepository.findAll();
		for (Transaction transaction : transactions) {
			for (String value : tokenizeString(transaction.getPrincipal())) {
				if (tokenMap.containsKey(value)) {
					Token t = tokenMap.get(value);
					t.getTransactions().add(transaction);
					t.setInfoMatches(t.getTransactions().size());
					t.setInfoSrc(t.getInfoSrc() + "\n" + transaction.getPrincipal());

				} else {
					Token t = new Token();
					t.setValue(value);
					Set<Transaction> transactionSet = new HashSet<>();
					transactionSet.add(transaction);
					t.setTransactions(transactionSet);
					t.setInfoMatches(1);
					t.setInfoSrc(transaction.getPrincipal());
					tokenMap.put(value, t);
				}
			}
		}
		tokenRepository.saveAll(tokenMap.values());
		log.info("AnalyserService service generated " + tokenMap.values().size() + "token");
	}

	private void updateStatistics() {
		Iterable<Transaction> transactions = transactionRepository.findAll();

		// All transactions with same amount of mone
		Map<BigDecimal, Set<Transaction>> byAmountMap = new HashMap<>();

		// All transactions with same tokens
		Map<String, Set<Transaction>> byTokenMap = new HashMap<>();

		// Updated transaction
		Map<Long, Transaction> unsavedTransactionMap = new HashMap<>();

		for (Transaction transaction : transactions) {
			// Money Map
			if (byAmountMap.containsKey(transaction.getAmount())) {
				byAmountMap.get(transaction.getAmount()).add(transaction);
			} else {
				Set<Transaction> transactionSet = new HashSet<>();
				transactionSet.add(transaction);
				byAmountMap.put(transaction.getAmount(), transactionSet);
			}

			// Tokens
			if (transaction.getTokens() == null) {
				transaction.setTokenList(tokenizeString(transaction.getPrincipal()));
				// to be updated
				unsavedTransactionMap.put(transaction.getId(), transaction);
			}
			if (byTokenMap.containsKey(transaction.getTokens())) {
				byTokenMap.get(transaction.getTokens()).add(transaction);
			} else {
				Set<Transaction> transactionSet = new HashSet<>();
				transactionSet.add(transaction);
				byTokenMap.put(transaction.getTokens(), transactionSet);
			}
		}
		for (Entry<String, Set<Transaction>> set : byTokenMap.entrySet()) {

			// order transactions by valuta date old to new
			List<Transaction> sortedTransactions = new ArrayList<>(set.getValue());
			Collections.sort(sortedTransactions, (o1, o2) -> {
				if (o1.getValutaDate() == null || o2.getValutaDate() == null)
					return 0;
				return o1.getValutaDate().compareTo(o2.getValutaDate());
			});

			// Try to guess payment intevall
			PaymentIntervall repeating = PaymentIntervall.NONE;
			if (sortedTransactions.size() > 1) {
				repeating = guessRepeatingInterval(sortedTransactions);
			}
			for (Transaction t : sortedTransactions) {
				if (t.getPaymentIntervall() == null) {
					t.setPaymentIntervall(repeating);
					unsavedTransactionMap.put(t.getId(), t);
				}
				if (!PaymentIntervall.NONE.equals(repeating)) {

					DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
					System.out.println(t.getAmount() + ", " + df.format(t.getValutaDate()) + ", " + repeating + ", "
							+ t.getPrincipal() + ", " + tokenizeString(t.getPrincipal()));

					for (Transaction at : byAmountMap.get(t.getAmount())) {
						if (!at.getTokens().equals(t.getTokens())) {
							System.out.println("No match with amount: " + at.getPrincipal());
						}
					}
				}
			}
			transactionRepository.saveAll(unsavedTransactionMap.values());
		}

	}

	private long getNumberOfDaysBetweenTwoDates(Date date1, Date date2) {

		Instant instant1 = date1.toInstant();
		LocalDate localDateTime1 = instant1.atZone(ZoneId.systemDefault()).toLocalDate();

		Instant instant2 = date2.toInstant();
		LocalDate localDateTime2 = instant2.atZone(ZoneId.systemDefault()).toLocalDate();

		Period p = Period.between(localDateTime1, localDateTime2);
		return p.get(ChronoUnit.DAYS);
	}

	private PaymentIntervall mapDaysToMonthlyPeriods(long days) {
		if (days <= 31)
			return PaymentIntervall.MONTHLY;
		if (days <= 92)
			return PaymentIntervall.QUARTERLY;
		if (days <= 183)
			return PaymentIntervall.HALF_YEARLY;
		return PaymentIntervall.YEARLY;
	}

	private PaymentIntervall guessRepeatingInterval(List<Transaction> transactions) {

		// Calculate distance
		Set<PaymentIntervall> month = new HashSet<>();

		for (int i = 0; i < transactions.size() - 1; i++) {
			Date date1 = transactions.get(i).getValutaDate();
			Date date2 = transactions.get(i + 1).getValutaDate();
			Long diff = Long.valueOf(getNumberOfDaysBetweenTwoDates(date1, date2));
			month.add(mapDaysToMonthlyPeriods(diff));
		}
		if (month.size() > 1) {
			System.out.println("# Scheiße #" + new ArrayList(month) + transactions);
		} else {
			System.out.println("Repeating: " + month.iterator().next());
		}

		return month.iterator().next();
	}

}
