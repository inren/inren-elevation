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

package de.inren.frontend.banking.filter;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.inren.data.domain.banking.Category;
import de.inren.data.domain.banking.CategoryFilter;
import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.banking.TransactionCategoryFilterSpecification;
import de.inren.data.repositories.banking.TransactionRepository;
import de.inren.frontend.banking.TransactionWorktopManageDelegate;
import de.inren.frontend.common.dataprovider.ARepositoryDataProvider;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateCategoryFilterPanel extends ABasePanel<CategoryFilter> implements IAdminPanel {

    private static Logger                                log = LoggerFactory.getLogger(EditOrCreateCategoryFilterPanel.class);
    @SpringBean
    private BankDataService                              bankDataService;

    @SpringBean
    private TransactionRepository                        transactionRepository;

    private final IWorktopManageDelegate<CategoryFilter> categoryFilterWorktopManageDelegate;

    private final CategoryFilter                         categoryFilter;
    private final TransactionWorktopManageDelegate       transactionWorktopManageDelegate;

    public EditOrCreateCategoryFilterPanel(String componentId, IModel<CategoryFilter> m, IWorktopManageDelegate<CategoryFilter> delegate) {
        super(componentId);
        if (m != null) {
            categoryFilter = m.getObject();
        } else {
            categoryFilter = new CategoryFilter();
        }
        this.categoryFilterWorktopManageDelegate = delegate;
        this.transactionWorktopManageDelegate = null;
    }

    public EditOrCreateCategoryFilterPanel(String componentId, Model<CategoryFilter> m, TransactionWorktopManageDelegate transactionWorktopManageDelegate) {
        super(componentId);
        if (m != null) {
            categoryFilter = m.getObject();
        } else {
            categoryFilter = new CategoryFilter();
        }
        this.categoryFilterWorktopManageDelegate = null;
        this.transactionWorktopManageDelegate = transactionWorktopManageDelegate;
    }

    @Override
    protected void initGui() {

        final Component table = getTable("table");

        Form<CategoryFilter> form = new Form<CategoryFilter>("form", new CompoundPropertyModel<CategoryFilter>(categoryFilter));

        StringResourceModel lCategory = new StringResourceModel("category.label", EditOrCreateCategoryFilterPanel.this, null);
        form.add(new Label("category.label", lCategory));
        form.add(new DropDownChoice<Category>("category", getCategories(), new IChoiceRenderer<Category>() {

            @Override
            public Object getDisplayValue(Category object) {
                return object.getName();
            }

            @Override
            public String getIdValue(Category object, int index) {
                return String.valueOf(index);
            }

            @Override
            public Category getObject(String id, IModel<? extends List<? extends Category>> choices) {
                return Strings.isEmpty(id) ? null : choices.getObject().get(Integer.valueOf(id));
            }
        }));

        StringResourceModel lAccountingTextFilter = new StringResourceModel("accountingTextFilter.label", EditOrCreateCategoryFilterPanel.this, null);
        form.add(new Label("accountingTextFilter.label", lAccountingTextFilter));
        form.add(new TextField<String>("accountingTextFilter", String.class).setRequired(false).setLabel(lAccountingTextFilter).setRequired(false)
                .setLabel(lAccountingTextFilter));

        StringResourceModel lPrincipalFilter = new StringResourceModel("principalFilter.label", EditOrCreateCategoryFilterPanel.this, null);
        form.add(new Label("principalFilter.label", lPrincipalFilter));
        form.add(new TextField<String>("principalFilter", String.class).setRequired(false).setLabel(lPrincipalFilter).setRequired(false)
                .setLabel(lPrincipalFilter));

        StringResourceModel lPurposeFilter = new StringResourceModel("purposeFilter.label", EditOrCreateCategoryFilterPanel.this, null);
        form.add(new Label("purposeFilter.label", lPurposeFilter));
        form.add(new TextField<String>("purposeFilter", String.class).setRequired(false).setLabel(lPurposeFilter).setRequired(false).setLabel(lPurposeFilter));

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
                    CategoryFilter u = bankDataService.save((CategoryFilter) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateCategoryFilterPanel.this, null).getString());
                    switchToManagePanel(target);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", EditOrCreateCategoryFilterPanel.this, null).getString());
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

        form.add(new AjaxButton("testFilter") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    target.add(table);

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", EditOrCreateCategoryFilterPanel.this, null).getString());
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

        add(table);

    }

    private List<Category> getCategories() {
        return bankDataService.findAllCategories();
    }

    private Component getTable(final String id) {
        AjaxFallbackDefaultDataTableBuilder<Transaction> builder = new AjaxFallbackDefaultDataTableBuilder<Transaction>(EditOrCreateCategoryFilterPanel.this);

        Component table = builder.addDataProvider(getDataProvider()).addPropertyColumn("id", true).addPropertyColumn("hashCode", true)
                .addPropertyColumn("category", true).addPropertyColumn("accountNumber", true).addPropertyColumn("accountingDate", true)
                .addPropertyColumn("valutaDate", true).addPropertyColumn("principal", true).addPropertyColumn("accountingText", true)
                .addPropertyColumn("purpose", true).addPropertyColumn("amount", true).addPropertyColumn("transactionCurrency", true)
                .addPropertyColumn("balance", true).addPropertyColumn("balanceCurrency", true).setNumberOfRows(10).build(id);
        TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
        table.add(tableBehavior);
        return table;
    }

    private SortableDataProvider<Transaction, String> getDataProvider() {
        return new ARepositoryDataProvider<Transaction>() {

            @Override
            public long size() {
                return transactionRepository.count(createSpecification());
            }

            @Override
            protected Page<Transaction> getPage(Pageable pageable) {
                return transactionRepository.findAll(createSpecification(), pageable);
            }
        };
    }

    protected Specification<Transaction> createSpecification() {
        return new TransactionCategoryFilterSpecification(categoryFilter);
    }

    private void switchToManagePanel(AjaxRequestTarget target) {
        if (categoryFilterWorktopManageDelegate != null) {
            categoryFilterWorktopManageDelegate.switchToComponent(target, categoryFilterWorktopManageDelegate.getManagePanel());
        } else {
            transactionWorktopManageDelegate.switchToComponent(target, transactionWorktopManageDelegate.getManagePanel());
        }
    }
}
