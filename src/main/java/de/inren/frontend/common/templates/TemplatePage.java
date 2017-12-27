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
package de.inren.frontend.common.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.ComponentPosition;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.inren.frontend.application.ApplicationStatus;
import de.inren.frontend.application.InRenApplication;
import de.inren.frontend.auth.LoginPage;
import de.inren.frontend.auth.LogoutPage;
import de.inren.frontend.common.language.LanguageSwitcherPanel;
import de.inren.frontend.navigation.GNode;
import de.inren.frontend.navigation.NavList;
import de.inren.frontend.navigation.NavigationElement;
import de.inren.frontend.navigation.NavigationProvider;
import de.inren.security.BasicAuthenticationSession;

/**
 * @author Ingo Renner
 * 
 */
public class TemplatePage<T> extends GenericWebPage<T> {

	private final static Logger log = LoggerFactory.getLogger(TemplatePage.class);

	public TemplatePage() {
		super();
	}

	public TemplatePage(IModel<T> model) {
		super(model);
	}

	public TemplatePage(PageParameters parameters) {
		super(parameters);
	}

	@Override
	public void renderHead(final IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(TemplatePage.class, "inren.css")));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		configureTheme(getPageParameters());
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new HtmlTag("html"));

		add(new MetaTag("viewport", Model.of("viewport"), Model.of("width=device-width, initial-scale=1.0")));
		add(new MetaTag("description", Model.of("description"), Model.of("InRen")));
		add(new MetaTag("author", Model.of("author"), Model.of("Ingo Renner <ingo@inren.de>")));

		DebugBar debugBar = new DebugBar("debug");
		debugBar.add(AttributeModifier.append("style", "z-index:2000;"));
		add(debugBar);

		Navbar navbar = createNavbar("navbar");
		navbar.add(AttributeModifier.append("style", "padding-top: 20px;"));
		add(navbar);

		add(new Footer("footer"));

		add(getLeftComponent("left"));

		add(new FeedbackPanel("feedbackPanel").setOutputMarkupId(true));

		add(new BootstrapBaseBehavior());
	}

	/**
	 * creates a new {@link Navbar} instance
	 * 
	 * @param markupId
	 *            The components markup id.
	 * @return a new {@link Navbar} instance
	 */
	protected Navbar createNavbar(String markupId) {
		Navbar navbar = new Navbar(markupId);

		navbar.setPosition(Navbar.Position.STATIC_TOP);

		// show brand name
		navbar.setBrandName(() -> {
			ApplicationStatus status = ((InRenApplication) Application.get()).getApplicationStatus();
			return "InRen (#=" + status.getSessions().size() + "/A=" + status.getActiveSessions() + "/T="
					+ status.getTemporarySessions() + "/I=" + status.getInvalidSessions() + ")";
		});

		navbar.addComponents(
				NavigationProvider.get().getTopNavbarComponents(getActivePermissions(), TemplatePage.this));

		// Theme selector on the right.
		final List<INavbarComponent> components = new ArrayList<INavbarComponent>();
		// components.add(new ImmutableNavbarComponent(new ThemesDropDown(),
		// Navbar.ComponentPosition.RIGHT));
		if (isSignedIn()) {
			components.add(new ImmutableNavbarComponent(new NavbarButton<LogoutPage>(LogoutPage.class,
					new StringResourceModel("logout.label", TemplatePage.this, null)).setIconType(GlyphIconType.globe),
					ComponentPosition.RIGHT));
		} else {
			components.add(new ImmutableNavbarComponent(new NavbarButton<LoginPage>(LoginPage.class,
					new StringResourceModel("login.label", TemplatePage.this, null)).setIconType(GlyphIconType.globe),
					ComponentPosition.RIGHT));
		}
		// change language
		components.add(new ImmutableNavbarComponent(new LanguageSwitcherPanel("component"), ComponentPosition.RIGHT));
		navbar.addComponents(components);
		return navbar;
	}

	@Override
	public BasicAuthenticationSession getSession() {
		return (BasicAuthenticationSession) super.getSession();
	}

	private boolean isSignedIn() {
		return getSession().isSignedIn();
	}

	private Collection<String> getActivePermissions() {
		return getSession().getRoles();
	}

	protected boolean hasNavigation() {
		return true;
	}

	/**
	 * sets the theme for the current user.
	 * 
	 * @param pageParameters
	 *            current page parameters
	 */
	private void configureTheme(PageParameters pageParameters) {
		StringValue theme = pageParameters.get("theme");

		if (!theme.isEmpty()) {
			IBootstrapSettings settings = Bootstrap.getSettings(getApplication());
			settings.getActiveThemeProvider().setActiveTheme(theme.toString(""));
		}
	}

	protected Component getLeftComponent(String id) {
		log.debug("getLeftComponent for class: " + getClass());
		GNode<NavigationElement> menu = NavigationProvider.get().getSideNavbarComponents(getClass(),
				getActivePermissions(), TemplatePage.this);
		if (menu == null) {
			return new Label(id, "").setVisible(false);
		} else {
			return new NavList(id, menu);
		}
	}
}
