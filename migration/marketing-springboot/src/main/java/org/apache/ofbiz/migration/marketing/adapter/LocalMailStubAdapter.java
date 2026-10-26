package org.apache.ofbiz.migration.marketing.adapter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.ofbiz.migration.marketing.port.MailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stub mail adapter: records requests instead of rendering screens and sending SMTP mail.
 */
@Component
public class LocalMailStubAdapter implements MailPort {

    private static final Logger LOG = LoggerFactory.getLogger(LocalMailStubAdapter.class);
    private final List<MailRequest> sent = new CopyOnWriteArrayList<>();

    @Override
    public void sendMailFromScreen(MailRequest request) {
        sent.add(request);
        LOG.info("Stub mail {} queued for screen {}", request.emailType(), request.bodyScreenUri());
    }

    public List<MailRequest> sentMails() {
        return List.copyOf(sent);
    }

    public void clear() {
        sent.clear();
    }
}
