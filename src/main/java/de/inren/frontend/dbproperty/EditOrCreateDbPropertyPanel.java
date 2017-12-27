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

package de.inren.frontend.dbproperty;

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

import de.inren.data.domain.dbproperty.DbProperty;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.dbproperty.DbPropertyService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateDbPropertyPanel extends ABasePanel implements IAdminPanel {
    @SpringBean
    private DbPropertyService dbPropertyService;

    private final IWorktopManageDelegate<DbProperty> delegate;

    final DbProperty dbProperty;

    public EditOrCreateDbPropertyPanel(String componentId, IModel<DbProperty> m, IWorktopManageDelegate<DbProperty> delegate) {
        super(componentId);
        if (m != null) {
            dbProperty = m.getObject();
        } else {
            dbProperty = new DbProperty();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<DbProperty> form = new Form<DbProperty>("form", new CompoundPropertyModel<DbProperty>(dbProperty));

        StringResourceModel lName = new StringResourceModel("name.label", EditOrCreateDbPropertyPanel.this, null);
        form.add(new Label("name.label", lName));
        form.add(new TextField<String>("name", String.class).setRequired(true).setLabel(lName).setRequired(false).setLabel(lName));

        StringResourceModel lHost = new StringResourceModel("host.label", EditOrCreateDbPropertyPanel.this, null);
        form.add(new Label("host.label", lHost));
        form.add(new TextField<String>("host", String.class).setRequired(false).setLabel(lHost).setRequired(false).setLabel(lHost));

        StringResourceModel lValue = new StringResourceModel("value.label", EditOrCreateDbPropertyPanel.this, null);
        form.add(new Label("value.label", lValue));
        form.add(new TextField<String>("value", String.class).setRequired(true).setLabel(lValue).setRequired(false).setLabel(lValue));

        StringResourceModel lDescription = new StringResourceModel("description.label", EditOrCreateDbPropertyPanel.this, null);
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
                    DbProperty u = dbPropertyService.save((DbProperty) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateDbPropertyPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    form.error(new StringResourceModel("TODO", EditOrCreateDbPropertyPanel.this, null).getString());
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
