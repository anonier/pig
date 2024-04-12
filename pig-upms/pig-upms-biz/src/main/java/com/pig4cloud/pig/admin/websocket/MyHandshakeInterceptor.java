package com.pig4cloud.pig.admin.websocket;

import com.pig4cloud.pig.common.core.constant.CacheConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * webSocket握手拦截器
 *
 * @since 2022年9月16日16:57:52
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MyHandshakeInterceptor implements HandshakeInterceptor {

	private final CacheManager cacheManager;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) {
		log.info("HandshakeInterceptor beforeHandshake start...");
		if (request instanceof ServletServerHttpRequest) {
			Cache cache = cacheManager.getCache(CacheConstants.PROJECT_OAUTH_ACCESS);
			if (!request.getHeaders().containsKey("Sec-Websocket-Protocol")) {
				log.error("非法连接,关闭ws连接");
				return false;
			}
			Object token = request.getHeaders().getFirst("Sec-Websocket-Protocol");
			if (cache.get(token) == null) {
				log.error("{}认证未通过,关闭ws连接", token);
				return false;
			}
			log.info("{}ws连接成功", ((OAuth2Authorization) (cache.get(token).get())).getPrincipalName());
		}
		log.info("HandshakeInterceptor beforeHandshake end...");
		return true;
	}

	/**
	 * 初次握手访问后，将前端自定义协议头Sec-WebSocket-Protocol原封不动返回回去，否则会报错
	 *
	 * @param request
	 * @param serverHttpResponse
	 * @param webSocketHandler
	 * @param e
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
		log.info("HandshakeInterceptor afterHandshake start...");
		HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse httpResponse = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
		if (StringUtils.isNotEmpty(httpRequest.getHeader("Sec-WebSocket-Protocol"))) {
			httpResponse.addHeader("Sec-WebSocket-Protocol", httpRequest.getHeader("Sec-WebSocket-Protocol"));
		}
		log.info("HandshakeInterceptor afterHandshake end...");
	}
}
