package com.pig4cloud.pig.admin.api.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生分页vo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentPageVo extends Page<StudentPageVo> {

	/**
	 * 班级名称
	 */
	private String name;

	/**
	 * 锁定标记 0未锁定，9已锁定
	 */
	private String lockFlag;

	private Long classId;

	private Long deptId;

	private Long tenantId;
}