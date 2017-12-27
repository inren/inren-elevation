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
package de.inren.frontend.banking.common;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import de.inren.data.domain.banking.Transaction;
import de.inren.frontend.common.panel.ABasePanel;

/**
 * @author Ingo Renner
 *
 */
public class TransactionListPanel extends ABasePanel<List<Transaction>> {

    public TransactionListPanel(String id, IModel<List<Transaction>> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new ListView<Transaction>("list", getModel()) {

            @Override
            protected void populateItem(ListItem<Transaction> item) {
                item.add(new TransactionPanel("item", item.getModel()));
            }
        }.setReuseItems(false));

    }
}
