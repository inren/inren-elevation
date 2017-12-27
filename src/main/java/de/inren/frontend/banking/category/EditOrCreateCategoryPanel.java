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
/**
 * Copygroup 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.inren.frontend.banking.category;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.data.domain.banking.Category;
import de.inren.frontend.banking.TransactionWorktopManageDelegate;
import de.inren.frontend.banking.common.PrincipalTablePanel;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateCategoryPanel extends ABasePanel<Category> implements IAdminPanel {

    private static Logger                          log = LoggerFactory.getLogger(EditOrCreateCategoryPanel.class);
    @SpringBean
    private BankDataService                        bankDataService;

    private final IWorktopManageDelegate<Category> categoryWorktopManageDelegate;

    private final Category                         category;
    private final TransactionWorktopManageDelegate transactionWorktopManageDelegate;

    public EditOrCreateCategoryPanel(String componentId, IModel<Category> m, IWorktopManageDelegate<Category> delegate) {
        super(componentId);
        if (m != null) {
            category = m.getObject();
        } else {
            category = new Category();
        }
        this.categoryWorktopManageDelegate = delegate;
        this.transactionWorktopManageDelegate = null;
    }

    public EditOrCreateCategoryPanel(String componentId, Model<Category> m, TransactionWorktopManageDelegate transactionWorktopManageDelegate) {
        super(componentId);
        if (m != null) {
            category = m.getObject();
        } else {
            category = new Category();
        }
        this.categoryWorktopManageDelegate = null;
        this.transactionWorktopManageDelegate = transactionWorktopManageDelegate;
    }

    @Override
    protected void initGui() {

        Form<Category> form = new Form<Category>("form", new CompoundPropertyModel<Category>(category));

        StringResourceModel lName = new StringResourceModel("name.label", EditOrCreateCategoryPanel.this, null);
        form.add(new Label("name.label", lName));
        form.add(new TextField<String>("name", String.class).setRequired(true).setLabel(lName).setRequired(false).setLabel(lName));

        StringResourceModel lDescription = new StringResourceModel("description.label", EditOrCreateCategoryPanel.this, null);
        form.add(new Label("description.label", lDescription));
        form.add(new TextField<String>("description", String.class).setRequired(true).setLabel(lDescription).setRequired(false).setLabel(lDescription));

        StringResourceModel lIncome = new StringResourceModel("income.label", EditOrCreateCategoryPanel.this, null);
        form.add(new Label("income.label", lIncome));
        form.add(new CheckBox("income").setLabel(lIncome));

        StringResourceModel lMarksMonth = new StringResourceModel("marksMonth.label", EditOrCreateCategoryPanel.this, null);
        form.add(new Label("marksMonth.label", lMarksMonth));
        form.add(new CheckBox("marksMonth").setLabel(lMarksMonth));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
                switchToManagePanel(target);
            }
        });

        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    Category u = bankDataService.save((Category) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateCategoryPanel.this, null).getString());
                    switchToManagePanel(target);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", EditOrCreateCategoryPanel.this, null).getString());
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

        add(new PrincipalTablePanel("pribcipalInfo"));
    }

    private void switchToManagePanel(AjaxRequestTarget target) {
        if (categoryWorktopManageDelegate != null) {
            categoryWorktopManageDelegate.switchToComponent(target, categoryWorktopManageDelegate.getManagePanel());
        } else {
            transactionWorktopManageDelegate.switchToComponent(target, transactionWorktopManageDelegate.getManagePanel());
        }
    }
}
