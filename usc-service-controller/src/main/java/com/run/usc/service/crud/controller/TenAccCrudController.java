/*
 * File name: TenAccCrudController.java
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

package com.run.usc.service.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.crud.service.TenAccessCrudService;

/**
 * @Description: 接入方控制类
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */
@RestController
@RequestMapping(value = UscUrlConstants.TENEMENT_ACCESS_INFO)
public class TenAccCrudController {

	@Autowired
	private TenAccessCrudService tenAccess;



	/**
	 * 保存接入方信息
	 * 
	 * @param TenementAccessInfo
	 *            包含accessName,accessRootDomain,accesssTenementId的json字符串对象
	 */

	@RequestMapping(value = UscUrlConstants.TENEMENT_ACCESS_SAVE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> saveAccessInfo(@RequestBody String tenementAccessInfo) {
		return tenAccess.saveAccessInfo(tenementAccessInfo);
	}



	/**
	 * 更新接入方数据
	 * 
	 * @param TenementAccessInfo
	 *            包含acessName，accessRootDomain，accesssTenementId的json字符串
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_ACCESS_UPDATE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> updateAccessInfo(@RequestBody String tenementAccessInfo) {
		return tenAccess.updateAccessInfo(tenementAccessInfo);
	}



	/**
	 * 删除接入方数据（逻辑删除）
	 * 
	 * @param TenementAccessInfo
	 *            需包含_id的json字符串对象
	 */

	@RequestMapping(value = UscUrlConstants.TENEMENT_ACCESS_DELETE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> deleteAccessInfo(@RequestBody String accessIds) {
		return tenAccess.deleteAccessInfo(accessIds);
	}



	/**
	 * 
	 * 
	 * @Description:启用和停用接入方状态
	 * @param stateInfo，包含id和State必填字段
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_ACCESS_STATE_EDIT, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> switchState(@RequestBody String stateInfo) {
		return tenAccess.switchState(stateInfo);
	}

}