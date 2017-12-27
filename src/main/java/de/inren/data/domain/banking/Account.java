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

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 *
 */
@Entity(name = "baccount")
public class Account extends DomainObject {

	private String number;
	private String owner;
	private String name;
	private BigDecimal amount;
	private Date updated;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [number=").append(number).append(", owner=").append(owner).append(", name=")
				.append(name).append(", amount=").append(amount).append(", getAmount()=").append(getAmount())
				.append(", getName()=").append(getName()).append(", getNumber()=").append(getNumber())
				.append(", getOwner()=").append(getOwner()).append(", getId()=").append(getId()).append(", isNew()=")
				.append(isNew()).append(", toString()=").append(super.toString()).append(", hashCode()=")
				.append(hashCode()).append(", getClass()=").append(getClass()).append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
