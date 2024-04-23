package com.pig4cloud.pig.common.redisson.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis配置
 */
@Data
@ConfigurationProperties("spring.data.redis")
public class RedissonProperties {

	private String host;

	private String password;

	private Integer connectTimeout;

	private Integer database;

	private Integer port;
}
