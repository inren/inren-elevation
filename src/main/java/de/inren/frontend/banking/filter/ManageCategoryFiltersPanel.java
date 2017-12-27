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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
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
import de.inren.data.domain.banking.CategoryFilter;
import de.inren.data.repositories.banking.CategoryFilterRepository;
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
public class ManageCategoryFiltersPanel extends ManagePanel implements IAdminPanel {

	@SpringBean
	private BankDataService bankDataService;

	@SpringBean
	private CategoryFilterRepository categoryRepository;

	private final IWorktopManageDelegate<CategoryFilter> delegate;

	public ManageCategoryFiltersPanel(String id, IWorktopManageDelegate<CategoryFilter> delegate) {
		super(id);
		this.delegate = delegate;
	}

	@Override
	protected final Component getTable(final String id) {
		AjaxFallbackDefaultDataTableBuilder<CategoryFilter> builder = new AjaxFallbackDefaultDataTableBuilder<CategoryFilter>(
				ManageCategoryFiltersPanel.this);

		Component table = builder.addDataProvider(new RepositoryDataProvider<CategoryFilter>(categoryRepository))
				.add(new AbstractColumn<CategoryFilter, String>(
						new StringResourceModel("actions.label", ManageCategoryFiltersPanel.this, null)) {
					@Override
					public void populateItem(Item<ICellPopulator<CategoryFilter>> cellItem, String componentId,
							IModel<CategoryFilter> rowModel) {

						final ActionPanelBuilder linkBuilder = ActionPanelBuilder.getBuilder();
						final CategoryFilter category = rowModel.getObject();
						// edit link
						linkBuilder.add(new EditActionLink(true) {
							@Override
							public void onClick(AjaxRequestTarget target) {
								delegate.switchToComponent(target,
										delegate.getEditPanel(new Model<CategoryFilter>(category)));
							}
						});

						ButtonGroup bg = new ButtonGroup(componentId) {

							@Override
							protected List<AbstractLink> newButtons(String buttonMarkupId) {
								List<AbstractLink> links = new ArrayList<AbstractLink>();
								BootstrapAjaxLink<String> edit = new BootstrapAjaxLink<String>("button",
										Buttons.Type.Menu) {

									@Override
									public void onClick(AjaxRequestTarget target) {
										delegate.switchToComponent(target,
												delegate.getEditPanel(new Model<CategoryFilter>(category)));

									}
								};
								edit.add(AttributeModifier.append("title",
										new StringResourceModel("edit.filter", this)));
								edit.setIconType(GlyphIconType.pencil);
								edit.setSize(Buttons.Size.Mini);
								links.add(edit);
								// Filter anwenden
								BootstrapAjaxLink<String> apply = new BootstrapAjaxLink<String>("button",
										Buttons.Type.Menu) {

									@Override
									public void onClick(AjaxRequestTarget target) {
										bankDataService.applyCategoryToTransactions(category);
										target.add(ManageCategoryFiltersPanel.this);
									}
								};
								apply.add(AttributeModifier.append("title",
										new StringResourceModel("apply.filter", this)));
								apply.setIconType(GlyphIconType.plus);
								apply.setSize(Buttons.Size.Mini);
								links.add(apply);
								// Filter entfernen
								BootstrapAjaxLink<String> remove = new BootstrapAjaxLink<String>("button",
										Buttons.Type.Menu) {

									@Override
									public void onClick(AjaxRequestTarget target) {
										bankDataService.removeCategoryFromTransactions(category);
										target.add(ManageCategoryFiltersPanel.this);
									}
								};
								remove.add(AttributeModifier.append("title",
										new StringResourceModel("remove.filter", this)));
								remove.setIconType(GlyphIconType.minus);
								remove.setSize(Buttons.Size.Mini);
								links.add(remove);

								return links;
							}
						};

						cellItem.add(bg);
					}
				}).addPropertyColumn("id", true).addPropertyColumn("category.name", true)
				.addPropertyColumn("accountingTextFilter", true).addPropertyColumn("principalFilter", true)
				.addPropertyColumn("purposeFilter", true).addPropertyColumn("matches", true)

				.setNumberOfRows(10).build(id);
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
				StringResourceModel srm = new StringResourceModel("actions.create.category",
						ManageCategoryFiltersPanel.this, null);
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
