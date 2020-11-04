/*
 * File name: AccSourceCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月4日 ... ...
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.base.crud.AccSourceBaseCrudService;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description: 接入方资源crud
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */
@Service
public class AccSourceCrudService {
	private static final Logger			logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private AccSourceBaseCrudService	tenaccCrud;



	public Result<?> saveAccSourceInfo(String resourceInfo) {
		logger.info(String.format("[saveAccSourceInfo()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<?> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[saveAccSourceInfo()->fail:%s]", resourceInfo));
				return checResult;
			}
			RpcResponse<?> res = tenaccCrud.saveAccSourceInfo(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveAccSourceInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveAccSourceInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveAccSourceInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据接入方秘钥添加接入方资源信息
	 * @param resourceInfo
	 * @return
	 */
	public Result<JSONObject> saveAccSourceInfoBySecret(String resourceInfo) {
		logger.info(String.format("[saveAccSourceInfo()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[saveAccSourceInfo()->fail:%s]", resourceInfo));
				return checResult;
			}
			RpcResponse<JSONObject> res = tenaccCrud.saveAccSourceInfoBySecret(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[saveAccSourceInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[saveAccSourceInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[saveAccSourceInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据接入方秘钥修改接入方资源
	 * @param resourceInfo
	 * @return
	 */
	public Result<JSONObject> updateSourceBySecret(String resourceInfo) {
		logger.info(String.format("[updateAccSourceInfo()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<JSONObject> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[updateAccSourceInfo()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse<JSONObject> res = tenaccCrud.updateSourceBySecret(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateAccSourceInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateAccSourceInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateAccSourceInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	public Result updateAccSourceInfo(String resourceInfo) {
		logger.info(String.format("[updateAccSourceInfo()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[updateAccSourceInfo()->fail:%s]", checResult.getException()));
				return checResult;
			}
			RpcResponse res = tenaccCrud.updateAccSourceInfo(JSON.parseObject(resourceInfo));
			if (res.isSuccess()) {
				logger.info(String.format("[updateAccSourceInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[updateAccSourceInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateAccSourceInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings({ "rawtypes" })
	public Result deleteAccSourceInfo(String accSourceIds) {
		logger.info(String.format("[deleteAccSourceInfo()->request param:%s]", accSourceIds));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(accSourceIds);
			if (null != checResult) {
				logger.error(String.format("[deleteAccSourceInfo()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 判断删除id是否为空
			JSONObject tenementCodesJson = JSON.parseObject(accSourceIds);
			String ids = tenementCodesJson.getString(UscConstants.ID_);
			if (StringUtils.isEmpty(ids)) {
				logger.error(String.format("[deleteAccSourceInfo()->error:%s--->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return ResultBuilder.noBusinessResult();
			}
			String[] tenmentCodes = ids.split(",", -1);
			List<String> deleIds = new ArrayList<String>();
			for (String id : tenmentCodes) {
				deleIds.add(id);
			}

			// @TODO
			// 删除接入方资源
			RpcResponse<List<String>> res = tenaccCrud.deleteCascadeAccSourceInfo(deleIds);
			if (res.isSuccess()) {
				// 删除资源与角色的关联
				// userRoleSer.removeRoleRsMenu(deleIds);

				logger.info(String.format("[deleteAccSourceInfo()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[deleteAccSourceInfo()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[deleteAccSourceInfo()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:修改资源启用状态
	 * @param id
	 * @param state
	 * @return
	 */
	public Result<String> swateSourceState(String resourceInfo) {
		logger.info(String.format("[swateSourceState()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[swateSourceState()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 资源id,状态
			JSONObject resouJson = JSON.parseObject(resourceInfo);
			String id = resouJson.getString(UscConstants.ID_);
			String state = resouJson.getString(UscConstants.STATE);

			RpcResponse<String> res = tenaccCrud.swateSourceState(id, state);
			if (res.isSuccess()) {
				logger.info(String.format("[swateSourceState()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[swateSourceState()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[swateSourceState()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:接入方资源授权
	 * @param resourceInfo
	 * @return
	 */
	public Result<String> sourceAuthoriz(String resourceInfo) {
		logger.info(String.format("[sourceAuthoriz()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[sourceAuthoriz()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 资源id
			JSONObject resouJson = JSON.parseObject(resourceInfo);
			String sourceId = resouJson.getString(UscConstants.SOURCE_ID);
			// urlIds集合
			JSONArray urlIds = resouJson.getJSONArray(UscConstants.URLIDS);

			RpcResponse<String> res = tenaccCrud.sourceAuthoriz(sourceId, urlIds);
			if (res.isSuccess()) {
				logger.info(String.format("[sourceAuthoriz()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[sourceAuthoriz()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[sourceAuthoriz()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:删除接入方资源授权
	 * @param resourceInfo
	 * @return
	 */
	public Result<String> delSourceAuthoriz(String resourceInfo) {
		logger.info(String.format("[delSourceAuthoriz()->request param:%s]", resourceInfo));
		try {
			// 参数基础校验
			Result<String> checResult = ExceptionChecked.checkRequestParam(resourceInfo);
			if (null != checResult) {
				logger.error(String.format("[delSourceAuthoriz()->fail:%s]", checResult.getException()));
				return checResult;
			}

			// 资源id
			JSONObject resouJson = JSON.parseObject(resourceInfo);
			String sourceId = resouJson.getString(UscConstants.SOURCE_ID);
			// urlIds集合
			JSONArray urlIds = resouJson.getJSONArray(UscConstants.URLIDS);

			RpcResponse<String> res = tenaccCrud.delSourceAuthoriz(sourceId, urlIds);
			if (res.isSuccess()) {
				logger.info(String.format("[delSourceAuthoriz()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[delSourceAuthoriz()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[delSourceAuthoriz()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
