/*
 * File name: TenAccQueryController.java
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

package com.run.usc.service.query.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.authz.api.constants.AuthzConstants;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.base.query.TenAccBaseQueryService;

/**
 * @Description: 接入方控制类
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = UscUrlConstants.TENEMENT_ACCESS_INFO)
public class TenAccQueryController {

	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private TenAccBaseQueryService	tenaccQuery;



	/**
	 * 
	 * @Description:租户分页查询
	 * @param pageInfo
	 *            包含租户名，接入方信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.TENEMENT_AND_ACCESS_RS_SEARCH, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getAccessInfoByPage(@RequestBody String pageInfoJson) {
		logger.info("[getTenementByPage()->request param:" + pageInfoJson + "]");
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error("[getTenementByPage()->fail:" + checResult.getException() + "]");
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			// 查询条件
			String accessName = pageInfo.getString(TenementConstant.TENEMENT_ACCESS_NAME);

			Map<String, String> map = new HashMap<String, String>();
			map.put(UscConstants.PAGESIZE, pageSize);
			map.put(UscConstants.PAGENUMBER, pageNumber);
			map.put(TenementConstant.TENEMENT_ACCESS_NAME, accessName);

			RpcResponse<?> res = tenaccQuery.getAccessInfoByPage(map);
			if (res.isSuccess()) {
				logger.info("[getTenementByPage()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getTenementByPage()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getTenementByPage()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 通过租户id获取所有接入方信息
	 * 
	 * @param tenementId
	 *            租户id
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_ACCESS_BY_TENEMTN_ID, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> getAccessInfoByTenId(@RequestBody String tenementId) {
		logger.info(String.format("[getAccessInfoByTenId()->request param:%s]", tenementId));
		try {
			// 参数基础校验
			Result<?> checResult = ExceptionChecked.checkRequestParam(tenementId);
			if (null != checResult) {
				logger.error(String.format("[getAccessInfoByTenId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(tenementId);
			String id = pageInfo.getString(UscConstants.ID);

			RpcResponse<?> res = tenaccQuery.getAccessInfoByTenId(id);
			if (res.isSuccess()) {
				logger.info(String.format("[getAccessInfoByTenId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAccessInfoByTenId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getTenementAccessInfoByTenementId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description: 根据接入方查询该接入方下面的所有资源
	 * @param id
	 *            接入方id
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE_GET_ALL, method = RequestMethod.POST)
	public Result<?> getAllAccSouByAccId(@RequestBody String accessIdInfo) {
		logger.info(String.format("[getAccessInfoByTenId()->request param:%s]", accessIdInfo));
		try {
			// 参数基础校验
			Result<?> checResult = ExceptionChecked.checkRequestParam(accessIdInfo);
			if (null != checResult) {
				logger.error(String.format("[getAccessInfoByTenId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(accessIdInfo);
			String id = pageInfo.getString(UscConstants.ID);
			String orgType = pageInfo.getString(TenementConstant.SOURCE_TYPE);
			String applicationType = pageInfo.getString(UscConstants.APPLICATIONTYPE);

			RpcResponse<?> res = tenaccQuery.getAllAccSouByAccId(id, orgType, applicationType);
			if (res.isSuccess()) {
				logger.info(String.format("[getAccessInfoByTenId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAccessInfoByTenId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getTenementAccessInfoByTenementId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:检测接入方是否有重名
	 * @param id
	 * @param accessName
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.CHECK_ACCESS_NAME, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> checkAccessName(@RequestBody String accessNameInfo) {
		logger.info(String.format("[checkAccessName()->request param:%s]", accessNameInfo));
		try {
			JSONObject tenementNameJson = JSON.parseObject(accessNameInfo);
			RpcResponse<?> res = tenaccQuery.checkAccessName(tenementNameJson.getString(UscConstants.ID_),
					tenementNameJson.getString(TenementConstant.TENEMENT_ACCESS_NAME));
			if (res.isSuccess()) {
				logger.info(String.format("[checkTeneResoName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkTeneResoName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[checkTeneResoName()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:检验名称是否重复
	 * @param name
	 *            名称
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE_CHECK_NAME, method = RequestMethod.POST)
	public Result<?> checkTeneResoName(@RequestBody String sourceNameInfo) {
		logger.info("[checkTeneResoName()->request param:" + sourceNameInfo + "]");
		try {
			JSONObject tenementNameJson = JSON.parseObject(sourceNameInfo);
			RpcResponse<?> res = tenaccQuery.checkSourceName(tenementNameJson.getString(TenementConstant.ACCESS_TYPE),
					tenementNameJson.getString(AuthzConstants.APPLICATION_TYPE),
					tenementNameJson.getString(UscConstants.NAME));
			if (res.isSuccess()) {
				logger.info(String.format("[checkTeneResoName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkTeneResoName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[checkTeneResoName()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}

}
