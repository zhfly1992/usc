/*
 * File name: TenementCrudController.java
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

package com.run.usc.service.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.crud.service.TenementCrudService;

/**
 * @Description: 租户curd类
 * @author: zhabing
 * @version: 1.0, 2017年6月29日
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = UscUrlConstants.TENEMENT)
public class TenementCrudController {

	@Autowired
	private TenementCrudService tenService;



	/**
	 * 
	 * @Description:新增租户信息
	 * @param tenementInfo
	 *            租户信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.TENEMENT_SAVE, method = RequestMethod.POST)
	public Result saveTenement(@RequestBody String tenementInfo) {
		return tenService.saveTenement(tenementInfo);
	}



	/**
	 * 
	 * @Description:修改租户信息
	 * @param tenementInfo
	 *            租户信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.TENEMENT_UPDATE, method = RequestMethod.POST)
	public Result updateTenement(@RequestBody String tenementInfo) {
		return tenService.updateTenement(tenementInfo);
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
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.TENEMENT_DELETE, method = RequestMethod.POST)
	public Result deleteTenement(@RequestBody String tenementCodes) {
		return tenService.deleteTenement(tenementCodes);
	}



	/**
	 * 
	 * @Description:租户信息更新,更新其中的字段
	 * @param tenementInfo
	 *            含有（_id）的json字符串对象
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UscUrlConstants.TENEMENT_UPDATE_FILED, method = RequestMethod.POST)
	public Result<?> updateField(@RequestBody String tenementInfo) {
		return tenService.updateField(tenementInfo);
	}

}
