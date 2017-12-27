package de.inren.frontend.banking.account;

import org.apache.wicket.Component;
import org.wicketstuff.annotation.mount.MountPath;

import de.inren.frontend.common.panel.WorktopPanel;
import de.inren.frontend.common.templates.SecuredPage;
import de.inren.service.banking.AccountState;

@MountPath(value = "/accounts")
public class ManageAccountsPage extends SecuredPage<AccountState> {

	@Override
	public Component createPanel(String wicketId) {
		final WorktopPanel w = new WorktopPanel(wicketId);
		w.setDelegate(new AccountWorktopManageDelegate(w));
		return w;
	}
}