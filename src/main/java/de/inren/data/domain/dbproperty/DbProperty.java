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
package de.inren.data.domain.dbproperty;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 * 
 */
@Entity
public class DbProperty extends DomainObject {

	public DbProperty(String name, String host, String value, String description) {
		super();
		this.name = name;
		this.host = host;
		this.value = value;
		this.description = description;
	}

	public DbProperty() {
		super();
	}

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "host", nullable = true)
	private String host;

	@Column(nullable = false)
	private String value;

	@Column(nullable = true)
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DbProperty [name=");
		builder.append(name);
		builder.append(", host=");
		builder.append(host);
		builder.append(", value=");
		builder.append(value);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

}
