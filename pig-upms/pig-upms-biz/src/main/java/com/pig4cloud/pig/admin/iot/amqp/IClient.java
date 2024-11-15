package com.pig4cloud.pig.admin.iot.amqp;

import com.aliyuncs.DefaultAcsClient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IClient {

	public int spaceId;
	public String regionId;
	public String accessKey;
	public String accessSecret;
	public DefaultAcsClient client;

}