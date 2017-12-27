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
package de.inren.data.domain.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import net.bull.javamelody.MonitoredWithSpring;
import de.inren.data.domain.core.DomainObject;

@Entity
@MonitoredWithSpring
public abstract class AuthorizedDomainObject extends DomainObject implements AccessAuthorization {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "domain_role", joinColumns = { @JoinColumn(name = "domain_role_domainid", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "domain_role_roleid", referencedColumnName = "id") })
    private Collection<Role> roles;

    @Override
    public Collection<Role> getRoles() {
        if (roles == null) {
            this.roles = new ArrayList<Role>();
        }
        return roles;
    }

    @Override
    public void setRoles(Collection<Role> roles) {
        this.roles = new ArrayList<Role>();
        this.roles.addAll(roles);
    }

    /**
     * Return all roles from getRoles.
     * 
     * If your object gets more roles (from groups for example), you must
     * override this method and add the roles there.
     */
    @Override
    public Collection<Role> getGrantedRoles() {
        Collection<Role> grantedRoles = new HashSet<Role>();
        grantedRoles.addAll(getRoles());
        return grantedRoles;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AuthorizedDomainObject [roles=");
        builder.append(roles);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

}
