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

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * 
 * @author Ingo Renner
 *
 * @param <T>
 */
public final class BooleanPropertyColumn<T> extends PropertyColumn<T, String> {
    private final String property;

    BooleanPropertyColumn(IModel<String> displayModel, String propertyExpression, String property) {
        super(displayModel, propertyExpression);
        this.property = property;
    }

    BooleanPropertyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression, String property) {
        super(displayModel, sortProperty, propertyExpression);
        this.property = property;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        PropertyModel<Boolean> model = new PropertyModel<Boolean>(rowModel, property);
        Boolean bool = model.getObject();
        if (bool == null) {
            bool = Boolean.FALSE;
        }
        item.add(bool ? new Label(componentId, "<span>+</span>").setEscapeModelStrings(false) : new Label(componentId, "<span>-</span>")
                .setEscapeModelStrings(false));
    }

}