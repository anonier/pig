package com.pig4cloud.pig.admin.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

	private final WarnSocketServer warnSocketServer;
	private final NoticeWebSocketServer noticeWebSocketServer;
	private final MyHandshakeInterceptor myHandshakeInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// 此处定义webSocket的连接地址以及允许跨域
		registry.addHandler(warnSocketServer, "/ws/warn").addInterceptors(myHandshakeInterceptor).setAllowedOrigins("*");

		registry.addHandler(noticeWebSocketServer, "/ws/notice").addInterceptors(myHandshakeInterceptor).setAllowedOrigins("*");
		// 同上，同时开启了Sock JS的支持，目的为了支持IE8及以下浏览器
		registry.addHandler(warnSocketServer, "/sockjs/websocket").addInterceptors(myHandshakeInterceptor).setAllowedOrigins("*").withSockJS();
	}
}
