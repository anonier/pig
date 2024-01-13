package com.pig4cloud.pig.common.api.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "地区表")
public class SysArea extends Model<SysArea> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 区划代码
	 */
	private Long code;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 级别1-5,省市县镇村
	 */
	private Integer level;

	/**
	 * 父级区划代码
	 */
	private Long pcode;

	/**
	 * 城乡分类
	 */
	private Integer category;
}
