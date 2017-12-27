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
package de.inren.frontend.application;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Session;

/**
 * Some status info about the application.
 * 
 * @author Ingo Renner
 *
 */
public class ApplicationStatus {

    private final List<Session> sessions = new ArrayList<Session>();

    public List<Session> getSessions() {
        return sessions;
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }

    public int getActiveSessions() {
        int activeSessions = 0;
        for (Session session : sessions) {
            if (!session.isSessionInvalidated() && !session.isSessionInvalidated()) {
                activeSessions++;
            }
        }
        return activeSessions;
    }

    public int getInvalidSessions() {
        int invalidSessions = 0;
        for (Session session : sessions) {
            if (session.isSessionInvalidated()) {
                invalidSessions++;
            }
        }
        return invalidSessions;
    }

    public int getTemporarySessions() {
        int temporarySessions = 0;
        for (Session session : sessions) {
            if (session.isTemporary()) {
                temporarySessions++;
            }
        }
        return temporarySessions;
    }
}
