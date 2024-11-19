package com.pig4cloud.pig.admin.api.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师分页vo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminPageVo extends Page<AdminPageVo> {

	/**
	 * 管理员名称
	 */
	private String name;

	/**
	 * 锁定标记 0未锁定，9已锁定
	 */
	private String lockFlag;
}