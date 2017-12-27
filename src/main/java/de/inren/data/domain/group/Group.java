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
package de.inren.data.domain.group;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.inren.data.domain.security.AuthorizedDomainObject;
import de.inren.data.domain.security.Role;

/**
 * 
 * @author Ingo Renner
 * 
 */
@Entity(name = "GroupOfRoles")
// Group is not allowed with mysql.
public class Group extends AuthorizedDomainObject {

	// @ManyToMany(fetch = FetchType.EAGER)
	// @JoinTable(name = "domain_role", joinColumns = { @JoinColumn(name =
	// "domain_role_domainid", referencedColumnName = "id") }, inverseJoinColumns =
	// {
	// @JoinColumn(name = "domain_role_roleid", referencedColumnName = "id") })
	// private final Collection<Role> roles;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = true)
	private String description;

	public Group() {
		this(null, null);
	}

	public Group(String name) {
		this(name, null);
	}

	public Group(String name, String description) {
		this.name = name;
		this.description = description;
		setRoles(new ArrayList<Role>());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		builder.append("Group [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}