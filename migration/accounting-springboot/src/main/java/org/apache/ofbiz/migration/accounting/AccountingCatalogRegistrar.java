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
package org.apache.ofbiz.migration.accounting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.common.generated.ServiceCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class AccountingCatalogRegistrar {
    private static final Logger LOG = LoggerFactory.getLogger(AccountingCatalogRegistrar.class);
    private static final int EXPECTED_ENTITY_AUTO_COUNT = 386;
    private final ServiceHandlerRegistry registry;

    public AccountingCatalogRegistrar(ServiceHandlerRegistry registry) {
        this.registry = registry;
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void registerCatalog() {
        Set<String> entityAutoNames = parseEntityAutoNames();
        if (entityAutoNames.size() != EXPECTED_ENTITY_AUTO_COUNT) {
            throw new IllegalStateException("Expected " + EXPECTED_ENTITY_AUTO_COUNT
                    + " Accounting entity-auto services but found " + entityAutoNames.size());
        }
        Map<String, ServiceHandlerRegistry.ServiceDefinition> catalog = new LinkedHashMap<>();
        for (String name : entityAutoNames) {
            ServiceCatalog.ServiceDefinition source = ServiceCatalog.find(name)
                    .orElseThrow(() -> new IllegalStateException("Accounting service missing from generated catalog: " + name));
            List<ServiceHandlerRegistry.ParamDefinition> params = source.params().stream()
                    .map(param -> new ServiceHandlerRegistry.ParamDefinition(
                            param.name(), param.mode(), param.optional(), param.type()))
                    .toList();
            catalog.put(name, new ServiceHandlerRegistry.ServiceDefinition(
                    source.name(), source.entity(), source.invoke(), source.permissionMainAction(), params));
        }
        registry.registerCatalog("accounting", catalog);
        LOG.info("Registered {} Accounting entity-auto services", catalog.size());
    }

    public Set<String> parseEntityAutoNames() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:ofbiz-servicedef/accounting/services_*.xml");
            Set<String> names = new LinkedHashSet<>();
            for (Resource resource : resources) {
                try (InputStream input = resource.getInputStream()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
                    NodeList services = factory.newDocumentBuilder().parse(input).getElementsByTagName("service");
                    for (int index = 0; index < services.getLength(); index++) {
                        Node node = services.item(index);
                        if (node instanceof Element service && "entity-auto".equals(service.getAttribute("engine"))) {
                            names.add(service.getAttribute("name"));
                        }
                    }
                }
            }
            return Set.copyOf(names);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to parse Accounting service definitions", exception);
        }
    }
}
