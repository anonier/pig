package com.pig4cloud.pig.common.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IMapper<T> extends BaseMapper<T> {

	/**
	 * 全量插入,等价于insert
	 *
	 * @param entityList
	 * @return
	 */
	int insertBatchSomeColumn(List<T> entityList);

	/**
	 * 全字段更新，不会忽略null值
	 *
	 * @param entity
	 */
	void alwaysUpdateSomeColumnById(@Param("et") T entity);
}