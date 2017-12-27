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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.MoneyPanel;
import de.inren.service.banking.TransactionSummary;

/**
 * Show the category and the sum of money.
 * 
 * @author Ingo Renner
 *
 */
public class TransactionSummaryPanel extends ABasePanel<TransactionSummary> {

    public TransactionSummaryPanel(String id, IModel<TransactionSummary> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        add(new Label("category", new PropertyModel<>(getModel(), "category")));
        add(new MoneyPanel("sum", new PropertyModel<>(getModel(), "sum")));
    }
}
