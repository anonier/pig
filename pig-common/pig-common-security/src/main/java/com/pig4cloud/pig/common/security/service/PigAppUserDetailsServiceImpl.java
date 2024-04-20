/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
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

package com.pig4cloud.pig.common.security.service;

import com.pig4cloud.pig.admin.api.dto.UserDTO;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.feign.RemoteUserService;
import com.pig4cloud.pig.common.core.constant.CacheConstants;
import com.pig4cloud.pig.common.core.constant.SecurityConstants;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Objects;

import static com.pig4cloud.pig.common.core.constant.CacheConstants.CLIENT_DETAILS_KEY;

/**
 * 用户详细信息
 *
 * @author lengleng hccake
 */
@Slf4j
@RequiredArgsConstructor
public class PigAppUserDetailsServiceImpl implements PigUserDetailsService {

	private final RemoteUserService remoteUserService;
	private final CacheManager cacheManager;

	/**
	 * 手机号登录
	 * @param phone 手机号
	 * @return
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String phone) {
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null && cache.get(phone) != null) {
			return (PigUser) cache.get(phone).get();
		}

		UserDTO userDTO = new UserDTO();
		userDTO.setPhone(phone);
		R<UserInfo> result = remoteUserService.info(userDTO, SecurityConstants.FROM_IN);

		UserDetails userDetails = getUserDetails(result);
		if (cache != null) {
			cache.put(phone, userDetails);
		}
		return userDetails;
	}

	/**
	 * 手机号登入
	 */
	@Override
	public UserDetails loadUserByUser(SysUser sysUser) {
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null && cache.get(sysUser.getPhone()) != null) {
			return (PigUser) cache.get(sysUser.getPhone()).get();
		}

		UserDTO userDTO = new UserDTO();
		userDTO.setPhone(sysUser.getPhone());
		R<UserInfo> result = remoteUserService.info(userDTO, SecurityConstants.FROM_IN);

		UserDetails userDetails = getUserDetails(result);
		if (cache != null) {
			cache.put(sysUser.getPhone(), userDetails);
		}
		return userDetails;
	}

	/**
	 * 是否支持此客户端校验
	 * @param clientId 目标客户端
	 * @return true/false
	 */
	@Override
	public boolean support(String clientId, String grantType) {
		Cache cache = cacheManager.getCache(CLIENT_DETAILS_KEY);
		assert cache != null;
		RegisteredClient registeredClient = (RegisteredClient) (Objects.requireNonNull(cache.get(clientId)).get());
		assert registeredClient != null;
		return registeredClient.getAuthorizationGrantTypes().stream().anyMatch(a -> a.getValue().equals(grantType) && grantType.equals("APP"));
	}

}
