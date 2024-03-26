package com.pig4cloud.pig.common.mybatis.base;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Face implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

    /**
     * 人脸id
     */
    private Long faceId;

    /**
     * 人脸图片
     */
    private String img;
}