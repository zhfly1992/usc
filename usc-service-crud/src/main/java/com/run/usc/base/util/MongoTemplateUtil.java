/*
 * File name: MongoTemplateUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月4日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description:MongoTemplateUtil CRUD工具类
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */
@Component
public class MongoTemplateUtil {
	@Autowired
	private MongoTemplate tenementTemplate;



	/**
	 * 
	 * @Description:根据表id查询表信息
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public RpcResponse<JSONObject> getModelById(Logger logger, String methodName, String collectionName, String id) {
		Query query = new Query();
		Criteria criteria = Criteria.where(UscConstants.ID_).is(id);
		query.addCriteria(criteria);

		JSONObject map = tenementTemplate.findOne(query, JSONObject.class, collectionName);
		if (null != map && !map.isEmpty()) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map.toJSONString()));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, map);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.GET_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
		}
	}



	/**
	 * 
	 * @Description:插入json参数都用进行的步骤，所以做统一封装
	 * @param objectToSave
	 * @param collectionName
	 */
	public RpcResponse<String> insert(Logger logger, String methodName, JSONObject objectToSave,
			String collectionName) {
		// 接入方编号
		String id = UUID.randomUUID().toString().replace("-", "");
		objectToSave.put(UscConstants.ID_, id);
		objectToSave.put(TenementConstant.TENEMENT_CREATE_DATE,
				DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		objectToSave.put(TenementConstant.TENEMENT_UPDATE_DATE,
				DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));

		// 去除权限验证信息
		objectToSave.remove(UscConstants.AUTHZ_INFO);

		objectToSave.put(TenementConstant.TENEMENT_DELETE_STATE, TenementConstant.STATE_NORMAL_ONE);
		objectToSave.put(TenementConstant.TENEMENT_STATE, TenementConstant.STATE_NORMAL_ONE);// 0停用1正常

		tenementTemplate.insert(objectToSave, collectionName);

		Query query = new Query();
		Criteria criteria = Criteria.where(UscConstants.ID_).is(id);
		query.addCriteria(criteria);

		JSONObject map = tenementTemplate.findOne(query, JSONObject.class, collectionName);

		if (null != map && !map.isEmpty()) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map.toJSONString()));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SAVE_SUCC, map.toJSONString());
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.SAVE_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SAVE_FAIL);
		}
	}



	/**
	 * 
	 * @Description:插入json参数都用进行的步骤，所以做统一封装
	 * @param objectToSave
	 * @param collectionName
	 */
	public RpcResponse<JSONObject> insertId(Logger logger, String methodName, JSONObject objectToSave,
			String collectionName, String id) {
		objectToSave.put(UscConstants.ID_, id);
		objectToSave.put(TenementConstant.TENEMENT_CREATE_DATE,
				DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		objectToSave.put(TenementConstant.TENEMENT_UPDATE_DATE,
				DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));

		// 去除权限验证信息
		objectToSave.remove(UscConstants.AUTHZ_INFO);

		objectToSave.put(TenementConstant.TENEMENT_DELETE_STATE, TenementConstant.STATE_NORMAL_ONE);
		objectToSave.put(TenementConstant.TENEMENT_STATE, TenementConstant.STATE_NORMAL_ONE);
		objectToSave.put(UscConstants.STATE, UscConstants.STATE_NORMAL_ONE);

		tenementTemplate.insert(objectToSave, collectionName);

		Query query = new Query();
		Criteria criteria = Criteria.where(UscConstants.ID_).is(id);
		query.addCriteria(criteria);

		JSONObject map = tenementTemplate.findOne(query, JSONObject.class, collectionName);

		if (null != map && !map.isEmpty()) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map.toJSONString()));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SAVE_SUCC, map);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.SAVE_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SAVE_FAIL);
		}

	}



	/**
	 * 
	 * @Description:修改
	 * @param objectToSave
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public RpcResponse<JSONObject> update(Logger logger, String methodName, JSONObject objectToSave,
			String collectionName, String id) {
		// 修改接入方信息
		Criteria criteria = Criteria.where(UscConstants.ID_).is(id);
		Query query = new Query(criteria);
		String tenementInfoStr = JSON.toJSONString(objectToSave);
		Update update = MongoUtils.jsonStringToUpdate(tenementInfoStr);
		update.set(TenementConstant.TENEMENT_UPDATE_DATE,
				DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		WriteResult updateMulti = tenementTemplate.updateMulti(query, update, collectionName);
		if (updateMulti.getN() > 0) {
			logger.debug(String.format("[%s()->success:%s]", methodName, objectToSave.toJSONString()));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC, objectToSave);
		} else {
			logger.debug(String.format("[updateAccessInfo()->fail:%s", UscConstants.UPDATE_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
		}
	}



	/**
	 * 
	 * @Description:批量删除
	 * @param objectToSave
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public RpcResponse<List<String>> delete(Logger logger, String methodName, String collectionName, List<String> ids) {
		// 修改接入方信息
		Criteria criteria = Criteria.where(UscConstants.ID_).in(ids);
		Query query = new Query(criteria);
		Update update = new Update();
		update.set(TenementConstant.TENEMENT_DELETE_STATE, TenementConstant.STATE_STOP_ZERO);
		update.set(TenementConstant.TENEMENT_UPDATE_DATE,
				DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
		WriteResult res = tenementTemplate.updateMulti(query, update, collectionName);
		if (res.getN() > 0) {
			logger.debug(String.format("[%s()->deleteId:%s]", methodName, ids));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.DEL_SUCC, ids);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.DEL_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.DEL_FAIL);
		}
	}



	/**
	 * 
	 * @Description:校验是否存在子类集合
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param parentKey
	 * @param parentValue
	 * @return
	 */
	public Boolean checkIsDelete(String collectionName, String parentKey, List<String> parentValue) {
		Query query = new Query();
		Criteria criteria = Criteria.where(parentKey).in(parentValue);
		Criteria criteriaDel = Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE);
		query.addCriteria(criteria);
		query.addCriteria(criteriaDel);
		return tenementTemplate.exists(query, collectionName);
	}



	/**
	 * 
	 * @Description:批量更改状态信息
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param parentKey
	 * @param parentValue
	 * @return
	 */
	public int switchState(String collectionName, String parentKey, List<String> parentValue, String state) {
		Query query = new Query();
		Criteria criteria = Criteria.where(parentKey).in(parentValue);
		query.addCriteria(criteria);
		Update update = new Update();
		update.set(UscConstants.STATE, state);
		update.set(TenementConstant.TENEMENT_UPDATE_DATE,
				DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));

		return tenementTemplate.updateMulti(query, update, collectionName).getN();
	}



	/**
	 * 
	 * @Description:查询单列集合
	 * @param collectionName
	 * @param queryKey
	 *            查询关键字
	 * @param query
	 *            查询条件
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getListByKey(String collectionName, Query query, String queryKey) {
		List<Map> map = tenementTemplate.find(query, Map.class, collectionName);
		List keys = new ArrayList<>();
		if (map != null && !map.isEmpty()) {
			for (Map accMap : map) {
				keys.add(accMap.get(queryKey));
			}
		}
		return keys;
	}



	/**
	 * 
	 * @Description:重名校验
	 * @param name
	 * @param parentId
	 *            父类id
	 * @param selfId
	 *            需要排除的id，一般用户更新
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private boolean nameCheck(String parentId, String parentKey, String name, String nameKey, String selfId,
			String selfKey, String collectionName) {
		Query queryT = new Query();
		Criteria criteriaT = Criteria.where(nameKey).is(name).and(TenementConstant.TENEMENT_DELETE_STATE)
				.is(TenementConstant.STATE_NORMAL_ONE).and(parentKey).is(parentId);

		criteriaT.and(selfKey).nin(selfId);
		queryT.addCriteria(criteriaT);
		List<? extends Map<String, Object>> tenementInfoListT = (List<? extends Map<String, Object>>) tenementTemplate
				.find(queryT, new HashMap<String, Object>().getClass(), collectionName);
		if (tenementInfoListT != null && !tenementInfoListT.isEmpty()) {
			return true;
		}
		return false;
	}



	/**
	 * 
	 * @Description:递归查询所有子子孙孙
	 */
	@SuppressWarnings("rawtypes")
	public void recursiveSourceInfos(String parentKey, String parentValue, String collectionName, List<Map> sourceIds)
			throws Exception {
		// 封装查询条件
		Query query = new Query();
		Criteria criteria = Criteria.where(parentKey).is(parentValue);
		query.addCriteria(criteria);
		List<Map> list = tenementTemplate.find(query, Map.class, collectionName);
		if (list.size() != 0) {
			for (Map map : list) {
				sourceIds.add(map);
				recursiveSourceInfos(parentKey, map.get(UscConstants.ID_).toString(), collectionName, sourceIds);
			}
		}
	}

}
