package com.pig4cloud.pig.common.mybatis.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.pig4cloud.pig.common.mybatis.base.Face;

import java.util.List;

public class FaceTypeHandler extends AbstractJsonTypeHandler<List<Face>> {

	public FaceTypeHandler(Class<?> type) {
		super(type);
	}

	@Override
	public List<Face> parse(String json) {
		return JSONArray.parseArray(json, Face.class);
	}

	@Override
	public String toJson(Object obj) {
		return JSON.toJSONString(obj);
	}
}