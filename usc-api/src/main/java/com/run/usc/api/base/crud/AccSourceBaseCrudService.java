/*
 * File name: AccSourceBaseCrudService.java
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

package com.run.usc.api.base.crud;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 接入方资源crud
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */

public interface AccSourceBaseCrudService {
	/**
	 * 
	 * @Description:保存接入方资源信息
	 * @param accessSourceInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveAccSourceInfo(JSONObject accessSourceInfo) throws Exception;



	/**
	 * 
	 * @Description:根据接入方秘钥保存接入方资源信息
	 * @param accessSourceInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveAccSourceInfoBySecret(JSONObject accessSourceInfo) throws Exception;



	/**
	 * 
	 * @Description: update the updateAccSourceInfo
	 * @param accessSourceInfo
	 * @return
	 */
	RpcResponse<JSONObject> updateAccSourceInfo(JSONObject accessSourceInfo) throws Exception;



	/**
	 * 
	 * @Description:修改接入方资源状态
	 * @param accessSourceInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> swateSourceState(String id, String state) throws Exception;



	/**
	 * 
	 * @Description:根据接入方秘钥修改接入方资源信息
	 * @param accessSourceInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updateSourceBySecret(JSONObject accessSourceInfo) throws Exception;



	/**
	 * 删除接入方资源数据（逻辑删除）
	 * 
	 * @param ids
	 *            需包含_id的json字符串对象
	 */
	RpcResponse<List<String>> deleteAccSourceInfo(List<String> ids) throws Exception;



	/**
	 * 
	 * @Description:资源授权
	 * @param sourceId
	 * @param urlIds
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> sourceAuthoriz(String sourceId, JSONArray urlIds) throws Exception;



	/**
	 * 
	 * @Description:资源授权
	 * @param sourceId
	 * @param urlIds
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> delSourceAuthoriz(String sourceId, JSONArray urlIds) throws Exception;



	/**
	 * 删除接入方资源数据（级联删除）
	 * 
	 * @param ids
	 *            需包含_id的json字符串对象
	 */
	RpcResponse<List<String>> deleteCascadeAccSourceInfo(List<String> ids);
}
