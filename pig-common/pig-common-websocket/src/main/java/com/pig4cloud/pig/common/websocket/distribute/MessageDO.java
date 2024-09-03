package com.pig4cloud.pig.common.websocket.distribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Hccake 2021/1/12
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MessageDO {

	/**
	 * ws路径
	 */
	private String path;

	/**
	 * 是否广播
	 */
	private Boolean needBroadcast;

	/**
	 * sessionKeys
	 */
	private List<Object> sessionKeys;

	/**
	 * tokenKeys
	 */
	private List<String> tokens;

	/**
	 * 需要发送的消息文本
	 */
	private String messageText;

	/**
	 * 构建需要广播的message
	 *
	 * @author lingting 2021-03-25 17:28
	 */
	public static MessageDO broadcastMessage(String text) {
		return new MessageDO().setMessageText(text).setNeedBroadcast(true);
	}

}
