/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.deployer.spi.cloudfoundry;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;

/**
 * @author Soby Chacko
 */
public class CloudFoundryAppDeployerEnhancements implements AppDeployerEnhancements {

	private final CloudFoundryDeployerProperties properties;

	private final ConcurrentMap<String, String> enhancedAppInfo = new ConcurrentHashMap<>();

	public CloudFoundryAppDeployerEnhancements(CloudFoundryDeployerProperties properties) {
		this.properties = properties;
	}

	@Override
	public String getUniquelyPrefixedApp(AppDeploymentRequest request) {
		String key = String.format("%s-%s", properties.getUsername(), deploymentId(request));
		if (enhancedAppInfo.containsKey(key)) {
			return enhancedAppInfo.get(key);
		}

		String uniquePrefixedAppName = String.format("%s-%s-%s", properties.getUsername(),
				RandomStringUtils.randomAlphanumeric(16), deploymentId(request));

		enhancedAppInfo.put(key, uniquePrefixedAppName);

		return uniquePrefixedAppName;
	}

	private String deploymentId(AppDeploymentRequest request) {
		return Optional.ofNullable(request.getEnvironmentProperties().get(AppDeployer.GROUP_PROPERTY_KEY))
				.map(groupName -> String.format("%s-", groupName))
				.orElse("") + request.getDefinition().getName();
	}
}
