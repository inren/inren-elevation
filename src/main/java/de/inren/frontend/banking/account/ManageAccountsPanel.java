package de.inren.frontend.banking.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonGroup;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.inren.data.domain.banking.Account;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ActionPanelBuilder;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.frontend.common.panel.ManagePanel;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;
import de.inren.service.banking.AccountState;
import de.inren.service.banking.BankDataService;

public class ManageAccountsPanel extends ManagePanel implements IAdminPanel {

	@SpringBean
	private BankDataService bankDataService;

	private final IWorktopManageDelegate<AccountState> delegate;

	private final IModel<List<AccountState>> accountStateModel = new ListModel<AccountState>(
			new ArrayList<AccountState>());

	public ManageAccountsPanel(String id, IWorktopManageDelegate<AccountState> delegate) {
		super(id);
		this.delegate = delegate;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		accountStateModel.getObject().clear();
		accountStateModel.getObject().addAll(loadAccountInformation());
	}

	@Override
	protected final Component getTable(final String id) {
		AjaxFallbackDefaultDataTableBuilder<AccountState> builder = new AjaxFallbackDefaultDataTableBuilder<AccountState>(
				ManageAccountsPanel.this);

		Component table = builder.addDataProvider(getDataProvider()).add(new AbstractColumn<AccountState, String>(
				new StringResourceModel("actions.label", ManageAccountsPanel.this, null)) {
			@Override
			public void populateItem(Item<ICellPopulator<AccountState>> cellItem, String componentId,
					IModel<AccountState> rowModel) {

				final ActionPanelBuilder linkBuilder = ActionPanelBuilder.getBuilder();
				final AccountState accountState = rowModel.getObject();
				final ButtonGroup bg = new ButtonGroup(componentId) {

					@Override
					protected List<AbstractLink> newButtons(String buttonMarkupId) {
						List<AbstractLink> res = new ArrayList<AbstractLink>();
						BootstrapAjaxLink<String> edit = new BootstrapAjaxLink<String>("button", Buttons.Type.Menu) {

							@Override
							public void onClick(AjaxRequestTarget target) {
								delegate.switchToComponent(target,
										delegate.getEditPanel(new Model<AccountState>(accountState)));

							}
						};
						edit.setIconType(GlyphIconType.pencil);
						edit.setSize(Buttons.Size.Mini);
						edit.setVisible(!accountState.getAccount().getOwner().equals("∑"));
						res.add(edit);
						return res;
					}
				};
				cellItem.add(bg);
			}
		}).addPropertyColumn("account.owner").addPropertyColumn("account.number").addPropertyColumn("account.name")
				.addMoneyPropertyColumn("amount").addPropertyColumn("lastUpdate").setNumberOfRows(50).build(id);
		TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
		table.add(tableBehavior);
		return table;
	}

	private List<AccountState> loadAccountInformation() {
		List<AccountState> allAccountStates = bankDataService.loadAllAccountStates();
		Account account = new Account();
		account.setOwner("∑");
		AccountState summary = new AccountState(account, BigDecimal.ZERO, new Date());
		for (AccountState accountState : allAccountStates) {
			summary.setAmount(summary.getAmount().add(accountState.getAmount()));
		}
		allAccountStates.add(summary);
		return allAccountStates;
	}

	private SortableDataProvider<AccountState, String> getDataProvider() {
		return new SortableDataProvider<AccountState, String>() {

			@Override
			public Iterator<AccountState> iterator(long first, long count) {
				return accountStateModel.getObject().subList((int) first, (int) first + (int) count).iterator();
			}

			@Override
			public long size() {
				return accountStateModel.getObject().size();
			}

			@Override
			public IModel<AccountState> model(AccountState object) {
				return Model.of(object);
			}

		};
	}
}
