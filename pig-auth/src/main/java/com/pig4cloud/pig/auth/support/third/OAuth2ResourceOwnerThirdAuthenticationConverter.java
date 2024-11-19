package com.pig4cloud.pig.auth.support.third;

import com.pig4cloud.pig.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationConverter;
import com.pig4cloud.pig.common.core.constant.SecurityConstants;
import com.pig4cloud.pig.common.security.util.OAuth2EndpointUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

public class OAuth2ResourceOwnerThirdAuthenticationConverter
		extends OAuth2ResourceOwnerBaseAuthenticationConverter<OAuth2ResourceOwnerThirdAuthenticationToken> {

	/**
	 * 是否支持此convert
	 *
	 * @param grantType 授权类型
	 * @return
	 */
	@Override
	public boolean support(String grantType) {
		return SecurityConstants.THIRD.equals(grantType);
	}

	@Override
	public OAuth2ResourceOwnerThirdAuthenticationToken buildToken(Authentication clientPrincipal, Set requestedScopes,
																  Map additionalParameters) {
		return new OAuth2ResourceOwnerThirdAuthenticationToken(new AuthorizationGrantType(SecurityConstants.THIRD),
				clientPrincipal, requestedScopes, additionalParameters);
	}

	/**
	 * 校验扩展参数 密码模式密码必须不为空
	 *
	 * @param request 参数列表
	 */
	@Override
	public void checkParams(HttpServletRequest request) {
		MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
		String username = parameters.getFirst(SecurityConstants.USERNAME);
		if (!StringUtils.hasText(username) || parameters.get(SecurityConstants.USERNAME).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, SecurityConstants.THIRD,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
		String secretKey = parameters.getFirst(SecurityConstants.SECRET_KEY);
		if (!StringUtils.hasText(secretKey) || parameters.get(SecurityConstants.SECRET_KEY).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, SecurityConstants.THIRD,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
	}
}
