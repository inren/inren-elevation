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
package de.inren.frontend.common.manage;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import de.inren.frontend.common.panel.WorktopPanel;

/**
 * @author Ingo Renner
 *
 */
public abstract class AWorktopManageDelegate<T extends Serializable> implements IWorktopManageDelegate<T> {

	private WorktopPanel<T> panel;

	public AWorktopManageDelegate(WorktopPanel<T> panel) {
		this.panel = panel;
	}

	@Override
	public void switchToComponent(AjaxRequestTarget target, Component replacement) {
		if (target != null) {
			panel.addOrReplace(replacement);
			target.add(replacement);
		}
		panel.addOrReplace(replacement);
	}

	public WorktopPanel<T> getPanel() {
		return panel;
	}
}