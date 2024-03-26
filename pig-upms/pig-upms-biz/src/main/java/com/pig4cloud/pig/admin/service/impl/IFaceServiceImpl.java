package com.pig4cloud.pig.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pig4cloud.pig.common.mybatis.base.Face;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.vo.FaceItemsVo;
import com.pig4cloud.pig.admin.mapper.SysUserMapper;
import com.pig4cloud.pig.admin.service.IFaceService;
import com.pig4cloud.pig.common.file.core.FileProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级表
 *
 * @author yb
 * @date 2023-11-17 16:17:20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IFaceServiceImpl implements IFaceService {

    private final FileProperties fileProperties;
	private final SysUserMapper sysUserMapper;
    private com.aliyun.facebody20191230.Client client;

	@Override
	public void face(String type, Long faceId, SysUser sysUser, String img) {
		List<Face> faceVos = sysUser.getFace();
		if ("add".equals(type)) {
			JSONObject obj = getFaceEntity(fileProperties.getVisual().getDbName(), sysUser.getUserId());
			if (ObjectUtil.isEmpty(obj)) {
				addFaceEntity(fileProperties.getVisual().getDbName(), sysUser.getUserId(), sysUser.getUsername());
			}
			JSONObject faceObj = addFace(fileProperties.getVisual().getDbName(), img, sysUser.getUserId());
			faceVos.add(new Face() {
				{
					setFaceId(faceObj.getLong("FaceId"));
					setImg(img);
				}
			});
		} else {
			Map<Long, Face> map = faceVos.stream().collect(Collectors.toMap(Face::getFaceId, a -> a, (k1, k2) -> k1));
			if (ObjectUtil.isEmpty(faceId) || ObjectUtil.isEmpty(map.get(faceId))) {
				log.error("人脸不存在");
				throw new RuntimeException("人脸不存在");
			}
			deleteFace(fileProperties.getVisual().getDbName(), String.valueOf(faceId));
			faceVos = faceVos.stream().filter(f -> !faceId.equals(f.getFaceId())).collect(Collectors.toList());
		}
		List<Face> finalFaceVos = faceVos;
		sysUserMapper.update(new LambdaUpdateWrapper<SysUser>()
				.eq(SysUser::getUserId, sysUser.getUserId())
				.set(SysUser::getFace, JSONArray.toJSONString(finalFaceVos)));
	}

    /**
     * API 相关
     *
     * @param action params
     * @return OpenApi.Params
     */
    public com.aliyun.teaopenapi.models.Params createApiInfo(String action) {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(fileProperties.getVisual().getAccessKeyId())
                .setAccessKeySecret(fileProperties.getVisual().getAccessSecretKey());
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";
        try {
            client = new com.aliyun.facebody20191230.Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        com.aliyun.teaopenapi.models.Params params = new com.aliyun.teaopenapi.models.Params()
                // 接口名称
                .setAction(action)
                // 接口版本
                .setVersion("2019-12-30")
                // 接口协议
                .setProtocol("HTTPS")
                // 接口 HTTP 方法
                .setMethod("POST").setAuthType("AK").setStyle("RPC")
                // 接口 PATH
                .setPathname("/")
                // 接口请求体内容格式
                .setReqBodyType("formData")
                // 接口响应体内容格式
                .setBodyType("json");
        return params;
    }

    @Override
    public JSONObject addFace(String dbName, String img, Long id) {
        try {
            com.aliyun.teaopenapi.models.Params params = createApiInfo("AddFace");
            // body params
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("DbName", dbName);
            body.put("ImageUrl", img);
            body.put("EntityId", id);
//        body.put("ExtraData", null);
//        body.put("QualityScoreThreshold", 100.0);
//        body.put("SimilarityScoreThresholdInEntity", 100.0);
//        body.put("SimilarityScoreThresholdBetweenEntity", 100.0);
            // runtime options
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest().setBody(body);
            // 复制代码运行请自行打印 API 的返回值
            // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
            Map<String, ?> map = client.callApi(params, request, runtime);
            log.info(JSONObject.toJSONString(map));
            JSONObject obj = JSONObject.from(map);
            if (200 != obj.getInteger("statusCode")) {
                log.error("添加人脸异常:" + JSONObject.toJSONString(obj));
                throw new RuntimeException();
            } else {
                return JSONObject.from(JSONObject.from(map.get("body")).get("Data"));
            }
        } catch (Exception e) {
            log.error("添加人脸库异常" + ":db:" + dbName + ":img:" + img + ":id:" + id + e);
            throw new RuntimeException("添加人脸库异常", e);
        }
    }

    @Override
    public JSONObject addFaceEntity(String dbName, Long entityId, String labels) {
        try {
            com.aliyun.teaopenapi.models.Params params = createApiInfo("AddFaceEntity");
            // body params
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("DbName", dbName);
            body.put("EntityId", entityId);
            body.put("Labels", labels);
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest().setBody(body);
            Map<String, ?> map = client.callApi(params, request, runtime);
            log.info(JSONObject.toJSONString(map));
            Object obj = JSONObject.from(map.get("body")).get("Data");
            if (ObjectUtil.isEmpty(obj)) {
                return null;
            } else {
                return JSONObject.from(obj);
            }
        } catch (Exception e) {
            log.error("添加样本数据异常" + e);
            throw new RuntimeException("添加样本数据异常", e);
        }
    }

    @Override
    public FaceItemsVo searchFace(String dbName, String img) {
        try {
            com.aliyun.teaopenapi.models.Params params = createApiInfo("SearchFace");
            // body params
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("DbName", dbName);
            body.put("ImageUrl", img);
            body.put("Limit", 5);
//            body.put("DbNames", null);
//            body.put("QualityScoreThreshold", 50);
//            body.put("MaxFaceNum", null);
            // runtime options
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest().setBody(body);
            // 复制代码运行请自行打印 API 的返回值
            // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
            Map<String, ?> map = client.callApi(params, request, runtime);
            log.info(JSONObject.toJSONString(map));
            if (ObjectUtil.notEqual(200, map.get("statusCode"))) {
                throw new RuntimeException();
            }
            JSONObject mapObj = JSONObject.from(map);
            JSONObject bodyObj = JSONObject.from(mapObj.get("body"));
            JSONObject dataObj = JSONObject.from(bodyObj.get("Data"));
            JSONObject matchList = JSONObject.from(JSONArray.from(dataObj.get("MatchList")).get(0));
            List<FaceItemsVo> faceItems = JSONArray.parseArray(String.valueOf(matchList.get("FaceItems")), FaceItemsVo.class);
            if (CollUtil.isEmpty(faceItems)) {
                return null;
            }
            List<FaceItemsVo> face = faceItems.stream()
                    .filter(f -> f.getScore().compareTo(new BigDecimal("0.6")) >= 0)
                    .sorted(Comparator.comparing(FaceItemsVo::getScore).reversed())
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(face)) {
                return null;
            } else {
                return face.get(0);
            }
        } catch (Exception e) {
            log.error("人脸1:N查询异常:" + e.getMessage());
            throw new RuntimeException("人脸1:N查询异常", e);
        }
    }

    @Override
    public JSONObject createFaceDb(String dbName) throws Exception {
        com.aliyun.teaopenapi.models.Params params = createApiInfo("CreateFaceDb");
        // body params
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("Name", dbName);
        // runtime options
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest().setBody(body);
        // 复制代码运行请自行打印 API 的返回值
        // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
        Map<String, ?> map = client.callApi(params, request, runtime);
        log.info(JSONObject.toJSONString(map));
        Object obj = JSONObject.from(map.get("body")).get("Data");
        if (ObjectUtil.isEmpty(obj)) {
            return null;
        } else {
            return JSONObject.from(obj);
        }
    }

    @Override
    public JSONObject deleteFace(String dbName, String FaceId) {
        try {
            com.aliyun.teaopenapi.models.Params params = createApiInfo("DeleteFace");
            // body params
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("DbName", dbName);
            body.put("FaceId", FaceId);
            // runtime options
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                    .setBody(body);
            // 复制代码运行请自行打印 API 的返回值
            // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
            Map<String, ?> map = client.callApi(params, request, runtime);
            log.info(JSONObject.toJSONString(map));
            Object obj = JSONObject.from(map.get("body")).get("Data");
            if (ObjectUtil.isEmpty(obj)) {
                return null;
            } else {
                return JSONObject.from(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public JSONObject deleteFaceDb(String dbName) throws Exception {
        com.aliyun.teaopenapi.models.Params params = createApiInfo("DeleteFaceDb");
        // body params
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("Name", dbName);
        // runtime options
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                .setBody(body);
        // 复制代码运行请自行打印 API 的返回值
        // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
        Map<String, ?> map = client.callApi(params, request, runtime);
        log.info(JSONObject.toJSONString(map));
        Object obj = JSONObject.from(map.get("body")).get("Data");
        if (ObjectUtil.isEmpty(obj)) {
            return null;
        } else {
            return JSONObject.from(obj);
        }
    }

    @Override
    public JSONObject deleteFaceEntity(String dbName, Long entityId) {
        try {
            com.aliyun.teaopenapi.models.Params params = createApiInfo("DeleteFaceEntity");
            // body params
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("DbName", dbName);
            body.put("EntityId", entityId);
            // runtime options
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                    .setBody(body);
            // 复制代码运行请自行打印 API 的返回值
            // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
            Map<String, ?> map = client.callApi(params, request, runtime);
            log.info(JSONObject.toJSONString(map));
            Object obj = JSONObject.from(map.get("body")).get("Data");
            if (ObjectUtil.isEmpty(obj)) {
                return null;
            } else {
                return JSONObject.from(obj);
            }
        } catch (Exception e) {
            log.error("删除人脸实体异常" + e);
            throw new RuntimeException("删除人脸实体异常", e);
        }
    }

    @Override
    public JSONObject UpdateFaceEntity(String dbName, Long entityId, String Labels) throws Exception {
        com.aliyun.teaopenapi.models.Params params = createApiInfo("UpdateFaceEntity");
        // body params
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("DbName", dbName);
        body.put("EntityId", entityId);
        body.put("Labels", Labels);
        // runtime options
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                .setBody(body);
        // 复制代码运行请自行打印 API 的返回值
        // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
        Map<String, ?> map = client.callApi(params, request, runtime);
        log.info(JSONObject.toJSONString(map));
        Object obj = JSONObject.from(map.get("body")).get("Data");
        if (ObjectUtil.isEmpty(obj)) {
            return null;
        } else {
            return JSONObject.from(obj);
        }
    }

    @Override
    public JSONObject getFaceEntity(String dbName, Long entityId) {
        try {
            com.aliyun.teaopenapi.models.Params params = createApiInfo("GetFaceEntity");
            // body params
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("DbName", dbName);
            body.put("EntityId", entityId);
            // runtime options
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                    .setBody(body);
            // 复制代码运行请自行打印 API 的返回值
            // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
            Map<String, ?> map = client.callApi(params, request, runtime);
            log.info(JSONObject.toJSONString(map));
            Object obj = JSONObject.from(map.get("body")).get("Data");
            if (ObjectUtil.isEmpty(obj)) {
                return null;
            } else {
                return JSONObject.from(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("获取人脸实体异常");
        }
    }

    @Override
    public JSONObject ListFaceEntities(Map<String, Object> body) throws Exception {
        com.aliyun.teaopenapi.models.Params params = createApiInfo("ListFaceEntities");
        // body params
        body.put("DbName", null);
        body.put("Offset", null);
        body.put("Limit", null);
        body.put("Token", null);
        body.put("Labels", null);
        body.put("EntityIdPrefix", null);
        body.put("Order", null);
        // runtime options
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                .setBody(body);
        // 复制代码运行请自行打印 API 的返回值
        // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
        Map<String, ?> map = client.callApi(params, request, runtime);
        log.info(JSONObject.toJSONString(map));
        Object obj = JSONObject.from(map.get("body")).get("Data");
        if (ObjectUtil.isEmpty(obj)) {
            return null;
        } else {
            return JSONObject.from(obj);
        }
    }

    @Override
    public JSONObject listFaceDbs() throws Exception {
        com.aliyun.teaopenapi.models.Params params = createApiInfo("ListFaceDbs");
        // body params
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("Offset", null);
        body.put("Limit", null);
        // runtime options
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.teaopenapi.models.OpenApiRequest request = new com.aliyun.teaopenapi.models.OpenApiRequest()
                .setBody(body);
        // 复制代码运行请自行打印 API 的返回值
        // 返回值为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
        Map<String, ?> map = client.callApi(params, request, runtime);
        log.info(JSONObject.toJSONString(map));
        Object obj = JSONObject.from(map.get("body")).get("Data");
        if (ObjectUtil.isEmpty(obj)) {
            return null;
        } else {
            return JSONObject.from(obj);
        }
    }
}