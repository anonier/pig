package com.pig4cloud.pig.common.redisson.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis配置
 */
@Data
@ConfigurationProperties("spring.redisson")
public class RedissonProperties {

	private String address;

	private String password;

	private Integer timeout;
}
