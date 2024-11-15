package com.pig4cloud.pig.admin.iot.mq;

import com.alibaba.fastjson2.JSONObject;
import com.pig4cloud.pig.admin.api.vo.MqttReceiveVo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
public class IMqttCallback implements MqttCallbackExtended {

	private final ThreadPoolTaskExecutor iotExecutor;
	private final MqttClient mqttClient;
	private final int qosLevel;
	private final String[] topic;


	public IMqttCallback(MqttClient mqttClient, int qosLevel, String[] topic, ThreadPoolTaskExecutor iotExecutor) {
		this.iotExecutor = iotExecutor;
		this.mqttClient = mqttClient;
		this.qosLevel = qosLevel;
		this.topic = topic;
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		/**
		 * 客户端连接成功后就需要尽快订阅需要的 topic
		 */
		System.out.println("mqtt connect success");
		iotExecutor.submit(() -> {
			try {
				final int[] qos = {qosLevel};
				mqttClient.subscribe(topic, qos);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void connectionLost(Throwable throwable) {
		throwable.printStackTrace();
	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) {
		/**
		 * 消费消息的回调接口，需要确保该接口不抛异常，该接口运行返回即代表消息消费成功。
		 * 消费消息需要保证在规定时间内完成，如果消费耗时超过服务端约定的超时时间，对于可靠传输的模式，服务端可能会重试推送，业务需要做好幂等去重处理。超时时间约定参考限制
		 * https://help.aliyun.com/document_detail/63620.html?spm=a2c4g.11186623.6.546.229f1f6ago55Fj
		 */
		String msg = new String(mqttMessage.getPayload());
		System.out.println(
				"receive msg from topic " + s + " , body is " + msg);
		if (!isJSON(msg)) {
			return;
		}
		MqttReceiveVo vo = JSONObject.parseObject(msg, MqttReceiveVo.class);
		if (!"server".equals(vo.getCategory())) {
			return;
		}
		if ("".equals(vo.getTarget())) {
			//todo
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		System.out.println("send msg succeed topic is : " + iMqttDeliveryToken.getTopics()[0]);
	}

	public boolean isJSON(String str) {
		boolean result = false;
		try {
			JSONObject.parse(str);
			result = true;
		} catch (Exception e) {
			log.error("非json数据不做处理");
		}
		return result;
	}
}
