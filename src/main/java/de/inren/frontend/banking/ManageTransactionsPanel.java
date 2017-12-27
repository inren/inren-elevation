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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonGroup;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.banking.TransactionSpecification;
import de.inren.data.repositories.banking.TransactionRepository;
import de.inren.frontend.common.dataprovider.TransactionDataProvider;
import de.inren.frontend.common.panel.ActionPanelBuilder;
import de.inren.frontend.common.panel.EditActionLink;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.frontend.common.panel.ManagePanel;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;

/**
 * @author Ingo Renner
 * 
 */
public class ManageTransactionsPanel extends ManagePanel implements IAdminPanel {

    @SpringBean
    private TransactionRepository                  ransactionRepository;

    private final TransactionWorktopManageDelegate delegate;

    private final IModel<Transaction> filterModel = Model.of(new Transaction());

    private Component table;

    public ManageTransactionsPanel(String id, TransactionWorktopManageDelegate delegate) {
        super(id);
        this.delegate = delegate;
    }

    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        
        add(new TransactionFilterPanel("filter", filterModel){
            @Override
            protected void filterChanged(AjaxRequestTarget target, IModel<Transaction> model) {
                if(target!=null && table!=null) {
                    target.add(table);
                }
            }
        });
        
    }
    
    @Override
    protected final Component getTable(final String id) {
        if(table == null) {
        AjaxFallbackDefaultDataTableBuilder<Transaction> builder = new AjaxFallbackDefaultDataTableBuilder<Transaction>(ManageTransactionsPanel.this);

        table = builder.addDataProvider(getDataProvider())
                .add(new AbstractColumn<Transaction, String>(new StringResourceModel("actions.label", ManageTransactionsPanel.this, null)) {
                    @Override
                    public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId, IModel<Transaction> rowModel) {

                        final ActionPanelBuilder linkBuilder = ActionPanelBuilder.getBuilder();
                        final Transaction transaction = rowModel.getObject();
                        // edit link
                        linkBuilder.add(new EditActionLink(true) {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                delegate.switchToComponent(target, delegate.getEditPanel(Model.of(transaction)));
                            }
                        });

                        // TODO only delete rights not in use
                        ButtonGroup bg = new ButtonGroup(componentId) {

                            @Override
                            protected List<AbstractLink> newButtons(String buttonMarkupId) {
                                List<AbstractLink> res = new ArrayList<AbstractLink>();

                                // Edit Button
                                BootstrapAjaxLink<String> edit = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {
                                        delegate.switchToComponent(target, delegate.getEditPanel(Model.of(transaction)));

                                    }
                                };
                                edit.setIconType(GlyphIconType.pencil);
                                edit.setSize(Buttons.Size.Mini);
                                res.add(edit);
                                // Create Filter Button
                                BootstrapAjaxLink<String> createFilter = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {
                                        delegate.switchToComponent(target, delegate.getCreateFilterPanel(Model.of(transaction)));

                                    }
                                };
                                createFilter.setIconType(GlyphIconType.barcode);
                                createFilter.setSize(Buttons.Size.Mini);
                                res.add(createFilter);

                                return res;
                            }
                        };

                        // bg.add(new ToolbarBehavior());
                        cellItem.add(bg);
                    }
                }).addPropertyColumn("id", true).addPropertyColumn("hashCode", true).addPropertyColumn("category", true)
                .addPropertyColumn("accountNumber", true).addPropertyColumn("accountingDate", true).addPropertyColumn("no", true).addPropertyColumn("valutaDate", true)
                .addPropertyColumn("principal", true).addPropertyColumn("accountingText", true).addPropertyColumn("purpose", true)
                .addMoneyPropertyColumn("amount", true).addPropertyColumn("transactionCurrency", true).addPropertyColumn("balance", true)
                .addPropertyColumn("balanceCurrency", true).setNumberOfRows(10).build(id);
        TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
        table.add(tableBehavior);
        table.setOutputMarkupId(true);
        }
        return table;
    }

    private TransactionDataProvider getDataProvider() {
        TransactionSpecification transactionSpecification = new TransactionSpecification(filterModel.getObject());
        return new TransactionDataProvider(ransactionRepository, transactionSpecification);
    }

    @Override
    protected Component getActionPanel(String id) {
        // create link
        ButtonGroup bg = new ButtonGroup(id) {

            @Override
            protected List<AbstractLink> newButtons(String buttonMarkupId) {
                List<AbstractLink> res = new ArrayList<AbstractLink>();
                StringResourceModel srm = new StringResourceModel("actions.create.right", ManageTransactionsPanel.this, null);
                BootstrapAjaxLink<String> create = new BootstrapAjaxLink<String>("button", srm, Buttons.Type.Primary) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        delegate.switchToComponent(target, delegate.getEditPanel(null));
                    }
                };
                create.setIconType(GlyphIconType.plussign);
                res.add(create);
                return res;
            }
        };
        return bg;
    }
}
