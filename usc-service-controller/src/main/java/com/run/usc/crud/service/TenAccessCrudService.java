/*
 * File name: TenAccessCrudService.java
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
import com.run.usc.api.base.crud.TenAccBaseCrudService;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description: 接入方管理Crud
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */
@Service
public class TenAccessCrudService {

	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private TenAccBaseCrudService	tenaccCrud;



	@SuppressWarnings("rawtypes")
	public Result saveAccessInfo(String tenementAccessInfo) {
		logger.info(String.format("[saveAccessInfo()->request param:%s]", tenementAccessInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenementAccessInfo);
			if (null != checResult) {
				logger.error(String.format("[saveAccessInfo()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = tenaccCrud.saveAccessInfo(JSON.parseObject(tenementAccessInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveAccessInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveAccessInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveAccessInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	public Result updateAccessInfo(String tenementAccessInfo) {
		logger.info(String.format("[updateAccessInfo()->request param:%s]", tenementAccessInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenementAccessInfo);
			if (null != checResult) {
				logger.error(String.format("[updateAccessInfo()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = tenaccCrud.updateAccessInfo(JSON.parseObject(tenementAccessInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateAccessInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateAccessInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateAccessInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:删除租户信息
	 * @param tenementCodes
	 *            租户id集合
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result deleteAccessInfo(String accessIds) {
		logger.info(String.format("[deleteTenement()->request param:%s]", accessIds));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(accessIds);
			if (null != checResult) {
				logger.error(String.format("[deleteTenement()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 判断删除id是否为空
			JSONObject tenementCodesJson = JSON.parseObject(accessIds);
			String ids = tenementCodesJson.getString(UscConstants.ID_);
			if (StringUtils.isEmpty(ids)) {
				logger.error(String.format("[deleteTenement()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return ResultBuilder.noBusinessResult();
			}
			String[] tenmentCodes = ids.split(",", -1);
			List<String> deleIds = new ArrayList<String>();
			for (String id : tenmentCodes) {
				deleIds.add(id);
			}

			RpcResponse res = tenaccCrud.deleteAccessInfo(deleIds);
			if (res.isSuccess()) {
				logger.info(String.format("[deleteTenement()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[deleteTenement()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[deleteTenement()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:删除租户信息
	 * @param tenementCodes
	 *            租户id集合
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result switchState(String stateInfo) {
		logger.info(String.format("[switchState()->request param:%s]", stateInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(stateInfo);
			if (null != checResult) {
				logger.error(String.format("[switchState()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 判断删除id是否为空
			JSONObject accStateInfo = JSON.parseObject(stateInfo);
			RpcResponse res = tenaccCrud.switchState(accStateInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[switchState()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[switchState()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[switchState()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
