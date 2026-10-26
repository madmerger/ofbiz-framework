/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.marketing.adapter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.ofbiz.migration.marketing.port.MailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stub mail adapter: records the latest requests instead of rendering screens and sending SMTP mail.
 */
@Component
public class LocalMailStubAdapter implements MailPort {

    private static final Logger LOG = LoggerFactory.getLogger(LocalMailStubAdapter.class);
    static final int MAX_RECORDED = 100;
    private final Deque<MailRequest> sent = new ArrayDeque<>();

    @Override
    public synchronized void sendMailFromScreen(MailRequest request) {
        if (sent.size() == MAX_RECORDED) {
            sent.removeFirst();
        }
        sent.addLast(request);
        LOG.info("Stub mail {} queued for screen {}", request.emailType(), request.bodyScreenUri());
    }

    public synchronized List<MailRequest> sentMails() {
        return List.copyOf(sent);
    }

    public synchronized void clear() {
        sent.clear();
    }
}
