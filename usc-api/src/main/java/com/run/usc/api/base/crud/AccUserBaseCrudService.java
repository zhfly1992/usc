/*
 * File name: AccUserBaseCrudService.java
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

package com.run.usc.api.base.crud;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 接入方用户crud接口类
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */

public interface AccUserBaseCrudService {
	/**
	 * 
	 * @Description:保存接入方用户信息
	 * @param accessUserInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveAccUserInfo(JSONObject accessUserInfo) throws Exception;
	
	/**
	 * 
	 * @Description:保存接入方用户以及用户与组织，用户与角色的关系
	 * @param accessUserInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> saveUserRs(JSONObject accessUserInfo) throws Exception;
	
	/**
	 * 
	 * @Description:修改接入方用户以及用户与组织，用户与角色的关系
	 * @param accessUserInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updateUserRs(JSONObject accessUserInfo) throws Exception;
	
	/**
	 * 
	 * @Description:修改接入方用户以及用户与组织，用户与角色的关系
	 * @param accessUserInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> swateUserState(String userId,String state) throws Exception;



	/**
	 * 
	 * @Description: update the updateAccUserInfo
	 * @param accessUserInfo
	 * @return
	 */
	RpcResponse<JSONObject> updateAccUserInfo(JSONObject accessUserInfo) throws Exception;



	/**
	 * 删除接入方用户数据（逻辑删除）
	 * 
	 * @param ids
	 *            需包含_id的json字符串对象
	 */
	RpcResponse<List<String>> deleteAccUserInfo(List<String> ids) throws Exception;



	/**
	 * 
	 * @Description:保存接入方用户关系
	 * @param accessUserInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> saveAccessUserRs(JSONObject accessUserInfo) throws Exception;



	/**
	 * 
	 * @Description:删除接入方用户关系
	 * @param accessUserInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> delAccessUserRs(JSONObject accessUserInfo) throws Exception;

}
