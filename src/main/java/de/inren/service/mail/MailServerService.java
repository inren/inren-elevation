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
package de.inren.service.mail;

import java.util.List;

import net.bull.javamelody.MonitoredWithSpring;
import de.inren.data.domain.mail.MailServer;
import de.inren.data.domain.user.User;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 */
@MonitoredWithSpring
public interface MailServerService extends Initializable {

    MailServer save(MailServer mailserver);

    void delete(MailServer mailserver);

    MailServer load(Long id);

    List<MailServer> loadAll();

    MailServer loadMailServerByName(String name);

    void sendSignupMail(User user);

    void sendMail(String from, String to, String subject, String text);

    void sendMail(String mailserver, String from, String to, String cc, String bcc, String subject, String text, boolean html);
}
