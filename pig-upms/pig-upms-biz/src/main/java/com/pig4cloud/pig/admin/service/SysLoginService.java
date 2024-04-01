package com.pig4cloud.pig.admin.service;

import com.pig4cloud.pig.admin.api.dto.LoginDto;
import com.pig4cloud.pig.admin.api.vo.LoginVo;
import com.pig4cloud.pig.admin.component.LoginAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginService {

	private final LoginAdapter loginAdapter;

	/**
	 * 登录验证
	 */
	public LoginDto login(LoginVo vo) {
		LoginHandler loginHandler = loginAdapter.getInstance("login_" + vo.getType());
		try {
			return loginHandler.login(vo);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
