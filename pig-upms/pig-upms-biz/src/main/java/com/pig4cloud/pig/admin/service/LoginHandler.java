package com.pig4cloud.pig.admin.service;


import com.pig4cloud.pig.admin.api.dto.LoginDto;
import com.pig4cloud.pig.admin.api.vo.LoginVo;

public interface LoginHandler {

	/**
	 * 登录
	 *
	 * @param vo
	 * @return
	 */
	LoginDto login(LoginVo vo);
}
