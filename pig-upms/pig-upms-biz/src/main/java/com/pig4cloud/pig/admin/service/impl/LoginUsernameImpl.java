package com.pig4cloud.pig.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pig4cloud.pig.admin.api.dto.LoginDto;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.feign.RemoteApplyTokenService;
import com.pig4cloud.pig.admin.api.vo.LoginVo;
import com.pig4cloud.pig.admin.enums.EnumAuth;
import com.pig4cloud.pig.admin.service.LoginHandler;
import com.pig4cloud.pig.admin.service.SysUserService;
import com.pig4cloud.pig.admin.util.Base64Util;
import com.pig4cloud.pig.common.core.constant.SecurityConstants;
import com.pig4cloud.pig.common.core.util.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

@Slf4j
@Service("login_username")
@RefreshScope
@AllArgsConstructor
public class LoginUsernameImpl implements LoginHandler {
	private static final String clientId = "starlab";
	private static final String clientSecret = "starlab";
	private final RemoteApplyTokenService remoteApplyTokenService;
	private final SysUserService sysUserService;

	@Override
	public LoginDto login(LoginVo vo) {
		SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
				.eq(SysUser::getUsername, vo.getUsername())
				.eq(SysUser::getDelFlag, 0)
				.eq(SysUser::getLockFlag, 0));
		if (ObjectUtil.isEmpty(sysUser)) {
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
