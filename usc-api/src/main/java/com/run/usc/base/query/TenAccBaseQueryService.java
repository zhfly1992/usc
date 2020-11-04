/*
 * File name: TenAccBaseQueryService.java
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

package com.run.usc.base.query;

import java.util.List;
import java.util.Map;

import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 接入方查询类
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */

public interface TenAccBaseQueryService {
	/**
	 * 
	 * @Description:接入方分页查询
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccessInfoByPage(Map<String, String> pageInfo) throws Exception;



	/**
	 * 
	 * @Description:根据租户id查询他下面所有的接入方
	 * @param tenementId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getAccessInfoByTenId(String tenementId);



	/**
	 * 
	 * @Description:根据接入方id查询所有的接入方资源信息
	 * @param accessId
	 * @param selfId
	 *            本身资源id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getAllAccSouByAccId(String accessId, String orgType, String applicationType);



	/**
	 * 
	 * @Description:检测接入方是否有重名
	 * @param id
	 *            租户id
	 * @param accessName
	 * @return
	 */
	RpcResponse<Boolean> checkAccessName(String id, String accessName);



	/**
	 * 
	 * @Description:检测接入方资源是否有重名
	 * @param accessId
	 * @param accessName
	 * @return
	 */
	RpcResponse<Boolean> checkSourceName(String accessId, String applicationType, String accessName);



	/**
	 * 
	 * @Description:根据接入方秘钥查询接入方id
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<String> getAccessIdBySecret(String accessSecret);



	/**
	 * 
	 * @Description:根据接入方id查询接入方和租户信息
	 * @param accessId
	 *            接入方id
	 * @return
	 */
	RpcResponse<Map<String, Object>> getTenmentAccByAccId(String accessId);



	/**
	 * 
	 * @Description:根据接入方秘钥查询接入方和租户信息
	 * @param accessId
	 *            接入方id
	 * @return
	 */
	RpcResponse<Map<String, Object>> getTenmentAccBySecret(String accessSecret);
	
	/**
	 * 
	 * @Description:根据接入方秘钥查询接入方和租户信息
	 * @param accessId
	 *            接入方id
	 * @return
	 */
	RpcResponse<Map<String, Object>> getAccessInfoById(String id);

}
