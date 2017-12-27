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
package de.inren.frontend.banking.review;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.inren.frontend.common.panel.ABasePanel;

public class YearlyReviewPanel extends ABasePanel<Void> {

    private IModel<Integer> yearModel;
    private IModel<Integer> monthModel;
    private IModel<Date>    startDateModel;
    private IModel<Date>    endDateModel;

    public YearlyReviewPanel(String id) {
        super(id);
        initDateModels();
    }

    private void initDateModels() {
        Calendar cal = Calendar.getInstance();
        yearModel = Model.of(Integer.valueOf(cal.get(Calendar.YEAR)));
        monthModel = Model.of(Integer.valueOf(cal.get(Calendar.MONTH)));
        startDateModel = new AbstractReadOnlyModel<Date>() {

            @Override
            public Date getObject() {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, yearModel.getObject());
                cal.set(Calendar.MONTH, monthModel.getObject());
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            }
        };

        endDateModel = new AbstractReadOnlyModel<Date>() {

            @Override
            public Date getObject() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDateModel.getObject());
                cal.add(Calendar.MONTH, 12);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.add(Calendar.SECOND, -1);
                return cal.getTime();
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form<Void> form = new Form<Void>("form");
        // Year
        StringResourceModel lYear = new StringResourceModel("year.label", YearlyReviewPanel.this, null);
        form.add(new Label("year.label", lYear));
        DropDownChoice<Integer> yearChoice = new DropDownChoice<Integer>("year", yearModel, calculateYearChoice());
        yearChoice.setNullValid(false);
        form.add(yearChoice);

        // Month
        StringResourceModel lMonth = new StringResourceModel("month.label", YearlyReviewPanel.this, null);
        form.add(new Label("month.label", lMonth));
        DropDownChoice<Integer> monthChoice = new DropDownChoice<Integer>("month", monthModel, calculateMonthChoice());
        monthChoice.setChoiceRenderer(new IChoiceRenderer<Integer>() {

            @Override
            public Object getDisplayValue(Integer object) {
                return getString("month." + object + ".label");
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return String.valueOf(index);
            }

            @Override
            public Integer getObject(String id, IModel<? extends List<? extends Integer>> choices) {
                return choices.getObject().get(Integer.valueOf(id));
            }
        });
        monthChoice.setNullValid(false);
        form.add(monthChoice);

        final Component reviewPanel = new YearlyReviewResultPanel("review", startDateModel, endDateModel);
        form.add(reviewPanel);

        // calculate new
        form.add(new AjaxButton("calculate") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    target.add(reviewPanel);

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", YearlyReviewPanel.this, null).getString());
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

    private List<Integer> calculateMonthChoice() {
        return Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
    }

    private List<Integer> calculateYearChoice() {
        return Arrays.asList(new Integer[] { 2014, 2015, 2016, 2017 });
    }
}
