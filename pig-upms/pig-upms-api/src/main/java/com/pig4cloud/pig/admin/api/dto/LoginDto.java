package com.pig4cloud.pig.admin.api.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证服务申请令牌实体类
 */
@Data
@NoArgsConstructor
public class LoginDto {

	/**
	 * 身份令牌
	 */
	private String access_token;

	/**
	 * 刷新令牌
	 */
	private String refresh_token;

	/**
	 * 用户id
	 */
	private String user_id;

	/**
	 * 过期时间
	 */
	private String exp;

	/**
	 * 用户名称
	 */
	private String sub;

	/**
	 * 用户信息
	 */
	private JSONObject user_info;

	/**
	 * 用户名称
	 */
	private String username;

	/**
	 * token_type
	 */
	private String token_type;

	/**
	 * iss
	 */
	private String iss;

	/**
	 * license
	 */
	private String license;

	/**
	 * nbf
	 */
	private String nbf;

	/**
	 * expires_in
	 */
	private String expires_in;

	/**
	 * iat
	 */
	private String iat;

	/**
	 * iat
	 */
	private String jti;
}
