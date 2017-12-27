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
package de.inren.data.repositories.banking;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.inren.data.domain.banking.Transaction;

/**
 * @author Ingo Renner
 *
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

	Transaction findByHashCode(String hashCode);

	// // List<Transaction> findAllOrderByAccountingDateAscOrderByNoAsc();
	// List<Transaction> findAllByOrderByAccountingDateAndNoAsc();

	List<Transaction> findAllByOrderByValutaDateAsc();

	List<Transaction> findAllByCategory(String category);

	List<Transaction> findAllByCategory(String category, Sort sort);

	Transaction findFirstByAccountNumberOrderByValutaDateDesc(String accountNumber);

	List<Transaction> findAllByAccountNumberAndAccountingDate(String accountNumber, Date accountingDate);

}
