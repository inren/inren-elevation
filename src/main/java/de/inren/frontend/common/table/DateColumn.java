package de.inren.frontend.common.table;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.datetime.markup.html.basic.DateLabel;

public class DateColumn<T> extends PropertyColumn<T, String> {
	private final String property;
	private String format;

	DateColumn(IModel<String> displayModel, String format, String sortProperty, String propertyExpression,
			String property) {
		super(displayModel, sortProperty, propertyExpression);
		if (StringUtils.isEmpty(format)) {
			format = "dd.MM.yyyy";
		}
		this.format = format;
		this.property = property;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		PropertyModel<Date> model = new PropertyModel<>(rowModel, property);
		DateLabel dateLabel = DateLabel.forDatePattern(componentId, model, format);
		item.add(dateLabel);
	}

}
