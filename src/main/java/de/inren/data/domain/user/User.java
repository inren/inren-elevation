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
package de.inren.data.domain.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.inren.data.domain.group.Group;
import de.inren.data.domain.security.AuthorizedDomainObject;
import de.inren.data.domain.security.Role;

/**
 * 
 * Just some user entity.
 * 
 * @author Ingo Renner
 * 
 */
@Entity
public class User extends AuthorizedDomainObject implements UserDetails {

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String firstname;

	@Column(nullable = false)
	private String lastname;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_groups", joinColumns = {
			@JoinColumn(name = "user_groups_userid", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "user_groups_groupid", referencedColumnName = "id") })
	private Set<Group> groups;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	/** Remainder for the password, can be null */
	private String passRemainder;

	/** Date the account was created */
	private Date registeredDate;

	/** Date of the last login */
	private Date lastLogin;

	/** we don't delete users, we just mark them as deleted */
	private Boolean deleted;

	private String activationKey;

	public User() {
	}

	@Override
	public boolean isEnabled() {
		return accountNonExpired && accountNonLocked && credentialsNonExpired && !deleted;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	/** We use email for indent */
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassRemainder() {
		return passRemainder;
	}

	public void setPassRemainder(String passRemainder) {
		this.passRemainder = passRemainder;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	@Transient
	private Set<SimpleGrantedAuthority> authorities;

	@Override
	public Collection<SimpleGrantedAuthority> getAuthorities() {
		if (authorities == null) {
			initAuthorities();
		}
		return authorities;
	}

	private void initAuthorities() {
		this.authorities = new HashSet<SimpleGrantedAuthority>();
		Collection<Role> roles = getGrantedRoles();
		for (Role role : roles) {
			authorities.addAll(role.asAuthority());
		}
	}

	public Collection<Group> getGroups() {
		return new ArrayList<>(this.groups);
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = new HashSet<>(groups);
	}

	@Override
	public Collection<Role> getGrantedRoles() {
		Collection<Role> grantedRoles = new HashSet<Role>();

		grantedRoles.addAll(getRoles());
		for (Group group : getGroups()) {
			grantedRoles.addAll(group.getRoles());
		}
		return grantedRoles;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [email=");
		builder.append(email);
		builder.append(", password=");
		builder.append(password);
		builder.append(", firstname=");
		builder.append(firstname);
		builder.append(", lastname=");
		builder.append(lastname);
		builder.append(", groups=");
		builder.append(groups);
		builder.append(", accountNonExpired=");
		builder.append(accountNonExpired);
		builder.append(", accountNonLocked=");
		builder.append(accountNonLocked);
		builder.append(", credentialsNonExpired=");
		builder.append(credentialsNonExpired);
		builder.append(", passRemainder=");
		builder.append(passRemainder);
		builder.append(", registeredDate=");
		builder.append(registeredDate);
		builder.append(", lastLogin=");
		builder.append(lastLogin);
		builder.append(", deleted=");
		builder.append(deleted);
		builder.append(", activationKey=");
		builder.append(activationKey);
		builder.append(", authorities=");
		builder.append(authorities);
		builder.append("]");
		return builder.toString();
	}

}
