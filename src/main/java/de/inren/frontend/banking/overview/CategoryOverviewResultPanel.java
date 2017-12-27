package de.inren.frontend.banking.overview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.facade.banking.BankingFacade;
import de.inren.frontend.banking.common.TransactionSummaryListPanel;
import de.inren.frontend.banking.review.BalanceSummaryPanel;
import de.inren.frontend.common.panel.MoneyPanel;
import de.inren.service.banking.BankDataService;
import de.inren.service.banking.TransactionSummary;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;

public class CategoryOverviewResultPanel extends Panel {
	@SpringBean
	private BankingFacade bankingFacade;

	@SpringBean
	private BankDataService bankDataService;

	private ListModel<TransactionSummary> incomeModel;
	private ListModel<TransactionSummary> expensesModel;

	private IModel<Date> startDateModel;
	private IModel<Date> endDateModel;

	protected Map<String, List<BigDecimal>> categoryData = new HashMap<>();

	public CategoryOverviewResultPanel(String id, IModel<Date> startDateModel, IModel<Date> endDateModel) {
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
		incomeModel.getObject().addAll(bankDataService.calculateTransactionSummary(TransactionSummaryType.INCOME,
				startDateModel.getObject(), endDateModel.getObject()));
		expensesModel.getObject().clear();
		expensesModel.getObject().addAll(bankDataService.calculateTransactionSummary(TransactionSummaryType.EXPENSE,
				startDateModel.getObject(), endDateModel.getObject()));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final Component balances = new BalanceSummaryPanel("balances", startDateModel, endDateModel);
		final Component incomeTable = getListPanel("income", incomeModel);
		final Component expensesTable = getListPanel("expenses", expensesModel);
		final Component incomeTotalLabel = new MoneyPanel("incomeTotal", createIncomeSummaryModel())
				.setOutputMarkupId(true);
		final Component expensesTotalLabel = new MoneyPanel("expensesTotal", createExpensesSummaryModel())
				.setOutputMarkupId(true);
		final Component monthTotalLabel = new MoneyPanel("monthTotal", createMonthTotalSummaryModel())
				.setOutputMarkupId(true);

		add(balances);
		add(incomeTable);
		add(new Label("incomeTotal.label",
				new StringResourceModel("incomeTotal.label", CategoryOverviewResultPanel.this, null)));
		add(incomeTotalLabel);

		add(expensesTable);
		add(new Label("expensesTotal.label",
				new StringResourceModel("expensesTotal.label", CategoryOverviewResultPanel.this, null)));
		add(expensesTotalLabel);

		add(new Label("monthTotal.label",
				new StringResourceModel("monthTotal.label", CategoryOverviewResultPanel.this, null)));
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
