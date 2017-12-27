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
package de.inren.frontend.banking.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import de.inren.data.domain.banking.Transaction;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.MoneyPanel;

/**
 * @author Ingo Renner
 *
 */
public class TransactionPanel extends ABasePanel<Transaction> {

    public TransactionPanel(String id, IModel<Transaction> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("accountingText", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                return getModel().getObject().getDisplayValue();
            }
        });
        label.add(AttributeModifier.append("title", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                StringBuilder sb = new StringBuilder();
                Transaction transaction = getModel().getObject();
                sb
                        .append("ID: ")
                        .append(transaction.getId())
                        .append("\n")
                        .append("Hash: ")
                        .append(transaction.getHashCode())
                        .append("\n")
                        .append("AccountNumber: ")
                        .append(transaction.getAccountNumber())
                        .append("\n")
                        .append("Balance:  ")
                        .append(transaction.getBalance())
                        .append("\n")
                        .append("Text: ")
                        .append(transaction.getAccountingText().trim())
                        .append("\n")
                        .append("Principal: ")
                        .append(transaction.getPrincipal().trim())
                        .append("\n")
                        .append("Purpose: ")
                        .append(transaction.getPurpose().trim())
                        .append("\n")
                        .append("Accounting date: ")
                        .append(getFormatedDate(transaction.getAccountingDate()))
                        .append(" ")
                        .append(transaction.getNo())
                        .append("\n")
                        .append("Valuta date: ")
                        .append(getFormatedDate(transaction.getValutaDate()))
                        .append("\n")
                        .append("Amount: ")
                        .append(transaction.getAmount())
                        .append(" ")
                        .append(transaction.getTransactionCurrency())
                        .append(".");
                return sb.toString();
            }
        }));
        add(label);
        add(new MoneyPanel("amount", new PropertyModel<>(getModel(), "amount")));
    }

    private String getFormatedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(date);
    }
}
