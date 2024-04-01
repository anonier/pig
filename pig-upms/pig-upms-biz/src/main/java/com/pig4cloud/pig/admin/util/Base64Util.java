package com.pig4cloud.pig.admin.util;

import com.pig4cloud.pig.admin.enums.EnumCookie;
import com.pig4cloud.pig.admin.enums.EnumPunctuation;
import org.bouncycastle.util.encoders.Base64;

/**
 * Base64操作工具
 */
public class Base64Util {

	public static String getHttpBasic(String clientId, String clientSecret) {
		String string = clientId + EnumPunctuation.COLON.getDesc() + clientSecret;
		String encode = Base64.toBase64String(string.getBytes());
		return EnumCookie.BASIC.getDesc() + encode;
	}
}
