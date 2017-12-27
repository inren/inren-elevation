package de.inren.frontend.banking.account;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import de.inren.frontend.common.manage.AWorktopManageDelegate;
import de.inren.frontend.common.panel.WorktopPanel;
import de.inren.service.banking.AccountState;

public class AccountWorktopManageDelegate extends AWorktopManageDelegate<AccountState> {

	public AccountWorktopManageDelegate(WorktopPanel panel) {
		super(panel);
	}

	@Override
	public Panel getManagePanel() {
		final ManageAccountsPanel p = new ManageAccountsPanel(getPanel().getComponentId(), this);
		p.setOutputMarkupId(true);
		return p;
	}

	@Override
	public Panel getEditPanel(IModel<AccountState> m) {
		final EditAccountPanel p = new EditAccountPanel(getPanel().getComponentId(), m, this);
		p.setOutputMarkupId(true);
		return p;
	}

}
