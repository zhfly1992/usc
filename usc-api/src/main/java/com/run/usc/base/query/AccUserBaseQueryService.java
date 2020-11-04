/*
 * File name: AccUserBaseQueryService.java
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

package com.run.usc.base.query;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 接入方用户查询接口
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */

public interface AccUserBaseQueryService {
	/**
	 * 
	 * @Description:接入方用户分页查询
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccUserInfoByPage(Map<String, String> pageInfo) throws Exception;



	/**
	 * 
	 * @Description:根据角色Id分页查询该角色未添加的用户信息
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getUnUserInfoPageByRoleId(Map<String, String> pageInfo)
			throws Exception;



	/**
	 * 
	 * @Description:根据角色Id分页查询该角色所拥有的用户信息
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getUserInfoPageByRoleId(Map<String, String> pageInfo) throws Exception;



	/**
	 * 
	 * @Description:检测用户是否重名
	 * @param accessId
	 *            接入方id
	 * @param name
	 *            用户名称
	 * @return
	 */
	RpcResponse<Boolean> checkAccUserName(String accessId, String name);



	/**
	 * 
	 * @Description:接入方可添加用户分页查询
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAddUserInfoByPage(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:接入方用户分页查询
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getUserRsPageByCode(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:根据用户id查询用户所属接入方信息
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListAccessByUserId(String userId);



	/**
	 * 
	 * @Description:根据接入方ID集合查询接入方用户信息
	 * @param userIds
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListUserByUserIds(List<String> userIds);



	/**
	 * 
	 * @Description: 查询用户状态是否正常
	 * @param id
	 * @return
	 */
	Boolean checkUserState(String id);



	/**
	 * 
	 * @Description:根据用户id查询用户所属接入方信息
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListAccessByUserInfo(String userInfo);

}
