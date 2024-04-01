package com.pig4cloud.pig.admin.service;


import com.pig4cloud.pig.admin.api.vo.RegisterVo;
import com.pig4cloud.pig.common.core.util.R;

public interface RegisterHandler {

	/**
	 * 注册
	 *
	 * @param vo
	 * @return
	 */
	R<Object> register(RegisterVo vo);
}
