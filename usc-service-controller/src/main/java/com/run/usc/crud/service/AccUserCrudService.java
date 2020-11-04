/*
 * File name: AccUserCrudService.java
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

package com.run.usc.crud.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.base.crud.AccUserBaseCrudService;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description: 接入方用户controller-service类
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */

@Service
public class AccUserCrudService {
	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private AccUserBaseCrudService	accUserCrud;



	public Result<JSONObject> saveAccUserInfo(String resourceInfo) {
		logger.info(String.format("[saveAccUserInfo()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[saveAccUserInfo()->fail:%s]", resourceInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = accUserCrud.saveAccUserInfo(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveAccUserInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveAccUserInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveAccUserInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONObject> saveUserRs(String resourceInfo) {
		logger.info(String.format("[saveUserRs()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[saveUserRs()->fail:%s]", resourceInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = accUserCrud.saveUserRs(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveUserRs()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveUserRs()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveAccUserInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONObject> updateUserRs(String resourceInfo) {
		logger.info(String.format("[updateUserRs()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[updateUserRs()->fail:%s]", resourceInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = accUserCrud.updateUserRs(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateUserRs()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateUserRs()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateUserRs()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONObject> swateUserState(String userInfo) {
		logger.info(String.format("[swateUserState()->request param:%s]", userInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(userInfo);
			if (null != checResult) {
				logger.error(String.format("[swateUserState()->fail:%s]", userInfo));
				return checResult;
			}

			// 用户id和用户状态
			JSONObject userJson = JSON.parseObject(userInfo);
			String userId = userJson.getString(UscConstants.USER_ID);
			String state = userJson.getString(UscConstants.STATE);

			RpcResponse<JSONObject> res = accUserCrud.swateUserState(userId, state);
			if (res.isSuccess()) {
				logger.info(String.format("[swateUserState()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[swateUserState()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[swateUserState()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	public Result updateAccUserInfo(String resourceInfo) {
		logger.info(String.format("[updateAccUserInfo()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[updateAccUserInfo()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = accUserCrud.updateAccUserInfo(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateAccUserInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateAccUserInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateAccUserInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	public Result deleteAccUserInfo(String accSourceIds) {
		logger.info(String.format("[deleteAccUserInfo()->request param:%s]", accSourceIds));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(accSourceIds);
			if (null != checResult) {
				logger.error(String.format("[deleteAccUserInfo()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 判断删除id是否为空
			JSONObject tenementCodesJson = JSON.parseObject(accSourceIds);
			String ids = tenementCodesJson.getString(UscConstants.ID_);
			if (StringUtils.isEmpty(ids)) {
				logger.error(String.format("[deleteAccUserInfo()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return ResultBuilder.noBusinessResult();
			}
			String[] tenmentCodes = ids.split(",", -1);
			List<String> deleIds = new ArrayList<String>();
			for (String id : tenmentCodes) {
				deleIds.add(id);
			}

			RpcResponse res = accUserCrud.deleteAccUserInfo(deleIds);
			if (res.isSuccess()) {
				logger.info(String.format("[deleteAccUserInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[deleteAccUserInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[deleteAccUserInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	public Result saveAccessUserRs(String accessUserInfo) {
		logger.info(String.format("[saveAccessUserRs()->request param:%s]", accessUserInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(accessUserInfo);
			if (null != checResult) {
				logger.error(String.format("[saveAccessUserRs()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = accUserCrud.saveAccessUserRs(JSON.parseObject(accessUserInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveAccessUserRs()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveAccessUserRs()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveAccessUserRs()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	public Result delAccessUserRs(String accessUserInfo) {
		logger.info(String.format("[delAccessUserRs()->request param:%s]", accessUserInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(accessUserInfo);
			if (null != checResult) {
				logger.error(String.format("[delAccessUserRs()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = accUserCrud.delAccessUserRs(JSON.parseObject(accessUserInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[delAccessUserRs()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delAccessUserRs()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[delAccessUserRs()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
