package com.pig4cloud.pig.admin.api.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 注册入参对象
 */
@Data
public class RegisterVo implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 用户密码
	 */
	String password;

	/**
	 * 注册类型 username,mobile,sms
	 */
	String type;

	/**
	 * 手机
	 */
	String mobile;

	/**
	 * 账号
	 */
	String username;
}
