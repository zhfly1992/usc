/*
 * File name: UserCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月13日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.crud.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.base.crud.UserBaseCurdService;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description: 用户中心crud service
 * @author: zhabing
 * @version: 1.0, 2017年7月13日
 */
@Service
public class UserCrudService {
	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);
	@Autowired
	private UserBaseCurdService	userRpcCrud;



	/**
	 * 
	 * @Description:注册用户
	 * @param registerInfo
	 *            注册用户信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result registerUser(String registerInfo) {
		logger.info(String.format("[registerUser()->request param:%s]", registerInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(registerInfo);
			if (null != checResult) {
				logger.error(String.format("[registerUser()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 注册的json信息
			JSONObject registerObject = JSON.parseObject(registerInfo);

			RpcResponse res = userRpcCrud.registerUser(registerObject);
			if (res.isSuccess()) {
				logger.info(String.format("[registerUser()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[registerUser()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[registerUser()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	public Result loginout(String authzInfo) {
		logger.info(String.format("[loginout()->request param:%s]", authzInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(authzInfo);
			if (null != checResult) {
				logger.error(String.format("[loginout()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject authzObject = JSON.parseObject(authzInfo);

			// 获取token
			String token = authzObject.getString(UscConstants.TOKEN);
			RpcResponse res = userRpcCrud.loginout(token);
			if (res.isSuccess()) {
				logger.info(String.format("[loginout()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[loginout()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[loginout()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:修改密码
	 * @param authzInfo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result updatePass(String passInfo) {
		logger.info(String.format("[updatePass()->request param:%s]", passInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(passInfo);
			if (null != checResult) {
				logger.error(String.format("[updatePass()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject passJson = JSON.parseObject(passInfo);

			// 获取密码
			String password = passJson.getString(UscConstants.PASSWORD);

			// 获取token
			String token = passJson.getString(UscConstants.TOKEN);

			RpcResponse res = userRpcCrud.updatePassword(token, password);

			if (res.isSuccess()) {
				logger.info(String.format("[updatePass()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updatePass()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updatePass()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:绑定手机号
	 * @param authzInfo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result updatemobile(String authzInfo) {
		logger.info(String.format("[updatemobile()->request param:%s]", authzInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(authzInfo);
			if (null != checResult) {
				logger.error(String.format("[updatemobile()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject authzJson = JSON.parseObject(authzInfo);
			// 获取密码
			String token = authzJson.getString(UscConstants.TOKEN);

			// 获取验证码
			String sendNum = authzJson.getString(UscConstants.SEND_NUM);

			RpcResponse res = userRpcCrud.updatemobile(token, sendNum);

			if (res.isSuccess()) {
				logger.info(String.format("[updatemobile()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updatemobile()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updatemobile()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:激活门户注册用户
	 * @param registerSecret
	 *            注册秘钥
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result activateUser(String registerSecret) {
		logger.info(String.format("[activateUser()->request param:%s]", registerSecret));
		try {
			RpcResponse res = userRpcCrud.activateUser(registerSecret);

			if (res.isSuccess()) {
				logger.info(String.format("[activateUser()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[activateUser()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[activateUser()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据验证码重置密码
	 * @param registerSecret
	 *            注册秘钥
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result resetPasswordByAuthz(String registerSecret) {
		logger.info(String.format("[resetPasswordByAuthz()->request param:%s]", registerSecret));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(registerSecret);
			if (null != checResult) {
				logger.error(String.format("[resetPasswordByAuthz()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject sendfoJson = JSON.parseObject(registerSecret);

			// 获取电话号码或者邮箱
			String newPass = sendfoJson.getString(UscConstants.PASSWORD);
			String sendNum = sendfoJson.getString(UscConstants.SEND_NUM);
			// 获取是邮箱还是手机
			String type = sendfoJson.getString(UscConstants.TYPE);
			RpcResponse res = userRpcCrud.resetPasswordByAuthz(newPass, sendNum, type);
			if (res.isSuccess()) {
				logger.info(String.format("[resetPasswordByAuthz()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[resetPasswordByAuthz()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[resetPasswordByAuthz()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> activateUserByUserId(String userId, String authzBody) {
		try {
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(authzBody);
			if (null != checResult) {
				logger.error(String.format("[activateUserByUserId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject authzInfo = JSON.parseObject(authzBody);
			logger.info(String.format("[activateUserByUserId()->request param:%s]", userId));
			RpcResponse<Boolean> res = userRpcCrud.activateUserByUserId(userId, authzInfo);

			if (res.isSuccess()) {
				logger.info(String.format("[activateUserByUserId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[activateUserByUserId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[activateUserByUserId()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:通过用户id刷新登录时间
	 * @param userId
	 * @return
	 */
	public Result<Boolean> refreshLoginTime(String userId) {

		try {
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(userId);
			if (checResult != null) {
				logger.error(String.format("[refreshLoginTime()->fail:%s]", UscConstants.USER_ID));
				return checResult;
			}

			JSONObject userInfo = JSONObject.parseObject(userId);

			// 调用rpc
			RpcResponse<Boolean> res = userRpcCrud.refreshLoginTime(userInfo.getString(UscConstants.USER_ID));
			if (res.isSuccess()) {
				logger.info(String.format("[refreshLoginTime()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[refreshLoginTime()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[refreshLoginTime()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
