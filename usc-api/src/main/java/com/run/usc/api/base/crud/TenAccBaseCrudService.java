/*
 * File name: TenAccBaseCrudService.java
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

package com.run.usc.api.base.crud;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 接入方crud
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */

public interface TenAccBaseCrudService {

	/**
	 * 
	 * @Description:保存接入方信息
	 * @param TenementAccessInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> saveAccessInfo(JSONObject tenementAccessInfo) throws Exception;



	/**
	 * 
	 * @Description: update the TenementAccessInfo
	 * @param TenementAccessInfo
	 * @return
	 */
	RpcResponse<String> updateAccessInfo(JSONObject tenementAccessInfo) throws Exception;



	/**
	 * 删除接入方数据（逻辑删除）
	 * 
	 * @param ids
	 *            需包含_id的json字符串对象
	 */
	RpcResponse<List<String>> deleteAccessInfo(List<String> ids) throws Exception;



	/**
	 * 
	 * @Description:修改接入方状态
	 * @param accStateInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> switchState(JSONObject accStateInfo) throws Exception;

}
