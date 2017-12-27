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
package de.inren.frontend.componentaccess;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.security.ComponentAccess;
import de.inren.data.domain.security.Role;
import de.inren.data.domain.user.User;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.frontend.user.EditOrCreateUserPanel;
import de.inren.service.security.ComponentAccessService;
import de.inren.service.security.RoleService;

/**
 * @author Ingo Renner
 */
public class EditOrCreateComponentAccessPanel extends ABasePanel implements IAdminPanel {

    @SpringBean
    private ComponentAccessService componentAccessService;

    @SpringBean
    private RoleService roleService;

    private final IWorktopManageDelegate<ComponentAccess> delegate;

    final ComponentAccess componentAccess;

    public EditOrCreateComponentAccessPanel(String componentId, IModel<ComponentAccess> m, IWorktopManageDelegate<ComponentAccess> delegate) {
        super(componentId);
        if (m != null) {
            componentAccess = m.getObject();
        } else {
            componentAccess = new ComponentAccess();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<ComponentAccess> form = new Form<ComponentAccess>("form", new CompoundPropertyModel<ComponentAccess>(componentAccess));


        List<Role> allRoles = new ArrayList<Role>();
        try {
            allRoles = roleService.loadAllRoles();
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        StringResourceModel lRoles = new StringResourceModel("roles.label", EditOrCreateComponentAccessPanel.this, null);
        form.add(new Label("roles.label", lRoles));

        form.add(new Palette<Role>("roles", new ListModel<Role>(allRoles), new ChoiceRenderer<Role>("name", "id"), 5, false));

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
            	ComponentAccess componentAccess = componentAccessService.save((ComponentAccess) form.getModelObject());
                form.info(new StringResourceModel("feedback.success", EditOrCreateComponentAccessPanel.this, null).getString());
                delegate.switchToComponent(target, delegate.getManagePanel());

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
