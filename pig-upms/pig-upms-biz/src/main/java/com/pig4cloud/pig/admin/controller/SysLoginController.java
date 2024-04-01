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

package com.pig4cloud.pig.admin.controller;

import com.pig4cloud.pig.admin.api.dto.LoginDto;
import com.pig4cloud.pig.admin.api.vo.LoginVo;
import com.pig4cloud.pig.admin.service.SysLoginService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.annotation.Inner;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 登入管理
 */
@RestController
@AllArgsConstructor
@RequestMapping("/login")
public class SysLoginController {

	private final SysLoginService loginService;

	/**
	 * 人脸登录
	 */
	@Inner(false)
	@SysLog("人脸登录")
	@PostMapping("face")
	public R<LoginDto> face(MultipartFile file) {
		return R.ok(loginService.login(new LoginVo() {
			{
				setType("face");
				setFile(file);
			}
		}));
	}

	/**
	 * 手机号登录
	 */
	@Inner(false)
	@SysLog("手机号登录")
	@PostMapping("mobile")
	public R<LoginDto> mobile(@RequestBody LoginVo vo) {
		return R.ok(loginService.login(new LoginVo() {
			{
				setType("mobile");
				setPassword(vo.getPassword());
			}
		}));
	}

	/**
	 * 手机验证码登录
	 */
	@Inner(false)
	@SysLog("手机验证码登录")
	@PostMapping("sms")
	public R<LoginDto> sms(@RequestBody LoginVo vo) {
		return R.ok(loginService.login(new LoginVo() {
			{
				setType("sms");
				setCode(vo.getCode());
			}
		}));
	}

	/**
	 * 账号登入
	 */
	@Inner(false)
	@SysLog("账号登入")
	@PostMapping("username")
	public R<LoginDto> username(@RequestBody LoginVo vo) {
		return R.ok(loginService.login(new LoginVo() {
			{
				setType("username");
				setCode(vo.getPassword());
			}
		}));
	}
}
