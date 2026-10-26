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

import jakarta.persistence.EntityManager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
@Profile({"local", "test", "integration"})
public class SeedDataLoader implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(SeedDataLoader.class);
    private final EntityManager entityManager;
    private final EntityRegistry entities;

    public SeedDataLoader(EntityManager entityManager, EntityRegistry entities) {
        this.entityManager = entityManager;
        this.entities = entities;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath*:ofbiz-seed/*.xml");
        List<Resource> ordered = new ArrayList<>(List.of(resources));
        ordered.sort((left, right) -> Integer.compare(seedOrder(left), seedOrder(right)));
        for (Resource resource : ordered) {
            load(resource);
        }
        Resource demo = new PathMatchingResourcePatternResolver()
                .getResource("classpath:ofbiz-demo/AccountingDemoData.xml");
        if (demo.exists()) {
            load(demo);
        }
    }

    private static int seedOrder(Resource resource) {
        return switch (resource.getFilename()) {
            case "CommonExtSeedData.xml" -> 0;
            case "PartySeedData.xml" -> 1;
            case "ProductSeedData.xml" -> 2;
            case "OrderSeedData.xml" -> 3;
            case "AccountingSeedData.xml" -> 4;
            default -> 5;
        };
    }

    private void load(Resource resource) throws Exception {
        try (InputStream input = resource.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            NodeList records = factory.newDocumentBuilder().parse(input)
                    .getDocumentElement().getChildNodes();
            for (int index = 0; index < records.getLength(); index++) {
                Node node = records.item(index);
                if (node instanceof Element record) {
                    loadRecord(record, resource.getFilename());
                }
            }
        }
    }

    private void loadRecord(Element record, String filename) {
        String entityName = record.getTagName();
        try {
            Class<?> type = entities.entityClass(entityName);
            Object entity = entities.newEntity(type);
            NamedNodeMap attributes = record.getAttributes();
            for (int index = 0; index < attributes.getLength(); index++) {
                Node attribute = attributes.item(index);
                try {
                    entities.set(entity, attribute.getNodeName(), attribute.getNodeValue());
                } catch (MigrationException exception) {
                    LOG.warn("Skipping unknown field {}.{} in {}", entityName, attribute.getNodeName(), filename);
                }
            }
            if (entities.findByPrimaryKey(type, entities.toMap(entity)) != null) {
                return;
            }
            entityManager.persist(entity);
        } catch (MigrationException exception) {
            LOG.warn("Skipping unknown seed entity {} in {}", entityName, filename);
        }
    }
}
