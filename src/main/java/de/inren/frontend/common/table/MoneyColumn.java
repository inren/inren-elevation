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
package de.inren.frontend.common.table;

import java.math.BigDecimal;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import de.inren.frontend.common.panel.MoneyPanel;

/**
 * @author Ingo Renner
 *
 */
public class MoneyColumn<T> extends PropertyColumn<T, String> {
    private final String property;

    MoneyColumn(IModel<String> displayModel, String propertyExpression, String property) {
        super(displayModel, propertyExpression);
        this.property = property;
    }

    MoneyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression, String property) {
        super(displayModel, sortProperty, propertyExpression);
        this.property = property;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        PropertyModel<BigDecimal> model = new PropertyModel<BigDecimal>(rowModel, property);
        item.add(new MoneyPanel(componentId, model));
        item.add(AttributeModifier.append("class", "money"));
    }

}
