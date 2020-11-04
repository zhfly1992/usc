
/*
 * File name: UserBaseCurdServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月21日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.base.curd.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.run.authz.api.base.crud.AuthzBaseCurdService;
import com.run.authz.api.base.crud.UserRoleBaseCrudService;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.base.query.AuthzBaseQueryService;
import com.run.common.util.RegexUtil;
import com.run.common.util.UUIDUtil;
import com.run.encryt.util.MDCoder;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.sms.api.SendMailService;
import com.run.usc.api.base.crud.UserBaseCurdService;
import com.run.usc.api.base.util.ExceptionChecked;
import com.run.usc.api.base.util.RSAUtil;
import com.run.usc.api.base.util.TimeUtil;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.UserBaseQueryService;
import com.run.usc.base.util.MongoTemplateUtil;

/**
 * @Description: 用户中心crud
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */
public class UserBaseCurdRpcSerImpl implements UserBaseCurdService {

	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private MongoTemplate			mongoTemplate;
	@Autowired
	private MongoTemplateUtil		mongoTemplateUtil;

	@Autowired
	private UserBaseQueryService	userqueryRpcService;

	@Autowired
	private AuthzBaseCurdService	authzcrudRpcService;

	@Autowired
	private AuthzBaseQueryService	authzQueryRpcService;

	@Autowired
	private UserRoleBaseCrudService	userRoleCrud;

	@Autowired
	private SendMailService			sendMailService;

	/** 邮箱激活地址 */
	@Value("${emActivate.address:http://localhost}")
	private String					emActivateAdd;

	/** 1000*60*60*24 */
	@Value("${email.timeOut:86400000}")
	private String					emailTimeOut;

	/** 门户默认秘钥 */
	@Value("${defaultIotSecret:378a253bfb45cfd8}")
	private String					defaultIotSecret;

	/** 门户默认角色 */
	@Value("${defaultIotRole:e709187e3eee4e56976fc8c4c821913c}")
	private String					defaultIotRole;



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#saveUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<String> registerUser(JSONObject registerInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<String> rs = ExceptionChecked.checkRequestKey(logger, "registerUser", registerInfo,
					UscConstants.PASSWORD, UscConstants.USERTYPE);
			if (rs != null) {
				return rs;
			}
			// 邮箱,电话号码，登录名，用户类型
			String email = registerInfo.getString(UscConstants.EMAIL);
			String mobile = registerInfo.getString(UscConstants.MOBILE);
			String loginAccount = registerInfo.getString(UscConstants.LOGIN_ACCOUNT);
			String userType = registerInfo.getString(UscConstants.USERTYPE);

			// 邮箱，电话号码，登录名不能全部为空
			if (StringUtils.isEmpty(email) && StringUtils.isEmpty(mobile) && StringUtils.isEmpty(loginAccount)) {
				logger.error(String.format("[registerUser()->error:%s]", UscConstants.EMA_MOB_LOG_EXIST));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.EMA_MOB_LOG_EXIST);
			}

			// 检查emil账号格式是否合法
			if (!checkEmilInvalid(email)) {
				logger.error(String.format("[registerUser()->error:%s]", UscConstants.EMIL_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.EMIL_INVALID);
			}

			// 验证邮箱，用户名和电话是否存在
			if (userqueryRpcService.checkUserExistByUserType(email)) {
				logger.error(String.format("[registerUser()->error:%s]", UscConstants.EMIL_EXIT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.EMIL_EXIT);
			}

			if (userqueryRpcService.checkUserExistByUserType(mobile)) {
				logger.error(String.format("[registerUser()->error:%s]", UscConstants.PHONE_EXIT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PHONE_EXIT);
			}

			if (userqueryRpcService.checkUserExistByUserType(loginAccount)) {
				logger.error(String.format("[registerUser()->error:%s]", UscConstants.USER_USER_NAME_EXIT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.USER_USER_NAME_EXIT);
			}
			// 获取手机验证码
			String mobileActivationSign = registerInfo.getString(UscConstants.MOBILE_ACT_SIGN);

			// 如果不是手机验证码注册的， 默认注册的用户都是未激活状态
			if (StringUtils.isEmpty(mobileActivationSign)) {
				registerInfo.put(UscConstants.ACTIVATE_STATE, UscConstants.STATE_STOP_ZERO);
			} else {
				// 使用手机注册的，检查验证码是否正确，验证的key是手机号。value是验证码
				RpcResponse<String> res = authzQueryRpcService.getCacheValueById(mobile);
				if (res.isSuccess()) {
					String signNum = res.getSuccessValue();
					if (signNum.equals(mobileActivationSign)) {
						// 验证码正确，激活用户
						registerInfo.put(UscConstants.ACTIVATE_STATE, UscConstants.STATE_NORMAL_ONE);
					} else {
						logger.error(String.format("[registerUser()->error:%s]", UscConstants.SIGN_CHECK_ERRO));
						return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SIGN_CHECK_ERRO);
					}
				} else {
					return res;
				}
			}

			// 添加用户系列编号
			String uid = UUIDUtil.getUUID();
			registerInfo.put(UscConstants.USER_CODE, uid);
			registerInfo.put(UscConstants.USERNAME, loginAccount);
			registerInfo.put(UscConstants.ExpiredDate, TimeUtil.getExpiredDate());
			RpcResponse<JSONObject> result = mongoTemplateUtil.insertId(logger, "registerUser", registerInfo,
					MongodbConstants.MONGODB_USERINFO_COLL, uid);

			if (result.isSuccess()) {
				// 门户注册的用户默认添加接入方关联
				// 插入用户与接入方关系表中
				String rsId = UUID.randomUUID().toString().replace("-", "");
				JSONObject accuserRs = new JSONObject();
				accuserRs.put(UscConstants.ID_, rsId);
				accuserRs.put(TenementConstant.USER_ID, uid);
				// 如果是门户注册的用户同意设置默认的接入方，做统一管理
				String accessId = null;
				if (UscConstants.USERTYPE_COMPANY.equals(userType)
						|| UscConstants.USERTYPE_INDIVIDUAL.equals(userType)) {
					// 根据秘钥查询接入方id
					Query queryAccess = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(defaultIotSecret));
					Map<String, Object> map = mongoTemplate.findOne(queryAccess, Map.class,
							MongodbConstants.MONGODB_ACCESS_INFO_COLL);
					accessId = (String) map.get(UscConstants.ID_);
					accuserRs.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);

					// 插入默认角色
					JSONObject userRsRole = new JSONObject();
					userRsRole.put(UscConstants.USER_ID, uid);
					userRsRole.put(UscConstants.ACCESSID, accessId);
					userRsRole.put(UscConstants.ROLE_ID, defaultIotRole);

					userRoleCrud.saveUserRoleRs(userRsRole);

					userRsRole.put(AuthzConstants.ID_, UUID.randomUUID().toString().replace("-", ""));
					userRsRole.remove(UscConstants.ROLE_ID);

					mongoTemplate.insert(userRsRole, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);

				} else {
					// 如果是接入方注册用户,需要传接入方id
					accessId = registerInfo.getString(UscConstants.ACCESSID);
					if (StringUtils.isEmpty(accessId)) {
						logger.error(String.format("[registerUser()->error:%s]", TenementConstant.ACCESS_ID_NOTEXIST));
						return RpcResponseBuilder.buildErrorRpcResp(TenementConstant.ACCESS_ID_NOTEXIST);
					} else {
						accuserRs.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);
					}
					mongoTemplate.insert(accuserRs, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
				}

				// 如果是邮箱注册的，需要发送邮箱链接
				if (!StringUtils.isEmpty(email)) {
					// 门户注册成功之后向邮箱发送激活链接
					String secret = MDCoder.encodeMD5_16Hex(UUID.randomUUID().toString()).toLowerCase();
					authzcrudRpcService.createRedisCache(secret, uid, Long.parseLong(emailTimeOut));

					Map<String, String> mailInfo = new HashMap<>();
					mailInfo.put("destination", email);
					// 激活页面链接地址
					mailInfo.put("content", emActivateAdd + "?secret=" + secret);
					mailInfo.put("key", "用户激活");
					mailInfo.put("username", "");
					RpcResponse<Object> res = sendMailService.sendLinkUrl(mailInfo, 3);
					if (res.isSuccess()) {
						logger.info(String.format("[registerUser()->success:%s]", UscConstants.EMAIL_SEND_SUCC));
					}
				}
				logger.info(String.format("[registerUser()->error:%s]", UscConstants.GATWAY_REGISTER_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SAVE_SUCC, uid);
			} else {
				logger.error(String.format("[registerUser()->error:%s]", UscConstants.GATWAY_REGISTER_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SAVE_FAIL);
			}
		} catch (Exception e) {
			logger.error("[registerUser()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#accessRegisterUser(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<JSONObject> accessRegisterUser(JSONObject registerInfo, JSONObject authzInfo) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "accessRegisterUser", registerInfo,
					UscConstants.PASSWORD, UscConstants.EMAIL);
			if (rs != null) {
				return rs;
			}

			RpcResponse<JSONObject> rsAuthz = ExceptionChecked.checkRequestKey(logger, "accessRegisterUser", authzInfo,
					UscConstants.ACCESS_SECRET);
			if (rsAuthz != null) {
				return rsAuthz;
			}

			// 邮箱
			String email = registerInfo.getString(UscConstants.EMAIL);

			// 检查emil账号格式是否合法
			if (!checkEmilInvalid(email)) {
				logger.error(String.format("[accessRegisterUser()->error:%s]", UscConstants.EMIL_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.EMIL_INVALID);
			}

			// 接入方秘钥
			String secret = authzInfo.getString(UscConstants.ACCESS_SECRET);
			String accessId = null;

			// 根据接入方秘钥查询接入方信息
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ACCESS_SECRET).is(secret));
			List list = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCESS_INFO_COLL, query,
					UscConstants.ID_);
			if (list != null && !list.isEmpty()) {
				accessId = list.get(0) + "";
			}
			// 检查注册参数中登陆名是否已经注册
			Boolean checkLogin = checkAccUserName(accessId, email);
			if (checkLogin) {
				logger.error(String.format("[accessRegisterUser()->error:%s]", UscConstants.EMIL_EXIT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.EMIL_EXIT);
			}

			String uid = UUIDUtil.getUUID();
			// 添加用户系列编号
			registerInfo.put(UscConstants.USER_CODE, uid);
			registerInfo.put(UscConstants.LOGIN_ACCOUNT, email);
			registerInfo.put(UscConstants.ExpiredDate, TimeUtil.getExpiredDate());
			RpcResponse<JSONObject> result = mongoTemplateUtil.insertId(logger, "saveUser", registerInfo,
					MongodbConstants.MONGODB_USERINFO_COLL, uid);

			if (result.isSuccess()) {
				// 门户注册的用户默认添加接入方关联
				// 插入用户与接入方关系表中
				String rsId = UUID.randomUUID().toString().replace("-", "");
				JSONObject accuserRs = new JSONObject();
				accuserRs.put(UscConstants.ID_, rsId);
				accuserRs.put(TenementConstant.USER_ID, uid);
				accuserRs.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);
				mongoTemplate.insert(accuserRs, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
				return result;
			} else {
				return result;
			}
		} catch (Exception e) {
			logger.error("[accessRegisterUser()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#loginout(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> loginout(String tokenId) throws Exception {
		try {
			return authzcrudRpcService.removeToken(tokenId);
		} catch (Exception e) {
			logger.error(String.format("[loginout()->error:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#updatePassword(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> updatePassword(String tokenId, String password) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updatePassword", tokenId, password);
			if (rs != null) {
				return rs;
			}
			// 用户id
			String userId = null;
			RpcResponse<String> userIdRes = authzQueryRpcService.getCacheValueById(tokenId);
			if (userIdRes.isSuccess()) {
				userId = userIdRes.getSuccessValue();
			} else {
				return RpcResponseBuilder.buildErrorRpcResp(userIdRes.getMessage());
			}
			
			/** -----------------------新加代码------------------ */

			

			//获取私钥
			RpcResponse<String> privateKey = authzQueryRpcService.getCacheValueById(UscConstants.PRIVATE_KEY);
			if (!privateKey.isSuccess()) {
				logger.error(String.format("[updatePassword()->error:%s]", "password decrypt fail,can not get privateKey"));
				return RpcResponseBuilder.buildErrorRpcResp("解密失败，获取私钥失败");
			}

			
			// 通过私钥解密密码
			String decryptedPassword = RSAUtil.decrypt(password, privateKey.getSuccessValue());
			logger.info("updatePassword()->解密出的密码为：" + decryptedPassword);
			// 判断密码格式
			if (!decryptedPassword.matches(UscConstants.PASSWOR_FORMAT) || decryptedPassword.length() < 8 || decryptedPassword.length() > 30) {
				logger.error(String.format("[updatePassword()->error:%s]", UscConstants.PASSWORD_FORMAT_ERROR));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PASSWORD_FORMAT_ERROR);
			}
			
			String md5pass = DigestUtils.md5Hex(decryptedPassword);
			logger.info(String.format("[saveAccUserInfo()->md5加密后的密码:%s]", md5pass));
			
			
			
			JSONObject updateJson = new JSONObject();
			updateJson.put(UscConstants.PASSWORD, md5pass);
			updateJson.put(UscConstants.ExpiredDate, TimeUtil.getExpiredDate());
			return mongoTemplateUtil.update(logger, "updatePassword", updateJson,
					MongodbConstants.MONGODB_USERINFO_COLL, userId);
		} catch (Exception e) {
			logger.error("[updatePassword()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#updatemobile(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> updatemobile(String token, String sendNum) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse<JSONObject> rs = ExceptionChecked.checkRequestKey(logger, "updatemobile", token, sendNum);
			if (rs != null) {
				return rs;
			}
			// 用户id
			String userId = null;
			RpcResponse<String> userIdRes = authzQueryRpcService.getCacheValueById(token);
			if (userIdRes.isSuccess()) {
				userId = userIdRes.getSuccessValue();
			} else {
				logger.error(String.format("[updatemobile()->error:%s]", userIdRes.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(userIdRes.getMessage());
			}

			String mobile = null;
			// 电话号码
			RpcResponse<String> mobileInfo = authzQueryRpcService.getCacheValueById(sendNum);
			if (mobileInfo.isSuccess()) {
				String str = mobileInfo.getSuccessValue() + "";
				String[] split = str.split("-");
				mobile = split[0];
			} else {
				logger.error(String.format("[updatemobile()->error:%s]", mobileInfo.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(mobileInfo.getMessage());
			}

			JSONObject updateJson = new JSONObject();
			updateJson.put(UscConstants.MOBILE, mobile);

			RpcResponse<JSONObject> res = mongoTemplateUtil.update(logger, "updatemobile", updateJson,
					MongodbConstants.MONGODB_USERINFO_COLL, userId);

			// 如果修改成功，移出缓存中的信息
			if (res.isSuccess()) {
				authzcrudRpcService.removeToken(sendNum);
			}
			return res;

		} catch (Exception e) {
			logger.error("[updatePassword()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#activateUser(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Map> activateUser(String registerSecret) throws Exception {
		// 参数必填字段校验
		RpcResponse rs = ExceptionChecked.checkRequestKey(logger, "activateUser", registerSecret);
		if (rs != null) {
			return rs;
		}
		// 用户id
		String userId = null;
		RpcResponse<String> userIdRes = authzQueryRpcService.getCacheValueById(registerSecret);
		if (userIdRes.isSuccess()) {
			userId = userIdRes.getSuccessValue();
		} else {
			return RpcResponseBuilder.buildErrorRpcResp(userIdRes.getMessage());
		}
		// 更改激活状态
		JSONObject updateJson = new JSONObject();
		updateJson.put(UscConstants.ACTIVATE_STATE, UscConstants.STATE_NORMAL_ONE);

		RpcResponse<JSONObject> res = mongoTemplateUtil.update(logger, "activateUser", updateJson,
				MongodbConstants.MONGODB_USERINFO_COLL, userId);
		if (res.isSuccess()) {
			// 成功之后移出token信息，防止重复激活
			authzcrudRpcService.removeToken(registerSecret);
			// 查询个人信息
			Query queryUser = new Query(Criteria.where(UscConstants.ID_).is(userId));
			Map info = mongoTemplate.findOne(queryUser, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
			logger.debug(String.format("[%s()->success:%s]", "activateUser", info));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.ACTIVATE_SUCC, info);
		} else {
			logger.debug(String.format("[activateUser()->fail:%s", UscConstants.ACTIVATE_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.ACTIVATE_FAIL);
		}

	}



	@SuppressWarnings("rawtypes")
	private Boolean checkAccUserName(String accessId, String name) {
		// 根据接入方id查询该接入方下面所有的用户id集合
		// 查询条件
		DBObject dbCondition = new BasicDBObject();
		dbCondition.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);

		// 查询显示的列
		DBObject dbObjectClum = new BasicDBObject();
		dbObjectClum.put(TenementConstant.USER_ID, 1);
		dbObjectClum.put(UscConstants.ID_, 0);

		Query querymodule = new BasicQuery(dbCondition, dbObjectClum);

		List<Map> userListId = mongoTemplate.find(querymodule, Map.class, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);

		List<Object> usersList = null;
		// 针对map封装成list String userId
		if (null != userListId && !userListId.isEmpty()) {
			usersList = new ArrayList<>();
			for (Map map : userListId) {
				usersList.add(map.get(TenementConstant.USER_ID));
			}
		} else {
			return false;
		}

		// 判断该接入方下面所有的正常用户是否存在该名称
		Query queryUser = new Query();
		Criteria cr = Criteria.where(UscConstants.ID_).in(usersList);
		Criteria crDel = Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE);
		Criteria crName = Criteria.where(UscConstants.LOGIN_ACCOUNT).is(name);
		queryUser.addCriteria(cr);
		queryUser.addCriteria(crDel);
		queryUser.addCriteria(crName);
		return mongoTemplate.exists(queryUser, MongodbConstants.MONGODB_USERINFO_COLL);
	}



	/**
	 * 
	 * 检查手机号码是否合法
	 *
	 * @param userInfo
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkMobileIsInvalid(String mobile) {
		if (!StringUtils.isEmpty(mobile)) {
			if (!RegexUtil.validateMobile(mobile)) {
				return false;
			}
		}
		return true;
	}



	/**
	 * 
	 * 检查emil是否合法
	 *
	 * @param userInfo
	 * @return
	 */
	private boolean checkEmilInvalid(String emial) {
		if (!StringUtils.isEmpty(emial)) {

			String rexEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
			Pattern p = Pattern.compile(rexEmail);
			Matcher m = p.matcher(emial);
			return m.matches();
		}

		return true;
	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#resetPasswordByAuthz(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<?> resetPasswordByAuthz(String newPass, String sendNum, String type) throws Exception {
		try {
			// 参数必填字段校验
			RpcResponse rs = ExceptionChecked.checkRequestKey(logger, "resetPassBysendNum", newPass, sendNum, type);
			if (rs != null) {
				return rs;
			}
			// 从redis缓存当中查询验证码
			RpcResponse<String> res = authzQueryRpcService.getCacheValueById(sendNum);
			if (res.isSuccess()) {

				// 解析数据获取手机号和账号或者邮箱
				Map<String, String> checkEmailMob = checkEmailMob(res.getSuccessValue(), type);
				RpcResponse<String> privateKey = authzQueryRpcService.getCacheValueById(UscConstants.PRIVATE_KEY);
				if (!privateKey.isSuccess()) {
					logger.error(String.format("[resetPasswordByAuthz()->error:%s]", "解密失败，获取私钥失败"));
					return RpcResponseBuilder.buildErrorRpcResp("解密失败，获取私钥失败");
				}
				 /** -----------------------新加代码------------------ */
				// 通过私钥解密密码
				String decryptedPassword = RSAUtil.decrypt(newPass,privateKey.getSuccessValue());
				logger.info("resetPasswordByAuthz()->解密出的密码为：" + decryptedPassword);
				// 判断密码格式
				if (!decryptedPassword.matches(UscConstants.PASSWOR_FORMAT) || decryptedPassword.length() < 8 || decryptedPassword.length() > 30) {
					logger.error(String.format("[resetPasswordByAuthz()->error:%s]", UscConstants.PASSWORD_FORMAT_ERROR));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PASSWORD_FORMAT_ERROR);
				}
				
				// md5加密
				logger.info(String.format("[resetPasswordByAuthz()->input newPass:%s]", decryptedPassword));
				newPass = DigestUtils.md5Hex(decryptedPassword);
				logger.info(String.format("[resetPasswordByAuthz()->md5加密后的密码:%s]", newPass));
				
				
				/** ------------------------新加代码结束 ------------------*/
				String value = checkEmailMob.get(UscConstants.EMAIL_MOBILE);
				
				// 修改密码
				Update update = new Update();
				update.set(UscConstants.PASSWORD, newPass);
				update.set(UscConstants.ExpiredDate, TimeUtil.getExpiredDate());
				// 组装三个或者条件
				BasicDBList values = new BasicDBList();
				values.add(new BasicDBObject(UscConstants.MOBILE, value));
				values.add(new BasicDBObject(UscConstants.EMAIL, value));

				DBObject dbCondition = new BasicDBObject();
				dbCondition.put(UscConstants.IS_DELETE, UscConstants.STATE_NORMAL_ONE);
				dbCondition.put(UscConstants.ACTIVATE_STATE, UscConstants.STATE_NORMAL_ONE);

				// 判断手机和账号的类型还是邮箱的类型
				if (UscConstants.EMAIL.equals(type)) {
					values.add(new BasicDBObject(UscConstants.LOGIN_ACCOUNT, value));
				} else {
					dbCondition.put(UscConstants.LOGIN_ACCOUNT, checkEmailMob.get(UscConstants.LOGIN_ACCOUNT));
				}

				dbCondition.put("$or", values);

				Query query = new BasicQuery(dbCondition);

				WriteResult resUp = mongoTemplate.updateMulti(query, update, MongodbConstants.MONGODB_USERINFO_COLL);
				if (resUp.getN() > 0) {
					authzcrudRpcService.removeToken(sendNum);
					logger.debug(String.format("[updateAccessInfo()->success:%s]", UscConstants.UPDATE_SUCC));
					return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC, null);
				} else {
					logger.debug(String.format("[updateAccessInfo()->fail:%s]", UscConstants.UPDATE_FAIL));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
				}
			} else {
				return RpcResponseBuilder.buildErrorRpcResp("验证码错误请重新输入！");
			}

		} catch (Exception e) {
			logger.error("[resetPassBysendNum()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#updateUser(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<?> updateUser(String updateUserParam, String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#updatemobile(java.lang.String)
	 */
	@Override
	public RpcResponse<?> updatemobile(String userInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#updateemail(java.lang.String)
	 */
	@Override
	public RpcResponse<?> updateemail(String userInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public RpcResponse<Boolean> activateUserByUserId(String userId, JSONObject state) {
		try {
			if (state == null) {
				return RpcResponseBuilder.buildErrorRpcResp("对象不能为空！");
			}
			if (!state.containsKey(UscConstants.ACTIVATE_STATE)) {
				logger.error(String.format("[activateUserByUserId()->error:%s]", UscConstants.ACTIVATE_STATE + "是必传的"));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.ACTIVATE_STATE + "是必传的");
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(userId)) {
				logger.error(String.format("[activateUserByUserId()->error:%s]", UscConstants.ID_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.ID_IS_NULL);
			}
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ID_).is(userId));
			Update update = new Update();
			update.set(UscConstants.ACTIVATE_STATE, state.getString(UscConstants.ACTIVATE_STATE));
			WriteResult updateMulti = mongoTemplate.updateMulti(query, update, MongodbConstants.MONGODB_USERINFO_COLL);
			if (updateMulti.getN() > 0) {
				logger.info(String.format("[activateUserByUserId()->error:%s,%s]", userId, UscConstants.UPDATE_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC, true);
			} else {
				logger.error(String.format("[activateUserByUserId()->error:%s,%s]", userId, UscConstants.UPDATE_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("[activateUserByUserId()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.UserBaseCurdService#refreshLoginTime(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> refreshLoginTime(String userId) throws Exception {

		try {

			if (org.apache.commons.lang3.StringUtils.isBlank(userId)) {
				logger.error(String.format("[refreshLoginTime()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[refreshLoginTime()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.USER_ID));
			}

			// 刷新登录时间
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ID_).is(userId));
			Update update = new Update();
			update.set(UscConstants.LOGIN_TIME, DateUtils.formatDate(new Date()));

			WriteResult updateMulti = mongoTemplate.updateMulti(query, update, MongodbConstants.MONGODB_USERINFO_COLL);
			if (updateMulti.getN() > 0) {
				logger.info(String.format("[refreshLoginTime()->error:%s,%s]", userId, UscConstants.UPDATE_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.UPDATE_SUCC, true);
			} else {
				logger.error(String.format("[refreshLoginTime()->error:%s,%s]", userId, UscConstants.UPDATE_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.UPDATE_FAIL);
			}

		} catch (Exception e) {
			logger.error("[refreshLoginTime()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @Description:解析type类型获取手机号和用户账号或者邮箱
	 * @param value
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> checkEmailMob(String value, String type) throws Exception {
		Map<String, String> checkEmailMobMap = Maps.newHashMap();
		if (UscConstants.EMAIL.equals(type)) {
			checkEmailMobMap.put(UscConstants.EMAIL_MOBILE, value);
			return checkEmailMobMap;
		}

		String[] split = value.split("-");
		checkEmailMobMap.put(UscConstants.EMAIL_MOBILE, split[0]);
		checkEmailMobMap.put(UscConstants.LOGIN_ACCOUNT, split[1]);
		return checkEmailMobMap;
	}

}
