package com.pig4cloud.pig.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.pig4cloud.pig.admin.api.entity.SysUser;
import com.pig4cloud.pig.admin.api.vo.FaceItemsVo;

import java.util.Map;

public interface IFaceService {

	/**
	 * 编辑face
	 *
	 * @param type
	 * @param faceId
	 * @param sysUser
	 * @param img
	 */
	void face(String type, Long faceId, SysUser sysUser, String img);

	/**
	 * 添加人脸数据
	 *
	 * @param img
	 * @param id
	 */
	JSONObject addFace(String dbName, String img, Long id);

	/**
	 * 添加人脸样本
	 *
	 * @param entityId
	 * @param labels
	 */
	JSONObject addFaceEntity(String dbName, Long entityId, String labels);

	/**
	 * 搜索人脸
	 *
	 * @param img
	 * @return
	 */
	FaceItemsVo searchFace(String dbName, String img);

	/**
	 * 创建人脸数据库
	 *
	 * @throws Exception
	 */
	JSONObject createFaceDb(String dbName) throws Exception;

	/**
	 * 删除人脸
	 *
	 * @param dbName
	 * @param FaceId
	 */
	JSONObject deleteFace(String dbName, String FaceId);

	/**
	 * 删除人脸数据库
	 *
	 * @param dbName
	 * @throws Exception
	 */
	JSONObject deleteFaceDb(String dbName) throws Exception;

	/**
	 * 删除人脸样本
	 *
	 * @param dbName
	 * @param entityId
	 * @throws Exception
	 */
	JSONObject deleteFaceEntity(String dbName, Long entityId);

	/**
	 * 更新人脸样本
	 *
	 * @param dbName
	 * @param entityId
	 * @param Labels
	 * @throws Exception
	 */
	JSONObject UpdateFaceEntity(String dbName, Long entityId, String Labels) throws Exception;

	/**
	 * 查询人脸样本
	 *
	 * @param dbName
	 * @param entityId
	 * @throws Exception
	 */
	JSONObject getFaceEntity(String dbName, Long entityId);

	/**
	 * 查询人脸样本列表
	 *
	 * @param body
	 * @throws Exception
	 */
	JSONObject ListFaceEntities(Map<String, Object> body) throws Exception;

	/**
	 * 查看人脸数据库列表
	 *
	 * @throws Exception
	 */
	JSONObject listFaceDbs() throws Exception;
}