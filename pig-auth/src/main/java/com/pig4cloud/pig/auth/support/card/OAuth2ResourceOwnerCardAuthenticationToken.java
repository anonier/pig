package com.pig4cloud.pig.auth.support.card;

import com.pig4cloud.pig.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * @author lengleng
 * @description 短信登录token信息
 */
public class OAuth2ResourceOwnerCardAuthenticationToken extends OAuth2ResourceOwnerBaseAuthenticationToken {

	public OAuth2ResourceOwnerCardAuthenticationToken(AuthorizationGrantType authorizationGrantType,
													  Authentication clientPrincipal, Set<String> scopes, Map<String, Object> additionalParameters) {
		super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
	}

}
