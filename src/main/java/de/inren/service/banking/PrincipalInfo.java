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
package de.inren.service.banking;

import java.io.Serializable;

/**
 * @author Ingo Renner
 *
 */
public class PrincipalInfo implements Serializable {

    private String  principal;
    private int     count;
    private boolean filtered;

    public PrincipalInfo(String principal, int count, boolean filtered) {
        super();
        this.principal = principal;
        this.count = count;
        this.filtered = filtered;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    @Override
    public String toString() {
        return "PrincipalInfo [principal=" + principal + ", count=" + count + ", filtered=" + filtered + "]";
    }

}
