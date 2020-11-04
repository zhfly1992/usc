/*
 * File name: AccSourceCrudRpcSerImpl.java
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

package com.run.usc.base.curd.service;

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
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.WriteResult;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.base.crud.AccSourceBaseCrudService;
import com.run.usc.api.base.util.ExceptionChecked;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.util.MongoTemplateUtil;

/**
 * @Description: 接入方资源crud
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */

public class AccSourceCrudRpcSerImpl implements AccSourceBaseCrudService {

	private static final Logger			logger	= Logger.getLogger(UscConstants.LOGKEY);
	@Autowired
	private MongoTemplate				tenementTemplate;

	@Autowired
	private MongoTemplateUtil			tenementTemplateUtil;

	@Autowired
	private UserRoleBaseQueryService	userRoleQuery;



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#saveAccSourceInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> saveAccSourceInfo(JSONObject accessSourceInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "saveAccSourceInfo", accessSourceInfo,
					TenementConstant.ACCESS_TYPE, TenementConstant.ACCESS_SOURCE_NAME,
					TenementConstant.ACCESS_SOURCE_DESC, TenementConstant.SOURCE_TYPE);
			if (rs != null) {
				return rs;
			}

			// 重名校验
			if (nameCheck(accessSourceInfo.getString(TenementConstant.ACCESS_TYPE),
					accessSourceInfo.getString(TenementConstant.ACCESS_SOURCE_NAME), null,
					accessSourceInfo.getString(TenementConstant.SOURCE_TYPE), null)) {
				logger.debug(String.format("[saveAccSourceInfo()->fail:%s]",
						TenementConstant.ACC_SOURCE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_SOURCE_SAVE_FAIL_NAME_EXITES);
			}

			// 插入操作
			String id = UUID.randomUUID().toString().replace("-", "");
			return tenementTemplateUtil.insertId(logger, "saveAccSourceInfo", accessSourceInfo,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL, id);

		} catch (Exception e) {
			logger.error("[saveAccSourceInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#saveAccSourceInfo(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<JSONObject> saveAccSourceInfoBySecret(JSONObject accessSourceInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "saveAccSourceInfoBySecret",
					accessSourceInfo, UscConstants.ACCESS_SECRET, TenementConstant.ACCESS_SOURCE_NAME,
					UscConstants.SOURCE_TYPE);
			if (rs != null) {
				return rs;
			}
			String accessSecret = accessSourceInfo.getString(UscConstants.ACCESS_SECRET);
			String sourceName = accessSourceInfo.getString(TenementConstant.ACCESS_SOURCE_NAME);
			String sourceType = accessSourceInfo.getString(UscConstants.SOURCE_TYPE);
			String parentId = accessSourceInfo.getString(UscConstants.PARENT_ID);

			// 封装查询条件
			Query checkOrgName = new Query(Criteria.where(UscConstants.ACCESSID).is(accessSecret));

			if (!org.apache.commons.lang3.StringUtils.isBlank(parentId)) {
				checkOrgName.addCriteria(Criteria.where(UscConstants.PARENT_ID).is(parentId));
			}
			checkOrgName.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			checkOrgName.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(sourceType));
			checkOrgName.addCriteria(Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).is(sourceName));

			// 校验名称是否重复
			Boolean check = tenementTemplate.exists(checkOrgName, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			if (check) {
				logger.error("saveAccSourceInfoBySecret()-->组织名重复");
				return RpcResponseBuilder.buildErrorRpcResp("组织名重复,添加失败");
			}

			// 根据接入方秘钥查询接入方id
			String secret = accessSourceInfo.getString(UscConstants.ACCESS_SECRET);
			Query query = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(secret));
			Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			// 接入方
			String accessId = (String) map.get(UscConstants.ID_);
			String accessName = (String) map.get(TenementConstant.TENEMENT_ACCESS_NAME);
			accessSourceInfo.put(UscConstants.ACCESSID, accessId);
			accessSourceInfo.put(TenementConstant.TENEMENT_ACCESS_NAME, accessName);

			String accessType = accessSourceInfo.getString(AuthzConstants.ACCESS_TYPE);
			if (StringUtils.isEmpty(accessType)) {
				// 这里假如为空。默认为locman添加组织的业务。如果后期还有很多应用。这儿需要改动和完善，，，
				accessSourceInfo.put(AuthzConstants.ACCESS_TYPE, "LOCMAN");
			}

			// 插入操作
			String id = UUID.randomUUID().toString().replace("-", "");
			return tenementTemplateUtil.insertId(logger, "saveAccSourceInfoBySecret", accessSourceInfo,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL, id);

		} catch (Exception e) {
			logger.error("[saveAccSourceInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#updateAccSourceInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> updateAccSourceInfo(JSONObject accessSourceInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateAccSourceInfo",
					accessSourceInfo, TenementConstant.ACCESS_TYPE, TenementConstant.ACCESS_SOURCE_NAME,
					TenementConstant.ACCESS_SOURCE_DESC, UscConstants.ID_, TenementConstant.SOURCE_TYPE);
			if (rs != null) {
				return rs;
			}

			// 修改操作
			String id = accessSourceInfo.getString(UscConstants.ID_);

			// 重名校验
			if (nameCheck(accessSourceInfo.getString(TenementConstant.ACCESS_TYPE),
					accessSourceInfo.getString(TenementConstant.ACCESS_SOURCE_NAME), id,
					accessSourceInfo.getString(TenementConstant.SOURCE_TYPE), null)) {
				logger.debug(String.format("[saveAccSourceInfo()->fail:%s]",
						TenementConstant.ACC_SOURCE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_SOURCE_SAVE_FAIL_NAME_EXITES);
			}

			return tenementTemplateUtil.update(logger, "updateAccSourceInfo", accessSourceInfo,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL, id);

		} catch (Exception e) {
			logger.error("[updateAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#updateSourceBySecret(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<JSONObject> updateSourceBySecret(JSONObject accessSourceInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateSourceBySecret",
					accessSourceInfo, UscConstants.ACCESS_SECRET, TenementConstant.ACCESS_SOURCE_NAME,
					UscConstants.ID_);
			if (rs != null) {
				return rs;
			}
			// 根据接入方秘钥查询接入方id
			String secret = accessSourceInfo.getString(UscConstants.ACCESS_SECRET);
			Query query = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(secret));
			Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			// 接入方类型
			String accessType = (String) map.get(UscConstants.ACCESSTYPE);
			String accessId = map.get(UscConstants.ID_) + "";
			String sourceType = accessSourceInfo.getString(TenementConstant.SOURCE_TYPE);
			String sourceName = accessSourceInfo.getString(TenementConstant.ACCESS_SOURCE_NAME);
			// 修改操作
			String id = accessSourceInfo.getString(UscConstants.ID_);

			// 重名校验
			if (nameCheck(accessType, sourceName, id, sourceType, accessId)) {
				logger.debug(String.format("[updateSourceBySecret()->fail:%s]",
						TenementConstant.ACC_SOURCE_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_SOURCE_SAVE_FAIL_NAME_EXITES);
			}

			return tenementTemplateUtil.update(logger, "updateSourceBySecret", accessSourceInfo,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL, id);

		} catch (Exception e) {
			logger.error(String.format("[updateSourceBySecret()->exception:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#deleteAccSourceInfo(java.util.List)
	 */
	@Override
	public RpcResponse<List<String>> deleteAccSourceInfo(List<String> ids) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deleteAccSourceInfo()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			return tenementTemplateUtil.delete(logger, "deleteAccSourceInfo",
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL, ids);
		} catch (Exception e) {
			logger.error(String.format("[updateAccessInfo()->exception:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:重名校验
	 * @param accessType
	 *            接入方类型 sourceName 资源名称 silfId 需要排除的id，一般用户更新 sourceType
	 *            资源类型(组织资源,菜单资源等) accessId 接人方id
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean nameCheck(String accessType, String sourceName, String silfId, String sourceType, String accessId) {
		Query queryT = new Query();
		Criteria criteriaT = Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).is(sourceName)
				.and(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE)
				.and(TenementConstant.ACCESS_TYPE).is(accessType).and(TenementConstant.SOURCE_TYPE).is(sourceType)
				.and(TenementConstant.TENEMENT_ACCESS_ID).is(accessId);

		criteriaT.and(UscConstants.ID_).nin(silfId);
		queryT.addCriteria(criteriaT);
		List<? extends Map<String, Object>> tenementInfoListT = (List<? extends Map<String, Object>>) tenementTemplate
				.find(queryT, new HashMap<String, Object>(16).getClass(), MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
		if (tenementInfoListT != null && !tenementInfoListT.isEmpty()) {
			return true;
		}
		return false;
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#swateSourceState(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<String> swateSourceState(String id, String state) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(id)) {
				logger.error(String.format("[swateSourceState()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(state)) {
				logger.error(String.format("[swateSourceState()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 根据资源id查询他所有的子类资源
			List<String> allIds = new ArrayList<>();
			getAllChildList(id, allIds);

			// 校验这些组织是否关联了岗位,关联了岗位不能禁用
			for (String orgId : allIds) {
				RpcResponse<List<Map>> res = userRoleQuery.getRoleListByOrgId(orgId);
				List<Map> map = res.getSuccessValue();
				if (null != map && map.size() != 0) {
					Query queryOrg = new Query(Criteria.where(UscConstants.ID_).is(orgId));
					Map mapMess = tenementTemplate.findOne(queryOrg, Map.class,
							MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
					logger.error(String.format(UscConstants.SWATE_FAIL, mapMess.get(UscConstants.SOURCE_NAME)));
					return RpcResponseBuilder.buildErrorRpcResp(
							String.format(UscConstants.SWATE_FAIL, mapMess.get(UscConstants.SOURCE_NAME)));
				}
			}

			// 修改接入方资源状态
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ID_).in(allIds));
			Update update = new Update();
			update.set(UscConstants.STATE, state);
			WriteResult updateMulti = tenementTemplate.updateMulti(query, update,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL);

			if (updateMulti.getN() > 0) {
				logger.debug(String.format("[swateSourceState()->success:%s]", id));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC, id);
			} else {
				logger.error(String.format("[swateSourceState()->fail:%s", UscConstants.UPDATE_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error(String.format("[swateSourceState()->exception:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("unchecked")
	private void getAllChildList(String id, List<String> allIds) {
		allIds.add(id);
		// 根据资源id查询他所有的子类资源
		Query queryChild = new Query();
		queryChild.addCriteria(Criteria.where(UscConstants.PARENT_ID).is(id));
		List<String> listIds = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_TEN_RES_INFO_COLL, queryChild,
				UscConstants.ID_);
		for (String ids : listIds) {
			getAllChildList(ids, allIds);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#sourceAuthoriz(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<String> sourceAuthoriz(String sourceId, JSONArray urlIds) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(sourceId)) {
				logger.error(String.format("[sourceAuthoriz()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			if (null == urlIds || urlIds.isEmpty()) {
				logger.error(String.format("[sourceAuthoriz()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.URLIDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 组装插入数据
			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < urlIds.size(); i++) {
				Map<String, Object> map = new HashMap<>(16);
				String id = UUID.randomUUID().toString().replace("-", "");
				String urlId = urlIds.getString(i);
				map.put(UscConstants.ID_, id);
				map.put(UscConstants.URLID, urlId);
				map.put(UscConstants.SOURCE_ID, sourceId);
				maps.add(map);
			}
			tenementTemplate.insert(maps, MongodbConstants.MONGODB_URL_SOURCE_COLL);

			logger.debug(String.format("[sourceAuthoriz()->success:%s]", urlIds));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SAVE_SUCC, urlIds.toJSONString());

		} catch (Exception e) {
			logger.error(String.format("[sourceAuthoriz()->exception:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#delSourceAuthoriz(java.lang.String,
	 *      com.alibaba.fastjson.JSONArray)
	 */
	@Override
	public RpcResponse<String> delSourceAuthoriz(String sourceId, JSONArray urlIds) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(sourceId)) {
				logger.error(String.format("[delSourceAuthoriz()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			if (null == urlIds || urlIds.isEmpty()) {
				logger.error(String.format("[delSourceAuthoriz()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.URLIDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 组装插入数据
			for (int i = 0; i < urlIds.size(); i++) {
				String urlId = urlIds.getString(i);
				Query query = new Query();
				query.addCriteria(Criteria.where(UscConstants.URLID).is(urlId));
				query.addCriteria(Criteria.where(UscConstants.SOURCE_ID).is(sourceId));
				tenementTemplate.remove(query, MongodbConstants.MONGODB_URL_SOURCE_COLL);
			}

			logger.debug(String.format("[delSourceAuthoriz()->success:%s]", urlIds));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.DEL_SUCC, urlIds.toJSONString());

		} catch (Exception e) {
			logger.error(String.format("[delSourceAuthoriz()->exception:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccSourceBaseCrudService#deleteCascadeAccSourceInfo(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<String>> deleteCascadeAccSourceInfo(List<String> ids) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deleteAccSourceInfo()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 删除子组织
			List<Map> childLists = Lists.newArrayList();
			for (String parentValue : ids) {
				if (StringUtils.isEmpty(parentValue)) {
					continue;
				}
				tenementTemplateUtil.recursiveSourceInfos(UscConstants.PARENT_ID, parentValue,
						MongodbConstants.MONGODB_TEN_RES_INFO_COLL, childLists);
			}
			for (Map map : childLists) {
				ids.add(map.get(UscConstants.ID_) + "");
			}
			return tenementTemplateUtil.delete(logger, "deleteAccSourceInfo",
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL, ids);

		} catch (Exception e) {
			logger.error(String.format("[deleteCascadeAccSourceInfo()->exception:%s", e));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
