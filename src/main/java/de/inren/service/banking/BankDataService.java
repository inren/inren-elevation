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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.inren.data.domain.banking.Category;
import de.inren.data.domain.banking.CategoryFilter;
import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.tagging.Tag;
import de.inren.service.Initializable;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * @author Ingo Renner
 *
 */
@MonitoredWithSpring
public interface BankDataService extends Initializable {

	void importTransactionCsv(byte[] bytes) throws IOException;

	void importFinanceCsv(byte[] bytes) throws IOException;

	Transaction save(Transaction transaction);

	CategoryFilter save(CategoryFilter category);

	List<String> getCategoryNames();

	void removeCategoryFromTransactions(CategoryFilter categoryFilter);

	void applyCategoryToTransactions(CategoryFilter categoryFilter);

	List<Category> findAllCategories();

	List<AccountState> loadAllAccountStates();

	void applyCategoryToTransactions(Category category);

	void removeCategoryFromTransactions(Category category);

	void deleteCategory(Category category);

	Category save(Category category);

	Tag save(Tag tag);

	Collection<TransactionSummary> calculateTransactionSummary(TransactionSummaryType transactionSummaryType, Date from,
			Date until);

	List<PrincipalInfo> getPrincipalInfo();

	BalanceSummary loadBalanceSummary(Date from, Date until);

	List<Category> findAllCategoriesForMonthReport();

	List<Transaction> findTransactionsByCategory(Category category);

	void deleteTag(Tag tag);

	void applyTagToTransactions(Tag tag);

	void removeTagFromTransactions(Tag tag);

	List<String> loadAllTagNames();

	List<Integer> getYearsOfAvailabledata();

}
