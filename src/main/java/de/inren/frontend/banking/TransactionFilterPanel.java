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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.security.Right;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.frontend.role.EditOrCreateRolePanel;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 * 
 */
public class TransactionFilterPanel extends ABasePanel<Transaction> implements IAdminPanel {

    @SpringBean
    private BankDataService                           bankDataService;

    final IModel<Transaction>                                 transactionModel;

    public TransactionFilterPanel(String componentId, IModel<Transaction> m) {
        super(componentId);
        if (m != null) {
            transactionModel = m;
        } else {
            transactionModel = Model.of(new Transaction());
        }
    }

    @Override
    protected void initGui() {

        Form<Transaction> form = new Form<Transaction>("form", new CompoundPropertyModel<Transaction>(transactionModel));

        StringResourceModel lCategory = new StringResourceModel("category.label", TransactionFilterPanel.this, null);
        form.add(new Label("category.label", lCategory));
        form.add(new DropDownChoice<String>("category", getCategories()));
        
        StringResourceModel lCategoryFixed = new StringResourceModel("categoryFixed.label", TransactionFilterPanel.this, null);
        form.add(new Label("categoryFixed.label", lCategoryFixed));
        form.add(new CheckBox("categoryFixed").setLabel(lCategoryFixed));

        StringResourceModel lPrincipal = new StringResourceModel("principal.label", TransactionFilterPanel.this, null);
        form.add(new Label("principal.label", lPrincipal));
        form.add(new TextField<String>("principal", String.class).setRequired(false).setLabel(lPrincipal).setEnabled(false));

        StringResourceModel lAccountingtext = new StringResourceModel("accountingText.label", TransactionFilterPanel.this, null);
        form.add(new Label("accountingText.label", lAccountingtext));
        form.add(new TextField<String>("accountingText", String.class).setRequired(false).setLabel(lAccountingtext).setEnabled(false));

        StringResourceModel lPurpose = new StringResourceModel("purpose.label", TransactionFilterPanel.this, null);
        form.add(new Label("purpose.label", lPurpose));
        form.add(new TextField<String>("purpose", String.class).setRequired(true).setLabel(lPurpose).setLabel(lPurpose).setEnabled(false));


        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                filterChanged(target, transactionModel);
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

    protected void filterChanged(AjaxRequestTarget target, IModel<Transaction> transactionModel2) {
        
    }

    private List<String> getCategories() {
        return bankDataService.getCategoryNames();
    }
    
}
