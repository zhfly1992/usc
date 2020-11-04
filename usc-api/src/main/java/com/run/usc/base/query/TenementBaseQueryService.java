/*
 * File name: TenementBaseQueryService.java
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

package com.run.usc.base.query;

import java.util.List;
import java.util.Map;

import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;

/**
 * @Description:租户查询接口类
 * @author: zhabing
 * @version: 1.0, 2017年6月29日
 */

public interface TenementBaseQueryService {
	/**
	 * 
	 * @Description:分页查询租户
	 * @param pageInfo
	 *            分页查询参数
	 * @return
	 */
	public RpcResponse<Pagination<Map<String, Object>>> getTenementByPage(Map<String, String> pageInfo)
			throws Exception;



	/**
	 * 
	 * @Description:查询所有租户信息
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public RpcResponse<List> getTenementAll() throws Exception;



	/**
	 * 
	 * @Description:校验租户是否重名
	 * @param tenementName
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> nameCheack(String tenementName);



	/**
	 * 通过tenementName获取租户信息
	 * 
	 * @param tenementName
	 *            包含tenementName的json字符串对象 默认模糊查询，包含match字段为1时完全匹配
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public RpcResponse<List> getTenementByTenementName(String tenementName, String match) throws Exception;
	
	/**
	 * 
	* @Description: 根据租户名称查询所有的接入方集合id
	* @param tenementName
	* @return
	 */
	public List<String>  getAccessListByTenementName(String tenementName,String accessId);
}
