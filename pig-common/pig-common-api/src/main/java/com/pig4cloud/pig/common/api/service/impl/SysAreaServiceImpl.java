package com.pig4cloud.pig.common.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.api.service.SysAreaService;
import com.pig4cloud.pig.common.api.dto.SysAreaDto;
import com.pig4cloud.pig.common.api.entity.SysArea;
import com.pig4cloud.pig.common.api.mapper.SysAreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地区实现类
 */
@Slf4j
@Service
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, SysArea> implements SysAreaService {

	@Override
	public List<SysAreaDto> all() {
		List<SysArea> sysAreas = this.list(new LambdaQueryWrapper<SysArea>().lt(SysArea::getLevel, 4));
		List<SysAreaDto> allData = BeanUtil.copyToList(sysAreas, SysAreaDto.class);
		//过滤出全部省份信息
		List<SysAreaDto> result = allData.stream().filter(address -> address.getLevel() == 1).collect(Collectors.toList());
		result.forEach(pro -> {
			//组装市信息
			List<SysAreaDto> city = allData.stream().filter(address -> address.getLevel() == 2 && address.getPcode().equals(pro.getCode())).collect(Collectors.toList());
			if (!city.isEmpty()) {
				pro.setJunior(city);
			}
			//组装区信息
			city.forEach(ci -> {
				List<SysAreaDto> area = allData.stream().filter(address -> address.getLevel() == 3 && address.getPcode().equals(ci.getCode())).collect(Collectors.toList());
				if (!area.isEmpty()) {
					ci.setJunior(area);
				}
			});
		});
		return result;
	}
}
