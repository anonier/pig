package com.pig4cloud.pig.common.websocket.custom;

import com.pig4cloud.pig.common.security.service.PigUser;
import com.pig4cloud.pig.common.websocket.holder.SessionKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author lengleng
 * @date 2021/10/4 websocket session 标识生成规则
 */
@Configuration
@RequiredArgsConstructor
public class PigxSessionKeyGenerator implements SessionKeyGenerator {

	private final ServiceInstance instance;

	/**
	 * 获取当前session的唯一标识
	 * @param webSocketSession 当前session
	 * @return session唯一标识
	 */
	@Override
	public Object sessionKey(WebSocketSession webSocketSession) {

		Object obj = webSocketSession.getAttributes().get("USER_KEY_ATTR_NAME");

		if (obj instanceof PigUser) {
			PigUser user = (PigUser) obj;
			// IP:port:userId 作为唯一区分
			return String.format("%s:%s:%s", instance.getHost(), instance.getPort(), user.getId());
		}

		return null;
	}

}
