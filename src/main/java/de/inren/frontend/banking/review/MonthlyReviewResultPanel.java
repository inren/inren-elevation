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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.frontend.banking.common.TransactionSummaryListPanel;
import de.inren.frontend.common.panel.MoneyPanel;
import de.inren.service.banking.BankDataService;
import de.inren.service.banking.TransactionSummary;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;

/**
 * @author Ingo Renner
 *
 */
public class MonthlyReviewResultPanel extends Panel {
    
    private final static Logger      log                    = LoggerFactory.getLogger(MonthlyReviewResultPanel.class);

    @SpringBean
    private BankDataService               bankDataService;

    private ListModel<TransactionSummary> incomeModel;
    private ListModel<TransactionSummary> expensesModel;

    private IModel<Date>                  startDateModel;
    private IModel<Date>                  endDateModel;

    public MonthlyReviewResultPanel(String id, IModel<Date> startDateModel, IModel<Date> endDateModel) {
        super(id);
        this.startDateModel = startDateModel;
        this.endDateModel = endDateModel;
        initDateModels();
    }

    private void initDateModels() {
        incomeModel = new ListModel<TransactionSummary>(new ArrayList<TransactionSummary>());
        expensesModel = new ListModel<TransactionSummary>(new ArrayList<TransactionSummary>());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        recalculate();
    }

    private void recalculate() {
        incomeModel.getObject().clear();
        incomeModel.getObject().addAll(
                bankDataService.calculateTransactionSummary(TransactionSummaryType.INCOME, startDateModel.getObject(), endDateModel.getObject()));
        expensesModel.getObject().clear();
        expensesModel.getObject().addAll(
                bankDataService.calculateTransactionSummary(TransactionSummaryType.EXPENSE, startDateModel.getObject(), endDateModel.getObject()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final Component balances = new BalanceSummaryPanel("balances", startDateModel, endDateModel);
        final Component incomeTable = getListPanel("income", incomeModel);
        final Component expensesTable = getListPanel("expenses", expensesModel);
        final Component incomeTotalLabel = new MoneyPanel("incomeTotal", createIncomeSummaryModel()).setOutputMarkupId(true);
        final Component expensesTotalLabel = new MoneyPanel("expensesTotal", createExpensesSummaryModel()).setOutputMarkupId(true);
        final Component monthTotalLabel = new MoneyPanel("monthTotal", createMonthTotalSummaryModel()).setOutputMarkupId(true);

        add(balances);
        add(incomeTable);
        add(new Label("incomeTotal.label", new StringResourceModel("incomeTotal.label", MonthlyReviewResultPanel.this, null)));
        add(incomeTotalLabel);

        add(expensesTable);
        add(new Label("expensesTotal.label", new StringResourceModel("expensesTotal.label", MonthlyReviewResultPanel.this, null)));
        add(expensesTotalLabel);

        add(new Label("monthTotal.label", new StringResourceModel("monthTotal.label", MonthlyReviewResultPanel.this, null)));
        add(monthTotalLabel);
        setOutputMarkupId(true);
    }

    private IModel<BigDecimal> createIncomeSummaryModel() {
        return new AbstractReadOnlyModel<BigDecimal>() {

            @Override
            public BigDecimal getObject() {
                BigDecimal sum = calculateTotal(incomeModel);
                return sum;
            }

        };
    }

    private IModel<BigDecimal> createExpensesSummaryModel() {
        return new AbstractReadOnlyModel<BigDecimal>() {

            @Override
            public BigDecimal getObject() {
                BigDecimal sum = calculateTotal(expensesModel);
                return sum;
            }

        };
    }

    private BigDecimal calculateTotal(ListModel<TransactionSummary> transactionSummaryModel) {
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionSummary transactionSummary : transactionSummaryModel.getObject()) {
            total = total.add(transactionSummary.getSum());
        }
        return total;
    }

    private Component getListPanel(String id, ListModel<TransactionSummary> model) {
        return new TransactionSummaryListPanel(id, model).setOutputMarkupId(true);
    }

    private IModel<BigDecimal> createMonthTotalSummaryModel() {
        return new AbstractReadOnlyModel<BigDecimal>() {

            @Override
            public BigDecimal getObject() {
                BigDecimal sum = calculateTotal(incomeModel).add(calculateTotal(expensesModel));
                return sum;
            }

        };
    }

}
