package de.inren.frontend.banking.account;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.data.domain.banking.Account;
import de.inren.data.repositories.banking.AccountRepository;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.banking.AccountState;

public class EditAccountPanel extends ABasePanel<AccountState> implements IAdminPanel {

	private static Logger log = LoggerFactory.getLogger(EditAccountPanel.class);
	@SpringBean
	private AccountRepository accountRepository;

	private final IWorktopManageDelegate<AccountState> categoryWorktopManageDelegate;

	private final AccountState accountState;

	public EditAccountPanel(String componentId, IModel<AccountState> m, IWorktopManageDelegate<AccountState> delegate) {
		super(componentId);
		if (m != null) {
			accountState = m.getObject();
		} else {
			throw new IllegalStateException("Model must not be null.");
		}
		this.categoryWorktopManageDelegate = delegate;
	}

	@Override
	protected void initGui() {

		Form<AccountState> form = new Form<AccountState>("form", new CompoundPropertyModel<AccountState>(accountState));

		form.add(new Label("owner.label", new StringResourceModel("account.owner.label", EditAccountPanel.this, null)));
		form.add(new Label("account.owner"));

		form.add(new Label("number.label",
				new StringResourceModel("account.number.label", EditAccountPanel.this, null)));
		form.add(new Label("account.number"));

		StringResourceModel lName = new StringResourceModel("account.name.label", EditAccountPanel.this, null);
		form.add(new Label("name.label", lName));

		form.add(new TextField<String>("account.name", String.class).setRequired(true).setLabel(lName)
				.setRequired(false).setLabel(lName));

		form.add(new AjaxLink<Void>("cancel") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				getSession().getFeedbackMessages().clear();
				switchToManagePanel(target);
			}
		});

		form.add(new AjaxButton("submit") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				try {
					AccountState accountStateToSave = form.getModelObject();
					Account account = accountRepository.save(accountStateToSave.getAccount());
					accountStateToSave.setAccount(account);

					form.info(new StringResourceModel("feedback.success", EditAccountPanel.this, null).getString());
					switchToManagePanel(target);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					form.error(new StringResourceModel("TODO", EditAccountPanel.this, null).getString());
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

	private void switchToManagePanel(AjaxRequestTarget target) {
		categoryWorktopManageDelegate.switchToComponent(target, categoryWorktopManageDelegate.getManagePanel());
	}
}
