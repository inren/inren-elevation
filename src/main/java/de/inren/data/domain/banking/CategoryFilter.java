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
package de.inren.data.domain.banking;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 *
 */
@Entity(name = "CategoryFilter")
public class CategoryFilter extends DomainObject {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Category_id")
	private Category category;

	// Filter for tagging
	private String accountingTextFilter;
	private String principalFilter;
	private String purposeFilter;

	private Integer matches = 0;

	public CategoryFilter() {
		super();
	}

	public CategoryFilter(Category category, String accountingTextFilter, String principalFilter,
			String purposeFilter) {
		super();
		this.category = category;
		this.accountingTextFilter = accountingTextFilter;
		this.principalFilter = principalFilter;
		this.purposeFilter = purposeFilter;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getAccountingTextFilter() {
		return accountingTextFilter;
	}

	public void setAccountingTextFilter(String accountingTextFilter) {
		this.accountingTextFilter = accountingTextFilter;
	}

	public String getPrincipalFilter() {
		return principalFilter;
	}

	public void setPrincipalFilter(String principalFilter) {
		this.principalFilter = principalFilter;
	}

	public String getPurposeFilter() {
		return purposeFilter;
	}

	public void setPurposeFilter(String purposeFilter) {
		this.purposeFilter = purposeFilter;
	}

	public Integer getMatches() {
		return matches;
	}

	public void setMatches(Integer matches) {
		this.matches = matches;
	}

	@Override
	public String toString() {
		return "CategoryFilter [category=" + category != null ? category.getName()
				: "null" + ", accountingTextFilter=" + accountingTextFilter + ", principalFilter=" + principalFilter
						+ ", purposeFilter=" + purposeFilter + "]";
	}
}
