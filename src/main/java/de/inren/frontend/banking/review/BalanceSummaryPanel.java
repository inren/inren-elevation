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
package de.inren.frontend.banking.review;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.banking.Account;
import de.inren.frontend.common.panel.MoneyPanel;
import de.inren.service.banking.BalanceSummary;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 *
 */
public class BalanceSummaryPanel extends Panel {
    @SpringBean
    private BankDataService        bankDataService;

    private IModel<Date>           fromModel;
    private IModel<Date>           untilModel;
    private IModel<BalanceSummary> balanceSummaryModel;

    public BalanceSummaryPanel(String id, IModel<Date> fromModel, IModel<Date> untilModel) {
        super(id);
        this.fromModel = fromModel;
        this.untilModel = untilModel;
        balanceSummaryModel = new LoadableDetachableModel<BalanceSummary>() {

            @Override
            protected BalanceSummary load() {
                return bankDataService.loadBalanceSummary(fromModel.getObject(), untilModel.getObject());
            }
        };
        this.setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        balanceSummaryModel.detach();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new ListView<BalanceSummaryEntry>("list", createBalanceSummaryEntryModel()) {

            @Override
            protected void populateItem(ListItem<BalanceSummaryEntry> item) {
                item.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")).setEscapeModelStrings(false));
                item.add(new Label("account", new PropertyModel<String>(item.getModel(), "account")).setEscapeModelStrings(false));
                item.add(new MoneyPanel("fromBalance", new PropertyModel<BigDecimal>(item.getModel(), "fromBalance")));
                item.add(new MoneyPanel("untilBalance", new PropertyModel<BigDecimal>(item.getModel(), "untilBalance")));
                item.add(new MoneyPanel("summary", new PropertyModel<BigDecimal>(item.getModel(), "summary")));
            }
        }.setReuseItems(false));

    }

    private class BalanceSummaryEntry implements Serializable {
        private String     account;
        private BigDecimal fromBalance;
        private BigDecimal untilBalance;
        private String     name;

        public BalanceSummaryEntry(String name, String account, BigDecimal fromBalance, BigDecimal untilBalance) {
            super();
            this.name = name;
            this.account = account;
            this.fromBalance = fromBalance;
            this.untilBalance = untilBalance;

        }

        @SuppressWarnings("unused")
        public String getAccount() {
            return account;
        }

        public void setFromBalance(BigDecimal fromBalance) {
            this.fromBalance = fromBalance;
        }

        public void setUntilBalance(BigDecimal untilBalance) {
            this.untilBalance = untilBalance;
        }

        public BigDecimal getFromBalance() {
            return fromBalance;
        }

        public BigDecimal getUntilBalance() {
            return untilBalance;
        }

        @SuppressWarnings("unused")
        public BigDecimal getSummary() {
            return untilBalance.subtract(fromBalance);
        }
    }

    private IModel<List<BalanceSummaryEntry>> createBalanceSummaryEntryModel() {
        return new LoadableDetachableModel<List<BalanceSummaryEntry>>() {

            @Override
            protected List<BalanceSummaryEntry> load() {
                List<BalanceSummaryEntry> balanceSummaryEntries = new
                        ArrayList<BalanceSummaryEntry>();
                BalanceSummary summary = balanceSummaryModel.getObject();
                BalanceSummaryEntry total = new BalanceSummaryEntry("&sum;", "", BigDecimal.ZERO, BigDecimal.ZERO);
                for (Account account : summary.getAccounts()) {
                    BigDecimal fromBalance = summary.getFromBalance().get(account.getNumber());
                    BigDecimal untilBalance = summary.getUntilBalance().get(account.getNumber());
                    balanceSummaryEntries.add(new BalanceSummaryEntry(account.getName(), account.getNumber(), fromBalance, untilBalance));
                    total.setFromBalance(total.getFromBalance().add(fromBalance));
                    total.setUntilBalance(total.getUntilBalance().add(untilBalance));
                }
                balanceSummaryEntries.add(total);
                return balanceSummaryEntries;
            }

        };

    }
}
