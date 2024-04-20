package com.pig4cloud.pig.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登入入参vo
 */
@Data
@Schema(description = "登入入参对象")
public class LoginVo implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 账号
	 */
	private String username;

	/**
	 * 卡号
	 */
	private String card;

	/**
	 * 手机
	 */
	private String phone;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 校验码(短信登入时使用)
	 */
	private String code;

	/**
	 * username,mobile,sms,face
	 */
	private String type;

	private MultipartFile file;
}
