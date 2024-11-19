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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 登入管理
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class SysLoginController {

	@Resource
	private SysLoginService loginService;

	@Value("${authRsaPri}")
	private String authRsaPri;

	/**
	 * 第三方认证
	 */
	@Inner(false)
	@SysLog("第三方认证")
	@PostMapping("third")
	public R<LoginDto> third(@RequestBody Map<String, String> authMsg) {
		try {
			Map<String, String> msgMap = new HashMap<>();
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				byte[] byteKey = Base64.getDecoder().decode(authRsaPri);
				PKCS8EncodedKeySpec x509EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PrivateKey privateKey = keyFactory.generatePrivate(x509EncodedKeySpec);
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				byte[] plainData = cipher.doFinal(Base64.getDecoder().decode(authMsg.get("authMsg")));
				String msg = new String(plainData);
				String[] info = msg.split("&");
				for (String inf : info) {
					String[] a = inf.split("=");
					msgMap.put(a[0], a[1]);
				}
			} catch (Exception e) {
				log.error("解密异常:{}", e.getMessage());
				throw new RuntimeException("解密异常");
			}
			if (!msgMap.containsKey("timestamp") || !msgMap.containsKey("accessKey") || !msgMap.containsKey("secretKey")) {
				log.error((!msgMap.containsKey("timestamp"))
						? "timestamp不存在" : ((!msgMap.containsKey("accessKey"))
						? "accessKey不存在" : ((!msgMap.containsKey("secretKey")) ? "secretKey不存在" : "")));
				throw new RuntimeException("参数缺失");
			}
			return R.ok(loginService.login(new LoginVo() {
				{
					setType("third");
					setAccessKey(msgMap.get("accessKey"));
					setSecretKey(msgMap.get("secretKey"));
				}
			}));
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
	}

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
				setPhone(vo.getPhone());
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
				setPhone(vo.getPhone());
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
				setUsername(vo.getUsername());
				setPassword(vo.getPassword());
			}
		}));
	}

	/**
	 * 刷卡登入
	 */
	@Inner(false)
	@SysLog("刷卡登入")
	@PostMapping("card")
	public R<LoginDto> card(@RequestBody LoginVo vo) {
		return R.ok(loginService.login(new LoginVo() {
			{
				setType("card");
				setCard(vo.getCard());
			}
		}));
	}
}
