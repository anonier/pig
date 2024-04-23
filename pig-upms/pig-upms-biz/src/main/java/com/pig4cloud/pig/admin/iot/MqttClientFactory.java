package com.pig4cloud.pig.admin.iot;

import cn.hutool.core.util.RandomUtil;
import com.pig4cloud.pig.admin.iot.util.ConnectionOptionWrapper;
import jakarta.annotation.Resource;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class MqttClientFactory {

	@Resource
	private ThreadPoolTaskExecutor iotExecutor;

	@Value("${mqtt.instanceId}")
	private String instanceId;

	@Value("${mqtt.endPoint}")
	private String endPoint;

	@Value("${mqtt.accessKey}")
	private String accessKey;

	@Value("${mqtt.secretKey}")
	private String secretKey;

	@Bean("myMqttClient")
	public MqttClient connect() throws MqttException, NoSuchAlgorithmException, InvalidKeyException {
		/**
		 * MQ4IOT clientId，由业务系统分配，需要保证每个 tcp 连接都不一样，保证全局唯一，如果不同的客户端对象（tcp 连接）使用了相同的 clientId 会导致连接异常断开。
		 * clientId 由两部分组成，格式为 GroupID@@@DeviceId，其中 groupId 在 MQ4IOT 控制台申请，DeviceId 由业务方自己设置，clientId 总长度不得超过64个字符。
		 */
		String clientId = "GID_cabinet@@@server_" + RandomUtil.randomString(6);
		/**
		 * MQ4IOT 消息的一级 topic，需要在控制台申请才能使用。
		 * 如果使用了没有申请或者没有被授权的 topic 会导致鉴权失败，服务端会断开客户端连接。
		 */
		final String[] topic = {"cabinet"};
		/**
		 * MQ4IOT支持子级 topic，用来做自定义的过滤，此处为示意，可以填写任何字符串，具体参考https://help.aliyun.com/document_detail/42420.html?spm=a2c4g.11186623.6.544.1ea529cfAO5zV3
		 * 需要注意的是，完整的 topic 参考 https://help.aliyun.com/document_detail/63620.html?spm=a2c4g.11186623.6.554.21a37f05ynxokW。
		 */
//        final String mq4IotTopic = parentTopic;
		/**
		 * QoS参数代表传输质量，可选0，1，2，根据实际需求合理设置，具体参考 https://help.aliyun.com/document_detail/42420.html?spm=a2c4g.11186623.6.544.1ea529cfAO5zV3
		 */
		final int qosLevel = 1;
		ConnectionOptionWrapper connectionOptionWrapper = new ConnectionOptionWrapper(instanceId, accessKey, secretKey, clientId);
		final MemoryPersistence memoryPersistence = new MemoryPersistence();
		/**
		 * 客户端使用的协议和端口必须匹配，具体参考文档 https://help.aliyun.com/document_detail/44866.html?spm=a2c4g.11186623.6.552.25302386RcuYFB
		 * 如果是 SSL 加密则设置ssl://endpoint:8883
		 */
		final MqttClient mqttClient = new MqttClient("tcp://" + endPoint + ":1883", clientId, memoryPersistence);
		/**
		 * 客户端设置好发送超时时间，防止无限阻塞
		 */
		mqttClient.setTimeToWait(5000);
		mqttClient.setCallback(new IMqttCallback(mqttClient, qosLevel, topic, iotExecutor));
		mqttClient.connect(connectionOptionWrapper.getMqttConnectOptions());
		return mqttClient;
	}
}
