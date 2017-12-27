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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.inren.data.domain.banking.Account;

/**
 * @author Ingo Renner
 *
 */
public class BalanceSummary implements Serializable {

    private Date                    from;
    private Date                    until;
    private List<Account>           accounts     = new ArrayList<Account>();
    private Map<String, BigDecimal> fromBalance  = new HashMap<String, BigDecimal>();
    private Map<String, BigDecimal> untilBalance = new HashMap<String, BigDecimal>();

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getUntil() {
        return until;
    }

    public void setUntil(Date until) {
        this.until = until;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Map<String, BigDecimal> getFromBalance() {
        return fromBalance;
    }

    public void setFromBalance(Map<String, BigDecimal> fromBalance) {
        this.fromBalance = fromBalance;
    }

    public Map<String, BigDecimal> getUntilBalance() {
        return untilBalance;
    }

    public void setUntilBalance(Map<String, BigDecimal> untilBalance) {
        this.untilBalance = untilBalance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BalanceSummary [from=");
        builder.append(from);
        builder.append(", until=");
        builder.append(until);
        builder.append(", accounts=");
        builder.append(accounts);
        builder.append(", fromBalance=");
        builder.append(fromBalance);
        builder.append(", untilBalance=");
        builder.append(untilBalance);
        builder.append("]");
        return builder.toString();
    }

}
