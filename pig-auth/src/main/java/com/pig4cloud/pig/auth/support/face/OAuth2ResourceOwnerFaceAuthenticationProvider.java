package com.pig4cloud.pig.auth.support.face;

import com.pig4cloud.pig.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationProvider;
import com.pig4cloud.pig.common.core.constant.SecurityConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Map;

/**
 * 人脸登录的核心处理
 */
public class OAuth2ResourceOwnerFaceAuthenticationProvider
		extends OAuth2ResourceOwnerBaseAuthenticationProvider<OAuth2ResourceOwnerFaceAuthenticationToken> {

	private static final Logger LOGGER = LogManager.getLogger(OAuth2ResourceOwnerFaceAuthenticationProvider.class);

	/**
	 * Constructs an {@code OAuth2AuthorizationCodeAuthenticationProvider} using the
	 * provided parameters.
	 *
	 * @param authenticationManager
	 * @param authorizationService  the authorization service
	 * @param tokenGenerator        the token generator
	 * @since 0.2.3
	 */
	public OAuth2ResourceOwnerFaceAuthenticationProvider(AuthenticationManager authenticationManager,
														 OAuth2AuthorizationService authorizationService,
														 OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
		super(authenticationManager, authorizationService, tokenGenerator);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		boolean supports = OAuth2ResourceOwnerFaceAuthenticationToken.class.isAssignableFrom(authentication);
		LOGGER.debug("supports authentication=" + authentication + " returning " + supports);
		return supports;
	}

	@Override
	public void checkClient(RegisteredClient registeredClient) {
		assert registeredClient != null;
		if (!registeredClient.getAuthorizationGrantTypes()
				.contains(new AuthorizationGrantType(SecurityConstants.FACE))) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
		}
	}

	@Override
	public UsernamePasswordAuthenticationToken buildToken(Map<String, Object> reqParameters) {
		String userId = (String) reqParameters.get(SecurityConstants.FACE_PARAMETER_NAME);
		return new UsernamePasswordAuthenticationToken(userId, null);
	}

}
