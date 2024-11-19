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

package com.pig4cloud.pig.admin.mapper.notenant;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.admin.api.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface NoTenantSysRoleMapper extends BaseMapper<SysRole> {

	/**
	 * 通过用户ID，查询角色信息
	 *
	 * @param userId
	 * @return
	 */
	@Select("<script>"
			+ "SELECT r.role_id,\n" +
			"r.role_name,\n" +
			"r.role_code,\n" +
			"r.role_desc,\n" +
			"r.create_time,\n" +
			"r.update_time, " +
			"r.del_flag " +
			"FROM sys_role r left join sys_user_role u on r.role_id = u.role_id " +
			"WHERE " +
			"r.del_flag = '0' " +
			"and u.user_id = #{userId} " +
			"</script>")
	List<SysRole> listRolesByUserId(@Param("userId") Long userId);
}
