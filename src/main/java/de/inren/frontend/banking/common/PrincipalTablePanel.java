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
package de.inren.frontend.banking.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;
import de.inren.service.banking.BankDataService;
import de.inren.service.banking.PrincipalInfo;

/**
 * @author Ingo Renner
 *
 */
public class PrincipalTablePanel extends ABasePanel<List<PrincipalInfo>> {

    @SpringBean
    private BankDataService bankDataService;

    public PrincipalTablePanel(String id) {
        super(id, new ListModel<PrincipalInfo>(new ArrayList<PrincipalInfo>()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(getTable("table"));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        getModel().getObject().clear();
        getModel().getObject().addAll(bankDataService.getPrincipalInfo());
    }

    private Component getTable(final String id) {
        AjaxFallbackDefaultDataTableBuilder<PrincipalInfo> builder = new AjaxFallbackDefaultDataTableBuilder<PrincipalInfo>(PrincipalTablePanel.this);

        Component table = builder.addDataProvider(getDataProvider()).addPropertyColumn("principal", true).addPropertyColumn("count", true)
                .addBooleanPropertyColumn("filtered", true).setNumberOfRows(50).build(id);
        TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
        table.add(tableBehavior);
        return table;
    }

    private SortableDataProvider<PrincipalInfo, String> getDataProvider() {
        return new SortableDataProvider<PrincipalInfo, String>() {

            @Override
            public Iterator<PrincipalInfo> iterator(long first, long count) {
                return getModel().getObject().subList((int) first, (int) first + (int) count).iterator();
            }

            @Override
            public long size() {
                return getModel().getObject().size();
            }

            @Override
            public IModel<PrincipalInfo> model(PrincipalInfo object) {
                return Model.of(object);
            }

        };
    }

}
