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
package de.inren.frontend.user;

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
import de.inren.data.domain.user.User;
import de.inren.data.repositories.user.UserRepository;
import de.inren.frontend.common.dataprovider.RepositoryDataProvider;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.frontend.common.panel.ManagePanel;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;

/**
 * @author Ingo Renner
 */
public class ManageUsersPanel extends ManagePanel implements IAdminPanel {

    @SpringBean
    private UserRepository userRepository;

    private final IWorktopManageDelegate<User> delegate;

    public ManageUsersPanel(String id, IWorktopManageDelegate<User> delegate) {
        super(id);
        this.delegate = delegate;
    }

    @Override
    protected final Component getTable(final String id) {
        AjaxFallbackDefaultDataTableBuilder<User> builder = new AjaxFallbackDefaultDataTableBuilder<User>(ManageUsersPanel.this);

        Component table = builder.addDataProvider(new RepositoryDataProvider<User>(userRepository))
                .add(new AbstractColumn<User, String>(new StringResourceModel("actions.label", ManageUsersPanel.this, null)) {

                    @Override
                    public void populateItem(Item<ICellPopulator<User>> cellItem, String componentId, IModel<User> rowModel) {

                        final User user = rowModel.getObject();

                        ButtonGroup bg = new ButtonGroup(componentId) {

                            @Override
                            protected List<AbstractLink> newButtons(String buttonMarkupId) {
                                List<AbstractLink> res = new ArrayList<AbstractLink>();

                                BootstrapAjaxLink<String> edit = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                    @Override
                                    public void onClick(AjaxRequestTarget target) {
                                        delegate.switchToComponent(target, delegate.getEditPanel(new Model<User>(user)));

                                    }
                                };
                                edit.setIconType(GlyphIconType.pencil);
                                edit.setSize(Buttons.Size.Mini);
                                res.add(edit);

                                // only delete other users
                                if (getUser() != null && !getUser().getUsername().equals(user.getEmail())) {
                                    BootstrapAjaxLink<String> delete = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

                                        @Override
                                        public void onClick(AjaxRequestTarget target) {

                                            try {
                                                // feedback
                                                getSession().getFeedbackMessages().clear();
                                                target.add(getFeedback());
                                                // delete
                                                userRepository.deleteById(user.getId());
                                                // manage
                                                Component table = getTable(id);
                                                ManageUsersPanel.this.addOrReplace(table);
                                                target.add(table);
                                            } catch (Exception e) {
                                                error(e.getMessage());
                                                target.add(getFeedback());
                                            }
                                        }
                                    };
                                    delete.setIconType(GlyphIconType.trash);
                                    delete.setSize(Buttons.Size.Mini);
                                    res.add(delete);
                                }
                                return res;
                            };
                        };
                        cellItem.add(bg);
                    }
                }).addPropertyColumn("id", true).addPropertyColumn("email", true).addPropertyColumn("firstname", true).addPropertyColumn("lastname", true)
                .addBooleanPropertyColumn("accountNonExpired", true).addBooleanPropertyColumn("accountNonLocked", true)
                .addBooleanPropertyColumn("credentialsNonExpired", true).addBooleanPropertyColumn("deleted", true).addPropertyColumn("activationKey", true)
                .addPropertyColumn("passRemainder", true).setNumberOfRows(10).build(id);
        TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
        table.add(tableBehavior);
        return table;
    }

    @Override
    protected Component getActionPanel(String id) {

        ButtonGroup bg = new ButtonGroup(id) {

            @Override
            protected List<AbstractLink> newButtons(String buttonMarkupId) {
                List<AbstractLink> res = new ArrayList<AbstractLink>();
                StringResourceModel srm = new StringResourceModel("actions.create.user", ManageUsersPanel.this, null);
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
