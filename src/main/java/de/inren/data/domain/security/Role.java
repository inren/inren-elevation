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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 * 
 */
@Entity
public class Role extends DomainObject {

    @Column(nullable = false, unique = true)
    private String            name;

    @Column(nullable = true)
    private String            description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_right", joinColumns = { @JoinColumn(name = "role_rightid", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "right_roleid", referencedColumnName = "id") })
    private Collection<Right> rights;

    public Role() {
        this(null, null);
    }

    public Role(String name) {
        this(name, null);
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.rights = new ArrayList<Right>();
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

    public Collection<Right> getRights() {
        return rights;
    }

    public void setRights(Collection<Right> rights) {
        this.rights = rights;
    }

    public Collection<SimpleGrantedAuthority> asAuthority() {
        Collection<SimpleGrantedAuthority> res = new ArrayList<SimpleGrantedAuthority>();
        for (Right right : rights) {
            res.add(new SimpleGrantedAuthority(name + ":" + right.getName()));
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", rights=");
        builder.append(rights);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((rights == null) ? 0 : rights.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (rights == null) {
            if (other.rights != null)
                return false;
        } else if (!rights.equals(other.rights))
            return false;
        return true;
    }
}