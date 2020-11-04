/*
 * File name: TenAccInfoCrudRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月3日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.base.curd.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.base.crud.TenAccBaseCrudService;
import com.run.usc.api.base.util.ExceptionChecked;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.util.MongoTemplateUtil;
import com.run.usc.base.util.MongoUtils;

/**
 * @Description: 接入方rpc类
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */

public class TenAccInfoCrudRpcSerImpl implements TenAccBaseCrudService {
	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);
	@Autowired
	private MongoTemplate		tenementTemplate;
	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;



	/**
	 * @see com.run.usc.api.base.crud.TenAccBaseCrudService#saveAccessInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> saveAccessInfo(JSONObject tenementAccessInfo) throws Exception {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(tenementAccessInfo.getString(TenementConstant.TENEMENT_ACCESS_NAME))) {
				logger.error(String.format("[saveAccessInfo()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ACCESS_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementAccessInfo.getString(TenementConstant.TENEMENT_ACCESS_ROOT_DOMAIN))) {
				logger.error(String.format("[saveAccessInfo()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ACCESS_ROOT_DOMAIN));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementAccessInfo.getString(UscConstants.ACCESS_SECRET))) {
				logger.error(String.format("[saveAccessInfo()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementAccessInfo.getString(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID))) {
				logger.error(String.format("[saveAccessInfo()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ACCESS_TENEMENT_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			String accessType = tenementAccessInfo.getString(TenementConstant.ACCESS_TYPE);
			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[saveAccessInfo()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 重名校验
			if (nameCheck(tenementAccessInfo.getString(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID),
					tenementAccessInfo.getString(TenementConstant.TENEMENT_ACCESS_NAME), null)) {
				logger.debug(String.format("[saveAccessInfo()->fail:%s]",
						TenementConstant.TENEMENT_ACC_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.TENEMENT_ACC_SAVE_FAIL_NAME_EXITES);
			}

			// 接入方编号
			String id = tenementAccessInfo.getString(UscConstants.ACCESS_SECRET);
			tenementAccessInfo.put(UscConstants.ID_, id);
			tenementAccessInfo.put(TenementConstant.TENEMENT_CREATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			tenementAccessInfo.put(TenementConstant.TENEMENT_UPDATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));

			tenementAccessInfo.put(TenementConstant.TENEMENT_DELETE_STATE, TenementConstant.STATE_NORMAL_ONE);
			// 0停用1正常
			tenementAccessInfo.put(TenementConstant.TENEMENT_STATE, TenementConstant.STATE_NORMAL_ONE);
			tenementTemplate.insert(tenementAccessInfo, MongodbConstants.MONGODB_ACCESS_INFO_COLL);

			// 根据id查询插入的数据返回给前端
			Query query = new Query();
			Criteria criteria = Criteria.where(UscConstants.ID_).is(id);
			query.addCriteria(criteria);

			JSONObject tenementInfoMap = tenementTemplate.findOne(query, JSONObject.class,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);

			if (!StringUtils.isEmpty(tenementInfoMap)) {
				// 默认将这个接入方类型下面所有的权限信息赋予给这个默认的接入方
				// permiBaseCrudService.addAccessRsPermi(id, accessType);暂时注释,勿删

				logger.debug(String.format("[saveAccessInfo()->success:%s]", tenementInfoMap.toJSONString()));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SAVE_SUCC, tenementInfoMap.toJSONString());
			} else {
				logger.debug(String.format("[saveAccessInfo()->fail:%s]", UscConstants.SAVE_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SAVE_FAIL);
			}
		} catch (Exception e) {
			logger.error("[saveAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @Description:重名校验
	 * @param name
	 * @param tenmentId
	 *            租户id
	 * @param silfId
	 *            需要排除的id，一般用户更新
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean nameCheck(String tenmentId, String accessName, String silfId) {
		Query queryT = new Query();
		Criteria criteriaT = Criteria.where(TenementConstant.TENEMENT_ACCESS_NAME).is(accessName)
				.and(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE)
				.and(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID).is(tenmentId);

		criteriaT.and(TenementConstant.TENEMENT_ID).nin(silfId);
		queryT.addCriteria(criteriaT);
		List<? extends Map<String, Object>> tenementInfoListT = (List<? extends Map<String, Object>>) tenementTemplate
				.find(queryT, new HashMap<String, Object>().getClass(), MongodbConstants.MONGODB_ACCESS_INFO_COLL);
		if (tenementInfoListT != null && !tenementInfoListT.isEmpty()) {
			return true;
		}
		return false;
	}



	/**
	 * @see com.run.usc.api.base.crud.TenAccBaseCrudService#updateAccessInfo(java.lang.String)
	 */
	@Override
	public RpcResponse<String> updateAccessInfo(JSONObject tenementAccessInfo) throws Exception {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(tenementAccessInfo.getString(UscConstants.ID_))) {
				logger.error(String.format("[updateAccessInfo()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ID_));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 重名校验
			if (nameCheck(tenementAccessInfo.getString(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID),
					tenementAccessInfo.getString(TenementConstant.TENEMENT_ACCESS_NAME),
					tenementAccessInfo.getString(UscConstants.ID_))) {
				logger.debug("[updateAccessInfo()->fail:" + TenementConstant.TENEMENT_ACC_SAVE_FAIL_NAME_EXITES + "]");
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.TENEMENT_ACC_SAVE_FAIL_NAME_EXITES);
			}

			// 修改接入方信息
			Criteria criteria = Criteria.where(UscConstants.ID_).is(tenementAccessInfo.getString(UscConstants.ID_));
			Query query = new Query(criteria);
			String tenementInfoStr = JSON.toJSONString(tenementAccessInfo);
			Update update = MongoUtils.jsonStringToUpdate(tenementInfoStr);
			update.set(TenementConstant.TENEMENT_UPDATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			WriteResult updateMulti = tenementTemplate.updateMulti(query, update,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			if (updateMulti.getN() > 0) {
				logger.debug(String.format("[updateAccessInfo()->success:%s]", tenementAccessInfo.toJSONString()));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC,
						tenementAccessInfo.toJSONString());
			} else {
				logger.debug(String.format("[updateAccessInfo()->fail:%s]", TenementConstant.UPDATE_TENEMENT_ACC_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("[updateAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.api.base.crud.TenAccBaseCrudService#deleteAccessInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<List<String>> deleteAccessInfo(List<String> ids) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deleteAccessInfo()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 校验接入方下面是否存在接入方资源信息
			boolean checkSour = tenementTemplateUtil.checkIsDelete(MongodbConstants.MONGODB_TEN_RES_INFO_COLL,
					TenementConstant.TENEMENT_ACCESS_ID, ids);
			if (checkSour) {
				logger.error(
						String.format("[deleteAccessInfo()->error:%s]", TenementConstant.DELETE_TENEMENT_ACC_EXI_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.DELETE_TENEMENT_ACC_EXI_FAIL);
			}

			// 校验接入方下面是否存在用户信息
			Query query1 = new Query();
			Criteria criteria1 = Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).in(ids);
			query1.addCriteria(criteria1);
			boolean checkUser = tenementTemplate.exists(query1, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			if (checkUser) {
				logger.error(
						String.format("[deleteAccessInfo()->error:%s]", TenementConstant.DELETE_ACC_USER_EXI_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.DELETE_ACC_USER_EXI_FAIL);
			}

			// 删除接入方
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_ID).in(ids);
			Query query = new Query(criteria);
			Update update = new Update();
			update.set(TenementConstant.TENEMENT_DELETE_STATE, TenementConstant.STATE_STOP_ZERO);
			update.set(TenementConstant.TENEMENT_UPDATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			WriteResult res = tenementTemplate.updateMulti(query, update, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			if (res.getN() > 0) {
				logger.debug(String.format("[deleteAccessInfo()->deleteAccessInfo:%s]", ids));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.DEL_SUCC, ids);
			} else {
				logger.debug(String.format("[deleteAccessInfo()->fail:%s]", TenementConstant.DELETE_TENEMENT_ACC_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.DEL_FAIL);
			}

		} catch (Exception e) {
			logger.error("[deleteAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.TenAccBaseCrudService#switchState(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<String> switchState(JSONObject accStateInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<String> rs = ExceptionChecked.checkRequestKey(logger, "switchState", accStateInfo,
					UscConstants.ID_, UscConstants.STATE);
			if (rs != null) {
				return rs;
			}
			String accessId = accStateInfo.getString(UscConstants.ID_);
			String state = accStateInfo.getString(UscConstants.STATE);

			Criteria criteria = Criteria.where(UscConstants.ID_).is(accessId);
			Query query = new Query(criteria);
			Update update = new Update();
			update.set(UscConstants.STATE, state);
			WriteResult updateMulti = tenementTemplate.updateMulti(query, update,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			if (updateMulti.getN() > 0) {
				logger.debug(String.format("[switchState()->switchState:%s]", update));

				// 停用或者启用所有的接入方资源
				Criteria crSour = Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).is(accessId);
				Query querySour = new Query(crSour);
				Update updateSour = new Update();
				updateSour.set(TenementConstant.TENEMENT_STATE, state);
				updateSour.set(TenementConstant.TENEMENT_UPDATE_DATE,
						DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
				tenementTemplate.updateMulti(querySour, updateSour, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
				// 停用所有的接入方用户
				// 查询关系表中用户集合
				List<Map> listUser = tenementTemplate.find(querySour, Map.class,
						MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
				List userIds = new ArrayList<>();
				for (Map map : listUser) {
					userIds.add(map.get(UscConstants.USER_ID));
				}
				// 停用时判断用户是否在其他应用下
				List deleteUserId = checkUserExistInOtherAccess(userIds, accessId);

				Criteria crUser = Criteria.where(UscConstants.ID_).in(deleteUserId);
				Query queryUser = new Query(crUser);
				Update updateUser = new Update();
				updateUser.set(TenementConstant.TENEMENT_STATE, state);
				updateUser.set(TenementConstant.TENEMENT_UPDATE_DATE,
						DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
				tenementTemplate.updateMulti(queryUser, updateUser, MongodbConstants.MONGODB_USERINFO_COLL);

				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC, null);
			} else {
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
			}

		} catch (Exception e) {
			logger.error("[switchState()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private List<String> checkUserExistInOtherAccess(List<String> userIds, String accessId) {

		// 查询用户存在于其他应用的情况
		Criteria checkUser = Criteria.where(UscConstants.USER_ID).in(userIds)
				.andOperator(Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).nin(accessId));
		Query cUser = new Query(checkUser);

		List<JSONObject> find = tenementTemplate.find(cUser, JSONObject.class,
				MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);

		// 获取应用id,查询应用当前可用状态
		List<String> otherAccessIds = Lists.newArrayList();
		for (JSONObject json : find) {
			String otherAccessId = json.getString(TenementConstant.TENEMENT_ACCESS_ID);
			otherAccessIds.add(otherAccessId);
		}
		// 查询应用的删除状态和停用启用状态
		Criteria checkAccess = Criteria.where(UscConstants.ID_).in(otherAccessIds).andOperator(
				Criteria.where(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE),
				Criteria.where(TenementConstant.TENEMENT_STATE).is(TenementConstant.STATE_NORMAL_ONE));

		DBObject dbObject = new BasicDBObject();
		DBObject fieldObject = new BasicDBObject();
		fieldObject.put(UscConstants.ID_, true);
		Query query = new BasicQuery(dbObject, fieldObject);
		Query cAccess = query.addCriteria(checkAccess);

		List<JSONObject> result = tenementTemplate.find(cAccess, JSONObject.class,
				MongodbConstants.MONGODB_ACCESS_INFO_COLL);

		for (JSONObject json : find) {
			String userId = json.getString(TenementConstant.USER_ID);
			String id = json.getString(TenementConstant.TENEMENT_ACCESS_ID);

			for (JSONObject jsonObject : result) {
				boolean flag = !StringUtils.isEmpty(userId) && jsonObject.containsValue(id);
				if (flag) {
					userIds.remove(userId);
				}
			}

		}

		return userIds;

	}
}
