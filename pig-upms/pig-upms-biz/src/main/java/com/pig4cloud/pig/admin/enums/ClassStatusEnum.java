package com.pig4cloud.pig.admin.enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * 班级状态枚举
 *
 * @since 2024-2-29
 */
@Getter
public enum ClassStatusEnum implements Serializable {
	open(1, "启用"),
	close(0, "禁用"),
	delete(-1, "删除");

	private final Integer code;
	private final String description;

	ClassStatusEnum(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
}
