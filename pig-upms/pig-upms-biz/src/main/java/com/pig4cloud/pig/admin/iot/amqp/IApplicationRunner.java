package com.pig4cloud.pig.admin.iot.amqp;

import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class IApplicationRunner implements ApplicationRunner {

	@Resource
	private SocketAmqpClientServer socketAmqpClientServer;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		socketAmqpClientServer.init();
	}

}