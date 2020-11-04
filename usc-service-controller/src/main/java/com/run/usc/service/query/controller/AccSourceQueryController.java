/*
 * File name: AccSourceQueryController.java
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

package com.run.usc.service.query.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.authz.api.constants.AuthzConstants;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.base.query.AccSourceBaseQueryService;

/**
 * @Description: 资源控制类
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE)
public class AccSourceQueryController {

	private static final Logger			logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired

	private AccSourceBaseQueryService	accSouQuery;



	/**
	 * 
	 * @Description:接入方资源分页查询
	 * @param pageInfo
	 *            包含租户名，接入方信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE_GET_PAGE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getSourceInfoByPage(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getSourceInfoByPage()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			// 查询条件
			String sourceName = pageInfo.getString(TenementConstant.ACCESS_SOURCE_NAME);
			// 接入方类型
			String accessType = pageInfo.getString(TenementConstant.ACCESS_TYPE);
			// 资源类型
			String sourceType = pageInfo.getString(TenementConstant.SOURCE_TYPE);

			Map<String, String> map = new HashMap<String, String>();
			map.put(UscConstants.PAGESIZE, pageSize);
			map.put(UscConstants.PAGENUMBER, pageNumber);
			map.put(TenementConstant.ACCESS_SOURCE_NAME, sourceName);
			map.put(TenementConstant.ACCESS_TYPE, accessType);
			map.put(TenementConstant.SOURCE_TYPE, sourceType);

			RpcResponse<?> res = accSouQuery.getAccSourceInfoByPage(map);
			if (res.isSuccess()) {
				logger.info(String.format("[getSourceInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getSourceInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.RESOURCE_TYPE_GET_ALL, method = RequestMethod.POST)
	public Result getAllSourceType(@RequestBody String dictionaryInfo) {
		logger.info(String.format("[getAllSourceType()->request param:%s", dictionaryInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(dictionaryInfo);
			if (null != checResult) {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject dicInfo = JSON.parseObject(dictionaryInfo);

			String dicName = dicInfo.getString(UscConstants.NAME);
			RpcResponse res = accSouQuery.getAllSourceType(dicName);
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
	 * @Description:根据资源id查询资源所绑定的url信息
	 * @param accessIdInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.GET_URL_BY_SOURCEID, method = RequestMethod.POST)
	public Result<List<Map>> getListUrlBySourceId(@RequestBody String sourceInfo) {
		logger.info(String.format("[getListUrlBySourceId()->request param:%s]", sourceInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(sourceInfo);
			if (null != checResult) {
				logger.error(String.format("[getListUrlBySourceId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject sourceObj = JSON.parseObject(sourceInfo);
			// 接入方秘钥
			String sourceId = sourceObj.getString(UscConstants.SOURCE_ID);

			RpcResponse<List<Map>> res = accSouQuery.getListUrlBySourceId(sourceId);
			if (res.isSuccess()) {
				logger.info(String.format("[getListUrlBySourceId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getListUrlBySourceId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[getListUrlBySourceId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据接入方id查询所有的接入方菜单信息
	 * @param accessIdInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.GET_MENU_BY_ACCESSID, method = RequestMethod.POST)
	public Result<List<Map>> getListMenuByAccessId(@RequestBody String accessIdInfo) {
		logger.info(String.format("[getListMenuByAccessId()->request param:%s]", accessIdInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(accessIdInfo);
			if (null != checResult) {
				logger.error(String.format("[getListMenuByAccessId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject accessIdObj = JSON.parseObject(accessIdInfo);
			// 接入方类型
			String accessType = accessIdObj.getString(TenementConstant.ACCESS_TYPE);
			// 应用类型
			String applicationType = accessIdObj.getString(UscConstants.APPLICATIONTYPE);

			RpcResponse<List<Map>> res = accSouQuery.getListMenuByAccessId(accessType, applicationType);
			if (res.isSuccess()) {
				logger.info(String.format("[getListMenuByAccessId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getListMenuByAccessId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[getListMenuByAccessId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据接入方id查询所有的接入方按钮信息
	 * @param accessIdInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.GET_BUTTON_BY_ACCESSID, method = RequestMethod.POST)
	public Result<List<Map>> getListButtonByAccessId(@RequestBody String accessIdInfo) {
		logger.info(String.format("[getListButtonByAccessId()->request param:%s]", accessIdInfo));
		try {
			// 参数基础校验
			Result<List<Map>> checResult = ExceptionChecked.checkRequestParam(accessIdInfo);
			if (null != checResult) {
				logger.error(String.format("[getListButtonByAccessId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject accessIdObj = JSON.parseObject(accessIdInfo);
			// 接入方id
			String accessId = accessIdObj.getString(UscConstants.ACCESSID);
			String buttonMenu = accessIdObj.getString(UscConstants.BUTTONMENU);

			RpcResponse<List<Map>> res = accSouQuery.getListButtonByAccessId(accessId, buttonMenu);
			if (res.isSuccess()) {
				logger.info(String.format("[getListButtonByAccessId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getListButtonByAccessId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[getListButtonByAccessId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/***
	 * 
	 * @Description:根据接入方id以及资源类型查询该接入方资源信息
	 * @param pageInfoJson
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACC_RESOURCE_GET_PAGE_BYTYPE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getAccSourcePageInfoByType(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getSourceInfoByPage()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			RpcResponse<Pagination<Map<String, Object>>> res = accSouQuery.getAccSourcePageInfoByType(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getSourceInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getSourceInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:接入方根级资源分页查询
	 * @param pageInfoJson
	 * @return Result
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.GET_PARENT_ACCSOURCE_BYPAGE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getParentAccSourcePageByType(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getSourceInfoByPage()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			RpcResponse<Pagination<Map<String, Object>>> res = accSouQuery.getParentAccSourcePageByType(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getSourceInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getSourceInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/***
	 * 
	 * @Description:根据接入方id以及资源类型查询该接入方资源信息
	 * @param pageInfoJson
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACC_RESOURCE_GET_BYTYPE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getAccSourceInfoByType(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getSourceInfoByPage()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			RpcResponse<List<Map>> res = accSouQuery.getAccSourceInfoByType(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getSourceInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getSourceInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getSourceInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/***
	 * 
	 * @Description:根据接入方id,资源id查询该资源已拥有或者未拥有的url信息
	 * @param pageInfoJson
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACC_RESOURCE_URL_BYPAGE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getAccSourceUrlByPage(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getAccSourceUrlByPage()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getAccSourceUrlByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			RpcResponse<Pagination<Map<String, Object>>> res = accSouQuery.getAccSourceUrlByPage(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getAccSourceUrlByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAccSourceUrlByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getAccSourceUrlByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/***
	 * 
	 * @Description:根据資源id查詢資源以及他的父類信息
	 * @param idInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACC_RESOURCE_BY_ID, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getAccSourceById(@RequestBody String idInfo) {
		logger.info(String.format("[getAccSourceById()->request param:%s", idInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(idInfo);
			if (null != checResult) {
				logger.error(String.format("[getAccSourceById()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject idJson = JSON.parseObject(idInfo);
			String id = idJson.getString(AuthzConstants.ID_);

			RpcResponse<Map> res = accSouQuery.getSourceMessageById(id);
			if (res.isSuccess()) {
				logger.info(String.format("[getAccSourceById()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAccSourceById()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getAccSourceById()->exception:%s]", e.getMessage()));
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
	public Result<Boolean> checkTeneResoName(@RequestBody String sourceNameInfo) {
		logger.info("[checkTeneResoName()->request param:" + sourceNameInfo + "]");
		try {

			// 参数基础校验
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(sourceNameInfo);
			if (null != checResult) {
				logger.error(String.format("[checkTeneResoName()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject tenementNameJson = JSON.parseObject(sourceNameInfo);
			RpcResponse<Boolean> res = accSouQuery.checkSourceName(
					tenementNameJson.getString(TenementConstant.ACCESS_SOURCE_NAME),
					tenementNameJson.getString(TenementConstant.ACCESS_TYPE));
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
	@RequestMapping(value = UscUrlConstants.CHECK_ORG_NAME, method = RequestMethod.POST)
	public Result<Boolean> checkOrgName(@RequestBody String sourceNameInfo) {
		logger.info("[checkOrgName()->request param:" + sourceNameInfo + "]");
		try {

			// 参数基础校验
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(sourceNameInfo);
			if (null != checResult) {
				logger.error(String.format("[checkOrgName()->fail:%s]", checResult.getException()));
				return checResult;
			}
			// TODO 组织id
			JSONObject tenementNameJson = JSON.parseObject(sourceNameInfo);
			RpcResponse<Boolean> res = accSouQuery.checkOrgName(
					tenementNameJson.getString(TenementConstant.ACCESS_SOURCE_NAME),
					tenementNameJson.getString(UscConstants.ACCESS_SECRET),
					tenementNameJson.getString(UscConstants.PARENT_ID),
					tenementNameJson.getString(UscConstants.SOURCE_TYPE),
					tenementNameJson.getString(UscConstants.ACCESSTYPE), null);
			if (res.isSuccess()) {
				logger.info(String.format("[checkOrgName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkOrgName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[checkOrgName()->exception:%s]", e.getMessage()));
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
	@RequestMapping(value = UscUrlConstants.CHECK_ORG_HAS_CHILD, method = RequestMethod.POST)
	public Result<Boolean> checkOrgHasChild(@RequestBody String sourceNameInfo) {
		logger.info("[checkOrgHasChild()->request param:" + sourceNameInfo + "]");
		try {

			// 参数基础校验
			Result<Boolean> checResult = ExceptionChecked.checkRequestParam(sourceNameInfo);
			if (null != checResult) {
				logger.error(String.format("[checkOrgHasChild()->fail:%s]", checResult.getException()));
				return checResult;
			}

			JSONObject tenementNameJson = JSON.parseObject(sourceNameInfo);
			RpcResponse<Boolean> res = accSouQuery.checkOrgHasChild(tenementNameJson.getString(UscConstants.ID));
			if (res.isSuccess()) {
				logger.info(String.format("[checkOrgName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkOrgName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[checkOrgName()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	@SuppressWarnings("unchecked")
	@CrossOrigin(origins = "*")
	@PostMapping(value = UscUrlConstants.FIND_ORG_NAME)
	public Result<Map<String, String>> findOrgName(@RequestBody JSONObject orgJsonIds) {

		try {

			if (orgJsonIds == null) {
				logger.error(String.format("[findOrgName()->error:%s]", UscConstants.NO_BUSINESS));
				return ResultBuilder.failResult(UscConstants.NO_BUSINESS);
			}

			List<String> sourceIds = orgJsonIds.getObject(UscConstants.USC_ORG_IDS, List.class);

			RpcResponse<List<Map<String, Object>>> res = accSouQuery.getSourceMessByIds(sourceIds);

			if (res.isSuccess()) {

				logger.info("[findUserIdsByKey()->success:" + res.getMessage() + "]");
				Map<String, String> orgidsMap = Maps.newHashMap();

				List<Map<String, Object>> successValue = res.getSuccessValue();
				for (Map<String, Object> map : successValue) {
					orgidsMap.put(map.get(UscConstants.ID_) + "", map.get(UscConstants.SOURCE_NAME) + "");
				}

				return ResultBuilder.successResult(orgidsMap, res.getMessage());

			} else {
				logger.error("[findUserIdsByKey()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[findOrgName()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	@SuppressWarnings("rawtypes")
	@CrossOrigin(origins = "*")
	@PostMapping(value = UscUrlConstants.FIND_ORG_ID)
	public Result<List<String>> getModelByParentId(@RequestBody JSONObject orgJson) {
		logger.info(String.format("[getModelByParentId()->request param:%s]", orgJson));
		try {
			if (orgJson == null || StringUtils.isBlank(orgJson.getString(UscConstants.ORGANIZED_ID))) {
				logger.error(String.format("[getModelByParentId()->error:%s]", UscConstants.NO_BUSINESS));
				return ResultBuilder.failResult(UscConstants.NO_BUSINESS);
			}

			RpcResponse<List<Map>> res = accSouQuery.getModelByParentId(orgJson.getString(UscConstants.ORGANIZED_ID));

			if (res.isSuccess()) {

				List<Map> successValue = res.getSuccessValue();
				List<String> orgIds = Lists.newArrayList();
				for (Map orgMap : successValue) {
					orgIds.add(orgMap.get(UscConstants.ID_) + "");
				}

				logger.error(String.format("[getModelByParentId()->suc:%s]", res.getMessage()));
				return ResultBuilder.successResult(orgIds, res.getMessage());
			} else {
				logger.error(String.format("[getModelByParentId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[getModelByParentId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}

	}



	@CrossOrigin(origins = "*")
	@PostMapping(value = UscUrlConstants.CHECK_SOURCE_ID)
	public Result<Boolean> checkSourceIdsHaveChildId(@RequestBody Map<String, List<String>> deleteIds) {
		logger.info(String.format("[checkSourceIdsHaveChildId()->request param:%s]", deleteIds));
		try {
			if (deleteIds == null) {
				logger.error(String.format("[checkSourceIdsHaveChildId()->error:%s]", UscConstants.NO_BUSINESS));
				return ResultBuilder.failResult(UscConstants.NO_BUSINESS);
			}

			RpcResponse<Boolean> res = accSouQuery.checkSourceIdsHaveChildId(deleteIds.get(UscConstants.USC_IDS));
			if (res.isSuccess()) {
				logger.error(String.format("[checkSourceIdsHaveChildId()->suc:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkSourceIdsHaveChildId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[checkSourceIdsHaveChildId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}

}
