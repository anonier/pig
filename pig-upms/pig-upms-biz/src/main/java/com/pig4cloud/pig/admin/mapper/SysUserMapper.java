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

package com.pig4cloud.pig.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.admin.api.dto.SysUserRoleDto;
import com.pig4cloud.pig.admin.api.dto.TeacherClassDto;
import com.pig4cloud.pig.admin.api.dto.UserDTO;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

	/**
	 * 通过用户名查询用户信息（含有角色信息）
	 *
	 * @param username 用户名
	 * @return userVo
	 */
	UserVO getUserVoByUsername(String username);

	/**
	 * 分页查询用户信息（含角色）
	 *
	 * @param userDTO 查询参数
	 * @return list
	 */
	Page<UserVO> getUserVosPage(@Param("query") UserPageVo userDTO);

	/**
	 * 通过ID查询用户信息
	 *
	 * @param id 用户ID
	 * @return userVo
	 */
	UserVO getUserVoById(Long id);

	/**
	 * 查询用户列表
	 *
	 * @param userDTO 查询条件
	 * @return
	 */
	List<UserVO> selectVoList(@Param("query") UserDTO userDTO);

	@Select("<script>"
			+ "SELECT u.* " +
//			",c.name as className " +
			"from sys_user u " +
			"left join sys_user_role ur on u.user_id = ur.user_id "
			+ "left join sys_role r on ur.role_id = r.role_id " +
//			"left join sys_class c on u.user_id = c.principal_id " +
			"WHERE u.del_flag = 0 and r.role_code = 'TEACHER' "
			+ "<if test='vo.name != null' > "
			+ "and u.nickname = #{vo.name} "
			+ "</if> "
			+ "<if test='vo.lockFlag != null' > "
			+ "and u.lock_flag = #{vo.lockFlag} "
			+ "</if> "
			+ "</script>")
	@ResultMap("TeacherClassResultMap")
	Page<TeacherClassDto> getTeachersPage(@Param("vo") TeacherPageVo vo);

	@Select("<script>"
			+ "SELECT u.* from sys_user u left join sys_user_role ur on u.user_id = ur.user_id "
			+ "left join sys_role r on ur.role_id = r.role_id "
			+ "WHERE u.del_flag = 0 and r.role_name = '学生' "
			+ "<if test='vo.classId != null' > "
			+ "and u.class_id = #{vo.classId} "
			+ "</if> "
			+ "<if test='vo.name != null' > "
			+ "and u.nickname = #{vo.name} "
			+ "</if> "
			+ "<if test='vo.lockFlag != null' > "
			+ "and u.lock_flag = #{vo.lockFlag} "
			+ "</if> "
			+ "</script>")
	Page<SysUser> getStudentPage(@Param("vo") StudentPageVo vo);

	@Select("<script>"
			+ "SELECT u.* " +
//			",c.name as className " +
			"from sys_user u " +
			"left join sys_user_role ur on u.user_id = ur.user_id "
			+ "left join sys_role r on ur.role_id = r.role_id " +
//			"left join sys_class c on u.user_id = c.principal_id " +
			"WHERE u.del_flag = 0 and r.role_code = 'TEACHER' "
			+ "<if test='vo.name != null' > "
			+ "and u.nickname like concat('%',#{vo.name},'%') "
			+ "</if> "
			+ "<if test='vo.lockFlag != null' > "
			+ "and u.lock_flag = #{vo.lockFlag} "
			+ "</if> "
			+ "</script>")
	List<SysUser> getTeachersList(@Param("vo") SysUser sysUser);

	@Select("<script>"
			+ "SELECT u.*,ur.id as userRoleId from sys_user_role ur left join sys_user u on u.user_id = ur.user_id "
			+ "left join sys_role r on ur.role_id = r.role_id "
			+ "WHERE u.del_flag = 0 and r.role_name != '学生' and r.role_name != '教师' and r.role_code != 'ROLE_ADMIN' "
			+ "<if test='vo.name != null' > "
			+ "and u.nickname = #{vo.username} "
			+ "</if> "
			+ "<if test='vo.lockFlag != null'> "
			+ "and u.lock_flag = #{vo.lockFlag} "
			+ "</if> "
			+ "</script>")
	Page<SysUserRoleDto> getAdminPage(@Param("vo") AdminPageVo vo);
}