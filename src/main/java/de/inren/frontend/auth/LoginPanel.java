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
package de.inren.frontend.auth;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.value.ValueMap;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.inren.frontend.application.ApplicationSettingsUtil;
import de.inren.frontend.common.panel.ABasePanel;

/**
 * Login the user an apply his settings.
 * 
 * @author Ingo Renner
 * 
 */
public class LoginPanel extends ABasePanel {

	private TextField<String> email;
	private PasswordTextField password;
	private final ValueMap properties = new ValueMap();

	public LoginPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final Form<Void> signInForm = new Form<Void>("form") {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				final StringResourceModel lEmail = new StringResourceModel("email.label", LoginPanel.this, null);
				add(new Label("email.label", lEmail));
				email = new RequiredTextField<String>("email", new PropertyModel<String>(properties, "email"), String.class);
				add(email.setLabel(lEmail));

				final StringResourceModel lPass = new StringResourceModel("password.label", LoginPanel.this, null);
				add(new Label("password.label", lPass));
				password = new PasswordTextField("password", new PropertyModel<String>(properties, "password"));
				add(password.setType(String.class).setLabel(lPass));

				BootstrapButton signInButton = new BootstrapButton("signup", new ResourceModel("signup.label"), Buttons.Type.Link) {
					@Override
					public void onSubmit() {
						// TODO redirect to subscribe page
						error("Signup not implemented yet.");
						invalid();
					}
				};
				signInButton.setDefaultFormProcessing(false);
				add(signInButton);
				add(new BootstrapButton("submit", new ResourceModel("submit.label"), Buttons.Type.Primary){
					@Override
					public void onSubmit() {
						super.onSubmit();
						if (getBasicAuthenticationSession().signIn(getEmail(), getPassword())) {
							ApplicationSettingsUtil.applySettings(getUserSettings());
							
							continueToOriginalDestination();
							setResponsePage(getApplication().getHomePage());
						} else {
							error(new StringResourceModel("signInFailed", LoginPanel.this, null).getString());
						}
					}
					
				});
			}
		};
		add(signInForm);
	}

	/**
	 * Convenience method to access the password.
	 * 
	 * @return The password
	 */
	public String getPassword() {
		return password.getInput();
	}

	/**
	 * Convenience method to access the email.
	 * 
	 * @return The email
	 */
	public String getEmail() {
		return email.getDefaultModelObjectAsString();
	}

}
