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
 * Copygroup 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.inren.frontend.banking.tagging;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import de.inren.data.domain.tagging.Tag;
import de.inren.frontend.common.manage.AWorktopManageDelegate;
import de.inren.frontend.common.panel.WorktopPanel;

/**
 * @author Ingo Renner
 * 
 */
public class TagWorktopManageDelegate extends AWorktopManageDelegate<Tag> {

    public TagWorktopManageDelegate(WorktopPanel panel) {
        super(panel);
    }

    @Override
    public Panel getManagePanel() {
        final ManageTagsPanel p = new ManageTagsPanel(getPanel().getComponentId(), this);
        p.setOutputMarkupId(true);
        return p;
    }

    @Override
    public Panel getEditPanel(IModel<Tag> m) {
        final EditOrCreateTagPanel p = new EditOrCreateTagPanel(getPanel().getComponentId(), m, this);
        p.setOutputMarkupId(true);
        return p;
    }
}
