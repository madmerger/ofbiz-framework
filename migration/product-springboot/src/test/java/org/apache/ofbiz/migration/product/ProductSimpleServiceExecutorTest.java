/*
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
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.simple.ProductSimpleServiceExecutor;
import org.apache.ofbiz.migration.product.simple.SimpleMethodFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
@Import(ProductSimpleServiceExecutorTest.UnportedProbeConfiguration.class)
class ProductSimpleServiceExecutorTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductSimpleServiceExecutor executor;

    @Autowired
    private ServiceHandlerRegistry registry;

    @Autowired
    @Qualifier("timestampFormatProbe")
    private SimpleMethodFile timestampFormatProbe;

    @ParameterizedTest
    @ValueSource(strings = {
        "2020-01-02 03:04:05",
        "2020-01-02",
        "2020-01-02T03:04:05",
        "2020-01-02T03:04:05Z"
    })
    void ac01_convertsOFBizAndIsoTimestampFormats(String timestamp) throws Exception {
        var metadata = ProductServiceMetadata.simpleServices().get("countProductInventoryOnHand");
        registry.registerHandler("product", "timestampFormatProbe", "FACILITY_UPDATE",
                input -> executor.execute("timestampFormatProbe", metadata, timestampFormatProbe, input));

        mockMvc.perform(post("/api/product/services/countProductInventoryOnHand")
                        .with(user("facility").authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_UPDATE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":"TEST_PRODUCT","facilityId":"TEST_FACILITY",
                                 "inventoryCountDate":"%s"}
                                """.formatted(timestamp)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/product/services/timestampFormatProbe")
                        .with(user("facility").authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_UPDATE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":"TEST_PRODUCT","facilityId":"TEST_FACILITY",
                                 "inventoryCountDate":"%s"}
                                """.formatted(timestamp)))
                .andExpect(status().isNotImplemented());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class UnportedProbeConfiguration {
        @Bean
        SimpleMethodFile timestampFormatProbe(ProductUnportedPort unportedPort) {
            return (serviceName, methodName, input) -> {
                if (!(input.get("inventoryCountDate") instanceof Timestamp)) {
                    throw new MigrationException(HttpStatus.BAD_REQUEST, "Timestamp was not converted");
                }
                return unportedPort.execute(serviceName);
            };
        }
    }
}
