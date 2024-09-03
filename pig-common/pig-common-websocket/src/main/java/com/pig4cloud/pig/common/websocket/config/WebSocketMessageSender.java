package com.pig4cloud.pig.common.websocket.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.pig4cloud.pig.common.core.util.SpringContextHolder;
import com.pig4cloud.pig.common.websocket.distribute.MessageDO;
import com.pig4cloud.pig.common.websocket.holder.WebSocketSessionHolder;
import com.pig4cloud.pig.common.websocket.message.JsonWebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hccake 2021/1/4
 * @version 1.0
 */
@Slf4j
public class WebSocketMessageSender {

	public static void broadcast(String message) {
		Collection<WebSocketSession> sessions = WebSocketSessionHolder.getSessions();
		for (WebSocketSession session : sessions) {
			send(session, message);
		}
	}

	public static boolean send(MessageDO dto, String message) {
//		WebSocketSession session = WebSocketSessionHolder.getSession(sessionKey);

		try {
			for (String token : dto.getTokens()) {
				Object obj = SpringContextHolder.getBean(dto.getPath());
				Class<?> cla = obj.getClass();
				Field field = cla.getDeclaredField("webSocketSet");
				field.setAccessible(true);
				ConcurrentHashMap<String, WebSocketSession> webSocketSet = (ConcurrentHashMap) field.get(obj);
				if (MapUtil.isEmpty(webSocketSet) || !webSocketSet.containsKey(token)) {
					log.info("[send] 当前 token：{} 对应 token 不在本服务中", token);
					return false;
				} else {
					Method method = cla.getMethod("sendString", String.class, String.class);
					method.invoke(obj, token, dto.getMessageText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void send(WebSocketSession session, JsonWebSocketMessage message) {
		send(session, JSONUtil.toJsonStr(message));
	}

	public static boolean send(WebSocketSession session, String message) {
		if (session == null) {
			log.error("[send] session 为 null");
			return false;
		}
		if (!session.isOpen()) {
			log.error("[send] session 已经关闭");
			return false;
		}
		try {
			session.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			log.error("[send] session({}) 发送消息({}) 异常", session, message, e);
			return false;
		}
		return true;
	}

}
