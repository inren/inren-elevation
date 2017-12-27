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

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * Simple panel to display a short list of strings in an ul li list.
 * If your list is longer as maybe 10 elements or your elements are more complex than one short String,
 * please consider to make your own ListPanel implementation.
 * 
 * @author Ingo Renner
 */
public class ListPanel<T> extends Panel {

    public ListPanel(String id, PropertyModel<List<T>> listModel, final String itemExpression) {
        super(id, listModel);
        add(new ListPanelView("itemlist", listModel.getObject(), itemExpression));
    }

    private final class ListPanelView extends ListView<T> {
        private final String itemExpression;

        private ListPanelView(String id, List<T> list, String itemExpression) {
            super(id, list);
            this.itemExpression = itemExpression;
        }

        @Override
        protected void populateItem(ListItem<T> item) {
            item.add(new Label("item", new PropertyModel<String>(item.getModelObject(), itemExpression)));
        }
    }
}
