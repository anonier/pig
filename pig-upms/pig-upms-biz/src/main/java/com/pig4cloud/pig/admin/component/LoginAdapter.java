package com.pig4cloud.pig.admin.component;

import com.pig4cloud.pig.admin.enums.EnumAuth;
import com.pig4cloud.pig.admin.service.LoginHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class LoginAdapter {

	private Map<String, LoginHandler> adaptMap;

	public LoginHandler getInstance(String type) {
		if (type == null) {
			type = EnumAuth.LOGIN_MOBILE.getDesc();
		}
		return adaptMap.getOrDefault(type, null);
	}
}
