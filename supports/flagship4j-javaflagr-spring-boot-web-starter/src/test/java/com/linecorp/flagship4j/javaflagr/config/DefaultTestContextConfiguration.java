/*
 * Copyright 2024 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.flagship4j.javaflagr.config;

import com.linecorp.flagship4j.javaflagr.fixture.TestContextFixture;
import com.linecorp.flagship4j.javaflagr.model.Context;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class DefaultTestContextConfiguration extends AbstractTestContextConfiguration {

    @Override
    public Context testContext() {
        return TestContextFixture.mockTestContext();
    }

}
