package com.pig4cloud.pig.admin.component;

import com.pig4cloud.pig.admin.enums.EnumAuth;
import com.pig4cloud.pig.admin.service.RegisterHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class RegisterAdapter {

	private Map<String, RegisterHandler> adaptMap;

	public RegisterHandler getInstance(String type) {
		if (type == null) {
			type = EnumAuth.REGISTER_MOBILE.getDesc();
		}
		return adaptMap.getOrDefault(type, null);
	}
}
