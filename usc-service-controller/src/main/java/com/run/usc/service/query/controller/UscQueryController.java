/*
 * File name: UscQueryController.java
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

package com.run.usc.service.query.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.base.query.AccSourceBaseQueryService;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description: 用户中心查询类
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = UscUrlConstants.API_USER_BASE_PATH)
public class UscQueryController {

	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private UserBaseQueryService	userQueryRpcService;
	@Autowired
	private AccSourceBaseQueryService test;
	
	
	/**
	 * 
	 * @Description:用户登录验证
	 * @param oauthRquestInfo
	 *            登录信息
	 * @return
	 */

	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.API_CODE_USERAUTH, method = RequestMethod.POST)
	public Result userAuthz(@RequestBody String loginInfo) {
		logger.info("[userAuthz()->request param:" + loginInfo + "]");
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(loginInfo);
			if (null != checResult) {
				logger.error(String.format("[userAuthz()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject loginJson = JSON.parseObject(loginInfo);
			// 用户名,密码
			String loginAccout = loginJson.getString(UscConstants.LOGIN_ACCOUNT);
			String password = loginJson.getString(UscConstants.PASSWORD);

			RpcResponse res = userQueryRpcService.userAuthz(loginAccout, password);
			if (res.isSuccess()) {
				logger.info(String.format("[userAuthz()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[userAuthz()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[userAuthz()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}
	}



	/***
	 * 
	 * @Description:判断门户用户手机或者邮箱是否存在
	 * @param checkInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.CHECK_USER_EXIST_EMIMOB, method = RequestMethod.POST)
	public Result checkUserExitByEmiMob(@RequestBody String emailMobInfo) {
		logger.info(String.format("[checkUserExitByEmiMob()->request param:%s]", emailMobInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(emailMobInfo);
			if (null != checResult) {
				logger.error(String.format("[checkUserExitByEmiMob()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject emailMobInfoJson = JSON.parseObject(emailMobInfo);

			// 获取电话号码或者邮箱
			String emailMob = emailMobInfoJson.getString(UscConstants.EMAIL_MOBILE);
			RpcResponse res = userQueryRpcService.checkUserExitByEmiMob(emailMob);
			if (res.isSuccess()) {
				logger.info(String.format("[checkUserExitByEmiMob()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkUserExitByEmiMob()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[checkUserExitByEmiMob()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:发送手机或者短信验证
	 * @param emailMobInfo
	 *            手机或者邮箱信息，type emial or phone
	 * @return
	 */
//	@SuppressWarnings("rawtypes")
//	@CrossOrigin(origins = "*")
//	@RequestMapping(value = UscUrlConstants.SEND_EMIMOB, method = RequestMethod.POST)
//	public Result sendEmiMob(@RequestBody String emailMobInfo) {
//		logger.info(String.format("[sendEmiMob()->request param:%s]", emailMobInfo));
//		try {
//			// 参数基础校验
//			Result checResult = ExceptionChecked.checkRequestParam(emailMobInfo);
//			if (null != checResult) {
//				logger.error(String.format("[sendEmiMob()->fail:%s]", checResult.getException()));
//				return checResult;
//			}
//
//			JSONObject emailMobInfoJson = JSON.parseObject(emailMobInfo);
//
//			// 获取电话号码或者邮箱
//			String emailMob = emailMobInfoJson.getString(UscConstants.EMAIL_MOBILE);
//			String type = emailMobInfoJson.getString(UscConstants.TYPE);
//			String loginAccount = emailMobInfoJson.getString(UscConstants.LOGIN_ACCOUNT);
//			RpcResponse res = userQueryRpcService.sendEmiMob(emailMob, type, loginAccount);
//			if (res.isSuccess()) {
//				logger.info(String.format("[sendEmiMob()->success:%s]", res.getMessage()));
//				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
//			} else {
//				logger.error(String.format("[sendEmiMob()->fail:%s]", res.getMessage()));
//				return ResultBuilder.failResult(res.getMessage());
//			}
//		} catch (Exception e) {
//			logger.error(String.format("[sendEmiMob()->exception:%s]", e.getMessage()));
//			return ResultBuilder.exceptionResult(e);
//		}
//	}



	/**
	 * 
	 * @Description:检查登录名
	 * @param userInfo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.CHEACK_LOGIN_NAME, method = RequestMethod.POST)
	public Result checkloginname(@RequestBody String userInfo) {
		logger.info(String.format("[checkloginname()->request param:%s]", userInfo));
		try {
			RpcResponse res = userQueryRpcService.checkloginname(userInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[checkloginname()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkloginname()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[checkloginname()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/***
	 * 
	 * @Description:根据接入方分页查询用户信息
	 * @param userInfo
	 *            分页信息， 用户名/邮箱/电话
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.QUERY_ACCESS_USER_PAGE, method = RequestMethod.POST)
	public Result getPageAllUserByKey(@RequestBody String userInfo) {
		logger.info(String.format("[getPageAllUserByKey()->request param:%s]", userInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(userInfo);
			if (null != checResult) {
				logger.error(String.format("[getPageAllUserByKey()->fail:%s]", checResult.getException()));
				return checResult;
			}
			// 分页信息
			JSONObject pageJson = JSON.parseObject(userInfo);
			RpcResponse res = userQueryRpcService.getPageAllUserByKey(pageJson);
			if (res.isSuccess()) {
				logger.info(String.format("[getPageAllUserByKey()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getPageAllUserByKey()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getAllUserByKey()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/***
	 * 
	 * @Description:根据token得到用户信息
	 * @param queryUserParam
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.QUERY_USER_TOKEN, method = RequestMethod.POST)
	public Result getUserByToken(@RequestBody String tokenInfo) {
		logger.info(String.format("[getUserByToken()->request param:%s]", tokenInfo));
		try {
			// 参数校验
			Result checJson = ExceptionChecked.checkRequestParam(tokenInfo);
			if (null != checJson) {
				logger.error(String.format("[getUserByToken()->fail:%s]", checJson.getException()));
				return checJson;
			}

			// 获取token
			JSONObject tokenJson = JSON.parseObject(tokenInfo);
			String token = tokenJson.getString(UscConstants.TOKEN);
			RpcResponse res = userQueryRpcService.getUserByToken(token);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserByToken()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserByToken()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getUserByToken()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/***
	 * 
	 * @Description:根据用户id查询用户信息
	 * @param userId
	 *            用户id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.QUERY_USER_BYID, method = RequestMethod.POST)
	public Result queryUserById(@RequestBody String userIdInfo) {
		logger.info(String.format("[queryUserById()->request param:%s]", userIdInfo));
		try {
			Result check = ExceptionChecked.checkRequestParam(userIdInfo);
			if (null != check) {
				logger.error(String.format("[queryUserById()->fail:%s]", check.getException()));
				return check;
			}

			JSONObject userIdJson = JSON.parseObject(userIdInfo);

			// 用户id
			String userId = userIdJson.getString(UscConstants.USER_ID);

			RpcResponse res = userQueryRpcService.getUserByUserId(userId);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserByToken()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserByToken()->success:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getUserByToken()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:根据登录名或者邮箱或者电话模糊查询用户列表
	 * @param loginAccount
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.QUERY_USER_BYKEY + "/{userKey}", method = RequestMethod.POST)
	public Result queryuserByName(@RequestBody String queryUserParam, @PathVariable String userKey) {
		// @todo 暂时还是用uscInfo,换的情况改动大
		logger.info("[queryuserByName()->request param:" + userKey + "]");
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(queryUserParam);
			if (null != checResult) {
				logger.error("[queryuserByName()->fail:" + checResult.getException() + "]");
				return checResult;
			}
			JSONObject queryUserParamJson = JSON.parseObject(queryUserParam);
			JSONObject userInfo = queryUserParamJson.getJSONObject(UscConstants.USC_INFO);
			// 获取租户接入方信息
			// String tenementCode =
			// userInfo.getString(ParamKeyConstants.TENEMENT_CODE);
			String tokenId = userInfo.getString(UscConstants.TOKEN);

			RpcResponse res = userQueryRpcService.queryuserByKey(userKey, tokenId);
			if (res.isSuccess()) {
				logger.info("[queryuserByName()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[queryuserByName()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[queryuserByName()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}

	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UscUrlConstants.FIND_FACTORY_IDS)
	public Result<List<Map<String, Object>>> findFactoryByIds(@RequestBody String userIdsJson) {
		logger.info("[findFactoryByIds()->request param:" + userIdsJson + "]");

		try {

			Result<List<Map<String, Object>>> checResult = ExceptionChecked.checkRequestParam(userIdsJson);
			if (null != checResult) {
				logger.error("[findFactoryByIds()->fail:" + checResult.getException() + "]");
				return checResult;
			}

			JSONObject userIds = JSON.parseObject(userIdsJson);
			JSONArray jsonArray = userIds.getJSONArray(UscConstants.USC_IDS);
			RpcResponse<List<Map<String, Object>>> res = userQueryRpcService
					.findFactoryByIds(jsonArray.toJavaList(String.class));

			if (res.isSuccess()) {
				logger.info("[findFactoryByIds()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[findFactoryByIds()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[findFactoryByIds()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}

	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UscUrlConstants.FIND_USER_KEYWORD)
	public Result<List<String>> findUserIdsByKey(@RequestBody JSONObject userKeyWord) {

		try {

			if (userKeyWord == null) {
				logger.error(String.format("[findUserIdsByKey()->error:%s]", UscConstants.NO_BUSINESS));
				return ResultBuilder.failResult(UscConstants.NO_BUSINESS);
			}

			String keyword = userKeyWord.getString(UscConstants.USC_KEY_WORD);
			String accessSecret = userKeyWord.getString(UscConstants.ACCESS_SECRET);

			RpcResponse<List<String>> res = userQueryRpcService.findUserIdsByKey(keyword, accessSecret);
			if (res.isSuccess()) {
				logger.info("[findUserIdsByKey()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[findUserIdsByKey()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[findUserIdsByKey()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}

	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@CrossOrigin(origins = "*")
	@PostMapping(value = UscUrlConstants.FIND_USER_NAME)
	public Result<Map<String, String>> findUserLoginAccout(@RequestBody JSONObject userIdsJson) {

		try {

			if (userIdsJson == null) {
				logger.error(String.format("[findUserLoginAccout()->error:%s]", UscConstants.NO_BUSINESS));
				return ResultBuilder.failResult(UscConstants.NO_BUSINESS);
			}

			List<String> userIds = userIdsJson.getObject(UscConstants.USC_USER_IDS, List.class);

			RpcResponse<List> res = userQueryRpcService.findUserInfoByIds(userIds);
			if (res.isSuccess()) {

				logger.info("[findUserIdsByKey()->success:" + res.getMessage() + "]");
				Map<String, String> useridsMap = Maps.newHashMap();
				List successValue = res.getSuccessValue();
				for (Object object : successValue) {
					Map<String, String> userMap = (Map) object;
					useridsMap.put(userMap.get(UscConstants.ID_), userMap.get(UscConstants.USERNAME));
				}
				return ResultBuilder.successResult(useridsMap, res.getMessage());

			} else {
				logger.error("[findUserIdsByKey()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error("[findUserIdsByKey()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}

	}
	/**
	 * 
	* @Description:测试
	* @param orgId
	* @return
	 */
	@CrossOrigin(origins = "*")
	@PostMapping(value = "/sss")
	public Result<List<String>> findUser(@RequestBody String orgId){
		RpcResponse<List<String>> list;
		try {
			list = test.findAllOrgParentId(orgId);
			return ResultBuilder.successResult(list.getSuccessValue(), "牛逼");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ResultBuilder.exceptionResult(e);
		}
		
	}

}
