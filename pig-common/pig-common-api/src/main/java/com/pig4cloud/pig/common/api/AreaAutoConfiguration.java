/*
 * Copyright (c) 2020 supertooth Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pig4cloud.pig.common.api;

import com.pig4cloud.pig.common.api.controller.AreaEndpoint;
import com.pig4cloud.pig.common.api.service.SysAreaService;
import com.pig4cloud.pig.common.api.service.impl.SysAreaServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 地区bean注入
 */
@Configuration(proxyBeanMethods = false)
public class AreaAutoConfiguration implements WebMvcConfigurer {

	@Bean
	@Primary
	@ConditionalOnMissingBean(SysAreaService.class)
	public SysAreaService sysAreaService() {
		return new SysAreaServiceImpl();
	}

	@Bean
	@ConditionalOnMissingBean
	public AreaEndpoint areaEndpoint(SysAreaService sysAreaService) {
		return new AreaEndpoint(sysAreaService);
	}
}
