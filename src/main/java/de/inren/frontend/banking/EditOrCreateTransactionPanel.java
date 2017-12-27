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
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.banking.Transaction;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateTransactionPanel extends ABasePanel<Transaction> implements IAdminPanel {
    @SpringBean
    private BankDataService                           bankDataService;

    private final IWorktopManageDelegate<Transaction> delegate;

    final Transaction                                 transaction;

    public EditOrCreateTransactionPanel(String componentId, IModel<Transaction> m, IWorktopManageDelegate<Transaction> delegate) {
        super(componentId);
        if (m != null) {
            transaction = m.getObject();
        } else {
            transaction = new Transaction();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<Transaction> form = new Form<Transaction>("form", new CompoundPropertyModel<Transaction>(transaction));

        StringResourceModel lCategory = new StringResourceModel("category.label", EditOrCreateTransactionPanel.this, null);
        form.add(new Label("category.label", lCategory));
        form.add(new DropDownChoice<String>("category", getCategories()));

        
        
        
        List<String> allTags = new ArrayList<String>();
        try {
            allTags = bankDataService.loadAllTagNames();
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        StringResourceModel lTagList = new StringResourceModel("tagList.label", EditOrCreateTransactionPanel.this, null);
        form.add(new Label("tagList.label", lTagList));

        form.add(new Palette<String>("tagList", new ListModel<String>(allTags), new IChoiceRenderer<String>() {

            @Override
            public Object getDisplayValue(String object) {
                return object;
            }

            @Override
            public String getIdValue(String object, int index) {
                return String.valueOf(index);
            }

            @Override
            public String getObject(String id, IModel<? extends List<? extends String>> choices) {
                return choices.getObject().get(Integer.valueOf(id));
            }
        },5, false));
        
        StringResourceModel lCategoryFixed = new StringResourceModel("categoryFixed.label", EditOrCreateTransactionPanel.this, null);
        form.add(new Label("categoryFixed.label", lCategoryFixed));
        form.add(new CheckBox("categoryFixed").setLabel(lCategoryFixed));

        StringResourceModel lPrincipal = new StringResourceModel("principal.label", EditOrCreateTransactionPanel.this, null);
        form.add(new Label("principal.label", lPrincipal));
        form.add(new TextField<String>("principal", String.class).setRequired(false).setLabel(lPrincipal).setEnabled(false));

        StringResourceModel lAccountingtext = new StringResourceModel("accountingText.label", EditOrCreateTransactionPanel.this, null);
        form.add(new Label("accountingText.label", lAccountingtext));
        form.add(new TextField<String>("accountingText", String.class).setRequired(false).setLabel(lAccountingtext).setEnabled(false));

        StringResourceModel lPurpose = new StringResourceModel("purpose.label", EditOrCreateTransactionPanel.this, null);
        form.add(new Label("purpose.label", lPurpose));
        form.add(new TextField<String>("purpose", String.class).setRequired(true).setLabel(lPurpose).setLabel(lPurpose).setEnabled(false));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
                delegate.switchToComponent(target, delegate.getManagePanel());
            }
        });

        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    Transaction u = bankDataService.save((Transaction) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateTransactionPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    form.error(new StringResourceModel("TODO", EditOrCreateTransactionPanel.this, null).getString());
                    target.add(getFeedback());
                }
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

    private List<String> getCategories() {
        return bankDataService.getCategoryNames();
    }
}
