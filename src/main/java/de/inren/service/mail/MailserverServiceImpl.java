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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import de.inren.data.domain.mail.MailServer;
import de.inren.data.domain.user.User;
import de.inren.data.repositories.mail.MailServerRepository;

/**
 * @author Ingo Renner
 */
@Service(value = "mailserverService")
@Transactional(readOnly = true)
public class MailserverServiceImpl implements MailServerService {
    private static final String DEFAULT_MAILSERVER_NAME = "default";

    private static final int DEFAULT_SMTP_PORT = 25;

    private final Logger log = LoggerFactory.getLogger(MailserverServiceImpl.class);

    @Resource
    private MailServerRepository mailserverDao;

    private VelocityEngine velocityEngine;

    private Map<String, JavaMailSenderImpl> mailSenders;

    @Transactional
    @Override
    public void init() {
        if (mailserverDao.count() == 0) {
            MailServer mailserver = new MailServer();
            mailserver.setName(DEFAULT_MAILSERVER_NAME);
            mailserver.setHost("localhost");
            mailserver.setPort(DEFAULT_SMTP_PORT);
            mailserver.setCurrent(true);
            mailserver = save(mailserver);
            log.info("auto generated default mailserver created. " + mailserver.getHost() + ":" + mailserver.getPort());
        }
        
        velocityEngine = new VelocityEngine();
        log.info("mail service initialized");
    }

    @Override
    @Transactional
    public final void delete(MailServer mailserver) {
        mailserverDao.deleteById(mailserverDao.findById(mailserver.getId()).get().getId());
    }

    @Override
    public final List<MailServer> loadAll() {
        return Lists.newArrayList(mailserverDao.findAll());
    }

    @Override
    public final MailServer load(Long id) {
        return mailserverDao.findById(id).get();
    }

    @Override
    @Transactional
    public final MailServer save(MailServer mailserver) {
        return mailserverDao.save(mailserver);
    }

    @Override
    public MailServer loadMailServerByName(String name) {
        return mailserverDao.findByName(name);
    }

    @Override
    public final void sendSignupMail(final User user) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        throw new IllegalStateException("Noch zu implementieren");
//        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "org/bricket/b4/mail/service/signup.vm", model);
//        sendMail("webmaster@bricket.org", user.getEmail(), "Account activation change configs", text);
    }

    @Override
    public final void sendMail(final String from, final String to, final String subject, final String text) {
        sendMail(null, from, to, null, null, subject, text, true);
    }

    @Override
    public final void sendMail(final String mailserver, final String from, final String to, final String cc, final String bcc, final String subject,
            final String text, final boolean html) {
        MimeMessagePreparator preparator = new MailMimeMessagePreparator(bcc, subject, text, to, cc, from, html);

        try {
            if (mailserver != null) {
                getMailSender(loadMailServerByName(mailserver)).send(preparator);
            } else {
                getMailSender(mailserverDao.findByCurrent(true)).send(preparator);
            }
        } catch (MailException me) {
            throw new RuntimeException(me.getMessage(), me);
        }
    }

    private JavaMailSenderImpl getMailSender(MailServer mailserver) {
        if (mailserver == null) {
            throw new RuntimeException("No mailserver found.");
        }

        if (!mailSenders.containsKey(mailserver.getName())) {
            JavaMailSenderImpl mailsender = new JavaMailSenderImpl();
            mailsender.setHost(mailserver.getHost());
            if (mailserver.getPort() > 0) {
                mailsender.setPort(mailserver.getPort());
            }
            if (mailserver.getUsername() != null) {
                mailsender.setUsername(mailserver.getUsername());
            }
            if (mailserver.getPassword() != null) {
                mailsender.setUsername(mailserver.getPassword());
            }
            mailSenders.put(mailserver.getName(), mailsender);
        }
        return mailSenders.get(mailserver.getName());
    }

    public final boolean send(SimpleMailMessage mailMsg) {
        return send(null, mailMsg);
    }

    public final boolean send(String server, SimpleMailMessage mailMsg) {
        JavaMailSenderImpl ms = server != null ? getMailSender(mailserverDao.findByName(server)) : getMailSender(mailserverDao.findByCurrent(true));
        try {
            ms.send(mailMsg);
        } catch (Exception me) {
            log.error("error sending mail", me);
            throw new RuntimeException(me.getMessage(), me);
        }
        return true;
    }

    private static final class MailMimeMessagePreparator implements MimeMessagePreparator {
        private final String bcc;
        private final String subject;
        private final String text;
        private final String to;
        private final String cc;
        private final String from;
        private final boolean html;

        private MailMimeMessagePreparator(String bcc, String subject, String text, String to, String cc, String from, boolean html) {
            this.bcc = bcc;
            this.subject = subject;
            this.text = text;
            this.to = to;
            this.cc = cc;
            this.from = from;
            this.html = html;
        }

        @Override
        public void prepare(MimeMessage mimeMessage) throws MessagingException {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(from);
            message.setTo(to);
            if (cc != null) {
                message.setCc(cc);
            }
            if (bcc != null) {
                message.setBcc(bcc);
            }
            message.setSubject(subject);
            message.setText(text, html);
        }
    }

}
