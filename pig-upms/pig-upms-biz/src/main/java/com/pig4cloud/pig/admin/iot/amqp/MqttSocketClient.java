package com.pig4cloud.pig.admin.iot.amqp;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MqttSocketClient {

	@Value("${mqtt.accessKey}")
	private String accessKey;
	@Value("${mqtt.secretKey}")
	private String accessKeySecret;
	@Value("${mqtt.regionId}")
	private String regionId;

	@Bean("socketClient")
	public IClient socketClientBuild() {
		IClientProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessKeySecret);
		DefaultAcsClient acsClient = new DefaultAcsClient(profile);
		IClient iClient = new IClient();
		iClient.accessKey = accessKey;
		iClient.accessSecret = accessKeySecret;
		iClient.regionId = regionId;
		iClient.client = acsClient;
		return iClient;
	}
}
