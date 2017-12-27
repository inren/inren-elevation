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
package de.inren.frontend.role;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.security.Right;
import de.inren.data.domain.security.Role;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.security.RightService;
import de.inren.service.security.RoleService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateRolePanel extends ABasePanel implements IAdminPanel {

    @SpringBean
    private RoleService roleService;

    @SpringBean
    private RightService rightService;

    private final IWorktopManageDelegate<Role> delegate;

    final Role role;

    public EditOrCreateRolePanel(String componentId, IModel<Role> m, IWorktopManageDelegate<Role> delegate) {
        super(componentId);
        if (m != null) {
            role = m.getObject();
        } else {
            role = new Role();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<Role> form = new Form<Role>("form", new CompoundPropertyModel<Role>(role));

        StringResourceModel lName = new StringResourceModel("name.label", EditOrCreateRolePanel.this, null);
        form.add(new Label("name.label", lName));
        form.add(new TextField<String>("name", String.class).setRequired(true).setLabel(lName).setRequired(false).setLabel(lName));

        StringResourceModel lDescription = new StringResourceModel("description.label", EditOrCreateRolePanel.this, null);
        form.add(new Label("description.label", lDescription));
        form.add(new TextField<String>("description", String.class).setRequired(false).setLabel(lDescription).setRequired(false).setLabel(lDescription));

        List<Right> allRights = new ArrayList<Right>();
        try {
            allRights = rightService.loadAllRights();
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        StringResourceModel lRights = new StringResourceModel("rights.label", EditOrCreateRolePanel.this, null);
        form.add(new Label("rights.label", lRights));

        form.add(new Palette<Right>("rights", new ListModel<Right>(allRights), new ChoiceRenderer<Right>("name", "id"), 5, false));

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
                    Role u = roleService.save((Role) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateRolePanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    form.error(new StringResourceModel("TODO", EditOrCreateRolePanel.this, null).getString());
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
}
