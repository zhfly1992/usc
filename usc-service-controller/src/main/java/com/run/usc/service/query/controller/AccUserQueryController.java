/*
 * File name: AccUserQueryController.java
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

package com.run.usc.service.query.controller;

import java.util.HashMap;
import java.util.List;
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
import com.run.entity.common.Pagination;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.base.query.AccUserBaseQueryService;

/**
 * @Description:接入方用户控制类
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_RS)
public class AccUserQueryController {
	private static final Logger		logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private AccUserBaseQueryService	accUserQuery;



	/**
	 * 
	 * @Description:接入方资源分页查询
	 * @param pageInfo
	 *            包含租户名，接入方信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_RS_QUERY_PAGE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getUserInfoByPage(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getUserInfoByPage()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getUserInfoByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			// 查询条件
			String loginAccount = pageInfo.getString(UscConstants.LOGIN_ACCOUNT);
			String tenementName = pageInfo.getString(AuthzConstants.TENEMENT_NAME);
			String accessId = pageInfo.getString(AuthzConstants.TENEMENT_ACCESS_ID);

			Map<String, String> map = new HashMap<String, String>();
			map.put(UscConstants.PAGESIZE, pageSize);
			map.put(UscConstants.PAGENUMBER, pageNumber);
			map.put(UscConstants.LOGIN_ACCOUNT, loginAccount);
			map.put(TenementConstant.TENEMENT_NAME, tenementName);
			map.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);

			RpcResponse<?> res = accUserQuery.getAccUserInfoByPage(map);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getUserInfoByPage()->exception:%s]", e.getMessage()));
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
	@RequestMapping(value = UscUrlConstants.TENEMENT_ACCUSER_CHECK_NAME, method = RequestMethod.POST)
	public Result<?> checkAccUserName(@RequestBody String accUserNameInfo) {
		logger.info("[checkAccUserName()->request param:" + accUserNameInfo + "]");
		try {
			JSONObject tenementNameJson = JSON.parseObject(accUserNameInfo);
			RpcResponse<?> res = accUserQuery.checkAccUserName(
					tenementNameJson.getString(TenementConstant.TENEMENT_ACCESS_ID),
					tenementNameJson.getString(UscConstants.NAME));
			if (res.isSuccess()) {
				logger.info(String.format("[checkAccUserName()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[checkAccUserName()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}

		} catch (Exception e) {
			logger.error(String.format("[getUserInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:接入方可添加用户分页信息
	 * @param tenPageInfo
	 *            包含租户名，接入方信息和分页信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_PAGE_QUERY_INFO, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getAddUserInfoByPage(@RequestBody String tenPageInfo) {
		logger.info(String.format("[getAddUserInfoByPage()->request param:%s", tenPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getAddUserInfoByPage()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(tenPageInfo);

			RpcResponse<?> res = accUserQuery.getAddUserInfoByPage(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getUserInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:查询接入方用户信息
	 * @param tenPageInfo
	 *            包含租户名，接入方信息和分页信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_PAGE_RS_INFO, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getUserRsPageByCode(@RequestBody String tenPageInfo) {
		logger.info(String.format("[getUserRsPageByCode()->request param:%s", tenPageInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(tenPageInfo);
			if (null != checResult) {
				logger.error(String.format("[getUserRsPageByCode()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(tenPageInfo);

			RpcResponse<?> res = accUserQuery.getUserRsPageByCode(pageInfo);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserRsPageByCode()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserRsPageByCode()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getUserRsPageByCode()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据角色Id分页查询该角色未添加的用户信息
	 * @param pageInfo
	 *            分页信息以及角色id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.GET_UN_USERINFO_BY_ROLE_PAGE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getUnUserInfoPageByRoleId(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getUserInfoPageByRoleId()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getUserInfoPageByRoleId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			// 角色id
			String roleId = pageInfo.getString(UscConstants.ROLE_ID);
			// 查询条件
			String loginAccount = pageInfo.getString(UscConstants.LOGIN_ACCOUNT);

			Map<String, String> map = new HashMap<String, String>();
			map.put(UscConstants.PAGESIZE, pageSize);
			map.put(UscConstants.PAGENUMBER, pageNumber);
			map.put(UscConstants.LOGIN_ACCOUNT, loginAccount);
			map.put(UscConstants.ROLE_ID, roleId);

			RpcResponse<Pagination<Map<String, Object>>> res = accUserQuery.getUnUserInfoPageByRoleId(map);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getUserInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据角色id分页查询该角色下面所有的用户信息
	 * @param pageInfo
	 *            分页信息以及角色id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.GET_USERINFO_BY_ROLE_PAGE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getUserInfoPageByRoleId(@RequestBody String pageInfoJson) {
		logger.info(String.format("[getUserInfoPageByRoleId()->request param:%s", pageInfoJson));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(pageInfoJson);
			if (null != checResult) {
				logger.error(String.format("[getUserInfoPageByRoleId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject pageInfo = JSON.parseObject(pageInfoJson);

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			// 角色id
			String roleId = pageInfo.getString(UscConstants.ROLE_ID);
			// 查询条件
			String loginAccount = pageInfo.getString(UscConstants.LOGIN_ACCOUNT);

			Map<String, String> map = new HashMap<String, String>();
			map.put(UscConstants.PAGESIZE, pageSize);
			map.put(UscConstants.PAGENUMBER, pageNumber);
			map.put(UscConstants.LOGIN_ACCOUNT, loginAccount);
			map.put(UscConstants.ROLE_ID, roleId);

			RpcResponse<Pagination<Map<String, Object>>> res = accUserQuery.getUserInfoPageByRoleId(map);
			if (res.isSuccess()) {
				logger.info(String.format("[getUserInfoByPage()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getUserInfoByPage()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getUserInfoByPage()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:根据接入方id查询用户所属接入方信息
	 * @param userInfo
	 *            用户id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.GET_ACCINFO_BY_USERID, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result getAccinfoByUserId(@RequestBody String userInfo) {
		logger.info(String.format("[getAccinfoByUserId()->request param:%s", userInfo));
		try {
			// 参数基础校验
			Result checResult = ExceptionChecked.checkRequestParam(userInfo);
			if (null != checResult) {
				logger.error(String.format("[getAccinfoByUserId()->fail:%s]", checResult.getException()));
				return checResult;
			}
			JSONObject userJson = JSON.parseObject(userInfo);

			String userId = userJson.getString(UscConstants.USER_ID);

			RpcResponse<List<Map>> res = accUserQuery.getListAccessByUserId(userId);
			if (res.isSuccess()) {
				logger.info(String.format("[getAccinfoByUserId()->success:%s]", res.getMessage()));
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error(String.format("[getAccinfoByUserId()->fail:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error(String.format("[getAccinfoByUserId()->exception:%s]", e.getMessage()));
			return ResultBuilder.exceptionResult(e);
		}
	}

}
