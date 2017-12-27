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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.data.domain.banking.Transaction;

/**
 * The total value of all transactions of one category.
 * 
 * @author Ingo Renner
 *
 */
public class TransactionSummary implements Serializable {

    private final static Logger      log                    = LoggerFactory.getLogger(TransactionSummary.class);

    public enum TransactionSummaryType {
        ALL, INCOME, EXPENSE;
    }

    String                 category;
    BigDecimal             sum;
    String                 currency;
    TransactionSummaryType transactionSummaryType;
    List<Transaction>      transactions = new ArrayList<Transaction>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Add the transaction list and update the summary counter
     * 
     * @param transaction
     */
    public void addTransaction(Transaction transaction) {
        setSum(getSum().add(transaction.getAmount()));
        getTransactions().add(transaction);
    }

    public TransactionSummaryType getTransactionSummaryType() {
        return transactionSummaryType;
    }

    public void setTransactionSummaryType(TransactionSummaryType transactionSummaryType) {
        this.transactionSummaryType = transactionSummaryType;
    }

    /**
     * @param category
     *            Category of the sum
     * @param sum
     *            the sum
     * @param currency
     *            the currency of the sum
     * @param transaction
     */
    public TransactionSummary(String category, BigDecimal sum, String currency, Transaction transaction) {
        super();
        this.category = category;
        this.sum = sum;
        this.currency = currency;
        getTransactions().add(transaction);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getSum() {
        return sum;
    }

    private void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "TransactionSummary [category=" + category + ", sum=" + sum + ", currency=" + currency + ", transactionSummaryType=" + transactionSummaryType
                + "]";
    }

}
