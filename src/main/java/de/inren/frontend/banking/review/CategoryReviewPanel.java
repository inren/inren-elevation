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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.datetime.markup.html.basic.DateLabel;

import de.inren.data.domain.banking.Category;
import de.inren.data.domain.banking.Transaction;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 *
 */
public class CategoryReviewPanel extends ABasePanel<Void> {

	@SpringBean
	private BankDataService bankDataService;

	private IModel<Integer> yearModel;
	private IModel<Integer> monthModel;
	private IModel<Date> startDateModel;
	private IModel<Date> endDateModel;
	private IModel<Category> categoryModel;

	private List<Transaction> availableTransactions;

	public CategoryReviewPanel(String id) {
		super(id);
		categoryModel = new Model<Category>(getFirstCategory());
		initTransactions();
		initDateModels();
	}

	private Category getFirstCategory() {
		List<Category> res = getAllCategory();
		return res.isEmpty() ? null : res.get(0);
	}

	private List<Category> getAllCategory() {
		return bankDataService.findAllCategoriesForMonthReport();
	}

	private void initDateModels() {
		Calendar cal = Calendar.getInstance();
		yearModel = Model.of(Integer.valueOf(cal.get(Calendar.YEAR)));
		monthModel = Model.of(Integer.valueOf(cal.get(Calendar.MONTH)));
		startDateModel = () -> {
			Calendar cal1 = Calendar.getInstance();
			cal1.set(Calendar.YEAR, yearModel.getObject());
			cal1.set(Calendar.MONTH, monthModel.getObject());
			cal1.set(Calendar.DAY_OF_MONTH, 1);
			cal1.set(Calendar.HOUR, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			cal1 = adjustStartByCategory(cal1);
			monthModel.setObject(cal1.get(Calendar.MONTH));
			return cal1.getTime();
		};

		endDateModel = () -> {
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(startDateModel.getObject());
			cal1 = adjustEndByCategory(cal1);
			cal1.set(Calendar.HOUR, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			cal1.add(Calendar.SECOND, -1);
			return cal1.getTime();
		};
	}

	protected Calendar adjustStartByCategory(Calendar cal) {
		Calendar before = Calendar.getInstance();
		before.setTime(cal.getTime());
		if (!availableTransactions.isEmpty()) {
			for (int i = 0; i < availableTransactions.size(); i++) {
				Transaction transaction = availableTransactions.get(i);
				if (transaction.getValutaDate().after(cal.getTime())) {
					Calendar after = Calendar.getInstance();
					after.setTime(transaction.getValutaDate());
					if (cal.get(Calendar.MONTH) == after.get(Calendar.MONTH)) {
						int dayDiffAfter = cal.get(Calendar.DAY_OF_MONTH) - after.get(Calendar.DAY_OF_MONTH);
						int dayDiffBefore = before.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH);
						return (dayDiffAfter < dayDiffBefore) ? after : before;
					} else {
						return after;
					}
				} else {
					before.setTime(transaction.getValutaDate());
				}
			}
		}
		cal.setTime(before.getTime());
		return cal;
	}

	protected Calendar adjustEndByCategory(Calendar cal) {
		if (!availableTransactions.isEmpty()) {
			for (int i = 0; i < availableTransactions.size(); i++) {
				Transaction transaction = availableTransactions.get(i);
				if (transaction.getValutaDate().after(cal.getTime())) {
					// first after startDate
					cal.setTime(transaction.getValutaDate());
					return cal;
				}
			}
		}
		// open end, just add 1,5 month
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, 14);
		return cal;
	}

	private void initTransactions() {
		if (categoryModel.getObject() != null) {
			availableTransactions = bankDataService.findTransactionsByCategory(categoryModel.getObject());
		} else {
			availableTransactions = new ArrayList<Transaction>();
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		Form<Void> form = new Form<Void>("form");
		// Year
		StringResourceModel lYear = new StringResourceModel("year.label", CategoryReviewPanel.this, null);
		form.add(new Label("year.label", lYear));
		DropDownChoice<Integer> yearChoice = new DropDownChoice<Integer>("year", yearModel, calculateYearChoice());
		yearChoice.setNullValid(false);
		form.add(yearChoice);

		// Month
		StringResourceModel lMonth = new StringResourceModel("month.label", CategoryReviewPanel.this, null);
		form.add(new Label("month.label", lMonth));
		final DropDownChoice<Integer> monthChoice = new DropDownChoice<Integer>("month", monthModel,
				calculateMonthChoice());
		monthChoice.setChoiceRenderer(new IChoiceRenderer<Integer>() {

			@Override
			public Object getDisplayValue(Integer object) {
				return getString("month." + object + ".label");
			}

			@Override
			public String getIdValue(Integer object, int index) {
				return String.valueOf(index);
			}

			@Override
			public Integer getObject(String id, IModel<? extends List<? extends Integer>> choices) {
				return choices.getObject().get(Integer.valueOf(id));
			}
		});
		monthChoice.setOutputMarkupId(true);
		monthChoice.setNullValid(false);
		form.add(monthChoice);

		final Component reviewPanel = new MonthlyReviewResultPanel("review", startDateModel, endDateModel);
		form.add(reviewPanel);

		// from until Date
		form.add(new Label("startDate.label",
				new StringResourceModel("startDate.label", CategoryReviewPanel.this, null)));
		DateLabel startDateLabel = DateLabel.forDatePattern("startDate", startDateModel, "dd.MM.yyyy");
		startDateLabel.setOutputMarkupId(true);
		form.add(startDateLabel);
		form.add(new Label("endDate.label", new StringResourceModel("endDate.label", CategoryReviewPanel.this, null)));
		DateLabel endDateLabel = DateLabel.forDatePattern("endDate", endDateModel, "dd.MM.yyyy");
		endDateLabel.setOutputMarkupId(true);
		form.add(endDateLabel);

		// Choose category
		StringResourceModel lCategory = new StringResourceModel("category.label", CategoryReviewPanel.this, null);
		form.add(new Label("category.label", lCategory));
		DropDownChoice<Category> categoryChoice = new DropDownChoice<Category>("category", categoryModel,
				getAllCategory());
		categoryChoice.setChoiceRenderer(new IChoiceRenderer<Category>() {

			@Override
			public Object getDisplayValue(Category object) {
				return object.getName();
			}

			@Override
			public String getIdValue(Category object, int index) {
				return String.valueOf(index);
			}

			@Override
			public Category getObject(String id, IModel<? extends List<? extends Category>> choices) {
				return Strings.isNotBlank(id) ? choices.getObject().get(Integer.valueOf(id)) : null;
			}
		});
		categoryChoice.setNullValid(false);
		categoryChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				initTransactions();
				target.add(monthChoice, startDateLabel, endDateLabel);
			}
		});
		form.add(categoryChoice);

		// calculate new
		form.add(new AjaxButton("calculate") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				try {
					target.add(reviewPanel, startDateLabel, endDateLabel, monthChoice);

				} catch (Exception e) {
					log.error(e.getMessage(), e);
					form.error(new StringResourceModel("TODO", CategoryReviewPanel.this, null).getString());
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

	}

	private List<Integer> calculateMonthChoice() {
		return Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
	}

	private List<Integer> calculateYearChoice() {
		return bankDataService.getYearsOfAvailabledata();
	}
}
