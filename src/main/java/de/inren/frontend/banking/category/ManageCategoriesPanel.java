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
import de.inren.data.domain.banking.Category;
import de.inren.data.repositories.banking.CategoryRepository;
import de.inren.frontend.common.dataprovider.RepositoryDataProvider;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ActionPanelBuilder;
import de.inren.frontend.common.panel.EditActionLink;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.frontend.common.panel.ManagePanel;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 * 
 */
public class ManageCategoriesPanel extends ManagePanel implements IAdminPanel {

    @SpringBean
    private BankDataService                        bankDataService;

    @SpringBean
    private CategoryRepository                     categoryRepository;

    private final IWorktopManageDelegate<Category> delegate;

    public ManageCategoriesPanel(String id, IWorktopManageDelegate<Category> delegate) {
        super(id);
        this.delegate = delegate;
    }

    @Override
    protected final Component getTable(final String id) {
        AjaxFallbackDefaultDataTableBuilder<Category> builder = new AjaxFallbackDefaultDataTableBuilder<Category>(ManageCategoriesPanel.this);

        Component table = builder.addDataProvider(new RepositoryDataProvider<Category>(categoryRepository))
                .add(new AbstractColumn<Category, String>(new StringResourceModel("actions.label", ManageCategoriesPanel.this, null)) {
                    @Override
                    public void populateItem(Item<ICellPopulator<Category>> cellItem, String componentId, IModel<Category> rowModel) {

                        final ActionPanelBuilder linkBuilder = ActionPanelBuilder.getBuilder();
                        final Category category = rowModel.getObject();
                        // edit link
                        linkBuilder.add(new EditActionLink(true) {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                delegate.switchToComponent(target, delegate.getEditPanel(new Model<Category>(category)));
                            }
                        });

                        ButtonGroup bg = new ButtonGroup(componentId) {

                            @Override
                            protected List<AbstractLink> newButtons(String buttonMarkupId) {
                                List<AbstractLink> res = new ArrayList<AbstractLink>();
                                BootstrapAjaxLink<String> edit = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {
                                        delegate.switchToComponent(target, delegate.getEditPanel(new Model<Category>(category)));

                                    }
                                };
                                edit.setIconType(GlyphIconType.pencil);
                                edit.setSize(Buttons.Size.Mini);
                                res.add(edit);
                                // Delete Category
                                BootstrapAjaxLink<String> delete = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {
                                        bankDataService.deleteCategory(category);
                                        target.add(ManageCategoriesPanel.this);
                                    }
                                };
                                delete.setIconType(GlyphIconType.trash);
                                delete.setSize(Buttons.Size.Mini);
                                res.add(delete);
                                // Filter anwenden
                                BootstrapAjaxLink<String> apply = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {
                                        bankDataService.applyCategoryToTransactions(category);
                                        target.add(ManageCategoriesPanel.this);
                                    }
                                };
                                apply.setIconType(GlyphIconType.plus);
                                apply.setSize(Buttons.Size.Mini);
                                res.add(apply);
                                // Filter entfernen
                                BootstrapAjaxLink<String> remove = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {
                                        bankDataService.removeCategoryFromTransactions(category);
                                        target.add(ManageCategoriesPanel.this);
                                    }
                                };
                                remove.setIconType(GlyphIconType.minus);
                                remove.setSize(Buttons.Size.Mini);
                                res.add(remove);

                                return res;
                            }
                        };

                        cellItem.add(bg);
                    }
                }).addPropertyColumn("id", true).addBooleanPropertyColumn("income", true).addBooleanPropertyColumn("marksMonth", true)
                .addPropertyColumn("name", true).setNumberOfRows(10).build(id);
        TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
        table.add(tableBehavior);
        return table;
    }

    @Override
    protected Component getActionPanel(String id) {
        // create link
        ButtonGroup bg = new ButtonGroup(id) {

            @Override
            protected List<AbstractLink> newButtons(String buttonMarkupId) {
                List<AbstractLink> res = new ArrayList<AbstractLink>();
                StringResourceModel srm = new StringResourceModel("actions.create.category", ManageCategoriesPanel.this, null);
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
