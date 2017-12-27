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
package de.inren.frontend.usersettings;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.ITheme;
import de.inren.data.domain.user.User;
import de.inren.data.domain.usersettings.UserSettings;
import de.inren.frontend.application.ApplicationSettingsUtil;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.user.UserService;
import de.inren.service.usersettings.UserSettingsService;

/**
 * @author Ingo Renner
 *
 */
public class EditOrCreateUserSettingsPanel extends ABasePanel implements IAdminPanel {
    @SpringBean
    private UserService userService;

    @SpringBean
    private UserSettingsService userSettingsService;

    private UserSettings userSettings;

    private final User user;

    public EditOrCreateUserSettingsPanel(String id) {
        super(id);
        user = getUser();
        initSettings();
    }

    private void initSettings() {
        try {
            userSettings = userSettingsService.loadByUser(user.getId());
        } catch (RuntimeException e) {
            // TODO Feedbackpannel Meldung
            userSettings = new UserSettings();
        }
    }

    private List<String> getThemes() {
        List<ITheme> themes = Bootstrap.getSettings(getApplication()).getThemeProvider().available();
        List<String> res = new ArrayList<String>();
        for (ITheme t : themes) {
            res.add(t.name());
        }
        return res;
    }

    @Override
    protected void initGui() {

        final Form<UserSettings> form = new Form<UserSettings>("form", new CompoundPropertyModel<UserSettings>(userSettings));

        StringResourceModel lName = new StringResourceModel("name.label", EditOrCreateUserSettingsPanel.this, null);
        form.add(new Label("name.label", lName));

        form.add(new DropDownChoice<String>("name", new PropertyModel<String>(userSettings, "theme"), getThemes()));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
                initSettings();
                target.add(form);
            }
        });

        form.add(new Button("submit") {

            @Override
            public void onSubmit() {
                try {
                    UserSettings u = userSettingsService.save(form.getModelObject());

                    ApplicationSettingsUtil.applySettings(u);

                    getFeedback().info(new StringResourceModel("feedback.success", EditOrCreateUserSettingsPanel.this, null).getString());
                } catch (RuntimeException e) {
                    getFeedback().error(new StringResourceModel("TODO", EditOrCreateUserSettingsPanel.this, null).getString());
                }
            }

            @Override
            public void onError() {
                FeedbackPanel f = getFeedback();
                f.info("Error");
            }
        });

        add(form);
    }

}
