package com.pig4cloud.pig.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.pig4cloud.pig.admin.api.dto.LoginDto;
import com.pig4cloud.pig.admin.api.feign.RemoteApplyTokenService;
import com.pig4cloud.pig.admin.api.vo.FaceItemsVo;
import com.pig4cloud.pig.admin.api.vo.LoginVo;
import com.pig4cloud.pig.admin.enums.EnumAuth;
import com.pig4cloud.pig.admin.service.IFaceService;
import com.pig4cloud.pig.admin.service.LoginHandler;
import com.pig4cloud.pig.admin.util.Base64Util;
import com.pig4cloud.pig.common.file.core.FileProperties;
import com.pig4cloud.pig.common.file.oss.OssProperties;
import com.pig4cloud.pig.common.file.oss.service.OssTemplate;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URL;

@Slf4j
@Service("login_face")
@RefreshScope
@AllArgsConstructor
public class LoginFaceImpl implements LoginHandler {
	private static final String clientId = "starlab";
	private static final String clientSecret = "starlab";
	private final RemoteApplyTokenService remoteApplyTokenService;
	private final OssTemplate ossTemplate;
	private final FileProperties fileProperties;
	private final IFaceService iFaceService;

	@Override
	@SneakyThrows
	public LoginDto login(LoginVo vo) {
		URL url = ossTemplate.uploadEncrypt(new OssProperties() {
			{
				setAccessKey(fileProperties.getOss().getAccessKey());
				setSecretKey(fileProperties.getOss().getSecretKey());
				setEndpoint(fileProperties.getOss().getShEndpoint());
				setBucket(fileProperties.getOss().getShBucket());
				setPath("face");
			}
		}, vo.getFile());

		FaceItemsVo faceItemsVo = iFaceService.searchFace(fileProperties.getVisual().getDbName(), String.valueOf(url));
		if (ObjectUtil.isEmpty(vo)) {
			log.error("未查到改该人脸用户");
			throw new RuntimeException("未查到改该人脸用户");
		}
		return getToken(faceItemsVo.getEntityId());
	}

	/**
	 * 获取token
	 *
	 * @param userId 用户id
	 */
	private LoginDto getToken(Long userId) {
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add(EnumAuth.GRANT_TYPE.getDesc(), EnumAuth.FACE.getDesc());
		body.add(EnumAuth.USERID.getDesc(), String.valueOf(userId));
		String Authorization = Base64Util.getHttpBasic(clientId, clientSecret);
		JSONObject object = remoteApplyTokenService.applyToken(body, Authorization);
		LoginDto result = BeanUtil.copyProperties(object, LoginDto.class, "user_info");
		result.setUser_info(JSONObject.from(object.get("user_info")));
		return result;
	}
}
