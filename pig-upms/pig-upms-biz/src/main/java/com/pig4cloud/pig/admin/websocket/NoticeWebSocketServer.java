package com.pig4cloud.pig.admin.websocket;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NoticeWebSocketServer implements WebSocketHandler {

	private static final ConcurrentHashMap<String, WebSocketSession> webSocketSet = new ConcurrentHashMap<>();

	/**
	 * 建立连接后触发的回调
	 */
	@Override
	public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
		webSocketSet.put(Objects.requireNonNull(session.getAcceptedProtocol()), session);
		session.sendMessage(new TextMessage("saoliwa"));
		log.info("有新连接加入！当前在线人数为:{}", webSocketSet.size());
	}

	/**
	 * 收到消息时触发的回调
	 */
	@Override
	public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) {
		log.info("收到新的消息！内容:{}", message.getPayload());
	}

	/**
	 * 发生异常，关闭连接
	 */
	@Override
	public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) {
		webSocketSet.remove(session);
		log.info("websocket发生异常！", exception);
	}

	/**
	 * 关闭连接
	 */
	@Override
	public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) {
		try {
			webSocketSet.remove(Objects.requireNonNull(session.getAcceptedProtocol()));
		} finally {
			if (session.isOpen()) {
				try {
					session.close();
				} catch (IOException e) {
					log.error("关闭ws连接");
				}
			}
		}
		log.debug("webSocket关闭连接，状态：{}，当前连接数：{}", closeStatus, webSocketSet.size());
	}

	/**
	 * 是否支持消息分片
	 */
	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * 发送消息
	 */
	public static void sendString(String token, String message) throws IOException {
		for (ConcurrentHashMap.Entry<String, WebSocketSession> entry : webSocketSet.entrySet()) {
			if (entry.getKey().equals(token) && entry.getValue().isOpen()) {
				entry.getValue().sendMessage(new TextMessage(message));
			}

		}
		log.debug("webSocket发送消息，内容：{}，当前连接数：{}", message, webSocketSet.size());
	}
}