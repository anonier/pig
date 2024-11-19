package com.pig4cloud.pig.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pig4cloud.pig.admin.api.dto.LoginDto;
import com.pig4cloud.pig.admin.api.entity.SysTenant;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.feign.RemoteApplyTokenService;
import com.pig4cloud.pig.admin.api.vo.LoginVo;
import com.pig4cloud.pig.admin.enums.EnumAuth;
import com.pig4cloud.pig.admin.mapper.SysTenantMapper;
import com.pig4cloud.pig.admin.mapper.notenant.NoTenantSysUserMapper;
import com.pig4cloud.pig.admin.service.LoginHandler;
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
	private static final String clientId = "starlab";
	private static final String clientSecret = "starlab";
	private final RemoteApplyTokenService remoteApplyTokenService;
	private final NoTenantSysUserMapper sysUserMapper;
	private final SysTenantMapper sysTenantMapper;

	@Override
	@SneakyThrows
	public LoginDto login(LoginVo vo) {
		SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
				.eq(SysUser::getPhone, vo.getPhone())
				.eq(SysUser::getDelFlag, 0));
		if (ObjUtil.isEmpty(sysUser)) {
			log.error("未查询到该用户");
			throw new RuntimeException("未查询到该用户");
		}
		if ("9".equals(sysUser.getLockFlag())) {
			log.error("该用户被停用");
			throw new RuntimeException("该用户被停用");
		}
		SysTenant sysTenant = sysTenantMapper.selectById(sysUser.getTenantId());
		if (sysTenant.getStatus() == 0) {
			log.error("机构已关闭,用户无法登入");
			throw new RuntimeException("机构已关闭,用户无法登入");
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
