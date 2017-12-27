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
package de.inren.service.banking;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.inren.data.domain.banking.Account;
import de.inren.data.domain.banking.Category;
import de.inren.data.domain.banking.CategoryDto;
import de.inren.data.domain.banking.CategoryFilter;
import de.inren.data.domain.banking.CategoryFilterDto;
import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.banking.TransactionCategoryFilterSpecification;
import de.inren.data.domain.banking.TransactionDateSpecification;
import de.inren.data.domain.tagging.Tag;
import de.inren.data.repositories.banking.AccountRepository;
import de.inren.data.repositories.banking.CategoryFilterRepository;
import de.inren.data.repositories.banking.CategoryRepository;
import de.inren.data.repositories.banking.TransactionRepository;
import de.inren.data.repositories.tagging.TagRepository;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;
import de.inren.service.dbproperty.DbPropertyService;

@Service(value = "bankDataService")
@Transactional(readOnly = true)
public class BankDataServiceImpl implements BankDataService {

	protected static final String ING_DIBA_DATE_PATTERN = "dd.MM.yyyy";

	protected static final String SPARKASSE_DATE_PATTERN = "dd.MM.yy";

	private final static Logger log = LoggerFactory.getLogger(BankDataServiceImpl.class);

	private static final Sort SORT_VALUTA_ASC = new Sort(Direction.ASC, "valutaDate");

	private static final String WATCHBANKDATA = "watchBankData";
	private static final String WATCHBANKDATAINDIR = "watchBankDataInDir";
	private static final String WATCHBANKDATABACKUPDIR = "watchBankDataBackupDir";

	private static final String KONTOAUSGLEICH = "Kontoausgleich";

	@Resource
	private AccountRepository accountRepository;

	@Resource
	private TransactionRepository transactionRepository;

	@Resource
	private CategoryRepository categoryRepository;

	@Resource
	private TagRepository tagRepository;

	@Resource
	private CategoryFilterRepository categoryFilterRepository;

	@Resource
	private DbPropertyService dbPropertyService;

	private boolean initDone = false;

	@Override
	public void init() {
		if (!initDone) {
			log.info("BankDataService init start.");
			dbPropertyService.init();
			// // reset all fixed Categories
			// List<Transaction> all = transactionRepository.findAll();
			// for (Transaction transaction : all) {
			// transaction.setCategoryFixed(false);
			// transaction.setCategory(null);
			// }
			// transactionRepository.saveAll(all);

			final Category category = categoryRepository.findByname(KONTOAUSGLEICH);
			if (category == null) {
				Category cat = new Category(KONTOAUSGLEICH, false, false, "Im Ergebnis sollte die Summe hier  0 sein.");
				categoryRepository.save(cat);
			}

			initPrivateData();

			checkForDublicates();

			markInterchanges();

			for (CategoryFilter filter : categoryFilterRepository.findAll()) {
				applyCategoryToTransactions(filter);
			}

			printSummary();

			tryInitDirectoryWatchdog();
			initDone = true;
			log.info("BankDataService init done.");
		}

	}

	private void printSummary() {
		Map<Category, Map<Date, TransactionSummary>> data = calculateMonthlyOverview();
		StringBuilder sb = new StringBuilder();
		for (Category category : data.keySet()) {
			sb.append(Strings.padEnd("Kategory\\Date", 14, ' '));
			if (data.get(category) != null) {

				for (Date date : data.get(category).keySet()) {
					sb.append("|").append(Strings.padStart(date.getMonth() + "." + date.getYear(), 7, ' '));
				}
				sb.append("\n").append(Strings.padEnd(category.getName(), 14, ' '));
				for (Date date : data.get(category).keySet()) {

					sb.append("|");
					if (data.get(category).get(date) != null) {
						sb.append(Strings.padStart(data.get(category).get(date).getSum().toString(), 7, ' '));
					} else {
						sb.append(Strings.padStart("-", 7, ' '));
					}
				}
			}
		}
		System.out.println(sb.toString());
	}

	private void initPrivateData() {
		String privatedata = System.getenv().get("private.data.home");
		if (privatedata != null) {
			log.info("Private init starts.");
			log.info("Create backup.");
			try {
				backupCategoriesToJson(privatedata);
			} catch (Exception e) {
				log.error("Category backup failed: " + e.getMessage(), e);
			}
			try {
				backupCategoryFiltersToJson(privatedata);
			} catch (Exception e) {
				log.error("Categoryfilter backup failed: " + e.getMessage(), e);
			}
			log.info("Backup done.");

			try {
				log.info("Import data.");
				importCategoriesFromJson(privatedata);
				importCategoryFiltersFromJson(privatedata);
			} catch (Exception e) {
				log.error("import failed: " + e.getMessage(), e);
			}

		}
		log.info("Private init done.");
	}

	private String getDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return now.format(formatter);
	}

	private void backupCategoryFiltersToJson(String privatedata) throws IOException {
		File categoryFilterFile = new File(privatedata + "/banking/categoryFilter_" + getDateTime() + ".json");
		List<CategoryFilter> allFilter = categoryFilterRepository.findAll();
		List<CategoryFilterDto> categorieFilterDtos = new ArrayList<>();
		for (CategoryFilter categoryFilter : allFilter) {
			categorieFilterDtos.add(new CategoryFilterDto(categoryFilter));
		}
		Gson gson = new Gson();
		String data = gson.toJson(categorieFilterDtos);
		Files.write(Paths.get(categoryFilterFile.getAbsolutePath()), data.getBytes());
	}

	private void importCategoryFiltersFromJson(String privatedata) throws IOException {
		File categoryFilterFile = new File(privatedata + "/banking/categoryFilter.json");
		if (categoryFilterFile.exists() && categoryFilterFile.canRead()) {
			log.info("Importing categoryFilters, if needed.");
			List<CategoryFilter> allFilter = categoryFilterRepository.findAll();
			String content = new String(Files.readAllBytes(Paths.get(categoryFilterFile.toURI())));
			Gson gson = new Gson();
			List<CategoryFilterDto> categorieFilterDtos = gson.fromJson(content,
					new TypeToken<List<CategoryFilterDto>>() {
					}.getType());
			for (CategoryFilterDto categoryFilterDto : categorieFilterDtos) {
				boolean exists = false;
				for (CategoryFilter categoryFilter : allFilter) {
					exists = exists || categoryFilterDto.matches(categoryFilter);
					if (exists) {
						break;
					}
				}
				if (!exists) {
					Category category = categoryRepository.findByname(categoryFilterDto.getCategoryName());
					if (category != null) {
						CategoryFilter categoryFilter = categoryFilterDto.toCategoryFilter(category);
						categoryFilter = save(categoryFilter);
						log.info("Imported: {0}", categoryFilter);
					}
				}
			}
		}
	}

	private void backupCategoriesToJson(String privatedata) throws IOException {
		File categoryFile = new File(privatedata + "/banking/category_" + getDateTime() + ".json");
		List<Category> all = categoryRepository.findAll();
		List<CategoryDto> categorieDtos = new ArrayList<>();
		for (Category category : all) {
			categorieDtos.add(new CategoryDto(category));
		}
		Gson gson = new Gson();
		String data = gson.toJson(categorieDtos);
		Files.write(Paths.get(categoryFile.getAbsolutePath()), data.getBytes());
	}

	private void importCategoriesFromJson(String privatedata) throws IOException {
		File categoryFile = new File(privatedata + "/banking/category.json");
		if (categoryFile.exists() && categoryFile.canRead()) {
			log.info("Importing categories, if needed.");
			String content = new String(Files.readAllBytes(Paths.get(categoryFile.toURI())));
			Gson gson = new Gson();
			List<CategoryDto> categorieDtos = gson.fromJson(content, new TypeToken<List<CategoryDto>>() {
			}.getType());
			for (CategoryDto categoryDto : categorieDtos) {
				Category category = categoryRepository.findByname(categoryDto.getName());
				if (category == null) {
					category = save(categoryDto.toCategory());
					log.info("Imported: " + category);
				}
			}
		}
	}

	private void markInterchanges() {
		log.info("Check interchange entries start.");
		Category category = categoryRepository.findByname(KONTOAUSGLEICH);

		Set<String> owners = new HashSet<>();

		for (Account account : accountRepository.findAll()) {
			owners.add(account.getOwner());
		}

		List<Transaction> all = transactionRepository.findAll();
		List<Transaction> toSave = new ArrayList<>();
		for (Transaction transaction : all) {
			if (!transaction.isCategoryFixed()) {
				for (String owner : owners) {
					if (owner.contains(transaction.getPrincipal())) {
						// Kandidat für neuen Kontoausgleich
						transaction.setCategoryFixed(true);
						transaction.setCategory(category.getName());
						toSave.add(transaction);
					}
				}
			}
		}
		if (!toSave.isEmpty()) {
			log.info("Updating " + toSave.size() + " transaction.");
			transactionRepository.saveAll(toSave);
		}
		log.info("Check interchange entries done.");
	}

	private void checkForDublicates() {
		log.info("Check for dublicate entries");
		Iterable<Transaction> all = transactionRepository.findAll();
		Map<String, Transaction> map = new HashMap<>();
		int size = 0;
		for (Transaction transaction : all) {
			String hash = transaction.createSimpleHashCode();
			size++;
			if (map.containsKey(hash)) {
				transactionRepository.delete(transaction);
				log.info("Deleted dublicate Key: " + map.get(hash) + ", " + transaction.getId());
			} else {
				map.put(hash, transaction);
			}
		}
		if (size == map.size()) {
			log.info("No dublicate entries found.");
		} else {
			log.info("Check for dublicate entries done, dublicate entries are logged above.");
		}
	}

	private void tryInitDirectoryWatchdog() {
		log.info("Setup automatic csv import support.");
		String startWatchdog = dbPropertyService.getValue(WATCHBANKDATA);
		if (!Strings.isNullOrEmpty(startWatchdog)) {
			Boolean init = Boolean.parseBoolean(startWatchdog);
			log.info("import is " + (init ? "enabled" : "disabled."));
			if (init) {
				File inDirFile = null;
				File bakDirFile = null;
				String indirName = dbPropertyService.getValue(WATCHBANKDATAINDIR);
				if (!Strings.isNullOrEmpty(indirName)) {
					log.info("Import directory is " + indirName);
					inDirFile = new File(indirName);
					if (!inDirFile.exists()) {
						log.info("Import directory create " + (inDirFile.mkdirs() ? "sucess" : "failed."));
					}
					String bakdir = dbPropertyService.getValue(WATCHBANKDATABACKUPDIR);
					if (inDirFile.exists() && inDirFile.isDirectory() && !Strings.isNullOrEmpty(bakdir)) {
						log.info("Backup directory is " + bakdir);
						bakDirFile = new File(bakdir);
						if (!bakDirFile.exists()) {
							log.info("Backup directory create " + (bakDirFile.mkdirs() ? "sucess" : "failed."));
						}
					}
				}
				if (bakDirFile.exists() && bakDirFile.isDirectory()) {
					try {
						startWatchdogServive(Paths.get(inDirFile.toURI()), bakDirFile);
					} catch (Exception e) {
						log.error("Watchdog for bankdada import not started.", e);
					}
				} else {
					log.info("import configuration failed.");
				}
			}
		}

	}

	private static class WatchdogQueueReader implements Runnable {
		private WatchService watcherService;
		private BankDataServiceImpl bankDataServiceImpl;
		private File bakDirFile;
		private Path path;

		public WatchdogQueueReader(BankDataServiceImpl bankDataServiceImpl, Path path, File bakDirFile) {
			this.bankDataServiceImpl = bankDataServiceImpl;
			this.path = path;
			this.bakDirFile = bakDirFile;
			// We obtain the file system of the Path
			FileSystem fs = path.getFileSystem();
			try {
				this.watcherService = fs.newWatchService();
				// We register the path to the service
				// We watch for creation events
				path.register(this.watcherService, ENTRY_CREATE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				// get the first event before looping
				WatchKey key = watcherService.take();
				while (key != null) { // Start the infinite polling loop
					// Dequeueing events
					Kind<?> kind = null;
					for (WatchEvent<?> watchEvent : key.pollEvents()) {
						// Get the type of the event
						kind = watchEvent.kind();
						if (OVERFLOW == kind) {
							continue; // loop
						} else if (ENTRY_CREATE == kind) {
							// A new Path was created
							Object obj = watchEvent.context();
							if (obj instanceof Path) {
								Path newPath = (Path) obj;
								// Output

								log.info("Found path to import: " + newPath.getFileName());

								if (newPath.toString().startsWith("Umsatzanzeige_")) {
									File toImport = new File(path.toFile(), newPath.getFileName().toString());
									try {
										final String content = new String(
												Files.readAllBytes(Paths.get(toImport.toURI())));
										bankDataServiceImpl.importTransactionCsv(content.getBytes("UTF-8"));
										File backupFile = new File(bakDirFile, newPath.getFileName() + ".imported");
										log.info("Backup file: " + backupFile.getAbsolutePath());
										Files.move(Paths.get(toImport.toURI()), Paths.get(backupFile.toURI()));
									} catch (IOException e) {
										File backupFile = new File(bakDirFile, newPath.getFileName() + ".failed");
										log.info("Backup file: " + backupFile.getAbsolutePath());
										try {
											Files.move(Paths.get(toImport.toURI()), Paths.get(backupFile.toURI()));
											File errorFile = new File(bakDirFile, newPath.getFileName() + ".error");
											Files.write(Paths.get(errorFile.toURI()), e.toString().getBytes("UTF-8"));
										} catch (IOException e1) {
											log.error(e.getMessage(), e);
										}
									}
								}
							} else {
								log.info("New something created: " + obj.getClass().getName());
							}
						}
					}

					key.reset();
					key = watcherService.take();
				}
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	private void startWatchdogServive(Path path, File bakDirFile) throws Exception {
		log.info("Starting watchdog for fileimport on path " + path);
		WatchdogQueueReader fileWatcher = new WatchdogQueueReader(BankDataServiceImpl.this, path, bakDirFile);
		Thread th = new Thread(fileWatcher, "Bank_csv_import_file_watcher");
		th.start();
	}

	@Override
	public void importTransactionCsv(byte[] bytes) throws IOException {
		Iterable<CSVRecord> records = getIngDibaCsvFormat().parse(createReader(bytes));
		// Puh, wie erkennen wir das Format ???
		for (CSVRecord record : records) {
			if (record.getRecordNumber() == 1) {
				if (record.get(0).startsWith("Umsatzanzeige vom")) {
					// Bisher nur Ing Diba
					importTransactionIngDibaCsv(getIngDibaCsvFormat().parse(createReader(bytes)));
					break;
				}
				if (record.toString().contains(
						"values=[Auftragskonto, Buchungstag, Valutadatum, Buchungstext, Verwendungszweck, Beguenstigter/Zahlungspflichtiger, Kontonummer, BLZ, Betrag, Waehrung, Info]]")) {
					importTransactionSparkasse(getIngDibaCsvFormat().parse(createReader(bytes)));
				}
			}
		}
	}

	private void importTransactionIngDibaCsv(Iterable<CSVRecord> records) throws IOException {
		Account account = new Account();
		Stack<Transaction> transactionStack = new Stack<>();
		for (CSVRecord record : records) {
			log.info((int) record.getRecordNumber() + " : " + record.toString());
			switch ((int) record.getRecordNumber()) {
			case 1: // Umsatzanzeige mit Kunde jetzt in einer Zeile
				account.setOwner(record.get(2).trim());
				break;
			case 2: // Konto
				account = setKonto(account, record);
				break;
			case 3: // Zeitraum
				String[] date = record.get(1).split("-");
				account.setUpdated(getDate(date[1], ING_DIBA_DATE_PATTERN));
				break;
			case 4: // Kontosaldo
				account.setAmount(getBigDecimal(record.get(1).trim()));
				account = updateAccount(account);
				break;
			case 5: // Überschriften
				break;
			default: // Eintrag
				Transaction transaction = new Transaction();
				transaction.setAccountNumber(account.getNumber().trim());
				transaction.setAccountingDate(getDate(record.get(0), ING_DIBA_DATE_PATTERN));
				transaction.setValutaDate(getDate(record.get(1), ING_DIBA_DATE_PATTERN));
				transaction.setPrincipal(record.get(2).trim());
				transaction.setAccountingText(record.get(3).trim());
				transaction.setPurpose(record.get(4).trim());
				transaction.setAmount(getBigDecimal(record.get(5)));
				transaction.setTransactionCurrency(record.get(6).trim());
				transaction.setBalance(getBigDecimal(record.get(7)));
				transaction.setBalanceCurrency(record.get(8).trim());
				transaction.setHashCode(transaction.createHashCode());

				// Erstmal in Memory speichern
				transactionStack.push(transaction);
			}
		}

		// Ermitteln der start no für gleiche Datumswerte
		boolean firstEntry = true;
		Long no = 1L;
		Date startDate = null;
		if (!transactionStack.isEmpty()) {
			Transaction first = transactionStack.peek();
			startDate = first.getAccountingDate();
			List<Transaction> res = transactionRepository
					.findAllByAccountNumberAndAccountingDate(first.getAccountNumber(), startDate);
			if (!res.isEmpty()) {
				for (Transaction transaction : res) {
					if (first.getHashCode().equals(transaction.getHashCode())) {
						if (transaction.getNo() != null) {
							no = transaction.getNo();
						} else {
							no = 99L;
						}
						break;
					} else {
						if (transaction.getNo() != null) {
							no = transaction.getNo() > no ? transaction.getNo() : no;
						} else {
							no++;
						}
					}
				}
			}
			log.info("Date Sort number, starting with: " + no + " for " + getFormatedDate(startDate));
		}
		while (!transactionStack.isEmpty() && startDate != null) {
			Transaction transaction = transactionStack.pop();
			Transaction oldTransaction = transactionRepository.findByHashCode(transaction.getHashCode());
			if (!firstEntry) {
				if (startDate.before(transaction.getAccountingDate())) {
					startDate = transaction.getAccountingDate();
					no = 1L;
				} else {
					no++;
				}
			} else {
				firstEntry = false;
			}
			// only save new transactions
			if (oldTransaction == null) {
				transaction.setNo(no);
				transactionRepository.save(transaction);
				log.info("saved transaction: " + transaction);
			} else {
				if (oldTransaction.getNo() == null) {
					oldTransaction.setNo(no);
					transactionRepository.save(oldTransaction);
					log.info("updated transaction: " + oldTransaction);
				} else {
					log.info("Die Transaktion ist bekannt:" + transaction);
				}
			}
		}

		// Add the categories to the new (all) transactions. Should be
		// optimized.
		Iterable<Category> categories = categoryRepository.findAll();
		for (Category category : categories) {
			applyCategoryToTransactions(category);
		}
	}

	private void importTransactionSparkasse(Iterable<CSVRecord> records) throws IOException {
		Account account = new Account();
		for (CSVRecord record : records) {
			switch ((int) record.getRecordNumber()) {
			case 1: // Überschriften
				break;
			default: // Eintrag
				Transaction transaction = new Transaction();
				try {
					// "Auftragskonto";
					String accountNr = record.get(0).trim();
					if (!accountNr.equals(account.getNumber())) {
						account.setName("TODO Name");
						account.setNumber(accountNr);
						account.setOwner("TODO owner");
						account.setAmount(BigDecimal.ZERO);
						account = validateAccount(account);
					}
					transaction.setAccountNumber(account.getNumber().trim());

					// "Buchungstag";"Valutadatum";
					transaction.setAccountingDate(getDate(record.get(1), SPARKASSE_DATE_PATTERN));
					transaction.setValutaDate(getDate(record.get(2), SPARKASSE_DATE_PATTERN));

					// "Buchungstext";"Verwendungszweck";
					transaction.setAccountingText(record.get(3).trim());
					transaction.setPurpose(record.get(4).trim());

					// "Beguenstigter/Zahlungspflichtiger";
					transaction.setPrincipal(record.get(5).trim());

					// "Kontonummer";"BLZ";
					// 6 und 7
					// "Betrag";"Waehrung";"Info"
					transaction.setAmount(getBigDecimal(record.get(8)));
					transaction.setBalanceCurrency(record.get(9).trim());

					transaction.setTransactionCurrency(record.get(9).trim());
				} catch (Exception e) {
					log.error("cCould't parse transaction: " + record.toString());
					break;
				}

				transaction.setHashCode(transaction.createHashCode());
				Transaction oldTransaction = transactionRepository.findByHashCode(transaction.getHashCode());
				// only save new transactions
				if (oldTransaction == null) {
					transactionRepository.save(transaction);
				}
			}
		}
		// Add the categories to the new (all) transactions. Should be
		// optimized.
		Iterable<Category> categories = categoryRepository.findAll();
		for (Category category : categories) {
			applyCategoryToTransactions(category);
		}
	}

	private Account setKonto(Account account, CSVRecord record) {

		if (record.get(1).contains(":")) { // Alte Notation
			String[] vals = record.get(1).split(":");
			account.setName(vals[0].trim());
			account.setNumber(vals[1].trim());
			account = validateAccount(account);
		} else {
			// DE70500105175400959226 anstatt
			// Girokonto: 5400959226
			// DiBA BLZ 500 105 17
			// BLZ +BIC = DE + Prüfziffer + BLZ
			if (record.get(1).startsWith("DE") && record.get(1).contains("50010517")) {
				account.setName("");
				String[] vals = record.get(1).split("50010517");
				account.setNumber(vals[1].trim());
				account = validateAccount(account);
			}
		}
		return account;
	}

	private BigDecimal getBigDecimal(String value) {
		value = value.replace(".", "");
		value = value.replace(',', '.');
		return BigDecimal.valueOf(Double.parseDouble(value));
	}

	protected Date getDate(String dateStr, String datePattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
		try {
			return formatter.parse(dateStr.trim());
		} catch (ParseException e) {
			throw new IllegalStateException(e);
		}

	}

	private String getFormatedDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(ING_DIBA_DATE_PATTERN);
		return dateFormat.format(date);
	}

	private Account validateAccount(Account account) {
		Account entity = accountRepository.findByNumber(account.getNumber());
		if (entity == null) {
			entity = accountRepository.save(account);
		} else {
			if (!account.getName().equals(entity.getName())) {
				entity.setName(account.getName());
				entity = accountRepository.save(entity);
			}
		}
		return entity;
	}

	private Account updateAccount(Account account) {
		Account entity = accountRepository.findByNumber(account.getNumber());
		if (entity == null) {
			entity = accountRepository.save(account);
		} else {
			account.setId(entity.getId());
			account = accountRepository.save(account);
		}
		return account;
	}

	@Override
	public void importFinanceCsv(byte[] bytes) throws IOException {
		Iterable<CSVRecord> records = getIngDibaCsvFormat().parse(createReader(bytes));
		for (CSVRecord record : records) {
			log.info(record.toString());
		}
		throw new IllegalStateException("not implemented");
	}

	private Reader createReader(byte[] bytes) throws UnsupportedEncodingException {
		String csv = new String(bytes, "ISO-8859-15");
		Reader reader = new BufferedReader(new StringReader(csv));
		return reader;
	}

	private CSVFormat getIngDibaCsvFormat() {
		return CSVFormat.DEFAULT.withDelimiter(';');
	}

	@Override
	public Transaction save(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	@Override
	public CategoryFilter save(CategoryFilter categoryFilter) {
		CategoryFilter savedCategoryFilter = categoryFilterRepository.save(categoryFilter);
		applyCategoryToTransactions(savedCategoryFilter);
		return categoryFilterRepository.getOne(savedCategoryFilter.getId());
	}

	@Override
	public List<String> getCategoryNames() {
		List<String> result = new ArrayList<String>();
		Iterable<Category> categories = categoryRepository.findAll();
		for (Category category : categories) {
			result.add(category.getName());
		}
		return result;
	}

	@Override
	public void removeCategoryFromTransactions(CategoryFilter categoryFilter) {

		List<Transaction> entities = transactionRepository
				.findAll(new TransactionCategoryFilterSpecification(categoryFilter), SORT_VALUTA_ASC);
		Iterator<Transaction> iterator = entities.iterator();
		while (iterator.hasNext()) {
			Transaction transaction = iterator.next();
			if (transaction.isCategoryFixed()) {
				iterator.remove();
			} else {
				transaction.setCategory(null);
			}

		}
		transactionRepository.saveAll(entities);
	}

	@Override
	public void applyCategoryToTransactions(CategoryFilter categoryFilter) {
		List<Transaction> entities = transactionRepository
				.findAll(new TransactionCategoryFilterSpecification(categoryFilter), SORT_VALUTA_ASC);
		Iterator<Transaction> iterator = entities.iterator();
		categoryFilter.setMatches(0);
		while (iterator.hasNext()) {
			Transaction transaction = iterator.next();
			if (transaction.isCategoryFixed()) {
				iterator.remove();
			} else {
				transaction.setCategory(categoryFilter.getCategory().getName());
				categoryFilter.setMatches(categoryFilter.getMatches() + 1);
			}
		}
		transactionRepository.saveAll(entities);
		categoryFilterRepository.save(categoryFilter);
	}

	@Override
	public List<Category> findAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public void applyCategoryToTransactions(Category category) {
		for (CategoryFilter categoryFilter : category.getFilter()) {
			List<Transaction> entities = transactionRepository
					.findAll(new TransactionCategoryFilterSpecification(categoryFilter));
			categoryFilter.setMatches(0);
			for (Transaction transaction : entities) {
				if (!transaction.isCategoryFixed()) {
					transaction.setCategory(category.getName());
					categoryFilter.setMatches(categoryFilter.getMatches() + 1);
				}
			}
			transactionRepository.saveAll(entities);
			categoryFilterRepository.save(categoryFilter);
		}

	}

	@Override
	public void removeCategoryFromTransactions(Category category) {
		List<Transaction> entities = transactionRepository.findAllByCategory(category.getName());
		for (Transaction transaction : entities) {
			transaction.setCategory(null);
		}
		transactionRepository.saveAll(entities);
	}

	@Override
	public void deleteCategory(Category category) {
		categoryFilterRepository.deleteAll(category.getFilter());
		removeCategoryFromTransactions(category);
		categoryRepository.delete(category);
	}

	@Override
	public Category save(Category entity) {
		return categoryRepository.save(entity);
	}

	@Override
	public Collection<TransactionSummary> calculateTransactionSummary(TransactionSummaryType transactionSummaryType,
			Date from, @Nullable Date until) {
		if (transactionSummaryType.name().equals(TransactionSummaryType.ALL.name())) {
			Map<String, TransactionSummary> summery = new HashMap<String, TransactionSummary>();
			List<Transaction> entities = transactionRepository.findAll(new TransactionDateSpecification(from, until));
			for (Transaction transaction : entities) {
				if (StringUtils.isEmpty(transaction.getCategory())) {
					transaction.setCategory("no category");
				}
				if (summery.containsKey(transaction.getCategory())) {
					TransactionSummary sum = summery.get(transaction.getCategory());
					sum.addTransaction(transaction);
					log.info(String.valueOf(transaction.getAmount()) + "=>" + sum.toString());
				} else {
					summery.put(transaction.getCategory(), new TransactionSummary(transaction.getCategory(),
							transaction.getAmount(), transaction.getBalanceCurrency(), transaction));
				}
			}
			return summery.values();
		} else {
			return calculateTransactionSummary(
					transactionSummaryType.name().equals(TransactionSummaryType.INCOME.name()), from, until);
		}
	}

	private Collection<TransactionSummary> calculateTransactionSummary(boolean income, Date from,
			@Nullable Date until) {
		Map<String, TransactionSummary> summery = new HashMap<String, TransactionSummary>();
		List<Category> categories = categoryRepository.findByIncome(income);
		Set<String> categoryNames = new HashSet<String>();
		if (!income) {
			categoryNames.add("no category");
		}
		for (Category category : categories) {
			categoryNames.add(category.getName());
		}

		List<Transaction> entities = transactionRepository.findAll(new TransactionDateSpecification(from, until),
				SORT_VALUTA_ASC);
		log.info("calculateTransactionSummary found " + entities.size() + " transactions, income=" + income + ", from="
				+ from + ", until=" + until);
		for (Transaction transaction : entities) {
			if (!income && StringUtils.isEmpty(transaction.getCategory())) {
				transaction.setCategory("no category");
			}
			if (categoryNames.contains(transaction.getCategory())) {
				if (summery.containsKey(transaction.getCategory())) {
					TransactionSummary sum = summery.get(transaction.getCategory());
					sum.addTransaction(transaction);
				} else {
					summery.put(transaction.getCategory(), new TransactionSummary(transaction.getCategory(),
							transaction.getAmount(), transaction.getBalanceCurrency(), transaction));
				}
			}
		}
		log.info("Calculated " + summery.values().size() + " entries.");
		return summery.values();
	}

	public Map<Category, Map<Date, TransactionSummary>> calculateMonthlyOverview() {
		Map<Category, Map<Date, TransactionSummary>> overviewMap = new TreeMap<>();

		// All categories
		List<Category> categories = findAllCategories();
		Collections.sort(categories);

		for (Category category : categories) {
			Map<Date, TransactionSummary> categoryMap = new TreeMap<Date, TransactionSummary>();
			// All transactions sorted by date
			List<Transaction> transactions = findTransactionsByCategory(category);
			Collections.sort(transactions, (o1, o2) -> o1.getValutaDate().compareTo(o2.getValutaDate()));
			Date keyDate = null;
			for (Transaction transaction : transactions) {
				if (keyDate == null) {
					keyDate = transaction.getValutaDate();
				} else {
					Calendar cal = Calendar.getInstance();
					cal.setTime(keyDate);
					cal.add(Calendar.MONTH, 1);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					if (cal.getTime().before(transaction.getValutaDate())) {
						keyDate = transaction.getValutaDate();
					}
				}

				if (!categoryMap.containsKey(keyDate)) {
					TransactionSummary summery = new TransactionSummary(category.getName(), transaction.getAmount(),
							transaction.getBalanceCurrency(), transaction);
					categoryMap.put(keyDate, summery);
				} else {
					TransactionSummary summery = categoryMap.get(keyDate);
					summery.addTransaction(transaction);
				}
			}
			overviewMap.put(category, categoryMap);
		}
		return overviewMap;
	}

	@Override
	public List<PrincipalInfo> getPrincipalInfo() {
		Map<String, PrincipalInfo> infoMap = new HashMap<String, PrincipalInfo>();
		Iterable<Transaction> all = transactionRepository.findAll();
		for (Transaction transaction : all) {
			PrincipalInfo info;
			if (infoMap.containsKey(transaction.getPrincipal())) {
				info = infoMap.get(transaction.getPrincipal());
				info.setCount(info.getCount() + 1);
				info.setFiltered(info.isFiltered() || !StringUtils.isEmpty(transaction.getCategory()));
			} else {
				infoMap.put(transaction.getPrincipal(), new PrincipalInfo(transaction.getPrincipal(), 1,
						!StringUtils.isEmpty(transaction.getCategory())));
			}
		}
		return new ArrayList<PrincipalInfo>(infoMap.values());
	}

	@Override
	public BalanceSummary loadBalanceSummary(Date from, Date until) {
		BalanceSummary balanceSummary = new BalanceSummary();
		balanceSummary.setFrom(from);
		balanceSummary.setUntil(until);
		Iterable<Account> accounts = accountRepository.findAll();
		for (Account account : accounts) {
			balanceSummary.getAccounts().add(account);
			List<Transaction> transactions = transactionRepository
					.findAll(new TransactionDateSpecification(from, until, account.getNumber()), SORT_VALUTA_ASC);
			if (!transactions.isEmpty()) {
				balanceSummary.getFromBalance().put(account.getNumber(), transactions.get(0).getBalance());
				balanceSummary.getUntilBalance().put(account.getNumber(),
						transactions.get(transactions.size() - 1).getBalance());
			} else {
				balanceSummary.getFromBalance().put(account.getNumber(), BigDecimal.ZERO);
				balanceSummary.getUntilBalance().put(account.getNumber(), BigDecimal.ZERO);
			}
		}
		return balanceSummary;
	}

	@Override
	public List<Category> findAllCategoriesForMonthReport() {
		return categoryRepository.findByMarksMonth(true);
	}

	@Override
	public List<Transaction> findTransactionsByCategory(Category category) {
		return transactionRepository.findAllByCategory(category.getName(), SortByValutaDateAsc());
	}

	private Sort SortByValutaDateAsc() {
		return new Sort(Sort.Direction.ASC, "valutaDate");
	}

	@Override
	public Tag save(Tag tag) {
		return tagRepository.save(tag);
	}

	@Override
	public void deleteTag(Tag tag) {
		tagRepository.delete(tag);

	}

	@Override
	public void applyTagToTransactions(Tag tag) {
		// TODO-inren Was hatte ich hier nur vor?
		System.out.println("applyTagToTransactions(Tag " + tag + ")");
	}

	@Override
	public void removeTagFromTransactions(Tag tag) {
		// TODO-inren Was hatte ich hier nur vor?
		System.out.println("removeTagFromTransactions(Tag " + tag + ")");
	}

	@Override
	public List<String> loadAllTagNames() {
		List<String> names = new ArrayList<>();
		Iterable<Tag> all = tagRepository.findAll();
		for (Tag tag : all) {
			names.add(tag.getName());
		}
		return names;
	}

	@Override
	public List<AccountState> loadAllAccountStates() {
		List<AccountState> result = new ArrayList<AccountState>();
		for (Account account : accountRepository.findAll()) {
			result.add(new AccountState(account, account.getAmount(), account.getUpdated()));
		}
		return result;
	}

	@Override
	public List<Integer> getYearsOfAvailabledata() {

		// TODO-inren Werte aus DB berechnen
		return Arrays.asList(new Integer[] { 2016, 2017 });
	}

}
