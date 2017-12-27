package de.inren.frontend.banking.overview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class CategoryOverviewPanel extends AFromUntilPanel<Void> {

	public CategoryOverviewPanel(String id) {
		super(id);
	}

	@Override
	int monthRange() {
		return 12;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();

	}

	@Override
	protected Component createResultPanel(String id, IModel<Date> startDateModel, IModel<Date> endDateModel) {
		return new Label(id, () -> {
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			return df.format(startDateModel.getObject()) + " - " + df.format(endDateModel.getObject());
		});
	}

}
