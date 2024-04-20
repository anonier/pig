package com.pig4cloud.pig.auth.support.card;

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

/**
 * @author lengleng
 * @date 2022-05-31
 *
 * 短信登录转换器
 */
public class OAuth2ResourceOwnerCardAuthenticationConverter
		extends OAuth2ResourceOwnerBaseAuthenticationConverter<OAuth2ResourceOwnerCardAuthenticationToken> {

	/**
	 * 是否支持此convert
	 * @param grantType 授权类型
	 * @return
	 */
	@Override
	public boolean support(String grantType) {
		return SecurityConstants.CARD.equals(grantType);
	}

	@Override
	public OAuth2ResourceOwnerCardAuthenticationToken buildToken(Authentication clientPrincipal, Set requestedScopes,
                                                                 Map additionalParameters) {
		return new OAuth2ResourceOwnerCardAuthenticationToken(new AuthorizationGrantType(SecurityConstants.CARD),
				clientPrincipal, requestedScopes, additionalParameters);
	}

	/**
	 * 校验扩展参数 密码模式密码必须不为空
	 * @param request 参数列表
	 */
	@Override
	public void checkParams(HttpServletRequest request) {
		MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
		String card = parameters.getFirst(SecurityConstants.CARD);
		if (!StringUtils.hasText(card) || parameters.get(SecurityConstants.CARD).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, SecurityConstants.CARD,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
	}

}
