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
package de.inren.frontend.common.panel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * @author Ingo Renner
 *
 */
public class MoneyPanel extends Panel {

    private Label label;

    public MoneyPanel(String id, IModel<BigDecimal> model) {
        super(id, model);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (((BigDecimal) getDefaultModelObject()).floatValue() <= 0f) {
            label.add(AttributeModifier.replace("class", "moneyNegative"));
        } else {
            if (((BigDecimal) getDefaultModelObject()).floatValue() == 0f) {
                label.add(AttributeModifier.replace("class", "moneyBlackZero"));
            } else {
                label.add(AttributeModifier.replace("class", "moneyPositive"));
            }
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        label = new Label("id", getFormatedMoneyModel());
        add(label);
    }

    private IModel<String> getFormatedMoneyModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                BigDecimal money = (BigDecimal) getDefaultModelObject();
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                return numberFormat.format(money);
            }

        };
    }
}
