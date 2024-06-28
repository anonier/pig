package com.pig4cloud.pig.common.mybatis.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.pig4cloud.pig.common.mybatis.base.Face;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedTypes({List.class})
public class FaceTypeHandler extends AbstractJsonTypeHandler<List<Face>> {

	public FaceTypeHandler(Class<?> type) {
		super(type);
	}

	@Override
	public List<Face> parse(String json) {
		return JSONArray.parseArray(json, Face.class);
	}

	@Override
	public String toJson(List<Face> obj) {
		return JSON.toJSONString(obj);
	}
}