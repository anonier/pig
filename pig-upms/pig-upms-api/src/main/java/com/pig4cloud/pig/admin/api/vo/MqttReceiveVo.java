package com.pig4cloud.pig.admin.api.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MqttReceiveVo implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

    /**
     * 消息幂等id
     */
    private Long id;

    /**
     * 源
     */
    private String source;

    /**
     * 目标范畴 server:machine
     */
    private String category;

    /**
     * 目的
     */
    private String target;

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 操作
     */
    private JSONObject operate;
}
