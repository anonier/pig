package com.pig4cloud.pig.admin.iot.amqp;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.iot.model.v20180120.PubRequest;
import com.aliyuncs.iot.model.v20180120.PubResponse;
import com.pig4cloud.pig.common.core.util.R;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SocketAmqpIotService {

	@Value("${mqtt.socketProductKey}")
	private String socketProductKey;
	@Value("${mqtt.iotInstanceId}")
	private String iotInstanceId;

	@Resource
	private IClient socketClient;

	public R pub(String topic, String message) {
		PubRequest request = new PubRequest();
		request.setIotInstanceId(iotInstanceId);
		request.setProductKey(socketProductKey);
		request.setMessageContent(Base64.encodeBase64String(message.getBytes()));
		request.setTopicFullName(topic);
		request.setQos(0); //目前支持QoS0和QoS1。
		try {
			PubResponse response = socketClient.client.getAcsResponse(request);
			System.out.println("getSuccess = " + response.getSuccess());
			System.out.println("getCode = " + response.getCode());
			System.out.println("getErrorMessage = " + response.getErrorMessage());
			return R.ok();
		} catch (ServerException e) {
			log.error(e.getErrMsg());
			throw new RuntimeException(e.getErrMsg());
		} catch (ClientException e) {
			System.out.println("ErrCode:" + e.getErrCode());
			System.out.println("ErrMsg:" + e.getErrMsg());
			log.error(e.getErrMsg());
			throw new RuntimeException(e.getErrMsg());
		}
	}
}
