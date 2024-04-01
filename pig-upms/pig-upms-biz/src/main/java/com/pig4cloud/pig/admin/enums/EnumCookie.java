package com.pig4cloud.pig.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yb
 * @since 2021-6-28
 */
@Getter
@AllArgsConstructor
public enum EnumCookie {

    UID("uid"),

    BASIC("Basic ");

    /**
     * 字段
     */
    private final String desc;

}
