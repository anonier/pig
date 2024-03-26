package com.pig4cloud.pig.admin.api.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class FaceItemsVo implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

    private Long EntityId;

    private Long FaceId;

    private BigDecimal Score;

    private BigDecimal Confidence;

    private String DbName;
}
