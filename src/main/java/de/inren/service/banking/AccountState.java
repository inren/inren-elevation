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
/**
 * 
 */
package de.inren.service.banking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import de.inren.data.domain.banking.Account;

/**
 * @author Ingo Renner
 *
 */
public class AccountState implements Serializable {

    private Account account;
    private BigDecimal amount = BigDecimal.ZERO;
    private Date lastUpdate = new Date(0);
    public AccountState(Account account, BigDecimal amount, Date lastUpdate) {
        super();
        this.account = account;
        this.amount = amount;
        this.lastUpdate = lastUpdate;
    }
    
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccountState [account=").append(account).append(", amount=").append(amount).append(", lastUpdate=").append(lastUpdate).append("]");
        return builder.toString();
    }
    
}
