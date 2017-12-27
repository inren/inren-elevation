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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.pingunaut.wicket.chartjs.chart.impl.Line;
import com.pingunaut.wicket.chartjs.core.panel.LineChartPanel;
import com.pingunaut.wicket.chartjs.data.LineChartData;
import com.pingunaut.wicket.chartjs.data.sets.LineDataSet;

import de.inren.data.domain.banking.Transaction;
import de.inren.data.repositories.banking.TransactionRepository;
import de.inren.facade.banking.BankingFacade;
import de.inren.service.banking.AccountState;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 *
 */
public class YearlyReviewResultPanel extends Panel {
    @SpringBean
    private BankingFacade bankingFacade;
    
    @SpringBean
    private BankDataService       bankDataService;

    private IModel<Date>          startDateModel;
    private IModel<Date>          endDateModel;

    public YearlyReviewResultPanel(String id, IModel<Date> startDateModel, IModel<Date> endDateModel) {
        super(id);
        this.startDateModel = startDateModel;
        this.endDateModel = endDateModel;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        LineChartPanel lineChartPanel = new LineChartPanel("lineChartPanel", Model.of(new Line()), 1000, 700);
        add(lineChartPanel);

        List<String> labels = new ArrayList<String>();
        List<BigDecimal> values = new ArrayList<BigDecimal>();

        LineChartData<LineDataSet> lineData = new LineChartData<LineDataSet>();
        bankingFacade.fillInMonthlyBalance("5401031887", labels, values);
        lineData.setLabels(labels);
        LineDataSet e1 = new LineDataSet(values);
        e1.setPointColor("blue");
        lineData.getDatasets().add(e1);
        
        List<String> labels2 = new ArrayList<String>();
        List<BigDecimal> values2 = new ArrayList<BigDecimal>();
        bankingFacade.fillInMonthlyBalance("5387449206", labels2, values2);
        LineDataSet e = new LineDataSet(values2);
        e.setPointColor("red");
        lineData.getDatasets().add(e);
        
        
        lineChartPanel.getChart().setData(lineData);

    }


}
