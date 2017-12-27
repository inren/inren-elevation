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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 *
 */
@Entity(name = "Category")
public class Category extends DomainObject implements Comparable<Category> {

	public Category() {
		super();
	}

	public Category(String name, boolean income, boolean marksMonth, String description) {
		super();
		this.name = name;
		this.income = income;
		this.marksMonth = marksMonth;
		this.description = description;
	}

	@Column(nullable = false, unique = true)
	private String name;

	// income or expense (default)
	private boolean income;

	// Can be used to find start and end date for a (money) month period (time
	// from
	// one income to another).
	private boolean marksMonth;

	@Column(nullable = true)
	private String description;

	@OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
	private Set<CategoryFilter> filter;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIncome() {
		return income;
	}

	public void setIncome(boolean income) {
		this.income = income;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<CategoryFilter> getFilter() {
		return new ArrayList<CategoryFilter>(filter);
	}

	public void setFilter(List<CategoryFilter> filter) {
		this.filter = new HashSet<>(filter);
	}

	public boolean isMarksMonth() {
		return marksMonth;
	}

	public void setMarksMonth(boolean marksMonth) {
		this.marksMonth = marksMonth;
	}

	@Override
	public String toString() {
		return "Category [name=" + name + ", income=" + income + ", description=" + description + ", filter=" + "---"
				+ ", getId()=" + getId() + ", isNew()=" + isNew() + ", toString()=" + super.toString() + ", hashCode()="
				+ hashCode() + ", getClass()=" + getClass() + "]";
	}

	@Override
	public int compareTo(Category o) {
		if (this.isIncome() && o.isIncome()) {
			// same type, order by name
			return this.getName().compareTo(o.getName());
		}
		// Income before expense
		return this.isIncome() ? -1 : 1;
	}

}
