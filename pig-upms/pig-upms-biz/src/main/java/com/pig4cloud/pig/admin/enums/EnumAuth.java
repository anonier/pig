package com.pig4cloud.pig.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 鉴权枚举
 */
@Getter
@AllArgsConstructor
public enum EnumAuth {

	USERNAME("username"),

	USERID("userId"),

	PASSWORD("password"),

	SECRET_KEY("secretKey"),

	FACE("face"),

	CARD("card"),

	THIRD("third"),

	MOBILE("mobile"),

	CODE("code"),

	LOGIN_MOBILE("login_mobile"),

	LOGIN_SMS("login_sms"),

	LOGIN_USERNAME("login_username"),

	LOGIN_AUTH("login_auth"),

	LOGIN_FACE("login_face"),

	LOGIN_CARD("login_card"),

	REGISTER_MOBILE("register_mobile"),

	REGISTER_SMS("register_sms"),

	REGISTER_USERNAME("register_username"),

	ACCESS_TOKEN("access_token"),

	REFRESH_TOKEN("refresh_token"),

	GRANT_TYPE("grant_type");

	/**
	 * 字段
	 */
	private final String desc;

}
