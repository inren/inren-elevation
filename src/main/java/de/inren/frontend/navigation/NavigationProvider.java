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
package de.inren.frontend.navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.ComponentPosition;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.inren.data.domain.security.ComponentAccess;
import de.inren.data.domain.security.Role;
import de.inren.frontend.admin.AdminPage;
import de.inren.frontend.application.HomePage;
import de.inren.frontend.auth.LoginPage;
import de.inren.frontend.banking.AccountOverviewPage;
import de.inren.frontend.banking.BankDataImportPage;
import de.inren.frontend.banking.ManageTransactionsPage;
import de.inren.frontend.banking.account.ManageAccountsPage;
import de.inren.frontend.banking.category.ManageCategoriesPage;
import de.inren.frontend.banking.filter.ManageCategoryFiltersPage;
import de.inren.frontend.banking.overview.CategoryOverviewPage;
import de.inren.frontend.banking.review.CategoryReviewPage;
import de.inren.frontend.banking.review.MonthlyReviewPage;
import de.inren.frontend.banking.review.YearlyReviewPage;
import de.inren.frontend.banking.summery.TransactionSummeryPage;
import de.inren.frontend.banking.tagging.ManageTagsPage;
import de.inren.frontend.componentaccess.ManageComponentAccessPage;
import de.inren.frontend.dbproperty.ManageDbPropertiesPage;
import de.inren.frontend.group.ManageGroupsPage;
import de.inren.frontend.log4j.ManageLoggerSettingsPage;
import de.inren.frontend.mailserver.ManageMailserverPage;
import de.inren.frontend.right.ManageRightsPage;
import de.inren.frontend.role.ManageRolesPage;
import de.inren.frontend.user.ManageUsersPage;
import de.inren.frontend.usersettings.UserSettingsPage;
import de.inren.service.security.ComponentAccessService;
import de.inren.service.security.RoleService.Roles;

/**
 * 
 * Decide which item appears under which navigationbar.
 * 
 * Hard coded version. Has to be replaced by a db service once in the future.
 * 
 * @author Ingo Renner
 * 
 */
public class NavigationProvider {

	@SpringBean
	private ComponentAccessService componentAccessService;

	private static final List<String> EMPTY_LIST = Collections.<String>emptyList();

	private static NavigationProvider navigationProvider;

	private GTree<NavigationElement> tree;

	private NavigationProvider() {
		Injector.get().inject(this);
		initNavigation();
	}

	public static NavigationProvider get() {
		if (navigationProvider == null) {
			navigationProvider = new NavigationProvider();
		}
		return navigationProvider;
	}

	private Collection<String> getRoles(String name) {
		ComponentAccess componentAccess = componentAccessService.findComponentAccessByName(name);
		Collection<String> res = new ArrayList<String>();
		if (componentAccess != null) {
			for (Role role : componentAccess.getGrantedRoles()) {
				for (SimpleGrantedAuthority simpleGrantedAuthority : role.asAuthority()) {
					res.add(simpleGrantedAuthority.getAuthority());
				}
			}
		}
		return res;
	}

	@Cacheable("ComponentAccess")
	public List<INavbarComponent> getTopNavbarComponents(Collection<String> roles, Component component) {

		List<INavbarComponent> res = new ArrayList<INavbarComponent>();
		// Root
		if (hasRight(getRoles(tree.getRoot().getData().getClazz().getSimpleName()), roles)) {
			res.add(createINavbarComponent(tree.getRoot().getData(), component));
		}
		// and 1 level
		for (GNode<NavigationElement> e : tree.getRoot().getChildren()) {
			if (hasRight(getRoles(e.getData().getClazz().getSimpleName()), roles)) {
				res.add(createINavbarComponent(e.getData(), component));
			}
		}

		return res;
	}

	@Cacheable("ComponentAccess")
	public GNode<NavigationElement> getSideNavbarComponents(Class clazz, Collection<String> roles,
			Component component) {

		GNode<NavigationElement> res = null;
		GNode<NavigationElement> r = null;
		for (GNode<NavigationElement> n : tree.getRoot().getChildren()) {

			if (findNode(n, clazz) != null) {
				r = n;
				break;
			}
		}
		if (r != null) {
			if (hasRight(getRoles(r.getData().getClazz().getSimpleName()), roles)) {
				res = new GNode<NavigationElement>(r.getData());
			}
			// and 1 level
			for (GNode<NavigationElement> e : r.getChildren()) {
				if (hasRight(getRoles(e.getData().getClazz().getSimpleName()), roles)) {
					res.addChild(e);
				}
			}
		}
		return res;
	}

	private GNode<NavigationElement> findNode(GNode<NavigationElement> node, Class clazz) {
		if (node.getData().getClazz().equals(clazz)) {
			return node;
		}
		for (GNode<NavigationElement> n : node.getChildren()) {
			GNode<NavigationElement> r = findNode(n, clazz);
			if (r != null) {
				return r;
			}
		}
		return null;
	}

	private INavbarComponent createINavbarComponent(NavigationElement data, Component component) {
		return new ImmutableNavbarComponent(new NavbarButton<LoginPage>(data.getClazz(),
				new StringResourceModel(data.getLanguageKey(), component, null)), data.getPosition());
	}

	private boolean hasRight(Collection<String> needRoles, Collection<String> givenRoles) {
		if (needRoles.isEmpty()) {
			return true;
		}
		for (String r : needRoles) {
			if (r.contains(":")) {
				if (givenRoles.contains(r)) {
					return true;
				}
			} else {
				for (String roleRight : givenRoles) {
					String role = roleRight.split(":")[0];
					if (r.equals(role)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void initNavigation() {

		List<String> bankRoles = Arrays.asList();

		// static hack to speed up development for other places
		GNode<NavigationElement> root = new GNode<NavigationElement>(new NavigationElement(HomePage.class, "Home.label",
				EMPTY_LIST, ComponentPosition.LEFT)).addChild(new GNode<NavigationElement>(
						new NavigationElement(ManageTransactionsPage.class, "Transactions.label", bankRoles,
								ComponentPosition.LEFT),
						Arrays.asList(
								new GNode<NavigationElement>(new NavigationElement(AccountOverviewPage.class,
										"AccountOverview.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(ManageAccountsPage.class,
										"ManageAccounts.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(ManageTransactionsPage.class,
										"Transactions.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(TransactionSummeryPage.class,
										"TransactionSummary.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(MonthlyReviewPage.class,
										"MonthlyReview.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(YearlyReviewPage.class,
										"YearlyReview.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(CategoryReviewPage.class,
										"CategoryReview.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(CategoryOverviewPage.class,
										"CategoryOverview.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(ManageCategoriesPage.class,
										"Categories.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(ManageTagsPage.class, "Tags.label",
										bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(ManageCategoryFiltersPage.class,
										"BankFilter.label", bankRoles, ComponentPosition.LEFT)),

								new GNode<NavigationElement>(new NavigationElement(BankDataImportPage.class,
										"BankImport.label", bankRoles, ComponentPosition.LEFT))

						)))

						.addChild(new GNode<NavigationElement>(
								new NavigationElement(AdminPage.class, "Admin.label",
										Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.RIGHT),
								Arrays.asList(new GNode<NavigationElement>(new NavigationElement(ManageUsersPage.class,
										"Users.label", Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

										new GNode<NavigationElement>(new NavigationElement(ManageGroupsPage.class,
												"Groups.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
												ComponentPosition.LEFT)),

										new GNode<NavigationElement>(new NavigationElement(ManageRightsPage.class,
												"Rights.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
												ComponentPosition.LEFT)),

										new GNode<NavigationElement>(new NavigationElement(
												ManageComponentAccessPage.class, "ComponentAccess.label",
												Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

										new GNode<NavigationElement>(new NavigationElement(ManageMailserverPage.class,
												"MailServer.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
												ComponentPosition.LEFT)),

										new GNode<NavigationElement>(new NavigationElement(ManageRolesPage.class,
												"Roles.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
												ComponentPosition.LEFT)),

										new GNode<NavigationElement>(new NavigationElement(ManageDbPropertiesPage.class,
												"DbProperty.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
												ComponentPosition.LEFT)),

										new GNode<NavigationElement>(new NavigationElement(
												ManageLoggerSettingsPage.class, "Logger.label",
												Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)))))
						.addChild(new GNode<NavigationElement>(new NavigationElement(UserSettingsPage.class,
								"Settings.label", Arrays.asList(Roles.ROLE_USER.name(), Roles.ROLE_ADMIN.name()),
								ComponentPosition.RIGHT)));

		tree = new GTree<NavigationElement>(root);
	}
}
