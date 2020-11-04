/*
 * File name: UscCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月21日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.crud.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.base.crud.UserBaseCurdService;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.crud.service.UserCrudService;

/**
 * @Description: 用户中心增删改
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = UscUrlConstants.API_USER_BASE_PATH)
public class UscCrudController {
	private static final Logger	logger	= Logger.getLogger(UscCrudController.class);
	@Autowired
	private UserBaseCurdService	userCrudRpcService;
	@Autowired
	private UserCrudService		userCrudService;



	/**
	 * 
	 * @Description:注册用户
	 * @param registerInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.API_CODE_REGISTER, method = RequestMethod.POST)
	public Result registerUser(@RequestBody String registerInfo) {
		return userCrudService.registerUser(registerInfo);
	}



	/**
	 * 
	 * @Description:退出登录
	 * @param authzInfo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.API_CODE_LOGINOUT, method = RequestMethod.POST)
	public Result loginout(@RequestBody String authzInfo) {
		return userCrudService.loginout(authzInfo);
	}



	/**
	 * 
	 * @Description:修改密码
	 * @param authzInfo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.API_UPDATE_PASS, method = RequestMethod.POST)
	public Result updatePass(@RequestBody String passInfo) {
		return userCrudService.updatePass(passInfo);
	}



	/**
	 * 
	 * @Description:绑定用户电话
	 * @param authzInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.UPDATE_MOBILE, method = RequestMethod.POST)
	public Result updatemobile(@RequestBody String authzInfo) {
		return userCrudService.updatemobile(authzInfo);
	}



	/**
	 * 
	 * @Description:根据秘钥激活门户注册用户
	 * @param authzInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.USER_ACTIVATE + "/{registerSecret}", method = RequestMethod.POST)
	public Result activateUser(@PathVariable String registerSecret) {
		return userCrudService.activateUser(registerSecret);
	}



	/**
	 * 
	 * @Description:根据用户Id激活
	 * @param authzInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.USER_ACTIVATE + UscUrlConstants.API_USER
			+ "/{userId}", method = RequestMethod.POST)
	public Result activateUserByUserId(@PathVariable String userId, @RequestBody String authzBody) {
		return userCrudService.activateUserByUserId(userId, authzBody);
	}



	/**
	 * 
	 * @Description:使用验证码修改密码
	 * @param resetpasswordInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.API_CODE_RESETPSD_AUTH, method = RequestMethod.POST)
	public Result resetPasswordByAuthz(@RequestBody String resetpasswordInfo) {
		return userCrudService.resetPasswordByAuthz(resetpasswordInfo);
	}



	/**
	 * 
	 * @Description:根据用户标识修改用户信息
	 * @param updateUserParam
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.API_CODE_UPDATEUSER + "/{id}", method = RequestMethod.POST)
	public Result updateUser(@RequestBody String updateUserParam, @PathVariable String id) {
		logger.info("[updateUser()->request param:" + updateUserParam + "]");
		try {
			RpcResponse res = userCrudRpcService.updateUser(updateUserParam, id);
			if (res.isSuccess()) {
				logger.info("[updateUser()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[authUser()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[authUser()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:修改用户邮箱
	 * @param userInfo
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.UPDATE_EMIL, method = RequestMethod.POST)
	public Result updateemail(@RequestBody String userInfo) {
		logger.info("[updateemail()->request param:" + userInfo + "]");
		try {
			RpcResponse res = userCrudRpcService.updateemail(userInfo);
			if (res.isSuccess()) {
				logger.info("[updateemail()->success:" + res.getMessage() + "]");
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				logger.error("[updateemail()->fail:" + res.getMessage() + "]");
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			logger.error("[updateemail()->exception]", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:通过用户id刷新登录时间
	 * @param userId
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@PostMapping(value = UscUrlConstants.REFRESH_LOGIN_TIME)
	public Result<Boolean> refreshLoginTime(@RequestBody String userId) {
		return userCrudService.refreshLoginTime(userId);
	}
}
