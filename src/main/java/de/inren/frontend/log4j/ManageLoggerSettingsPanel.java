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
/**
 * 
 */
package de.inren.frontend.log4j;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.form.FormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.inren.data.domain.loggerbean.LoggerBean;
import de.inren.data.repositories.loggerbean.LoggerBeanRepository;
import de.inren.frontend.common.dataprovider.RepositoryDataProvider;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.ListChoiceMarkupContainer;
import de.inren.frontend.common.table.AjaxFallbackDefaultDataTableBuilder;
import de.inren.service.log4j.LoggerAdminService;

/**
 * @author Ingo Renner
 *
 */
public class ManageLoggerSettingsPanel extends ABasePanel {
    @SpringBean
    private LoggerAdminService   loggerAdminService;

    @SpringBean
    private LoggerBeanRepository loggerBeanRepository;

    private LoggerBean           filterSettings = new LoggerBean();

    public ManageLoggerSettingsPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(getTable("table"));
        add(new LoggerFilterPanel("filter", Model.<LoggerBean> of(filterSettings)));
    }

    private Component getTable(final String id) {
        AjaxFallbackDefaultDataTableBuilder<LoggerBean> builder = new AjaxFallbackDefaultDataTableBuilder<LoggerBean>(ManageLoggerSettingsPanel.this);

        Component table = builder.addDataProvider(new RepositoryDataProvider<LoggerBean>(loggerBeanRepository) {
            @Override
            protected Page<LoggerBean> getPage(final Pageable pageable) {
                if (filterSettings == null) {
                    return getRepository().findAll(pageable);
                }
                final String name = Strings.isEmpty(filterSettings.getName()) ? "%" : "%" + filterSettings.getName() + "%";
                final String level = Strings.isEmpty(filterSettings.getLevel()) ? null : "%" + filterSettings.getLevel() + "%";
                if (level == null) {
                    return ((LoggerBeanRepository) getRepository()).findByNameLike(name, pageable);
                } else {
                    return ((LoggerBeanRepository) getRepository()).findByNameLikeAndLevelLike(name, level, pageable);

                }
            }

            @Override
            public long size() {
                PageRequest page = new PageRequest(0, 100);
                return getPage(page).getTotalElements();
            }

        }).addPropertyColumn("id", true).addPropertyColumn("name", true).addPropertyColumn("level", true)
                .add(new AbstractColumn<LoggerBean, String>(new StringResourceModel("level.label", ManageLoggerSettingsPanel.this, null)) {

                    @Override
                    public void populateItem(Item<ICellPopulator<LoggerBean>> cellItem, String componentId, IModel<LoggerBean> rowModel) {
                        ListChoice<String> listChoice = new ListChoice<String>(ListChoiceMarkupContainer.CHOICES_ID, new PropertyModel<String>(rowModel
                                .getObject(), "level"), loggerAdminService.getLogLevels()) {

                        };
                        listChoice.setNullValid(true);
                        listChoice.setMaxRows(1);
                        listChoice.add(new FormComponentUpdatingBehavior() {
                        	protected void onUpdate() {
                        		LoggerBean lb = rowModel.getObject();
                        		// TODO-inren prüfen ob das wirklich funktioniert
                        		loggerAdminService.setLevelAndSave(lb.getName(), getFormComponent().getInput());
                        	}
                        	});
                        ListChoiceMarkupContainer markup = new ListChoiceMarkupContainer(componentId, listChoice);
                        cellItem.add(markup);
                    }
                }).setNumberOfRows(10).build(id)
                ;
        TableBehavior tableBehavior = new TableBehavior().bordered().condensed();
        table.add(tableBehavior);
        return table;
    }

}
