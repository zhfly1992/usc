/*
 * File name: TenementCrudService.java
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
import com.run.usc.api.base.crud.TenementBaseCrudService;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description: 控制层service
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */
@Service
public class TenementCrudService {

	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private TenementBaseCrudService	tenementCrud;



	/**
	 * 
	 * @Description:新增租户信息
	 * @param tenementInfo
	 *            租户信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public Result saveTenement(String tenementInfo) {
		logger.info(String.format("[saveTenement()->request param:%s]", tenementInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenementInfo);
			if (null != checResult) {
				logger.error(String.format("[saveTenement()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = tenementCrud.saveTenement(JSON.parseObject(tenementInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveTenement()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveTenement()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveTenement()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	public Result updateTenement(String tenementInfo) {
		logger.info(String.format("[saveTenement()->request param:%s]", tenementInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenementInfo);
			if (null != checResult) {
				logger.error(String.format("[saveTenement()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = tenementCrud.updateTenement(JSON.parseObject(tenementInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveTenement()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveTenement()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveTenement()->exception]", e);
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
	public Result deleteTenement(String tenementCodes) {
		logger.info(String.format("[deleteTenement()->request param:%s]", tenementCodes));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenementCodes);
			if (null != checResult) {
				logger.error(String.format("[deleteTenement()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 判断删除id是否为空
			JSONObject tenementCodesJson = JSON.parseObject(tenementCodes);
			String ids = tenementCodesJson.getString(TenementConstant.TENEMENT_ID);
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

			RpcResponse res = tenementCrud.deleteTenement(deleIds);
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
	 * @Description:租户信息更新,更新其中的字段
	 * @param tenementInfo
	 *            含有（_id）的json字符串对象
	 */
	public Result<?> updateField(String tenementInfo) {
		logger.info(String.format("[deleteTenement()->request param:%s]", tenementInfo));
		try {
			// 参数基础校验
			Result<?> checResult = ExceptionChecked.checkRequestParam(tenementInfo);
			if (null != checResult) {
				logger.error(String.format("[updateField()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse<?> res = tenementCrud.updateField(JSON.parseObject(tenementInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateField()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateField()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateField()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
