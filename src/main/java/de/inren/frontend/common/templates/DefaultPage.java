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
package de.inren.frontend.common.templates;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

/**
 * 
 * @author Ingo Renner
 * 
 * @param <T>
 */
public class DefaultPage<T> extends TemplatePage<T> {

    private final static String WICKET_ID = "panel";

    @Override
    protected void onInitialize() {
	super.onInitialize();
	if (!hasBeenRendered()) {
	    add(createPanel(WICKET_ID));
	}
    }

    public Component createPanel(String wicketId) {
	return new Label(wicketId,
		"Please override 'createPanel(String wicketId)' in "
			+ getClass().getSimpleName());
    }
}
