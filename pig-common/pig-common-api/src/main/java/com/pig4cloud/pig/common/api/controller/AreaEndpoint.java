/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the supertooth.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.pig4cloud.pig.common.api.controller;

import com.pig4cloud.pig.common.api.dto.SysAreaDto;
import com.pig4cloud.pig.common.api.service.SysAreaService;
import com.pig4cloud.pig.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * area 对外提供服务端点
 */
@RestController
@AllArgsConstructor
@RequestMapping("area")
public class AreaEndpoint {

	private final SysAreaService sysAreaService;


	/**
	 * 获取省市区数据
	 *
	 * @return R
	 */
	@GetMapping("/all")
	public R<List<SysAreaDto>> getArea() {
		return R.ok(sysAreaService.all());
	}
}
