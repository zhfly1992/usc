/*
 * File name: AccUserCrudController.java
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

package com.run.usc.service.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.crud.service.AccUserCrudService;

/**
 * @Description: 接入方用户controller类
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_RS)
public class AccUserCrudController {
	@Autowired
	private AccUserCrudService accUser;



	/**
	 * 
	 * @Description:添加接入方用户
	 * @param userInfo
	 *            用户信息
	 * @return 添加结果
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_RS_SAVE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result saveAccUserInfo(@RequestBody String userInfo) {
		return accUser.saveAccUserInfo(userInfo);
	}



	/**
	 * 
	 * @Description:添加接入方用户和用户关联组织，关联角色的信息
	 * @param userInfo
	 *            用户信息
	 * @return 添加结果
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.SAVE_USER_RS, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result saveUserRs(@RequestBody String userInfo) {
		return accUser.saveUserRs(userInfo);
	}



	/**
	 * 
	 * @Description:修改接入方用户和用户关联组织，关联角色的信息
	 * @param userInfo
	 *            用户信息
	 * @return 添加结果
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.UPDATE_USER_RS, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result updateUserRs(@RequestBody String userInfo) {
		return accUser.updateUserRs(userInfo);
	}



	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.SWATE_USER_STATE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result swateUserState(@RequestBody String userInfo) {
		return accUser.swateUserState(userInfo);
	}



	/**
	 * 
	 * @Description:修改接入方用户
	 * @param userInfo
	 *            用户信息
	 * @return 修改结果
	 */
	@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_RS_UPDATE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> updateAccUserInfo(@RequestBody String userInfo) {
		return accUser.updateAccUserInfo(userInfo);
	}



	/**
	 * 
	 * @Description:批量删除用户
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.ACCESS_AND_USER_RS_DELETE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> deleteAccUserInfo(@RequestBody String userIds) {
		return accUser.deleteAccUserInfo(userIds);
	}



	/**
	 * 
	 * @Description:添加接入方用户关系
	 * @param accessUserInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACCESS_SAVE_USER_RS, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result saveAccessUserRs(@RequestBody String accessUserInfo) {
		return accUser.saveAccessUserRs(accessUserInfo);
	}



	/**
	 * 
	 * @Description:删除接入方用户关系
	 * @param accessUserInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = UscUrlConstants.ACCESS_DEL_RS, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result delAccessUserRs(@RequestBody String accessUserInfo) {
		return accUser.delAccessUserRs(accessUserInfo);
	}

}
