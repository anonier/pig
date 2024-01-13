package com.pig4cloud.pig.common.redisson.config;

import com.pig4cloud.pig.common.redisson.properties.RedissonProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfig {

	@Bean
	public RedissonClient redissonClient(RedissonProperties redissonProperties) {
		Config config = new Config();
		config.useSingleServer()
				.setAddress(redissonProperties.getAddress())
				.setPassword(redissonProperties.getPassword())
				.setConnectTimeout(redissonProperties.getTimeout());
		return Redisson.create(config);
	}
}
