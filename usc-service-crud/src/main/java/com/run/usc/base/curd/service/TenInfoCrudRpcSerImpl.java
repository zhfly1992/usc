/*
 * File name: TenementInfoCrudRpcServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月26日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.base.curd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.base.crud.TenementBaseCrudService;
import com.run.usc.api.base.util.ExceptionChecked;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.util.MongoTemplateUtil;
import com.run.usc.base.util.MongoUtils;

public class TenInfoCrudRpcSerImpl implements TenementBaseCrudService {
	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);
	@Autowired
	private MongoTemplate		tenementTemplate;
	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;



	/**
	 * @see com.run.usc.api.base.crud.TenementBaseCrudService#saveTenement(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> saveTenement(JSONObject tenementInfo) throws Exception {

		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_NUM))) {
				logger.error(String.format("[saveTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_NUM));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_NAME))) {
				logger.error(String.format("[saveTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_ADDRESS))) {
				logger.error(String.format("[saveTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ADDRESS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_PHONE))) {
				logger.error(String.format("[saveTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_PHONE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 接入方编号
			String code = tenementInfo.getString(TenementConstant.TENEMENT_NUM);
			tenementInfo.put(TenementConstant.TENEMENT_ID, code);
			tenementInfo.put(TenementConstant.TENEMENT_CODE, code);
			tenementInfo.put(TenementConstant.TENEMENT_STATE, TenementConstant.STATE_NORMAL_ONE);// 0停用1正常
			tenementInfo.put(TenementConstant.TENEMENT_CREATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			tenementInfo.put(TenementConstant.TENEMENT_UPDATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			tenementInfo.put(TenementConstant.TENEMENT_DELETE_STATE, TenementConstant.STATE_NORMAL_ONE);

			// 去除权限验证信息
			tenementInfo.remove(UscConstants.AUTHZ_INFO);

			// 重名校验
			String id = tenementInfo.getString(TenementConstant.TENEMENT_ID);
			// 租户名称
			String tenementName = tenementInfo.getString(TenementConstant.TENEMENT_NAME);
			if (this.nameCheack(tenementName, id)) {
				logger.debug(
						String.format("[saveTenement()->fail:%s]", TenementConstant.TENEMENT_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.TENEMENT_SAVE_FAIL_NAME_EXITES);
			}

			// 保存租户
			tenementTemplate.insert(tenementInfo, MongodbConstants.MONGODB_TENEMENT_INFO_COLL);

			// 根据id查询插入的数据返回给前端
			Query query = new Query();
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_ID).is(code)
					.and(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE);
			query.addCriteria(criteria);

			JSONObject tenementInfoMap = tenementTemplate.findOne(query, JSONObject.class,
					MongodbConstants.MONGODB_TENEMENT_INFO_COLL);

			if (!StringUtils.isEmpty(tenementInfoMap)) {
				logger.debug(String.format("[saveTenement()->success:%s]", tenementInfoMap.toJSONString()));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SAVE_SUCC, tenementInfoMap.toJSONString());
			} else {
				logger.debug(String.format("[saveTenement()->fail:%s]", TenementConstant.ADD_TENEMENT_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SAVE_FAIL);
			}
		} catch (Exception e) {
			logger.error("[saveTenement()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.TenementBaseCrudService#updateTenement(java.lang.String)
	 */
	@Override
	public RpcResponse<String> updateTenement(JSONObject tenementInfo) throws Exception {

		try {
			// 没有业务数据
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_PHONE))) {
				logger.error(String.format("[updateTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_PHONE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_NUM))) {
				logger.error(String.format("[updateTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_NUM));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_NAME))) {
				logger.error(String.format("[updateTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_ADDRESS))) {
				logger.error(String.format("[updateTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ADDRESS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(tenementInfo.getString(TenementConstant.TENEMENT_ID))) {
				logger.error(String.format("[updateTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 重名校验
			String id = tenementInfo.getString(TenementConstant.TENEMENT_ID);
			// 租户名称
			String tenementName = tenementInfo.getString(TenementConstant.TENEMENT_NAME);
			if (this.nameCheack(tenementName, id)) {
				logger.debug(
						String.format("[updateTenement()->fail:%s]", TenementConstant.TENEMENT_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.TENEMENT_SAVE_FAIL_NAME_EXITES);
			}

			// 修改租户信息
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_ID).is(id);
			Query query = new Query(criteria);
			String tenementInfoStr = JSON.toJSONString(tenementInfo);
			Update update = MongoUtils.jsonStringToUpdate(tenementInfoStr);
			update.set(TenementConstant.TENEMENT_UPDATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			WriteResult updateMulti = tenementTemplate.updateMulti(query, update,
					MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
			if (updateMulti.getN() > 0) {
				logger.debug(String.format("[updateTenement()->success:%s]", tenementInfo.toJSONString()));
				// 修改接入方关联信息
				JSONObject updateJson = new JSONObject();
				updateJson.put(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME,
						tenementInfo.getString(TenementConstant.TENEMENT_NAME));
				Criteria criteriaAcc = Criteria.where(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID).is(id);
				Query queryAcc = new Query(criteriaAcc);
				String accInfoStr = JSON.toJSONString(updateJson);
				Update updateAcc = MongoUtils.jsonStringToUpdate(accInfoStr);
				update.set(TenementConstant.TENEMENT_UPDATE_DATE,
						DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
				tenementTemplate.updateMulti(queryAcc, updateAcc, MongodbConstants.MONGODB_ACCESS_INFO_COLL);

				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC, tenementInfo.toJSONString());
			} else {
				logger.debug(String.format("[updateTenement()->fail:%s]", TenementConstant.UPDATE_TENEMENT_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
			}

		} catch (Exception e) {
			logger.error("[updateTenement()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:重名校验，// 存在返回true，不存在false
	 * @param name
	 * @param tenementId
	 *            需要排除的id,不需要排除则传递null或者字符串“”；
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean nameCheack(String name, String tenementId) throws Exception {
		Query queryT = new Query();
		Criteria criteriaT = Criteria.where(TenementConstant.TENEMENT_NAME).is(name)
				.and(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE);
		criteriaT.and(TenementConstant.TENEMENT_ID).nin(tenementId);
		queryT.addCriteria(criteriaT);
		Map<String, Object> map = tenementTemplate.findOne(queryT, Map.class,
				MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
		if (StringUtils.isEmpty(map) || map.isEmpty()) {
			return false;
		}
		return true;
	}



	/**
	 * @see com.run.usc.api.base.crud.TenementBaseCrudService#deleteTenement(com.alibaba.fastjson.JSONArray)
	 */
	@Override
	public RpcResponse<List<String>> deleteTenement(List<String> ids) throws Exception {

		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deleteTenement()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 校验租户下面是否有接入方，存在不允许删除
			boolean check = tenementTemplateUtil.checkIsDelete(MongodbConstants.MONGODB_ACCESS_INFO_COLL,
					TenementConstant.TENEMENT_ACCESS_TENEMENT_ID, ids);
			if (check) {
				logger.error(String.format("[deleteTenement()->error:%s]", TenementConstant.DELETE_TENEMENT_EXI_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.DELETE_TENEMENT_EXI_FAIL);
			}
			// 删除租户
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_ID).in(ids);
			Query query = new Query(criteria);
			Update update = new Update();
			update.set(TenementConstant.TENEMENT_DELETE_STATE, TenementConstant.STATE_STOP_ZERO);
			update.set(TenementConstant.TENEMENT_UPDATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			WriteResult res = tenementTemplate.updateMulti(query, update, MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
			if (res.getN() > 0) {
				logger.debug(String.format("[deleteTenement()->deleteTenement:%s]", ids));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.DEL_SUCC, ids);
			} else {
				logger.debug(String.format("[deleteTenement()->fail:%s]", TenementConstant.DELETE_TENEMENT_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.DEL_FAIL);
			}

		} catch (Exception e) {
			logger.error("[deleteTenement()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.TenementBaseCrudService#updateField(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<String> updateField(JSONObject tenementInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<String> rs = ExceptionChecked.checkRequestKey(logger, "updateField", tenementInfo,
					TenementConstant.TENEMENT_ID, TenementConstant.TENEMENT_STATE);
			if (rs != null) {
				return rs;
			}
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_ID)
					.is(tenementInfo.getString(TenementConstant.TENEMENT_ID));
			Query query = new Query(criteria);
			Update update = new Update();
			update.set(TenementConstant.TENEMENT_STATE, tenementInfo.getString(TenementConstant.TENEMENT_STATE));
			WriteResult updateMulti = tenementTemplate.updateMulti(query, update,
					MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
			if (updateMulti.getN() > 0) {
				logger.debug(String.format("[updateField()->updateField:%s]", update));
				// 停用或者启用所有关联的接入方
				Criteria criteriaAcc = Criteria.where(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID)
						.is(tenementInfo.getString(TenementConstant.TENEMENT_ID));
				Query queryAcc = new Query(criteriaAcc);
				Update updateAcc = new Update();
				updateAcc.set(TenementConstant.TENEMENT_STATE, tenementInfo.getString(TenementConstant.TENEMENT_STATE));
				update.set(TenementConstant.TENEMENT_UPDATE_DATE,
						DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
				tenementTemplate.updateMulti(queryAcc, updateAcc, MongodbConstants.MONGODB_ACCESS_INFO_COLL);

				// 查询租户下面所有的接入方
				List<Map> list = tenementTemplate.find(queryAcc, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);

				List listAcc = new ArrayList<>();
				if (null != list && list.size() != 0) {
					for (Map map : list) {
						listAcc.add(map.get(UscConstants.ID_));
					}
				}

				// 停用所有的接入方资源
				Criteria crSour = Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).in(listAcc);
				Query querySour = new Query(crSour);
				Update updateSour = new Update();
				updateSour.set(TenementConstant.TENEMENT_STATE,
						tenementInfo.getString(TenementConstant.TENEMENT_STATE));
				updateSour.set(TenementConstant.TENEMENT_UPDATE_DATE,
						DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
				tenementTemplate.updateMulti(querySour, updateSour, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
				// 停用所有的接入方用户
				// 查询关系表中用户集合
				List<Map> listUser = tenementTemplate.find(querySour, Map.class,
						MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
				List userIds = new ArrayList<>();
				if (null != list && list.size() != 0) {
					for (Map map : listUser) {
						userIds.add(map.get(UscConstants.USER_ID));
					}
				}

				Criteria crUser = Criteria.where(UscConstants.ID_).in(userIds);
				Query queryUser = new Query(crUser);
				Update updateUser = new Update();
				updateUser.set(TenementConstant.TENEMENT_STATE,
						tenementInfo.getString(TenementConstant.TENEMENT_STATE));
				updateUser.set(TenementConstant.TENEMENT_UPDATE_DATE,
						DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
				tenementTemplate.updateMulti(queryUser, updateUser, MongodbConstants.MONGODB_USERINFO_COLL);

				logger.debug(String.format("[updateField()->updateField:%s]",
						tenementInfo.getString(TenementConstant.TENEMENT_ID)));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC,
						tenementInfo.getString(TenementConstant.TENEMENT_ID));

			} else {
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
			}

		} catch (Exception e) {
			logger.error("[updateField()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
