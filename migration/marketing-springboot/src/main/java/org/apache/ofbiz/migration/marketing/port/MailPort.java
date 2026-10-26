package org.apache.ofbiz.migration.marketing.port;

import java.util.Map;

/**
 * Replaces the OFBiz {@code sendMailFromScreen} service used by contact list notifications.
 */
public interface MailPort {

    void sendMailFromScreen(MailRequest request);

    record MailRequest(
            String emailType,
            String sendTo,
            String sendFrom,
            String subject,
            String bodyScreenUri,
            String webSiteId,
            String communicationEventId,
            Map<String, Object> bodyParameters) { }
}
