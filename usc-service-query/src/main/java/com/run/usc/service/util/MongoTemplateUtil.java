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

package com.run.usc.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description:MongoTemplateUtil QUERY工具类
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
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

		if (!StringUtils.isEmpty(map)) {
			logger.debug(String.format("[%s()->success:%s]", methodName, map));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, map);
		} else {
			logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.GET_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
		}
	}



	/**
	 * 
	 * @Description:根据表父类id查询list集合
	 * @param logger
	 * @param methodName
	 * @param collectionName
	 * @param parentKey
	 * @param parentValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public RpcResponse<List<Map>> getModelByParentId(Logger logger, String methodName, String collectionName,
			String parentKey, String parentValue) {

		try {

			List<Map> sourcesInfos = Lists.newArrayList();
			recursiveSourceInfos(parentKey, parentValue, collectionName, sourcesInfos);

			if (sourcesInfos != null && sourcesInfos.size() != 0) {
				logger.debug(String.format("[%s()->success:%s]", methodName, sourcesInfos));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, sourcesInfos);
			} else {
				logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
			}
		} catch (Exception e) {
			logger.error("getModelByParentId()->", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

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



	@SuppressWarnings("unchecked")
	public void findParentId(Logger logger, String methodName, List<String> ids, String tableName) throws Exception {
		Query orgQueryInfo = new Query(Criteria.where(UscConstants.ID_).is(ids.get(0)));
		Map<String, Object> findOne = tenementTemplate.findOne(orgQueryInfo, Map.class, tableName);

		if (findOne == null) {
			logger.error(String.format("[%s()->error:%s]", methodName, UscConstants.GET_FAIL));
		}

		String parentId = (String) findOne.get(UscConstants.PARENT_ID);
		String resourceId = (String) findOne.get(UscConstants.ID_);

		// 如果为null，说明是最上级的id
		if (StringUtils.isEmpty(parentId)) {
			ids.set(0, resourceId);
		} else {
			ids.set(0, parentId);
			logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.RECURSION_ID));
			findParentId(logger, methodName, ids, tableName);
		}
	}



	@SuppressWarnings("unchecked")
	public void findAllParentId(Logger logger, String methodName, List<String> ids, String tableName) throws Exception {
		String sss=ids.get(0);
//		String str=sss.replaceAll("\r\n", "");
		Query orgQueryInfo = new Query(Criteria.where(UscConstants.ID_).is(sss));
		Map<String, Object> findOne = tenementTemplate.findOne(orgQueryInfo, Map.class, tableName);

		if (findOne == null) {
			logger.error(String.format("[%s()->error:%s]", methodName, UscConstants.GET_FAIL));
		}

		Object parentId = findOne.get(UscConstants.PARENT_ID);
		String resourceId = (String) findOne.get(UscConstants.ID_);

		// 如果为null，说明是最上级的id
		if (StringUtils.isEmpty(parentId)) {
			return;
		} else {
			ids.add(resourceId);
			ids.set(0, parentId.toString());
			logger.debug(String.format("[%s()->fail:%s]", methodName, UscConstants.RECURSION_ID));
			findAllParentId(logger, methodName, ids, tableName);
		}
	}
}
