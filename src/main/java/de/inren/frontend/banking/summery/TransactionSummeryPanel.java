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
package de.inren.frontend.banking.summery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.datetime.PatternDateConverter;
import org.wicketstuff.datetime.extensions.yui.calendar.DatePicker;
import org.wicketstuff.datetime.markup.html.form.DateTextField;

import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.inren.data.domain.banking.Transaction;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;
import de.inren.service.banking.BankDataService;
import de.inren.service.banking.TransactionSummary;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;

/**
 * @author Ingo Renner
 *
 */
public class TransactionSummeryPanel extends ABasePanel<Transaction> {

	@SpringBean
	private BankDataService bankDataService;
	private IModel<Date> startDateModel;
	private IModel<Date> endDateModel;
	private ListModel<TransactionSummary> transactionSummaryModel;

	public TransactionSummeryPanel(String id) {
		super(id);
		initDateModels();
	}

	private void initDateModels() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		startDateModel = Model.of(cal.getTime());
		cal.add(Calendar.MONTH, 1);
		endDateModel = Model.of(cal.getTime());
		transactionSummaryModel = new ListModel<TransactionSummary>(new ArrayList<TransactionSummary>());
		recalculate();
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}

	private void recalculate() {
		transactionSummaryModel.getObject().clear();
		transactionSummaryModel.getObject().addAll(bankDataService.calculateTransactionSummary(
				TransactionSummaryType.ALL, startDateModel.getObject(), endDateModel.getObject()));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		final Component table = getTable("table");

		Form<Void> form = new Form<Void>("form");

		// From Date
		StringResourceModel lStartDate = new StringResourceModel("startDate.label", TransactionSummeryPanel.this, null);
		form.add(new Label("startDate.label", lStartDate));
		DateTextField startDate = new DateTextField("startDate", startDateModel,
				new PatternDateConverter("dd.MM.yyyy", true));
		DatePicker startDatePicker = new DatePicker() {
			@Override
			protected boolean enableMonthYearSelection() {
				return true;
			}
		};
		startDatePicker.setShowOnFieldClick(true);
		startDate.add(startDatePicker);
		form.add(startDate);

		// Until Date
		StringResourceModel lEndDate = new StringResourceModel("endDate.label", TransactionSummeryPanel.this, null);
		form.add(new Label("endDate.label", lEndDate));
		DateTextField endDate = new DateTextField("endDate", endDateModel,
				new PatternDateConverter("dd.MM.yyyy", true));
		DatePicker endDatePicker = new DatePicker() {
			@Override
			protected boolean enableMonthYearSelection() {
				return true;
			}
		};
		endDatePicker.setShowOnFieldClick(true);
		endDate.add(endDatePicker);
		form.add(endDate);

		form.add(new AjaxButton("calculate") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				try {
					recalculate();
					target.add(table);

				} catch (Exception e) {
					log.error(e.getMessage(), e);
					form.error(new StringResourceModel("TODO", TransactionSummeryPanel.this, null).getString());
					target.add(getFeedback());
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				FeedbackPanel f = getFeedback();
				if (target != null && f != null) {
					target.add(f);
				}
			}
		});
		add(form);

		add(table);

	}

	private Component getTable(final String id) {
		AjaxFallbackDefaultDataTableBuilder<TransactionSummary> builder = new AjaxFallbackDefaultDataTableBuilder<TransactionSummary>(
				TransactionSummeryPanel.this);

		Component table = builder.addDataProvider(getDataProvider()).addPropertyColumn("category", true)
				.addMoneyPropertyColumn("sum", true).setNumberOfRows(30).build(id);
		TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
		table.add(tableBehavior);
		return table;
	}

	private SortableDataProvider<TransactionSummary, String> getDataProvider() {
		return new SortableDataProvider<TransactionSummary, String>() {

			@Override
			public Iterator<? extends TransactionSummary> iterator(long first, long count) {
				return transactionSummaryModel.getObject().subList((int) first, (int) first + (int) count).iterator();
			}

			@Override
			public long size() {
				return transactionSummaryModel.getObject().size();
			}

			@Override
			public IModel<TransactionSummary> model(TransactionSummary object) {
				return Model.of(object);
			}

		};
	}
}
