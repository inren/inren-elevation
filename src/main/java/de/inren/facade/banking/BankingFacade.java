/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.facade.banking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.banking.Transaction;
import de.inren.data.repositories.banking.TransactionRepository;
import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
@Service(value = "bankingFacade")
@Transactional(readOnly = true)
public class BankingFacade implements Serializable {
	private final static Logger log = LoggerFactory.getLogger(BankingFacade.class);

	@Autowired
	private TransactionRepository transactionRepository;

	public void fillInMonthlyBalance(String account, List<String> labels, List<BigDecimal> values) {

		// testOutput();

		Iterable<Transaction> transactions = transactionRepository.findAllByOrderByValutaDateAsc();
		Date date = null;
		Transaction prevTransaction = null;
		for (Transaction transaction : transactions) {
			if (account.equals(transaction.getAccountNumber())) {
				prevTransaction = transaction;
				if (date == null) {
					// init
					Calendar cal = Calendar.getInstance();
					cal.setTime(transaction.getAccountingDate());
					cal.set(Calendar.DAY_OF_MONTH, 1);
					if (cal.getTime().before(transaction.getAccountingDate())) {
						cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
					}
					date = cal.getTime();
				}
				if (transaction.getAccountingDate().before(date)) {
					prevTransaction = transaction;
				} else {
					labels.add(getFormatedDate(prevTransaction.getAccountingDate()));
					BigDecimal balance = prevTransaction.getBalance();
					values.add(balance);
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
					date = cal.getTime();
					prevTransaction = transaction;
				}

			}
		}
	}

	private String getFormatedDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return dateFormat.format(date);
	}

	private void testOutput() {

		Map<String, Set<Transaction>> map = new HashMap<String, Set<Transaction>>();
		Iterable<Transaction> all = transactionRepository.findAll();
		for (Transaction transaction : all) {
			final Set<Transaction> set;
			if (map.containsKey(transaction.getAccountNumber())) {
				set = map.get(transaction.getAccountNumber());
				set.add(transaction);
			} else {
				set = new HashSet<Transaction>();
				set.add(transaction);
				map.put(transaction.getAccountNumber(), set);
			}
		}

		for (String key : map.keySet()) {
			File out = new File("/home/ingo/transactions_" + key + ".log");
			if (out.exists()) {
				out.delete();
			}
			try {
				OutputStream os = new FileOutputStream(out);
				Set<Transaction> transactions = map.get(key);
				for (Transaction entry : transactions) {
					os.write(print(entry).getBytes(Charset.forName("UTF-8")));
				}
				os.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private String print(Transaction transaction) {
		StringBuilder builder = new StringBuilder();
		builder.append(getFormatedDate(transaction.getAccountingDate())).append(", ")
				.append(getFormatedDate(transaction.getAccountingDate())).append(", ").append(transaction.getBalance())
				.append(".\n");
		return builder.toString();
	}

	public Map<String, List<BigDecimal>> claculateCategoriesfor(Date object, Date object2) {
		HashMap<String, List<BigDecimal>> calculatedData = new HashMap<String, List<BigDecimal>>();
		// String ist die Kategory, das andere sind die Summierten Werte je Monat,
		// insgesamt 12 Werte im ersten Schrit.

		return calculatedData;
	}

}
