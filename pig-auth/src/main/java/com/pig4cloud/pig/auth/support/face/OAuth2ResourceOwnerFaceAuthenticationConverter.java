package com.pig4cloud.pig.auth.support.face;

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
public class OAuth2ResourceOwnerFaceAuthenticationConverter
		extends OAuth2ResourceOwnerBaseAuthenticationConverter<OAuth2ResourceOwnerFaceAuthenticationToken> {

	/**
	 * 是否支持此convert
	 * @param grantType 授权类型
	 * @return
	 */
	@Override
	public boolean support(String grantType) {
		return SecurityConstants.FACE.equals(grantType);
	}

	@Override
	public OAuth2ResourceOwnerFaceAuthenticationToken buildToken(Authentication clientPrincipal, Set requestedScopes,
																 Map additionalParameters) {
		return new OAuth2ResourceOwnerFaceAuthenticationToken(new AuthorizationGrantType(SecurityConstants.FACE),
				clientPrincipal, requestedScopes, additionalParameters);
	}

	/**
	 * 校验扩展参数 密码模式密码必须不为空
	 * @param request 参数列表
	 */
	@Override
	public void checkParams(HttpServletRequest request) {
		MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
		String useId = parameters.getFirst(SecurityConstants.FACE_PARAMETER_NAME);
		if (!StringUtils.hasText(useId) || parameters.get(SecurityConstants.FACE_PARAMETER_NAME).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, SecurityConstants.FACE_PARAMETER_NAME,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
	}

}
