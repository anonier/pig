package com.pig4cloud.pig.admin.api.feign;

import com.alibaba.fastjson2.JSONObject;
import com.pig4cloud.pig.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * feign请求
 */
@FeignClient(contextId = "remoteApplyTokenService", value = ServiceNameConstants.AUTH_SERVICE)
public interface RemoteApplyTokenService {

	@PostMapping(value = "/oauth2/token", consumes = {"application/x-www-form-urlencoded"})
	JSONObject applyToken(Map<String, ?> map, @RequestHeader("Authorization") String Authorization);
}
