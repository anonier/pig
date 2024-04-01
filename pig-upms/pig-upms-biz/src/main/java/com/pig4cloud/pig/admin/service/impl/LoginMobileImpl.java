package com.pig4cloud.pig.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.pig4cloud.pig.admin.api.dto.LoginDto;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.feign.RemoteApplyTokenService;
import com.pig4cloud.pig.admin.api.vo.LoginVo;
import com.pig4cloud.pig.admin.enums.EnumAuth;
import com.pig4cloud.pig.admin.service.LoginHandler;
import com.pig4cloud.pig.admin.service.SysUserService;
import com.pig4cloud.pig.admin.util.Base64Util;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

@Slf4j
@Service("login_mobile")
@RefreshScope
@AllArgsConstructor
public class LoginMobileImpl implements LoginHandler {
	private static final String clientId = "gc";
	private static final String clientSecret = "gc";
	private final RemoteApplyTokenService remoteApplyTokenService;
	private final SysUserService sysUserService;

	@Override
	@SneakyThrows
	public LoginDto login(LoginVo vo) {
		SysUser sysUser = sysUserService.getOne(null, vo.getPhone());
		if (ObjectUtil.isNotEmpty(sysUser)) {
			log.error("未查询到该用户");
			throw new RuntimeException("未查询到该用户");
		}
		return getToken(sysUser.getUsername(), vo.getPassword());
	}

	/**
	 * 获取token
	 *
	 * @param username 账号
	 * @param password 密码
	 * @return {@link LoginDto} 登入结果
	 */
	private LoginDto getToken(String username, String password) {
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add(EnumAuth.GRANT_TYPE.getDesc(), EnumAuth.PASSWORD.getDesc());
		body.add(EnumAuth.USERNAME.getDesc(), username);
		body.add(EnumAuth.PASSWORD.getDesc(), password);
		String Authorization = Base64Util.getHttpBasic(clientId, clientSecret);
		JSONObject object = remoteApplyTokenService.applyToken(body, Authorization);
		LoginDto result = BeanUtil.copyProperties(object, LoginDto.class, "user_info");
		result.setUser_info(JSONObject.from(object.get("user_info")));
		return result;
	}
}
