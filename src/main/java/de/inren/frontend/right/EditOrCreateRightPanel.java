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

package de.inren.frontend.right;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.security.Right;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.security.RightService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateRightPanel extends ABasePanel implements IAdminPanel {
    @SpringBean
    private RightService rightService;

    private final IWorktopManageDelegate<Right> delegate;

    final Right right;

    public EditOrCreateRightPanel(String componentId, IModel<Right> m, IWorktopManageDelegate<Right> delegate) {
        super(componentId);
        if (m != null) {
            right = m.getObject();
        } else {
            right = new Right();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<Right> form = new Form<Right>("form", new CompoundPropertyModel<Right>(right));

        StringResourceModel lName = new StringResourceModel("name.label", EditOrCreateRightPanel.this, null);
        form.add(new Label("name.label", lName));
        form.add(new TextField<String>("name", String.class).setRequired(true).setLabel(lName).setRequired(false).setLabel(lName));

        StringResourceModel lDescription = new StringResourceModel("description.label", EditOrCreateRightPanel.this, null);
        form.add(new Label("description.label", lDescription));
        form.add(new TextField<String>("description", String.class).setRequired(false).setLabel(lDescription).setRequired(false).setLabel(lDescription));

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
                    Right u = rightService.save((Right) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateRightPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    form.error(new StringResourceModel("TODO", EditOrCreateRightPanel.this, null).getString());
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
