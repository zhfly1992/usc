/*
 * File name: AccSourceCrudController.java
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

package com.run.usc.service.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.usc.api.constants.UscUrlConstants;
import com.run.usc.crud.service.AccSourceCrudService;

/**
 * @Description: 接入方资源curd控制类
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE)
public class AccSourceCrudController {
	@Autowired
	private AccSourceCrudService accSourceCrud;



	/**
	 * 
	 * @Description:添加接入方资源
	 * @param resourceInfo
	 *            资源信息
	 * @return 添加结果
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE_ADD, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> saveAccSourceInfo(@RequestBody String resourceInfo) {
		return accSourceCrud.saveAccSourceInfo(resourceInfo);
	}



	/**
	 * 
	 * @Description:根据接入方秘钥添加接入方资源信息
	 * @param resourceInfo
	 *            资源信息
	 * @return 添加结果
	 */
	@RequestMapping(value = UscUrlConstants.RESOURCE_ADD_BY_SECRET, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> saveAccSourceInfoBySecret(@RequestBody String resourceInfo) {
		return accSourceCrud.saveAccSourceInfoBySecret(resourceInfo);
	}



	/**
	 * 
	 * @Description:修改接入方资源
	 * @param resourceInfo
	 *            资源信息
	 * @return 修改结果
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE_UPDATE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> updateTenementResoInfo(@RequestBody String resourceInfo) {
		return accSourceCrud.updateAccSourceInfo(resourceInfo);
	}



	/**
	 * 
	 * @Description:根据接入方秘钥修改接入方资源
	 * @param resourceInfo
	 *            资源信息
	 * @return 修改结果
	 */
	@RequestMapping(value = UscUrlConstants.RESOURCE_UPDATE_BYSECRET, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<JSONObject> updateTenementResoInfoBySecret(@RequestBody String resourceInfo) {
		return accSourceCrud.updateSourceBySecret(resourceInfo);
	}



	/**
	 * 
	 * @Description:批量删除资源
	 * @param resourceIds
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.TENEMENT_RESOURCE_DELETE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<?> delTenementResoInfo(@RequestBody String resourceIds) {
		return accSourceCrud.deleteAccSourceInfo(resourceIds);
	}



	/**
	 * 
	 * @Description:修改接入方资源启用状态
	 * @param resourceInfo
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.SWATE_SOURCE_STATE, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<String> swateSourceState(@RequestBody String resourceInfo) {
		return accSourceCrud.swateSourceState(resourceInfo);
	}



	/**
	 * 
	 * @Description:接入方资源授权
	 * @param resourceInfo
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.SOURCE_AUTHORIZ, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<String> sourceAuthoriz(@RequestBody String resourceInfo) {
		return accSourceCrud.sourceAuthoriz(resourceInfo);
	}



	/**
	 * 
	 * @Description:删除接入方资源授权
	 * @param resourceInfo
	 * @return
	 */
	@RequestMapping(value = UscUrlConstants.DEL_SOURCE_AUTHORIZ, method = RequestMethod.POST)
	@CrossOrigin(origins = "*")
	public Result<String> delSourceAuthoriz(@RequestBody String resourceInfo) {
		return accSourceCrud.delSourceAuthoriz(resourceInfo);
	}
}
