/*
 * File name: AccSourceBaseQueryService.java
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

package com.run.usc.base.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 接入方资源查询
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */

public interface AccSourceBaseQueryService {
	/**
	 * 
	 * @Description:接入方分页查询
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccSourceInfoByPage(Map<String, String> pageInfo) throws Exception;



	/**
	 * 
	 * @Description:查询所有资源类型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getAllSourceType(String dicName) throws Exception;



	/***
	 * 
	 * @Description:根据接入方id查询该接入方下面所有的菜单资源信息
	 * @param accessId
	 *            接入方id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListMenuByAccessId(String accessId, String applicationType);



	/**
	 * @Description:根据接入方id查询接入方下按钮资源
	 * @param accessId
	 * @return RpcResponse<List<Map>>
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListButtonByAccessId(String accessId, String buttonMenu);



	/**
	 * @Description:根据id集合获取按钮
	 * @param buttonIds
	 * @return RpcResponse<List<Map>>
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListButtonByid(ArrayList<String> buttonIds);



	/***
	 * 
	 * @Description:根据菜单id集合查询菜单信息
	 * @param accessId
	 *            接入方id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListMenuByIds(List menuIds);



	/***
	 * 
	 * @Description:根据菜单id集合查询菜单信息
	 * @param accessId
	 *            接入方id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListMenuByIds(List menuIds, String applicationType);



	/***
	 * 
	 * @Description:根据资源id和资源类型查询该资源所绑定的url地址
	 * @param sourceId
	 *            资源id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getListUrlBySourceId(String sourceId);



	/***
	 * 
	 * @Description:根据资源id查询资源信息
	 * @param id
	 *            资源id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Map getSourceMessById(String id);



	/***
	 * 
	 * @Description:根据资源id查询父类资源信息
	 * @param id
	 *            资源id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<Map> getSourceMessageById(String id) throws Exception;



	/**
	 * 
	 * @Description:接入方资源分页查询
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccSourcePageInfoByType(JSONObject pageInfo) throws Exception;



	/**
	 * @Description:接入方根级资源查询
	 * @param pageInfo
	 * @return RpcResponse<Pagination<Map<String, Object>>>
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getParentAccSourcePageByType(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:接入方资源信息查询
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getAccSourceInfoByType(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:根据资源id集合查询资源信息
	 * @param sourceIds
	 *            资源ids集合
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getSourceMessByIds(List<String> sourceIds);



	/**
	 * 
	 * @Description:查询接入方资源未授权或者已授权的url地址
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Pagination<Map<String, Object>>> getAccSourceUrlByPage(JSONObject pageInfo) throws Exception;



	/**
	 * 
	 * @Description:门户接人方资源是否重复
	 * @param emailMob
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> checkSourceName(String sourceName, String accessType) throws Exception;



	/**
	 * 
	 * @Description:门户接人方组织是否重复
	 * @param emailMob
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> checkOrgName(String sourceName, String accessSecret, String parentId, String sourceType,
			String accessType, String id) throws Exception;



	/**
	 * @Description: 校验组织是否存在子类组织
	 * @param id
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> checkOrgHasChild(String id) throws Exception;



	/**
	 * 
	 * @Description:通过组织id查询最上级父类组织id
	 * @param orgId
	 * @return 父类组织id
	 * @throws Exception
	 */
	RpcResponse<String> findOrgParentId(String orgId) throws Exception;



	/**
	 * 
	 * @Description:通过组织id查询所有的父级组织id
	 * @param orgTd
	 * @return
	 */

	RpcResponse<List<String>> findAllOrgParentId(String orgId) throws Exception;



	/**
	 * 
	 * @Description: 通过组织id获取所有的子类组织
	 * @param resourceId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<List<Map>> getModelByParentId(String resourceId) throws Exception;



	/**
	 * 
	 * @Description:通过listId获取信息 key = id value = name
	 * @param sourcesId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<Map> findSourceInfo(List<String> sourcesIds) throws Exception;



	/**
	 * 
	 * @Description:通过接入方id查询接入方信息
	 * @param accessId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<Map> findAccInfoById(String accessId) throws Exception;



	/**
	 * 
	 * @Description:校验id下是否具有子资源
	 * @param ids
	 * @return
	 */
	RpcResponse<Boolean> checkSourceIdsHaveChildId(List<String> ids);

}
