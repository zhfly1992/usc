/*
 * File name: TenementBaseCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月26日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.api.base.crud;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 租户接口类
 * @author: zhabing
 * @version: 1.0, 2017年6月26日
 */
public interface TenementBaseCrudService {
	/**
	 * 
	 * @Description:租户信息保存
	 * @param tenementInfo
	 *            含有（tenementNum,tenementAddress,tenementName,tenementEmail,tenementPhone）的json字符串对象
	 */
	RpcResponse<String> saveTenement(JSONObject tenementInfo) throws Exception;



	/**
	 * 
	 * @Description:租户信息更新
	 * @param tenementInfo
	 *            含有（tenementNum,tenementAddress,tenementName,tenementEmail,tenementPhone）的json字符串对象
	 */
	RpcResponse<String> updateTenement(JSONObject tenementInfo) throws Exception;



	/**
	 * @Description:租户信息删除
	 * @param ids
	 *            还有_id的json字符串对象
	 */
	RpcResponse<List<String>> deleteTenement(List<String> ids) throws Exception;



	/**
	 * 
	 * @Description:租户信息更新
	 * @param tenementInfo
	 */
	RpcResponse<String> updateField(JSONObject tenementInfo) throws Exception;

}
