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
package de.inren.frontend.common.dataprovider;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.banking.TransactionSpecification;
import de.inren.data.repositories.banking.TransactionRepository;

/**
 * Easy to use dataprovider for PagingAndSortingRepository.
 * 
 * The repository must be injected by @SpringBean into the wicket component to
 * avoid serializing problems.
 * 
 * @author Ingo Renner
 *
 */
public class TransactionDataProvider extends ARepositoryDataProvider<Transaction> {

	private final TransactionRepository repository;

	private final TransactionSpecification specification;

	public TransactionDataProvider(TransactionRepository repository, TransactionSpecification specification) {
		super();
		this.repository = repository;
		this.specification = specification;
		setSort("valutaDate", SortOrder.DESCENDING);
	}

	@Override
	protected Page<Transaction> getPage(final Pageable pageable) {
		Page<Transaction> findAll = getRepository().findAll(specification, pageable);
		return findAll;
	}

	@Override
	public long size() {
		return getRepository().count(specification);
	}

	public TransactionRepository getRepository() {
		return repository;
	}
}
