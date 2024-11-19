package com.pig4cloud.pig.admin.api.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师分页vo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherPageVo extends Page<TeacherPageVo> {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 教师名称
	 */
	private String name;

	/**
	 * 教师id
	 */
	private Long teacherId;

	/**
	 * 锁定标记 0未锁定，9已锁定
	 */
	private String lockFlag;
}