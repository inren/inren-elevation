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

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import de.inren.data.domain.banking.Transaction;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.banking.TransactionSummary;

/**
 * Show the Category and the sum of money. Also the parts of which this sum is
 * calculated.
 * 
 * @author Ingo Renner
 *
 */
public class TransactionSummaryListPanel extends ABasePanel<List<TransactionSummary>> {

    public TransactionSummaryListPanel(String id, IModel<List<TransactionSummary>> model) {
        super(id, model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(TransactionSummaryListPanel.class, "toggle.css")));
        response.render(OnDomReadyHeaderItem.forScript(getToggleScript()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new ListView<TransactionSummary>("list", getModel()) {

            @Override
            protected void populateItem(ListItem<TransactionSummary> item) {
                TransactionSummaryPanel summary = new TransactionSummaryPanel("item", item.getModel());
                item.add(summary);
                item.add(new TransactionListPanel("itemDetail", new PropertyModel<List<Transaction>>(item.getModel(), "transactions")));
            }
        }.setReuseItems(false));
    }

    private String getToggleScript() {

        String js = "";
        try {
            js = IOUtils.toString(this.getClass().getResourceAsStream("toggle.js"), "UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return js;
    }
}
