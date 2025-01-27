/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.pig4cloud.pig.admin.api.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pig4cloud.pig.common.mybatis.base.Face;
import com.pig4cloud.pig.common.mybatis.handler.FaceTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@Data
@Schema(description = "用户")
@TableName(autoResultMap = true)
public class TeacherClassDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long userId;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 随机盐
	 */
	@JsonIgnore
	private String salt;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 修改人
	 */
	private String updateBy;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;

	/**
	 * 0-正常，1-删除
	 */
	@TableLogic
	private String delFlag;

	/**
	 * 锁定标记
	 */
	private String lockFlag;

	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 部门ID
	 */
	private Long deptId;

	/**
	 * 微信openid
	 */
	private String wxOpenid;

	/**
	 * 微信小程序openId
	 */
	private String miniOpenid;

	/**
	 * QQ openid
	 */
	private String qqOpenid;

	/**
	 * 码云唯一标识
	 */
	private String giteeLogin;

	/**
	 * 开源中国唯一标识
	 */
	private String oscId;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 客户端名称
	 */
	private String clientId;

	/**
	 * 身份id(学号,教师编号)
	 */
	private String identityId;

	/**
	 * 卡编号
	 */
	private String card;

	/**
	 * 班级id
	 */
	private Long classId;

	/**
	 * 租户id
	 */
	private Long tenantId;

	/**
	 * 性别 1男 2女
	 */
	private Integer sex;

	/**
	 * 人脸
	 */
	@TableField(typeHandler = FaceTypeHandler.class)
	private List<Face> face;
}
