package com.pig4cloud.pig.admin.iot.amqp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 企业配置，每个企业只允许一个配置
 */
@Data
@NoArgsConstructor
public class OfficeConfig {
	/**
	 * 记录标识
	 */
	public int id;
	/**
	 * 租用标识
	 */
	public int spaceId;
	/**
	 * 阿里云账号ID ，登录物联网平台控制台，移动鼠标指针到账号头像，可查看账号ID
	 */
	public String uid;
	/**
	 * 阿里云账号 AccessKey
	 */
	public String accessKey;
	/**
	 * 阿里云账号 AccessSecret
	 */
	public String accessSecret;
	/**
	 * 设备所在地域（与控制台上的地域对应）的ID，如：cn-shanghai
	 */
	public String regionId;

	/**
	 * 阿里云物联网 ProductKey
	 */
	public String productKey;
	/**
	 * （一般为空）阿里云物联网 实例ID。您可在物联网平台控制台的实例概览页面，查看当前实例的ID
	 * 若有ID值，必须传入该ID值，否则调用会失败。
	 * 若无实例概览页面或ID值，则无需传入。
	 */
	public String iotInstanceId;
	/**
	 * 阿里云物联网 ProductSecret
	 */
	public String productSecret;
	/**
	 * AMQP 消费组 ID
	 */
	public String consumerGroupId;


	/**
	 * 配置状态，0：未生效，1：已启用
	 */
	public int state;
	/**
	 * 创建时间
	 */
	public Date createTime;
	/**
	 * 更新时间
	 */
	public Date updateTime;
}