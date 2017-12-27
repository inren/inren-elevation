package de.inren.frontend.banking.overview;

import org.apache.wicket.Component;
import org.wicketstuff.annotation.mount.MountPath;

import de.inren.frontend.common.templates.SecuredPage;

@MountPath("CategoryOverview")
public class CategoryOverviewPage extends SecuredPage {

	@Override
	public Component createPanel(String wicketId) {
		return new CategoryOverviewPanel(wicketId);
	}
}
