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
package de.inren.frontend.banking;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.inren.data.domain.banking.CategoryFilter;
import de.inren.data.domain.banking.Transaction;
import de.inren.frontend.banking.filter.EditOrCreateCategoryFilterPanel;
import de.inren.frontend.common.manage.AWorktopManageDelegate;
import de.inren.frontend.common.panel.WorktopPanel;

/**
 * @author Ingo Renner
 * 
 */
public class TransactionWorktopManageDelegate extends AWorktopManageDelegate<Transaction> {

    public TransactionWorktopManageDelegate(WorktopPanel panel) {
        super(panel);
    }

    @Override
    public Panel getManagePanel() {
        final ManageTransactionsPanel p = new ManageTransactionsPanel(getPanel().getComponentId(), this);
        p.setOutputMarkupId(true);
        return p;
    }

    @Override
    public Panel getEditPanel(IModel<Transaction> m) {
        final EditOrCreateTransactionPanel p = new EditOrCreateTransactionPanel(getPanel().getComponentId(), m, this);
        p.setOutputMarkupId(true);
        return p;
    }

    public Panel getCreateFilterPanel(IModel<Transaction> m) {
        CategoryFilter c = new CategoryFilter();
        c.setPrincipalFilter(m.getObject().getPrincipal());
        c.setPurposeFilter(m.getObject().getPurpose());
        c.setAccountingTextFilter(m.getObject().getAccountingText());
        final EditOrCreateCategoryFilterPanel p = new EditOrCreateCategoryFilterPanel(getPanel().getComponentId(), Model.of(c), this);
        p.setOutputMarkupId(true);
        return p;
    }

}
