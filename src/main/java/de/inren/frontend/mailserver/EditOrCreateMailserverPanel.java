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

package de.inren.frontend.mailserver;

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

import de.inren.data.domain.mail.MailServer;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.mail.MailServerService;

/**
 * @author Ingo Renner
 * 
 */
public class EditOrCreateMailserverPanel extends ABasePanel implements IAdminPanel {
    @SpringBean
    private MailServerService mailServerService;

    private final IWorktopManageDelegate<MailServer> delegate;

    final MailServer mailserver;

    public EditOrCreateMailserverPanel(String componentId, IModel<MailServer> m, IWorktopManageDelegate<MailServer> delegate) {
        super(componentId);
        if (m != null) {
            mailserver = m.getObject();
        } else {
            mailserver = new MailServer();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<MailServer> form = new Form<MailServer>("form", new CompoundPropertyModel<MailServer>(mailserver));

        StringResourceModel lName = new StringResourceModel("name.label", EditOrCreateMailserverPanel.this, null);
        form.add(new Label("name.label", lName));
        form.add(new TextField<String>("name", String.class).setRequired(true).setLabel(lName).setRequired(false).setLabel(lName));

        StringResourceModel lHost = new StringResourceModel("host.label", EditOrCreateMailserverPanel.this, null);
        form.add(new Label("host.label", lHost));
        form.add(new TextField<String>("host", String.class).setRequired(false).setLabel(lHost).setRequired(false).setLabel(lHost));

        StringResourceModel lPort = new StringResourceModel("port.label", EditOrCreateMailserverPanel.this, null);
        form.add(new Label("port.label", lPort));
        form.add(new TextField<String>("port", String.class).setRequired(false).setLabel(lPort).setRequired(false).setLabel(lPort));

        StringResourceModel lUsername = new StringResourceModel("username.label", EditOrCreateMailserverPanel.this, null);
        form.add(new Label("username.label", lUsername));
        form.add(new TextField<String>("username", String.class).setRequired(false).setLabel(lUsername).setRequired(false).setLabel(lUsername));

        StringResourceModel lpassword = new StringResourceModel("password.label", EditOrCreateMailserverPanel.this, null);
        form.add(new Label("password.label", lpassword));
        form.add(new TextField<String>("password", String.class).setRequired(false).setLabel(lpassword).setRequired(false).setLabel(lpassword));

        StringResourceModel lCurrent = new StringResourceModel("current.label", EditOrCreateMailserverPanel.this, null);
        form.add(new Label("current.label", lCurrent));
        form.add(new TextField<String>("current", String.class).setRequired(false).setLabel(lCurrent).setRequired(false).setLabel(lCurrent));

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
                    MailServer u = mailServerService.save((MailServer) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateMailserverPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (Exception e) {
                    form.error(new StringResourceModel("TODO", EditOrCreateMailserverPanel.this, null).getString());
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
