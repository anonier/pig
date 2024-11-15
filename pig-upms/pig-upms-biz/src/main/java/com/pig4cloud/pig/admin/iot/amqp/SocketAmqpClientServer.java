package com.pig4cloud.pig.admin.iot.amqp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SocketAmqpClientServer {

	@Value("${mqtt.accessKey}")
	private String accessKey;
	@Value("${mqtt.secretKey}")
	private String accessSecret;
	@Value("${mqtt.iotInstanceId}")
	private String iotInstanceId;
	@Value("${mqtt.socketConsumerGroupId}")
	private String socketConsumerGroupId;
	@Value("${mqtt.host}")
	private String host;
	@Value("${mqtt.connectionCount}")
	private int connectionCount;

	private final String clientId = "f1df45";

	private final ExecutorService executorService = new ThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors(),
			Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(50000));

	public void init() throws Exception {
		List<Connection> connections = new ArrayList<>();

		for (int i = 0; i < connectionCount; i++) {
			long timeStamp = System.currentTimeMillis();
			String signMethod = "hmacsha1";

			String userName = clientId + "-" + i + "|authMode=aksign"
					+ ",signMethod=" + signMethod
					+ ",timestamp=" + timeStamp
					+ ",authId=" + accessKey
					+ ",iotInstanceId=" + iotInstanceId
					+ ",consumerGroupId=" + socketConsumerGroupId
					+ "|";
			String signContent = "authId=" + accessKey + "&timestamp=" + timeStamp;
			String password = doSign(signContent, accessSecret, signMethod);
			String connectionUrl = "failover:(amqps://" + host + ":5671?amqp.idleTimeout=80000)"
					+ "?failover.reconnectDelay=30";

			Hashtable<String, String> hashtable = new Hashtable<>();
			hashtable.put("connectionfactory.SBCF", connectionUrl);
			hashtable.put("queue.QUEUE", "default");
			hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
			Context context = new InitialContext(hashtable);
			ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
			Destination queue = (Destination) context.lookup("QUEUE");
			Connection connection = cf.createConnection(userName, password);
			connections.add(connection);

			((JmsConnection) connection).addConnectionListener(myJmsConnectionListener);
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			connection.start();
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(messageListener);
		}
	}

	private final MessageListener messageListener = message -> {
		try {
			executorService.submit(() -> processMessage(message));
		} catch (Exception e) {
			log.error("submit task occurs exception ", e);
		}
	};

	private void processMessage(Message message) {
		try {
			byte[] body = message.getBody(byte[].class);
			String content = new String(body);
			String topic = message.getStringProperty("topic");
			String messageId = message.getStringProperty("messageId");
			long generateTime = message.getLongProperty("generateTime");
			log.info("receive message"
					+ ",\n topic = " + topic
					+ ",\n messageId = " + messageId
					+ ",\n generateTime = " + generateTime
					+ ",\n content = " + content);

			String[] info = topic.split("/");
			String productKey = info[1];
			String deviceName = info[2];

			//记录
//			Device device = deviceService.getValueByProductDeviceName(productKey, deviceName);
//			if (device != null) {
//				WorkConsumer consumer = new WorkConsumer();
//				consumer.spaceId = device.spaceId;
//				consumer.deviceId = device.id;
//				consumer.productKey = productKey;
//				consumer.deviceName = deviceName;
//				consumer.topic = topic;
//				consumer.messageId = messageId;
//				consumer.generateTime = generateTime;
//				consumer.content = content;
//				consumer.messageTime = Util.longToDate(generateTime);
//				consumer.createTime = new Date();
//
//				workConsumerService.addRecord(consumer);
//			}
		} catch (Exception e) {
			log.error("processMessage occurs error ", e);
		}
	}

	private final JmsConnectionListener myJmsConnectionListener = new JmsConnectionListener() {
		/**
		 * 连接成功建立。
		 */
		@Override
		public void onConnectionEstablished(URI remoteURI) {
			log.info("onConnectionEstablished, remoteUri:{}", remoteURI);
		}

		/**
		 * 尝试过最大重试次数之后，最终连接失败。
		 */
		@Override
		public void onConnectionFailure(Throwable error) {
			log.error("onConnectionFailure, {}", error.getMessage());
		}

		/**
		 * 连接中断。
		 */
		@Override
		public void onConnectionInterrupted(URI remoteURI) {
			log.info("onConnectionInterrupted, remoteUri:{}", remoteURI);
		}

		/**
		 * 连接中断后又自动重连上。
		 */
		@Override
		public void onConnectionRestored(URI remoteURI) {
			log.info("onConnectionRestored, remoteUri:{}", remoteURI);
		}

		@Override
		public void onInboundMessage(JmsInboundMessageDispatch envelope) {
		}

		@Override
		public void onSessionClosed(Session session, Throwable cause) {
		}

		@Override
		public void onConsumerClosed(MessageConsumer consumer, Throwable cause) {
		}

		@Override
		public void onProducerClosed(MessageProducer producer, Throwable cause) {
		}
	};

	private String doSign(String toSignString, String secret, String signMethod) throws Exception {
		SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), signMethod);
		Mac mac = Mac.getInstance(signMethod);
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(toSignString.getBytes());
		return Base64.encodeBase64String(rawHmac);
	}
}
