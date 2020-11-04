/*
 * File name: TenementQueryController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月29日 ... ...
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
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.base.query.TenementBaseQueryService;

/**
 * @Description: 租户查询类
 * @author: zhabing
 * @version: 1.0, 2017年6月29日
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = UscUrlConstants.TENEMENT)
public class TenementQueryController {

	private static final Logger			logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private TenementBaseQueryService	tenementquery;



	/**
	 * 
	 * @Description:分页查询租户信息
	 * @param tenementInfo
	 *            租户信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.GET_TENEMENT_BY_PAGE, method = RequestMethod.POST)
	public Result getTenementByPage(@RequestBody String tenementInfo) {
		logger.info(String.format("[getTenementByPage()->request param:%s]", tenementInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenementInfo);
			if (null != checResult) {
				logger.error("[getTenementByPage()->fail:" + checResult.getException() + "]");
				return checResult;
			}
			JSONObject tenementJson = JSON.parseObject(tenementInfo);

			// 检验里面的值是否是json格式
			String pageInfoJson = tenementJson.getString(TenementConstant.TENEMENTINFO);
			Result checPage = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checPage) {
				logger.error("[getTenementByPage()->fail:" + checPage.getException() + "]");
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			// 查询条件
			String tenementName = pageInfo.getString(TenementConstant.TENEMENT_NAME);

			Map<String, String> map = new HashMap<String, String>();
			map.put(UscConstants.PAGESIZE, pageSize);
			map.put(UscConstants.PAGENUMBER, pageNumber);
			map.put(TenementConstant.TENEMENT_NAME, tenementName);

			RpcResponse<?> res = tenementquery.getTenementByPage(map);
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
	 * 获取所有租户信息
	 */
	@RequestMapping(value = UscUrlConstants.GET_TENEMENT_ALL, method = RequestMethod.GET)
	@CrossOrigin(origins = "*")
	public Result<?> getTenementAll() {
		logger.info("进入getTenementAll()方法");
		try {
			RpcResponse<?> res = tenementquery.getTenementAll();
			if (res.isSuccess()) {
				logger.info(String.format("[getTenementAll()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getTenementAll()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getTenementAll()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:校验租户名称是否重复
	 * @param tenementName
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_CHECK_NAME, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> nameCheack(@RequestBody String tenementName) {
		logger.info(String.format("[nameCheack()->request param:%s]", tenementName));

		try {
			JSONObject tenementNameJson = JSON.parseObject(tenementName);
			RpcResponse<?> res = tenementquery.nameCheack(tenementNameJson.getString(TenementConstant.TENEMENT_NAME));
			if (res.isSuccess()) {
				logger.info(String.format("[nameCheack()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[nameCheack()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[nameCheack()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 通过tenementName获取租户信息
	 * 
	 * @param tenementName
	 *            包含tenementName的json字符串对象 默认模糊查询，包含match字段为1时完全匹配
	 * 
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_BY_TENEMENT_NAME, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> getTenementByTenementName(@RequestBody String tenementNameJson) {
		logger.info(String.format("[getTenementByTenementName()->request param:%s]", tenementNameJson));
		try {
			// 参数基础校验
			Result<?> checResult = ExceptionChecked.checkRequestParam(tenementNameJson);
			if (null != checResult) {
				logger.error("[getTenementByTenementName()->fail:" + checResult.getException() + "]");
				return checResult;
			}
			JSONObject tenementJson = JSON.parseObject(tenementNameJson);
			String tenementName = tenementJson.getString(TenementConstant.TENEMENT_NAME);
			String match = tenementJson.getString(TenementConstant.TENEMENT_MATCH);

			RpcResponse<?> res = tenementquery.getTenementByTenementName(tenementName, match);
			if (res.isSuccess()) {
				logger.info("[getTenementByTenementName()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[getTenementByTenementName()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[getTenementByTenementName()->exception:" + e.getMessage() + "]");
			return ResultBuilder.exceptionResult(e);
		}
	}
}
