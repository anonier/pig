package com.pig4cloud.pig.common.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.common.api.dto.SysAreaDto;
import com.pig4cloud.pig.common.api.entity.SysArea;

import java.util.List;

public interface SysAreaService extends IService<SysArea> {

	/**
	 * 获取省市区数据
	 */
	List<SysAreaDto> all();
}
