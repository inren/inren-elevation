package de.inren.frontend.banking.overview;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.inren.frontend.common.panel.ABasePanel;

/**
 * 
 * @author ingo
 *
 * @param <T>
 * 
 *            Panel where the user can select the year an the current month and
 *            a calculate button.
 * 
 */
public abstract class AFromUntilPanel<T> extends ABasePanel<T> {

	private IModel<Integer> yearModel;
	private IModel<Integer> monthModel;

	private IModel<Date> startDateModel;
	private IModel<Date> endDateModel;

	public AFromUntilPanel(String id) {
		super(id);
		initDateModels();
	}

	private void initDateModels() {

		final Calendar inputCalendar = Calendar.getInstance();
		yearModel = Model.of(Integer.valueOf(inputCalendar.get(Calendar.YEAR)));
		monthModel = Model.of(Integer.valueOf(inputCalendar.get(Calendar.MONTH)));

		startDateModel = () -> {
			final Calendar startCalendar = Calendar.getInstance();
			startCalendar.set(Calendar.YEAR, yearModel.getObject());
			startCalendar.set(Calendar.MONTH, monthModel.getObject());
			startCalendar.set(Calendar.DAY_OF_MONTH, 1);
			startCalendar.set(Calendar.HOUR, 0);
			startCalendar.set(Calendar.MINUTE, 0);
			startCalendar.set(Calendar.SECOND, 0);
			startCalendar.set(Calendar.MILLISECOND, 0);
			return startCalendar.getTime();
		};

		endDateModel = () -> {
			return calculateEndDate().getTime();
		};

	}

	protected Calendar calculateEndDate() {
		final Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(startDateModel.getObject());
		endCalendar.add(Calendar.MONTH, monthRange());
		endCalendar.set(Calendar.HOUR, 0);
		endCalendar.set(Calendar.MINUTE, 0);
		endCalendar.set(Calendar.SECOND, 0);
		endCalendar.set(Calendar.MILLISECOND, 0);
		endCalendar.add(Calendar.SECOND, -1);
		return endCalendar;
	}

	/**
	 * 
	 * @return How many month are between start and end date.
	 */
	abstract int monthRange();

	@Override
	protected void onInitialize() {
		super.onInitialize();

		Form<Void> form = new Form<>("form");
		// Year
		StringResourceModel lYear = new StringResourceModel("year.label", AFromUntilPanel.this, null);
		form.add(new Label("year.label", lYear));
		DropDownChoice<Integer> yearChoice = new DropDownChoice<Integer>("year", yearModel, calculateYearChoice());
		yearChoice.setNullValid(false);
		form.add(yearChoice);

		// Month
		StringResourceModel lMonth = new StringResourceModel("month.label", AFromUntilPanel.this, null);
		form.add(new Label("month.label", lMonth));
		DropDownChoice<Integer> monthChoice = new DropDownChoice<Integer>("month", monthModel, calculateMonthChoice());
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
		monthChoice.setNullValid(false);
		form.add(monthChoice);

		final Component reviewPanel = createResultPanel("review", startDateModel, endDateModel);
		reviewPanel.setOutputMarkupId(true);
		form.add(reviewPanel);

		// calculate new
		form.add(new AjaxButton("calculate") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				try {
					target.add(reviewPanel);

				} catch (Exception e) {
					log.error(e.getMessage(), e);
					form.error(new StringResourceModel("TODO", AFromUntilPanel.this, null).getString());
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

	/**
	 * This component will get a refresh, when models are changed.
	 * 
	 * @param id
	 *            wicket id
	 * @param startDateModel
	 *            model with the current selected start date
	 * @param endDateModel
	 *            model with the current selected end date
	 * @return Component
	 */
	protected abstract Component createResultPanel(String id, IModel<Date> startDateModel, IModel<Date> endDateModel);

	private List<Integer> calculateMonthChoice() {
		return Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
	}

	private List<Integer> calculateYearChoice() {
		return Arrays.asList(new Integer[] { 2014, 2015, 2016, 2017 });
	}
}
