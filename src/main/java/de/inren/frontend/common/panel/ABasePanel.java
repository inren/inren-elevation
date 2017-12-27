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

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import de.inren.data.domain.user.User;
import de.inren.data.domain.usersettings.UserSettings;
import de.inren.frontend.application.InRenApplication;
import de.inren.security.BasicAuthenticationSession;

/**
 * Base panel with common utility methods.
 * 
 * @author Ingo Renner
 */
public abstract class ABasePanel<T> extends Panel {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private FeedbackPanel feedback;

    public ABasePanel(String id) {
        super(id);
    }

    public ABasePanel(String id, IModel<T> model) {
        super(id, model);
    }

    @SuppressWarnings("unchecked")
	public IModel<T> getModel() {
    	return (IModel<T>) getDefaultModel();
    }
    
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        try {
            this.feedback = getApplication() instanceof InRenApplication ? ((InRenApplication) getApplication()).getFeedbackPanel(getPage()) : null;

        } catch (Exception e) {
            log.error("Application Class = " + getApplication().getClass().getName());
            log.error("Page Class        = " + getPage().getClass().getName());
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initGui();
    }

    @Deprecated
    protected void initGui(){
    	
    }

    /**
     * @return the feedback panel
     */
    public FeedbackPanel getFeedback() {
        return feedback;
    }

    /**
     * @return the logged in user
     */
    public User getUser() {
        return ((BasicAuthenticationSession) getSession()).getUser();
    }

    public UserSettings getUserSettings() {
        return getBasicAuthenticationSession().getUserSettings();
    }

    /**
     * @return true if a user is signed in. false otherwise
     */
    public boolean isSignedIn() {
        return ((BasicAuthenticationSession) getSession()).isSignedIn();
    }

    /**
     * @return current authenticated wicket web session
     */
    public BasicAuthenticationSession getBasicAuthenticationSession() {
        return (BasicAuthenticationSession) BasicAuthenticationSession.get();
    }

    public boolean isSuperuser() {
        return false; // hasRole(Roles.ROLE_ADMIN.name());
    }

    public boolean hasRole(String role) {
        for (GrantedAuthority grantedAuthority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
