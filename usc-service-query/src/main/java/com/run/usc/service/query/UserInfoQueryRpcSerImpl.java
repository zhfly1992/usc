/*
 * File name: UserBaseQueryRpcServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月22日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.query;




import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.authz.api.base.crud.AuthzBaseCurdService;
import com.run.authz.api.base.util.ParamChecker;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.base.query.AuthzBaseQueryService;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.encryt.util.MDCoder;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.sms.api.SendMailService;

import com.run.usc.api.base.util.ExceptionChecked;
import com.run.usc.api.base.util.RSAUtil;
import com.run.usc.api.base.util.SendMessage;
import com.run.usc.api.base.util.TimeUtil;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.UserBaseQueryService;
import com.run.usc.service.util.MongoPageUtil;
import com.run.usc.service.util.MongoTemplateUtil;


/**
 * @Description: 用户中心查询服务
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */

public class UserInfoQueryRpcSerImpl implements UserBaseQueryService {
	@Autowired
	private MongoTemplate				mongoTemplate;
	@Autowired
	private MongoTemplateUtil			mongoTemplateUtil;
	@Autowired
	private AuthzBaseQueryService		authzqueryRpcService;
	@Autowired
	private AuthzBaseCurdService		authzcrudRpcService;
	@Autowired
	private SendMailService				sendMailService;
	// @Autowired
	// private SendSmsService sendSmsService;
	@Autowired
	private UserRoleBaseQueryService	userRoleQuery;
	/** 邮箱找回密码地址 */
	@Value("${emFindPass.address:http://localhost}")
	private String						emFindPassAddress;
	/** 1000*60*60*24 */
	@Value("${email.timeOut:86400000}")
	private String						emailTimeOut;
	/** 1000*60*15 */
	@Value("${mobile.timeOut:900000}")
	private String						mobileTimeOut;
	/** 1000*60*30 */
	@Value("${pcToken.timeOut:1800000}")
	private String						pcTokenTimeOut;
	/** 1000*60*30 */
	@Value("${appToken.timeOut:1800000}")
	private String						appTokenTimeOut;
	@Value("${messageGatewayUrl}")
	private String						messageGatewayUrl;

	private static final Logger			logger	= Logger.getLogger(UscConstants.LOGKEY);

	
	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#getUserIdByAccId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<List<String>> getUserIdByAccId(String accessId) throws Exception {
		try {

			// 查询接入方下的所有用户
			Query userAsQuery = new Query();
			userAsQuery.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));

			List<String> userAccList = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL,
					userAsQuery, UscConstants.USER_ID);

			Query userQuery = new Query();
			userQuery.addCriteria(Criteria.where(UscConstants.ID_).in(userAccList).and(UscConstants.IS_DELETE)
					.is(UscConstants.STATE_NORMAL_ONE).and(UscConstants.ACTIVATE_STATE)
					.is(UscConstants.STATE_NORMAL_ONE));

			List<String> listByKey = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_USERINFO_COLL, userQuery,
					UscConstants.USER_ID);

			logger.debug("[getUserIdByAccId()->success:根据接入方id查询用户列表成功" + listByKey + "]");
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, listByKey);
		} catch (Exception e) {
			logger.error("[getUserIdByAccId()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#checkUserExistByAccId(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Boolean checkUserExistByUserType(String emLogMob) throws Exception {
		if (!StringUtils.isEmpty(emLogMob)) {
			// 组装三个或者条件
			BasicDBList values = new BasicDBList();
			values.add(new BasicDBObject(UscConstants.LOGIN_ACCOUNT, emLogMob));
			values.add(new BasicDBObject(UscConstants.MOBILE, emLogMob));
			values.add(new BasicDBObject(UscConstants.EMAIL, emLogMob));

			DBObject dbCondition = new BasicDBObject();
			dbCondition.put(UscConstants.IS_DELETE, UscConstants.STATE_NORMAL_ONE);
			dbCondition.put("$or", values);

			Query querymodule = new BasicQuery(dbCondition);

			// 组装结果相当于 select * from table where (name=1 or name =2) and (type=1
			// or
			// type=2)
			Boolean check = mongoTemplate.exists(querymodule, MongodbConstants.MONGODB_USERINFO_COLL);
			return check;
		} else {
			return false;
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#checkUserExitByEmiMob(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkUserExitByEmiMob(String emailMob) throws Exception {
		try {
			boolean check = checkUserExistByUserType(emailMob);
			logger.debug(String.format("[userAuthz()->success:%s-->%s]", UscConstants.GET_SUCC, emailMob));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, check);

		} catch (Exception e) {
			logger.error("[getUserIdByAccId()->error:%s]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#sendEmiMob(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<String> sendEmiMob(String emailMob, String type, String loginAccount) throws Exception {
		try {
			logger.info(String.format("[sendEmiMob()->request params:%s,%s,%s]", emailMob, type, loginAccount));
			// 参数校验
			RpcResponse res = ExceptionChecked.checkRequestKey(logger, "sendEmiMob", emailMob, type);
			if (null != res) {
				return res;
			}
			String id = UUID.randomUUID().toString().replace("-", "");
			if (UscConstants.EMAIL.equals(type)) {

				String cachekey = MDCoder.encodeMD5_16Hex(id).toLowerCase();
				authzcrudRpcService.createRedisCache(cachekey, emailMob, Long.parseLong(emailTimeOut));

				Map<String, String> mailInfo = new HashMap<>();
				mailInfo.put("destination", emailMob);
				// 激活页面链接地址
				mailInfo.put("content", emFindPassAddress + "?secret=" + cachekey);
				mailInfo.put("key", "用户找回密码");
				mailInfo.put("username", "");
				RpcResponse<Object> sendRes = sendMailService.sendLinkUrl(mailInfo, 3);
				if (sendRes.isSuccess()) {
					logger.info(String.format("[sendEmiMob()->success:%s]", UscConstants.EMAIL_SEND_SUCC));
				} else {
					logger.info(String.format("[sendEmiMob()->fail:%s]", UscConstants.SEND_FAIL));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.SEND_FAIL);
				}
				// 将cachekey返回给前端
				logger.info(String.format("[sendEmiMob()->success:%s-->%s]", UscConstants.GET_SUCC, emailMob));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SEND_SUCC, cachekey);
			} else if (UscConstants.MOBILE.equals(type) || UscConstants.USC_TYPE_BIND.equals(type)) {

				if (StringUtils.isBlank(loginAccount)) {
					logger.error("[sendEmiMob()->用户名不存在]");
					RpcResponseBuilder.buildErrorRpcResp("[sendEmiMob()->用户名不存在]");
				}

				// 产生6位随机数
				String checkNum = "";
				for (int i = 0; i < 6; i++) {
					checkNum = (int) ((Math.random() * 10)) + checkNum;
				}

				// 修改密码时才需要验证，换绑手机时不需要
				if (UscConstants.MOBILE.equals(type)) {
					// 通过用户名和手机号查询是否存在数据
					// TODO
					Boolean checkPhoneLoginAccount = checkPhoneLoginAccount(loginAccount, emailMob);
					if (!checkPhoneLoginAccount) {
						logger.error("[sendEmiMob()->error:通过手机号和账号未查找到该用户！]");
						return RpcResponseBuilder.buildErrorRpcResp("通过手机号和账号未查找到该用户！");
					}
				}

				authzcrudRpcService.createRedisCache(checkNum, emailMob + "-" + loginAccount,
						Long.parseLong(mobileTimeOut));
				// 调用发送短信接口
				String content = String.format("您的验证码是：%s。请不要把验证码泄露给其他人。", checkNum);
				RpcResponse<String> sendRes = SendMessage.send(emailMob, content, messageGatewayUrl);
				// RpcResponse<Object> sendRes =
				// sendSmsService.sendCode(emailMob, checkNum, 1);
				if (sendRes.isSuccess()) {
					logger.info(String.format("[sendEmiMob()->success:%s]", UscConstants.EMAIL_SEND_SUCC));
				} else {
					logger.info(
							String.format("[sendEmiMob()->fail:%s,%s]", UscConstants.SEND_FAIL, sendRes.getMessage()));
					return RpcResponseBuilder.buildErrorRpcResp(sendRes.getMessage());
				}
				// 将cachekey返回给前端
				logger.debug(String.format("[sendEmiMob()->success:%s-->%s]", UscConstants.GET_SUCC, emailMob));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.SEND_SUCC, "");

			} else {
				logger.error(String.format("[sendEmiMob()->error:%s]", UscConstants.CHECK_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.CHECK_BUSINESS);
			}

		} catch (Exception e) {
			logger.error("[sendEmiMob()->error:%s]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#userAuthz(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Map> userAuthz(String loginAccout, String password) {
		try {
			// 参数必填字段校验
			RpcResponse rs = ExceptionChecked.checkRequestKey(logger, "userAuthz", loginAccout, password);
			if (rs != null) {
				return rs;
			}
			String token = UscConstants.TOKEN + "-" + UUID.randomUUID().toString().replace("-", "");
			return checkGatWayLogin(loginAccout, password, pcTokenTimeOut, token);

		} catch (Exception e) {
			logger.error("[userAuthz()->error:%s]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#userAuthz(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Map> appUserAuthz(String loginAccout, String password) {
		try {
			// 参数必填字段校验
			RpcResponse rs = ExceptionChecked.checkRequestKey(logger, "userAuthz", loginAccout, password);
			if (rs != null) {
				return rs;
			}
			String token = UscConstants.APP_TOKEN + "-" + UUID.randomUUID().toString().replace("-", "");
			return checkGatWayLogin(loginAccout, password, appTokenTimeOut, token);

		} catch (Exception e) {
			logger.error("[getUserIdByAccId()->error:%s]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @Description:校验平台用户登录
	 * @param authInfo
	 * @param loginAccout
	 * @param password
	 * @return
	 */

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private RpcResponse<Map> checUserLogin(JSONObject authInfo, String loginAccout, String password) {
		// 秘钥必填
		RpcResponse checRes = ExceptionChecked.checkRequestKey(logger, "userAuthz", authInfo,
				UscConstants.ACCESS_SECRET);
		if (checRes != null) {
			return checRes;
		}
		// 根据秘钥查询接入方信息
		String accessSecret = authInfo.getString(UscConstants.ACCESS_SECRET);
		Query query = new Query(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
		query.addCriteria(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
		query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
		Map<String, String> accessInfo = mongoTemplate.findOne(query, Map.class,
				MongodbConstants.MONGODB_ACCESS_INFO_COLL);
		if (accessInfo != null && accessInfo.size() == 0) {
			logger.debug(String.format("[userAuthz()->fail:%s]", UscConstants.ACCESS_LOGIN_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.ACCESS_LOGIN_FAIL);
		} else {
			String accessId = accessInfo.get(TenementConstant.TENEMENT_ACCESS_CODE);
			// 根据接入方与用户的关系表查询该接入方下面所有用户id
			Query userQuery = new Query(Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).is(accessId));

			// 组装用户id集合
			List<Map> userList = mongoTemplate.find(userQuery, Map.class, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			List userIds = new ArrayList<>();
			if (null != userList && userList.size() != 0) {
				for (Map userMap : userList) {
					userIds.add(userMap.get(UscConstants.USER_ID));
				}
			}

			Query checkUserQuery = new Query(Criteria.where(UscConstants.ID_).in(userIds));
			checkUserQuery.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			checkUserQuery.addCriteria(Criteria.where(UscConstants.LOGIN_ACCOUNT).is(loginAccout));
			checkUserQuery.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			Map user = mongoTemplate.findOne(checkUserQuery, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
			if (null != user && user.size() != 0) {
				// 登录成功
				// 查询用户信息返回给前端
				// 判断登录密码
				String loginpassword = (String) user.get(UscConstants.PASSWORD);
				if (password.equalsIgnoreCase(loginpassword)) {
					Map resultMess = new HashMap<>();
					// 封装接入方信息
					resultMess.put(TenementConstant.ACC_INFO, accessInfo);
					// 封装用户信息
					resultMess.put(UscConstants.USC_INFO, user);

					String tenementId = accessInfo.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID);
					// 登录成功查询租户信息
					Query queryTenInfo = new Query(Criteria.where(UscConstants.ID_).is(tenementId));

					Map map = mongoTemplate.findOne(queryTenInfo, Map.class,
							MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
					// 封装租户信息
					resultMess.put(TenementConstant.TEN_INFO, map);

					// 生成token信息
					try {
						String tokenId = UscConstants.TOKEN + "-" + UUID.randomUUID().toString().replace("-", "");
						RpcResponse<Map> res = authzcrudRpcService.createRedisCache(tokenId,
								(String) user.get(UscConstants.ID_), null);
						if (res.isSuccess()) {
							String token = res.getSuccessValue().get(UscConstants.TOKEN) + "";
							resultMess.put(UscConstants.TOKEN, token);
							logger.debug(String.format("[userAuthz()->success:%s-->%s]", UscConstants.USER_LOGIN_SUCC,
									resultMess));
							return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.USER_LOGIN_SUCC, resultMess);
						} else {
							return res;
						}
					} catch (Exception e) {
						logger.error(String.format("[userAuthz()->fail:%s]", UscConstants.TOKEN_CREATE_FAIL));
						return RpcResponseBuilder.buildErrorRpcResp(UscConstants.TOKEN_CREATE_FAIL);
					}

				} else {
					logger.debug(String.format("[userAuthz()->fail:%s", UscConstants.LOGIN_PASS_FAIL));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.LOGIN_PASS_FAIL);
				}
			} else {
				logger.debug(String.format("[userAuthz()->fail:%s", UscConstants.USER_LOGIN_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.USER_LOGIN_FAIL);
			}
		}
	}



	/**
	 * @Description:校验门户用户登录
	 * @param loginAccout
	 * @param password
	 * @param userType
	 * @return
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private RpcResponse<Map> checkGatWayLogin(String loginAccout, String password, String tokenTimeOut, String token) {

		DBObject dbCondition = new BasicDBObject();
		BasicDBList values = new BasicDBList();
		values.add(new BasicDBObject(UscConstants.LOGIN_ACCOUNT, loginAccout));
		values.add(new BasicDBObject(UscConstants.EMAIL, loginAccout));
		dbCondition.put("$or", values);
		dbCondition.put(UscConstants.IS_DELETE, UscConstants.STATE_NORMAL_ONE);
		dbCondition.put(UscConstants.ACTIVATE_STATE, UscConstants.STATE_NORMAL_ONE);
		Query query = new BasicQuery(dbCondition);

		Map<String, Object> check = mongoTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
		if (check != null && check.size() != 0) {
			if (check.get(UscConstants.STATE).equals(UscConstants.STATE_NORMAL_ONE)) {
				// 登录成功
				// 查询用户信息返回给前端
				// 判断登录密码
				
				//通过私钥解密密码
				String decryptedPassword = RSAUtil.decrypt(password);
				//判断密码是否符合格式
				if (!decryptedPassword.matches(UscConstants.PASSWOR_FORMAT)) {
					logger.error(String.format("[userAuthz()->fail:%s]", UscConstants.PASSWORD_FORMAT_ERROR));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PASSWORD_FORMAT_ERROR);
				}
				//密码md5加密
				password = DigestUtils.md5Hex(decryptedPassword);
				
				String loginpassword = (String) check.get(UscConstants.PASSWORD);
				if (password.equalsIgnoreCase(loginpassword)) {
					Object dueDateObject = check.get(UscConstants.ExpiredDate);
					if (!"endless".equals(String.valueOf(dueDateObject))) {
						if (null == dueDateObject || !StringUtils.isNumeric(dueDateObject + "")) {
							logger.error("[userAuthz()->fail:密码有效期格式错误]");
							return RpcResponseBuilder.buildErrorRpcResp("密码有效期格式错误");
						}
						long expiredDate = Long.valueOf(String.valueOf(dueDateObject));
						
						long nowTime = TimeUtil.getNowTime();
						if (nowTime >= expiredDate) {
							logger.error(String.format("[userAuthz()->fail:%s]", UscConstants.PASSWORD_TIMEOUT));
							return RpcResponseBuilder.buildErrorRpcResp(UscConstants.PASSWORD_TIMEOUT);
						}
					}
					

					// 登录成功封装用户信息，token信息
					Map resultMess = new HashMap<>();
					// 封装接入方信息
					// 封装用户信息
					// resultMess.put(UscConstants.USC_INFO, check);

					// 生成token信息
					try {
						String userId = (String) check.get(UscConstants.ID_);

						RpcResponse<Map> res = authzcrudRpcService.createRedisCache(token, userId,
								Long.parseLong(tokenTimeOut));
						if (res.isSuccess()) {
							resultMess.put(UscConstants.TOKEN, token);
							resultMess.put(UscConstants.USER_ID, userId);
							resultMess.put(UscConstants.LOGIN_ACCOUNT, loginAccout);
							//resultMess.put(UscConstants.PASSWORD, password);
							logger.debug(String.format("[userAuthz()->success:%s-->%s]", UscConstants.USER_LOGIN_SUCC,
									resultMess));
							return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.USER_LOGIN_SUCC, resultMess);
						} else {
							logger.error(res.getMessage());
							return RpcResponseBuilder.buildErrorRpcResp(res.getMessage());
						}
					} catch (Exception e) {
						logger.error(String.format("[userAuthz()->fail:%s]", UscConstants.TOKEN_CREATE_FAIL));
						return RpcResponseBuilder.buildErrorRpcResp(UscConstants.TOKEN_CREATE_FAIL);
					}

				} else {
					logger.debug(String.format("[userAuthz()->fail:%s]", UscConstants.LOGIN_PASS_FAIL));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.LOGIN_PASS_FAIL);
				}
			} else {
				logger.debug(String.format("[userAuthz()->fail:%s]", UscConstants.LOGIN_AUTH_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.LOGIN_AUTH_FAIL);
			}

		} else {
			logger.debug(String.format("[userAuthz()->fail:%s]", UscConstants.LOGIN_AUTH_FAIL));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.LOGIN_AUTH_FAIL);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#checkloginname(java.lang.String)
	 */
	@Override
	public RpcResponse<?> checkloginname(String userInfo) {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#loginout(java.lang.String)
	 */
	@Override
	public RpcResponse<?> loginout(String loginInfo) {

		return null;
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#getUser(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Object> getPageAllUserByKey(JSONObject pageInfo) throws Exception {

		try {
			// 分页信息，查询信息，秘钥信息
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			// 模糊查询条件
			String nameKey = pageInfo.getString(UscConstants.SELECT_KEY);
			// 接入方秘钥
			String secret = pageInfo.getString(UscConstants.ACCESS_SECRET);
			// 组织id
			String organizedId = pageInfo.getString(UscConstants.ORGANIZED_ID);
			// 角色名称
			String roleName = pageInfo.getString(AuthzConstants.ROLE_NAME);
			// 启用停用状态
			String state = pageInfo.getString(AuthzConstants.STATE);
			// 人员类型
			String peopleType = pageInfo.getString(UscConstants.PEOPLE_TYPE);
			// 是否接受短信
			String receiveSms = pageInfo.getString(UscConstants.RECEIVESMS);

			// 参数校验
			// 分页页数
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccUserInfoByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 秘钥不能为空
			if (StringUtils.isEmpty(secret)) {
				logger.error(String.format("[getAccUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 根据接入方秘钥查询接入方id
			Query query = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(secret));
			Map map = mongoTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			String accessId = (String) map.get(UscConstants.ID_);

			List<String> userIds = null;

			// 针对有组织业务，并且有组织查询条件的条件
			List<String> orginUserIds = null;
			List<String> organList = null;
			if (!StringUtils.isEmpty(organizedId)) {
				organList = new ArrayList<String>();
				organList.add(organizedId);
				// 查询该组织下面的子组织信息，采用递归查询
				organList.addAll(selectChildOrg(organizedId));
				// 查询该组织下面的人员信息
				orginUserIds = userRoleQuery.getUserIdByOrg(organList);
			} else {
				// 如果没有勾选组织，默认查询这个接入方下面所有的人员信息
				Query queryUserIds = new Query(Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).is(accessId));
				orginUserIds = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL,
						queryUserIds, UscConstants.USER_ID);
			}

			// 根据岗位过来用户
			List<String> roleUserIds = null;
			if (!StringUtils.isBlank(roleName)) {
				// 根据接入方和角色名称模糊名称查询用户列表
				roleUserIds = userRoleQuery.getUserListByRoleName(roleName, accessId);
			}

			// 根据组织和岗位筛选角色id集合
			if (orginUserIds == null && roleUserIds != null) {
				userIds = roleUserIds;
			} else if (orginUserIds != null && roleUserIds == null) {
				userIds = orginUserIds;
			} else if (orginUserIds != null && roleUserIds != null) {
				userIds = new ArrayList<String>();
				for (String orginUserId : orginUserIds) {
					for (String roleUserId : roleUserIds) {
						if (orginUserId.equals(roleUserId)) {
							userIds.add(roleUserId);
						}
					}
				}
			}

			BasicDBList conlist = new BasicDBList();
			DBObject dbCondition = new BasicDBObject();

			if (!StringUtils.isBlank(nameKey)) {
				// 组装三个或者条件
				Pattern patternAccout = Pattern.compile(UscConstants.REGX_LEFT + nameKey + UscConstants.REGX_RIGHT,
						Pattern.CASE_INSENSITIVE);
				Pattern patternMobile = Pattern.compile(UscConstants.REGX_LEFT + nameKey + UscConstants.REGX_RIGHT,
						Pattern.CASE_INSENSITIVE);
				Pattern patternEmail = Pattern.compile(UscConstants.REGX_LEFT + nameKey + UscConstants.REGX_RIGHT,
						Pattern.CASE_INSENSITIVE);
				Pattern patternUserName = Pattern.compile(UscConstants.REGX_LEFT + nameKey + UscConstants.REGX_RIGHT,
						Pattern.CASE_INSENSITIVE);

				BasicDBObject cdloginAccount = new BasicDBObject();
				cdloginAccount.put(UscConstants.LOGIN_ACCOUNT, patternAccout);
				conlist.add(cdloginAccount);

				BasicDBObject cdmobile = new BasicDBObject();
				cdmobile.put(UscConstants.MOBILE, patternMobile);
				conlist.add(cdmobile);

				BasicDBObject cdemail = new BasicDBObject();
				cdemail.put(UscConstants.EMAIL, patternEmail);
				conlist.add(cdemail);

				BasicDBObject cduserName = new BasicDBObject();
				cduserName.put(UscConstants.USERNAME, patternUserName);
				conlist.add(cduserName);

				dbCondition.put("$or", conlist);
			}
			dbCondition.put(UscConstants.ID_, new BasicDBObject("$in", userIds));
			dbCondition.put(UscConstants.IS_DELETE, UscConstants.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(state)) {
				dbCondition.put(UscConstants.STATE, state);
			}
			dbCondition.put(UscConstants.ACTIVATE_STATE, UscConstants.STATE_NORMAL_ONE);

			Query queryUser = new BasicQuery(dbCondition);

			// 校验人员类型 增加条件
			if (!StringUtils.isBlank(peopleType)) {
				queryUser.addCriteria(Criteria.where(UscConstants.PEOPLE_TYPE).is(peopleType));
			}
			// 校验是否能接受短信 增加条件
			if (!StringUtils.isBlank(receiveSms)) {
				if ("true".equals(receiveSms)) {
					queryUser.addCriteria(Criteria.where(UscConstants.RECEIVESMS_KEY).is(receiveSms));
				} else if ("false".equals(receiveSms)) {
					Criteria criteria = new Criteria();
					criteria.orOperator(Criteria.where(UscConstants.RECEIVESMS_KEY).is(receiveSms),
							Criteria.where(UscConstants.RECEIVESMS_KEY).exists(false));
					queryUser.addCriteria(criteria);
				}
			}
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					mongoTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), queryUser,
					MongodbConstants.MONGODB_USERINFO_COLL);

			List<Map<String, Object>> listdata = page.getDatas();

			// 根据查询的结果查询这个用户所拥有的角色信息，和组织信息
			if (listdata != null && listdata.size() != 0) {
				for (Map<String, Object> mapUser : listdata) {
					String userId = mapUser.get(UscConstants.ID_) + "";
					mapUser.put(UscConstants.ROLE_INFO, userRoleQuery.getRoleMessByUserId(userId, accessId));
				}
			}
			logger.debug(String.format("[getAccUserInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccUserInfoByPage()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("unchecked")
	public List<String> selectChildOrg(String orgId) {
		List<String> allOrg = new ArrayList<>();
		Query queryOrg = new Query(Criteria.where(UscConstants.PARENT_ID).is(orgId));
		List<String> orglist = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_TEN_RES_INFO_COLL, queryOrg,
				UscConstants.ID_);
		if (orglist != null && orglist.size() != 0) {
			allOrg.addAll(orglist);
			for (int i = 0; i < orglist.size(); i++) {
				List<String> orgs = selectChildOrg(orglist.get(i));
				if (orgs != null && orgs.size() != 0) {
					allOrg.addAll(orgs);
				}
			}
		}

		return allOrg;
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#getUserByToken(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<?> getUserByToken(String tokenId) throws Exception {
		return getUserByTokenMethod(tokenId);
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Map> getUserByUserId(String userId) {
		// 没有业务数据
		try {
			if (StringUtils.isBlank(userId)) {
				logger.error(String.format("[getUserByUserId()->error:%s-->%s", UscConstants.NO_BUSINESS,
						UscConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getUserByUserId()->error:%s-->%s",
						UscConstants.NO_BUSINESS, UscConstants.USER_ID));
			}
			Query query = new Query();
			Criteria cri = Criteria.where(UscConstants.ID_).is(userId);
			query.addCriteria(cri);

			Map<String, Object> userInfo = mongoTemplate.findOne(query, Map.class,
					MongodbConstants.MONGODB_USERINFO_COLL);
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, userInfo);

		} catch (Exception e) {
			logger.error("[getUserByUserId()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	private RpcResponse<Map> getUserByTokenMethod(String tokenId) {
		// 没有业务数据
		try {
			if (StringUtils.isBlank(tokenId)) {
				logger.error(String.format("[getUserByTokenMethod()->error:%s-->%s", UscConstants.NO_BUSINESS,
						UscConstants.TOKEN));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getUserByToken()->error:%s-->%s",
						UscConstants.NO_BUSINESS, UscConstants.TOKEN));
			}
			RpcResponse mess = authzqueryRpcService.getCacheValueById(tokenId);
			String userId = null;
			if (mess.isSuccess()) {
				userId = (String) mess.getSuccessValue();
			} else {
				return mess;
			}
			Query query = new Query();
			Criteria cri = Criteria.where(UscConstants.ID_).is(userId);
			query.addCriteria(cri);
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			// query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ACTIVATE_STATE).is(UscConstants.STATE_NORMAL_ONE));

			Map<String, Object> userInfo = mongoTemplate.findOne(query, Map.class,
					MongodbConstants.MONGODB_USERINFO_COLL);
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, userInfo);

		} catch (Exception e) {
			logger.error("[getUserByTokenMethod()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#queryuserByUserkey(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List> queryuserByKey(String userKey, String tokenId) throws Exception {
		try {
			if (StringUtils.isBlank(userKey)) {
				logger.error("[queryuserByKey()->error:" + UscConstants.NO_BUSINESS + ":" + UscConstants.USER_ID + "]");
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS + ":" + UscConstants.USER_ID);
			}

			if (StringUtils.isBlank(tokenId)) {
				logger.error("[queryuserByKey()->error:" + UscConstants.NO_BUSINESS + ":" + UscConstants.TOKEN + "]");
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS + ":" + UscConstants.TOKEN);
			}

			// 根据tokenId得到用户信息
			RpcResponse<?> mess = getUserByTokenMethod(tokenId);
			// 获得接入方信息
			Map<String, Object> map = (Map<String, Object>) mess.getSuccessValue();
			String accessId = map.get(UscConstants.ACCESSID) + "";

			// 组装查询和返回的字段
			Query query = new Query();
			Criteria cr = new Criteria();
			// 根据用户名,id。emial 邮箱模糊查询
			Pattern patternaccount = Pattern.compile(UscConstants.REGX_LEFT + userKey + UscConstants.REGX_RIGHT,
					Pattern.CASE_INSENSITIVE);
			query.addCriteria(cr.orOperator(Criteria.where(UscConstants.LOGIN_ACCOUNT).regex(patternaccount),
					Criteria.where(UscConstants.ID_).regex(patternaccount),
					Criteria.where(UscConstants.EMAIL).regex(patternaccount),
					Criteria.where(UscConstants.MOBILE).regex(patternaccount)));

			query.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));

			// 根据修改时间倒叙排序
			query.with(new Sort(new Order(Direction.DESC, UscConstants.API_PAGE_CREATE_TIME)));

			List<Map> usc = mongoTemplate.find(query, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, usc);
		} catch (Exception e) {
			logger.error("[queryuserByKey()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#querySourceChild(java.lang.String)
	 */
	@Override
	public RpcResponse<List<String>> querySourceChild(String sourceId) {
		try {
			if (StringUtils.isBlank(sourceId)) {
				logger.error(String.format("[querySourceChild()->error:%s-->%s", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[querySourceChild()->error:%s-->%s",
						UscConstants.NO_BUSINESS, UscConstants.SOURCE_ID));
			}
			// 根据父类组织id查询子类集合
			List<String> organList = null;
			organList = new ArrayList<String>();
			organList.add(sourceId);
			// 查询该组织下面的子组织信息，采用递归查询
			organList.addAll(selectChildOrg(sourceId));
			// 查询该组织下面的人员信息
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, organList);

		} catch (Exception e) {
			logger.error("[querySourceChild()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<List<Map>> getUserByUserIds(JSONObject paramInfo) throws Exception {
		try {
			if (StringUtils.isBlank(paramInfo.getString("userIds"))) {
				logger.error("[getUserByUserIds()->error:" + UscConstants.NO_BUSINESS + ":userIds]");
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS + ":userIds");
			}

			List<String> userIds = (List<String>) paramInfo.get("userIds");

			// 组装查询
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ID_).in(userIds));
			query.addCriteria(Criteria.where("receiveSms").is(paramInfo.getString("receiveSms")));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ACTIVATE_STATE).is(UscConstants.STATE_NORMAL_ONE));

			List<Map> usc = mongoTemplate.find(query, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, usc);
		} catch (Exception e) {
			logger.error("[getUserByUserIds()->error]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#getUserIdOrAccessByLogin(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public RpcResponse<String> getUserIdOrAccessByLogin(String loginAccount, String password, String accessSecret) {

		try {
			if (ParamChecker.isBlank(loginAccount)) {
				logger.error(String.format("[getUserIdOrAccessByLogin()->fail:登录名不能为空！]"));
				return RpcResponseBuilder.buildErrorRpcResp("登录名不能为空！");
			}

			if (ParamChecker.isBlank(password)) {
				logger.error(String.format("[getUserIdOrAccessByLogin()->fail:密码不能为空！]"));
				return RpcResponseBuilder.buildErrorRpcResp("密码不能为空！");
			}

			if (ParamChecker.isBlank(accessSecret)) {
				logger.error(String.format("[getUserIdOrAccessByLogin()->fail:接入方密钥不能为空！]"));
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}

			// 通过用户名密码查询 用户id 可能存在多个用户
			Query query = new Query(Criteria.where(UscConstants.LOGIN_ACCOUNT).is(loginAccount)
					.and(UscConstants.PASSWORD).is(password));
			List<String> listByKey = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_USERINFO_COLL, query,
					UscConstants.ID_);

			// 通过用户id以及接入方密钥 查询该接入方密钥下的正确的人
			Query userQuery = new Query(
					Criteria.where(UscConstants.ACCESSID).is(accessSecret).and(UscConstants.USER_ID).in(listByKey));

			Map<String, Object> userInfo = mongoTemplate.findOne(userQuery, Map.class,
					MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);

			if (userInfo == null) {
				logger.error(String.format("[getUserIdOrAccessByLogin()->fail:查询用户信息失败,用户ids不存在！]"));
				return RpcResponseBuilder.buildErrorRpcResp("查询用户信息失败,用户ids不存在！");
			}

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					userInfo.get(UscConstants.USER_ID).toString());

		} catch (Exception e) {
			logger.error("[getUserIdOrAccessByLogin()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#getUsageRate(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<String> getUsageRate(String accessId) throws Exception {

		try {

			if (StringUtils.isBlank(accessId)) {
				logger.error(String.format("[getUsageRate()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESSID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 通过接入方id查询所有的人
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));
			List<Map> userIds = mongoTemplate.find(query, Map.class, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			if (null == userIds || userIds.size() == 0) {
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, null);
			}
			List<String> userIdList = Lists.newArrayList();
			for (Map map : userIds) {
				userIdList.add(map.get(UscConstants.USER_ID).toString());
			}

			// 前一个月的时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1);
			Date m = calendar.getTime();
			// 通过接入方查询最近一个月登录的人
			Query querDate = new Query();
			querDate.addCriteria(Criteria.where(UscConstants.LOGIN_TIME).gte(DateUtils.formatDate(m)));
			querDate.addCriteria(Criteria.where(UscConstants.ID_).in(userIdList));
			List<Map> dateUserIds = mongoTemplate.find(querDate, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
			if (null == userIds || null == dateUserIds) {
				logger.info(String.format("[getUsageRate()->success:%s--%s]", userIds, dateUserIds));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, "0%");
			}

			// 创建一个数值格式化对象
			NumberFormat numberFormat = NumberFormat.getInstance();
			// 设置精确到小数点后2位
			numberFormat.setMaximumFractionDigits(2);
			String result = numberFormat.format((float) dateUserIds.size() / (float) userIds.size() * 100);

			logger.info(String.format("[getUsageRate()->success:%s--%s]", UscConstants.GET_SUCC, result));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, result);

		} catch (Exception e) {
			logger.error("getUsageRate()->error:" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#findFactoryByIds(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<List<Map<String, Object>>> findFactoryByIds(List<String> userIds) {

		try {

			if (userIds == null || userIds.size() == 0) {
				logger.error(String.format("[findFactoryByIds()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			List<Map<String, Object>> userFactorys = Lists.newArrayList();
			for (String userId : userIds) {
				Query query = new Query();
				query.addCriteria(Criteria.where(UscConstants.ID_).is(userId));
				Map<String, Object> userMap = mongoTemplate.findOne(query, Map.class,
						MongodbConstants.MONGODB_USERINFO_COLL);
				Map<String, Object> userFactoryMap = Maps.newHashMap();
				if (userMap == null) {
					logger.error(String.format("[findFactoryByIds()->error:%s-->%s]", UscConstants.USER_ID, userId));
					continue;
				}
				userFactoryMap.put(userId, userMap.get(UscConstants.FACTORY));
				userFactorys.add(userFactoryMap);
			}

			logger.info(String.format("[findFactoryByIds()->success:%s--%s]", UscConstants.GET_SUCC, userFactorys));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, userFactorys);

		} catch (Exception e) {
			logger.error("findFactoryByIds()->error:" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#findUserInfoByIds(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List> findUserInfoByIds(List<String> userIds) {

		try {

			if (userIds == null || userIds.size() == 0) {
				logger.error(String.format("[findUserInfoByIds()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ID_).in(userIds));

			List<Map> userInfos = mongoTemplate.find(query, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, userInfos);

		} catch (Exception e) {
			logger.error("findUserInfoByIds()->error:" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.UserBaseQueryService#findUserIdsByKey(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<List<String>> findUserIdsByKey(String keyWord, String accessSecret) {

		try {
			if (StringUtils.isBlank(keyWord) || StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[findUserInfoByIds()->error:%s]", UscConstants.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 通过接入方密钥查询用户Id
			Query uscRsAcc = new Query();
			uscRsAcc.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessSecret));

			List<String> userIds = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL,
					uscRsAcc, UscConstants.USER_ID);

			// 组装查询和返回的字段
			Query query = new Query();
			// 根据用户名模糊查询
			Pattern patternaccount = Pattern.compile(UscConstants.REGX_LEFT + keyWord + UscConstants.REGX_RIGHT,
					Pattern.CASE_INSENSITIVE);
			query.addCriteria(Criteria.where(UscConstants.USERNAME).regex(patternaccount));
			query.addCriteria(Criteria.where(UscConstants.ID_).in(userIds));
			List<String> listByKey = mongoTemplateUtil.getListByKey(MongodbConstants.MONGODB_USERINFO_COLL, query,
					UscConstants.ID_);

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, listByKey);
		} catch (Exception e) {
			logger.error("findUserInfoByIds()->error:" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @Description:通过用户名和手机号查询用户是否存在
	 * @param loginAccount
	 * @param emailMob
	 * @return
	 */
	private Boolean checkPhoneLoginAccount(String loginAccount, String emailMob) {
		Query query = new Query();
		query.addCriteria(Criteria.where(UscConstants.LOGIN_ACCOUNT).is(loginAccount));
		query.addCriteria(Criteria.where(UscConstants.MOBILE).is(emailMob));
		return mongoTemplate.exists(query, MongodbConstants.MONGODB_USERINFO_COLL);
	}



	@Override
	public RpcResponse<String> findPublicKeyForLoginEncode() {
		// TODO Auto-generated method stub
		try {
			logger.info("findPublicKeyForLoginEncode()->进入方法");
			String publicKey = RSAUtil.getPublicKey();
			String privateKey = RSAUtil.getPrivateKey();
			if (publicKey == null) {
				logger.error("findPublicKeyForLoginEncode()->error,获取到的公钥为null");
				return RpcResponseBuilder.buildErrorRpcResp("获取公钥失败");
			}
			if (privateKey == null) {
				logger.error("findPublicKeyForLoginEncode()->error,获取到的私钥为null");
				return RpcResponseBuilder.buildErrorRpcResp("获取公钥失败");
			}
			RpcResponse<Boolean> createRedisCacheForKey = authzcrudRpcService.createRedisCacheForKey(UscConstants.PRIVATE_KEY, privateKey);
			if (!createRedisCacheForKey.isSuccess()) {
				logger.error("[findPublicKeyForLoginEncode()->error,私钥放入redis失败]");
				return RpcResponseBuilder.buildErrorRpcResp("获取失败，秘钥放置缓存失败");
			}
			logger.info(String.format("findPublicKeyForLoginEncode()->获取的公钥为:%s", publicKey));
			return RpcResponseBuilder.buildSuccessRpcResp("获取成功", publicKey);
			
		} catch (Exception e) {
			logger.error("[findPublicKeyForLoginEncode()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		} 
	}
}
