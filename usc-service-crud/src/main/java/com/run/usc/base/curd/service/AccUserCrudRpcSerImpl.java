/*
 * File name: AccUserCrudRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月5日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.base.curd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.run.authz.api.base.crud.UserRoleBaseCrudService;
import com.run.authz.base.query.AuthzBaseQueryService;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.base.crud.AccUserBaseCrudService;
import com.run.usc.api.base.util.ExceptionChecked;
import com.run.usc.api.base.util.RSAUtil;
import com.run.usc.api.base.util.TimeUtil;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.util.MongoTemplateUtil;

/**
 * @Description: 用户中心crud-rpc
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */

public class AccUserCrudRpcSerImpl implements AccUserBaseCrudService {
	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);
	@Autowired
	private MongoTemplate			tenementTemplate;

	@Autowired
	private MongoTemplateUtil		tenementTemplateUtil;

	@Autowired
	private UserRoleBaseCrudService	userRole;

	@Autowired
	private AuthzBaseQueryService	authzQueryRpcService;



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#saveAccUserInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> saveAccUserInfo(JSONObject accessUserInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "saveAccUserInfo", accessUserInfo,
					TenementConstant.TENEMENT_ACCESS_ID, UscConstants.LOGIN_ACCOUNT, UscConstants.PASSWORD,
					UscConstants.MOBILE);
			if (rs != null) {
				return rs;
			}

			// 接入方id
			String accessId = accessUserInfo.getString(TenementConstant.TENEMENT_ACCESS_ID);
			// 重名校验
			JSONArray accArray = new JSONArray();
			accArray.add(accessId);
			if (nameCheck(accArray, accessUserInfo.getString(UscConstants.LOGIN_ACCOUNT), null)) {
				logger.debug(
						String.format("[saveAccUserInfo()->fail:%s]", TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES);
			}
			
			/** -----------------------新加代码------------------ */

			// 从redis缓存当中查询验证码
			String sendNum = accessUserInfo.getString(UscConstants.SEND_NUM);
			RpcResponse<String> cacheValueById = authzQueryRpcService.getCacheValueById(sendNum);
			if (!cacheValueById.isSuccess()) {
				logger.error(String.format("[saveAccUserInfo()->error,%s]", UscConstants.SIGN_CHECK_ERRO));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SIGN_CHECK_ERRO);
			}

			// 密码校验
			RpcResponse<String> privateKey = authzQueryRpcService.getCacheValueById(UscConstants.PRIVATE_KEY);
			if (!privateKey.isSuccess()) {
				logger.error(String.format("[saveAccUserInfo()->error:%s]", "password decrypt fail,can not get privateKey"));
				return RpcResponseBuilder.buildErrorRpcResp("解密失败，获取私钥失败");
			}

			String password = accessUserInfo.getString(UscConstants.PASSWORD);
			// 通过私钥解密密码
			String decryptedPassword = RSAUtil.decrypt(password, privateKey.getSuccessValue());
			logger.info("saveAccUserInfo()->解密出的密码为：" + decryptedPassword);
			// 判断密码格式
			if (!decryptedPassword.matches(UscConstants.PASSWOR_FORMAT) || decryptedPassword.length() < 8 || decryptedPassword.length() > 30) {
				logger.error(String.format("[saveAccUserInfo()->error:%s]", UscConstants.PASSWORD_FORMAT_ERROR));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PASSWORD_FORMAT_ERROR);
			}
			
			String md5pass = DigestUtils.md5Hex(decryptedPassword);
			logger.info(String.format("[saveAccUserInfo()->md5加密后的密码:%s]", md5pass));
			accessUserInfo.put(UscConstants.PASSWORD, md5pass);
			/** -----------------------新加代码完------------------ */

			// 去除jsonObject的accessId
			accessUserInfo.remove(TenementConstant.TENEMENT_ACCESS_ID);
			accessUserInfo.put(UscConstants.ExpiredDate, TimeUtil.getExpiredDate());
			// 插入用户表操作
			String id = UUID.randomUUID().toString().replace("-", "");
			RpcResponse<JSONObject> res = tenementTemplateUtil.insertId(logger, "saveAccUserInfo", accessUserInfo,
					MongodbConstants.MONGODB_USERINFO_COLL, id);

			// 插入用户与接入方关系表中
			String rsId = UUID.randomUUID().toString().replace("-", "");
			JSONObject accuserRs = new JSONObject();
			accuserRs.put(UscConstants.ID_, rsId);
			accuserRs.put(TenementConstant.USER_ID, id);
			accuserRs.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);
			tenementTemplate.insert(accuserRs, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);

			return res;

		} catch (Exception e) {
			logger.error("[saveAccSourceInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#saveUserRs(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> saveUserRs(JSONObject accessUserInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "saveAccUserInfo", accessUserInfo,
					UscConstants.ACCESS_SECRET, UscConstants.USERNAME, UscConstants.LOGIN_ACCOUNT,
					UscConstants.PASSWORD, UscConstants.MOBILE, UscConstants.ROLE_INFO, UscConstants.SEND_NUM);
			if (rs != null) {
				return rs;
			}
			
			/** -----------------------新加代码------------------ */

			// 从redis缓存当中查询验证码
			String sendNum = accessUserInfo.getString(UscConstants.SEND_NUM);
			RpcResponse<String> res = authzQueryRpcService.getCacheValueById(sendNum);
			if (!res.isSuccess()) {
				logger.error(String.format("[saveUserRs()->error,%s]", UscConstants.SIGN_CHECK_ERRO));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SIGN_CHECK_ERRO);
			}

			// 密码校验
			RpcResponse<String> privateKey = authzQueryRpcService.getCacheValueById(UscConstants.PRIVATE_KEY);
			if (!privateKey.isSuccess()) {
				logger.error(String.format("[saveUserRs()->error:%s]", "password decrypt fail,can not get privateKey"));
				return RpcResponseBuilder.buildErrorRpcResp("解密失败，获取私钥失败");
			}

			String password = accessUserInfo.getString(UscConstants.PASSWORD);
			// 通过私钥解密密码
			String decryptedPassword = RSAUtil.decrypt(password, privateKey.getSuccessValue());
			logger.info("saveUserRs()->解密出的密码为：" + decryptedPassword);
			// 判断密码格式
			if (!decryptedPassword.matches(UscConstants.PASSWOR_FORMAT) || decryptedPassword.length() < 8 || decryptedPassword.length() > 30) {
				logger.error(String.format("[saveUserRs()->error:%s]", UscConstants.PASSWORD_FORMAT_ERROR));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PASSWORD_FORMAT_ERROR);
			}
			
			String md5pass = DigestUtils.md5Hex(decryptedPassword);
			logger.info(String.format("[saveUserRs()->md5加密后的密码:%s]", md5pass));
			accessUserInfo.put(UscConstants.PASSWORD, md5pass);
			/** -----------------------新加代码完------------------ */
			

			// 角色信息
			JSONArray roleJson = accessUserInfo.getJSONArray(UscConstants.ROLE_INFO);
			if (roleJson == null || StringUtils.isEmpty(roleJson)) {
				logger.error(String.format("[saveUserRs()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ROLE_INFO));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[saveUserRs()->erro:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ROLE_INFO));
			}

			// 接入方秘钥
			String accessSecret = accessUserInfo.getString(UscConstants.ACCESS_SECRET);
			// 根据接入方秘钥查询接入方id
			Query query = new Query(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			JSONObject accessInfo = tenementTemplate.findOne(query, JSONObject.class,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			String accessId = (String) accessInfo.get(UscConstants.ID_);
			// 重名校验
			JSONArray accArray = new JSONArray();
			accArray.add(accessId);
			if (nameCheck(accArray, accessUserInfo.getString(UscConstants.LOGIN_ACCOUNT), null)) {
				logger.debug(
						String.format("[saveAccUserInfo()->fail:%s]", TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES);
			}

			if (nameCheck(accArray, accessUserInfo.getString(UscConstants.MOBILE), null)) {
				logger.debug(String.format("[saveAccUserInfo()->fail:%s]",
						TenementConstant.ACC_USER_SAVE_FAIL_MOBILE_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_USER_SAVE_FAIL_MOBILE_EXITES);
			}

			// 去除jsonObject的accessId
			accessUserInfo.remove(UscConstants.ACCESS_SECRET);
			accessUserInfo.remove(UscConstants.ROLE_INFO);
			// 插入用户表操作
			String id = UUID.randomUUID().toString().replace("-", "");
			accessUserInfo.put(UscConstants.ID_, id);
			accessUserInfo.put(UscConstants.USER_CODE, id);
			accessUserInfo.put(UscConstants.ACTIVATE_STATE, UscConstants.STATE_NORMAL_ONE);
			accessUserInfo.put(UscConstants.IS_DELETE, UscConstants.STATE_NORMAL_ONE);
			accessUserInfo.put(UscConstants.STATE, UscConstants.STATE_NORMAL_ONE);
			accessUserInfo.put(TenementConstant.TENEMENT_CREATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			accessUserInfo.put(TenementConstant.TENEMENT_UPDATE_DATE,
					DateUtils.stampToDate(Long.toString(System.currentTimeMillis())));
			accessUserInfo.put(UscConstants.ExpiredDate, TimeUtil.getExpiredDate());
			tenementTemplate.insert(accessUserInfo, MongodbConstants.MONGODB_USERINFO_COLL);

			// 插入用户与接入方关系表中
			String rsId = UUID.randomUUID().toString().replace("-", "");
			JSONObject accuserRs = new JSONObject();
			accuserRs.put(UscConstants.ID_, rsId);
			accuserRs.put(TenementConstant.USER_ID, id);
			accuserRs.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);
			tenementTemplate.insert(accuserRs, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);

			// 插入用户与角色的关系
			for (int i = 0; i < roleJson.size(); i++) {
				JSONObject userRoleRs = roleJson.getJSONObject(i);
				userRoleRs.put(UscConstants.ACCESSID, accessId);
				userRoleRs.put(UscConstants.USER_ID, id);
				userRole.saveUserRoleRs(userRoleRs);
			}
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SAVE_SUCC, accessUserInfo);

		} catch (Exception e) {
			logger.error("[saveAccSourceInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#saveUserRs(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> updateUserRs(JSONObject accessUserInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateUserRs", accessUserInfo,
					UscConstants.ID_, UscConstants.ACCESS_SECRET, UscConstants.USERNAME, UscConstants.PASSWORD,
					UscConstants.MOBILE, UscConstants.ROLE_INFO);
			if (rs != null) {
				return rs;
			}

			// 用户id
			String userId = accessUserInfo.getString(UscConstants.ID_);

			// 角色信息
			JSONArray roleJson = accessUserInfo.getJSONArray(UscConstants.ROLE_INFO);
			if (roleJson == null || StringUtils.isEmpty(roleJson)) {
				logger.error(String.format("[updateUserRs()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ROLE_INFO));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[updateUserRs()->erro:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ROLE_INFO));
			}
			
			/** -----------------------新加代码------------------ */
			String sendNum = accessUserInfo.getString(UscConstants.SEND_NUM);
			//验证码为空，没有修改密码
			if (StringUtils.isEmpty(sendNum)) {
				// 密码校验
				logger.info("updateUserRs()->password not change");
				RpcResponse<String> privateKey = authzQueryRpcService.getCacheValueById(UscConstants.PRIVATE_KEY);
				if (!privateKey.isSuccess()) {
					logger.error(String.format("[saveUserRs()->error:%s]", "password decrypt fail,can not get privateKey"));
					return RpcResponseBuilder.buildErrorRpcResp("解密失败，获取私钥失败");
				}

				String password = accessUserInfo.getString(UscConstants.PASSWORD);
				// 通过私钥解密密码
				String decryptedPassword = RSAUtil.decrypt(password, privateKey.getSuccessValue());
				logger.info("saveUserRs()->解密出的密码为：" + decryptedPassword);
				accessUserInfo.put(UscConstants.PASSWORD, decryptedPassword);
			}
			else{
				// 验证码不为空，密码被修改，密码校验
				RpcResponse<String> res = authzQueryRpcService.getCacheValueById(sendNum);
				if (!res.isSuccess()) {
					logger.error(String.format("[saveUserRs()->error,%s]", UscConstants.SIGN_CHECK_ERRO));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SIGN_CHECK_ERRO);
				}

				RpcResponse<String> privateKey = authzQueryRpcService.getCacheValueById(UscConstants.PRIVATE_KEY);
				if (!privateKey.isSuccess()) {
					logger.error(String.format("[saveUserRs()->error:%s]", "password decrypt fail,can not get privateKey"));
					return RpcResponseBuilder.buildErrorRpcResp("解密失败，获取私钥失败");
				}

				String password = accessUserInfo.getString(UscConstants.PASSWORD);
				// 通过私钥解密密码
				String decryptedPassword = RSAUtil.decrypt(password, privateKey.getSuccessValue());
				logger.info("saveUserRs()->解密出的密码为：" + decryptedPassword);
				// 判断密码格式
				if (!decryptedPassword.matches(UscConstants.PASSWOR_FORMAT) || decryptedPassword.length() < 8 || decryptedPassword.length() > 30) {
					logger.error(String.format("[saveUserRs()->error:%s]", UscConstants.PASSWORD_FORMAT_ERROR));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PASSWORD_FORMAT_ERROR);
				}
				
				String md5pass = DigestUtils.md5Hex(decryptedPassword);
				logger.info(String.format("[saveUserRs()->md5加密后的密码:%s]", md5pass));
				accessUserInfo.put(UscConstants.PASSWORD, md5pass);
			}
			
			
			/** -----------------------新加代码完------------------ */
			
			
			
			

			// 接入方秘钥
			String accessSecret = accessUserInfo.getString(UscConstants.ACCESS_SECRET);

			// 根据接入方秘钥查询接入方id
			Query query = new Query(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			JSONObject accessInfo = tenementTemplate.findOne(query, JSONObject.class,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			String accessId = (String) accessInfo.get(UscConstants.ID_);

			// 重名校验
			JSONArray accArray = new JSONArray();
			accArray.add(accessId);
			if (nameCheck(accArray, accessUserInfo.getString(UscConstants.LOGIN_ACCOUNT), userId)) {
				logger.debug(
						String.format("[updateUserRs()->fail:%s]", TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES);
			}

			if (nameCheck(accArray, accessUserInfo.getString(UscConstants.MOBILE), userId)) {
				logger.debug(String.format("[saveAccUserInfo()->fail:%s]",
						TenementConstant.ACC_USER_SAVE_FAIL_MOBILE_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_USER_SAVE_FAIL_MOBILE_EXITES);
			}

			// 去除jsonObject的accessId
			accessUserInfo.remove(UscConstants.ACCESS_SECRET);
			accessUserInfo.remove(UscConstants.ROLE_INFO);

			// 修改用户表操作
			RpcResponse<JSONObject> res = tenementTemplateUtil.update(logger, "updateUserRs", accessUserInfo,
					MongodbConstants.MONGODB_USERINFO_COLL, userId);

			// 删除用户与角色关系表->通过接入方密钥区分
			// userRole.delUserRoleRs(userId);
			userRole.delUserRoleRs(userId, accessSecret);

			// 插入用户与角色的关系
			for (int i = 0; i < roleJson.size(); i++) {
				JSONObject userRoleRs = roleJson.getJSONObject(i);
				userRoleRs.put(UscConstants.ACCESSID, accessId);
				userRoleRs.put(UscConstants.USER_ID, userId);
				userRole.saveUserRoleRs(userRoleRs);
			}
			return res;

		} catch (Exception e) {
			logger.error("[updateUserRs()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#updateAccUserInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<JSONObject> updateAccUserInfo(JSONObject accessUserInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updateAccUserInfo", accessUserInfo,
					TenementConstant.TENEMENT_ACCESS_ID, UscConstants.LOGIN_ACCOUNT, UscConstants.MOBILE,
					UscConstants.ID_);
			if (rs != null) {
				return rs;
			}

			// 修改操作
			String id = accessUserInfo.getString(UscConstants.ID_);

			// 重名校验
			if (nameCheck(accessUserInfo.getJSONArray(TenementConstant.TENEMENT_ACCESS_ID),
					accessUserInfo.getString(UscConstants.LOGIN_ACCOUNT), id)) {
				logger.debug(
						String.format("[saveAccUserInfo()->fail:%s]", TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES));
				return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACC_USER_SAVE_FAIL_NAME_EXITES);
			}

			// 1:代表用户修改的时候没有任何接入方。需要插入新的接入方
			if (accessUserInfo.getInteger("addAccessUserRs") == 1) {
				// 接入方id
				String accessId = accessUserInfo.getJSONArray(TenementConstant.TENEMENT_ACCESS_ID).getString(0);
				// 插入用户与接入方关系表中
				String rsId = UUID.randomUUID().toString().replace("-", "");
				JSONObject accuserRs = new JSONObject();
				accuserRs.put(UscConstants.ID_, rsId);
				accuserRs.put(TenementConstant.USER_ID, id);
				accuserRs.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);
				tenementTemplate.insert(accuserRs, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			}

			// 修改账号的同时，修改用户名
			accessUserInfo.put(UscConstants.USERNAME, accessUserInfo.getString(UscConstants.LOGIN_ACCOUNT));
			return tenementTemplateUtil.update(logger, "updateAccSourceInfo", accessUserInfo,
					MongodbConstants.MONGODB_USERINFO_COLL, id);

		} catch (Exception e) {
			logger.error("[updateAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#deleteAccUserInfo(java.util.List)
	 */
	@Override
	public RpcResponse<List<String>> deleteAccUserInfo(List<String> ids) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[deleteAccUserInfo()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			RpcResponse<List<String>> res = tenementTemplateUtil.delete(logger, "deleteAccUserInfo",
					MongodbConstants.MONGODB_USERINFO_COLL, ids);
			if (res.isSuccess()) {
				// 删除关系表
				Query query = new Query(Criteria.where(UscConstants.USER_ID).in(ids));
				tenementTemplate.remove(query, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			}
			return res;
		} catch (Exception e) {
			logger.error("[updateAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:重名校验
	 * @param name
	 * @param accessId
	 *            接人方id
	 * @param silfId
	 *            需要排除的id，一般用户更新
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean nameCheck(JSONArray accessId, String loginAccount, String silfId) {
		// 查询关系表获取接入方下面所有的用户id
		Query queryRs = new Query();
		Criteria criteriaT = Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).in(accessId);
		criteriaT.and(UscConstants.USER_ID).nin(silfId);
		queryRs.addCriteria(criteriaT);
		// 获取所有用户ids
		List<String> userIds = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL, queryRs,
				UscConstants.USER_ID);

		// 查询该用户所属接入方是否存在该登录名
		Query queryUser = new Query();
		queryUser.addCriteria(Criteria.where(UscConstants.ID_).in(userIds));
		queryUser.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
		queryUser.addCriteria(Criteria.where(UscConstants.LOGIN_ACCOUNT).is(loginAccount));

		List<? extends Map<String, Object>> tenementInfoListT = (List<? extends Map<String, Object>>) tenementTemplate
				.find(queryUser, new HashMap<String, Object>().getClass(), MongodbConstants.MONGODB_USERINFO_COLL);
		if (tenementInfoListT != null && !tenementInfoListT.isEmpty()) {
			return true;
		}
		return false;
	}



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#saveAccessUserRs(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> saveAccessUserRs(JSONObject accessUserInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<String> rs = ExceptionChecked.checkRequestKey(logger, "saveAccessUserRs", accessUserInfo,
					UscConstants.USER_ID, UscConstants.ACCESSCODE);
			if (rs != null) {
				return rs;
			}

			// 接入方code
			String accessCode = accessUserInfo.getString(TenementConstant.TENEMENT_ACCESS_CODE);
			// 用户ids
			String userIds = accessUserInfo.getString(UscConstants.USER_ID);

			String[] ids = userIds.split(",");
			for (String id : ids) {
				// 插入用户与接入方关系表中
				String rsId = UUID.randomUUID().toString().replace("-", "");
				JSONObject accuserRs = new JSONObject();
				accuserRs.put(UscConstants.ID_, rsId);
				accuserRs.put(TenementConstant.USER_ID, id);
				accuserRs.put(TenementConstant.TENEMENT_ACCESS_ID, accessCode);
				tenementTemplate.insert(accuserRs, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			}
			logger.debug(String.format("[saveAccessUserRs()->saveAccessUserRs:%s]", userIds));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.ADD_SUCC, userIds);

		} catch (Exception e) {
			logger.error("[updateAccessInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#delAccessUserRs(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> delAccessUserRs(JSONObject accessUserInfo) throws Exception {
		try {
			// 参数有效性校验
			RpcResponse<String> rs = ExceptionChecked.checkRequestKey(logger, "saveAccessUserRs", accessUserInfo,
					UscConstants.USER_ID, UscConstants.ACCESSCODE);
			if (rs != null) {
				return rs;
			}

			// 接入方code
			String accessCode = accessUserInfo.getString(TenementConstant.TENEMENT_ACCESS_CODE);
			// 用户ids
			String userIds = accessUserInfo.getString(UscConstants.USER_ID);

			String[] ids = userIds.split(",");
			int i = 0;
			for (String id : ids) {
				// 刪除用户与接入方关系表
				Query queryT = new Query();
				Criteria criteriaT = Criteria.where(UscConstants.USER_ID).is(id)
						.and(TenementConstant.TENEMENT_ACCESS_ID).is(accessCode);
				queryT.addCriteria(criteriaT);
				WriteResult result = tenementTemplate.remove(queryT, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
				i += result.getN();
			}

			if (i > 0) {
				logger.debug(String.format("[delAccessUserRs()->success:%s]", userIds));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.DEL_SUCC, userIds);
			} else {
				logger.debug(String.format("[delAccessUserRs()->fail:%s]", UscConstants.DEL_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.DEL_FAIL);
			}

		} catch (Exception e) {
			logger.error("[delAccessUserRs()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.api.base.crud.AccUserBaseCrudService#swateUserState(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> swateUserState(String userId, String state) throws Exception {
		try {
			// 参数有效性校验
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[swateUserState()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[swateUserState()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.USER_ID));
			}

			if (StringUtils.isEmpty(state)) {
				logger.error(String.format("[swateUserState()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.STATE));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[swateUserState()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.STATE));
			}

			// 修改人员状态信息
			Update update = new Update();
			update.set(UscConstants.STATE, state);
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ID_).is(userId));
			WriteResult res = tenementTemplate.updateMulti(query, update, MongodbConstants.MONGODB_USERINFO_COLL);
			if (res.getN() > 0) {
				JSONObject map = new JSONObject();
				map.put(UscConstants.USER_ID, userId);
				map.put(UscConstants.STATE, state);

				logger.info(String.format("[swateUserState()->success:%s]", userId));
				return RpcResponseBuilder.buildSuccessRpcResp(
						String.format("[swateUserState()->error:%s]", UscConstants.UPDATE_SUCC), map);
			} else {
				logger.error(String.format("[swateUserState()->error:%s]", UscConstants.UPDATE_SUCC));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[swateUserState()->error:%s]", UscConstants.UPDATE_FAIL));
			}

		} catch (Exception e) {
			logger.error("[swateUserState()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
