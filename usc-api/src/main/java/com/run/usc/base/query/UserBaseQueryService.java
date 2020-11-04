/*
 * File name: UserBaseQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月22日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.base.query;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 用户中心查询
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */
@SuppressWarnings("rawtypes")
public interface UserBaseQueryService {

	/**
	 * 
	 * @Description:根据接入方资源id查询用户id列表
	 * @param accessId
	 *            接入方id
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<String>> getUserIdByAccId(String accessId) throws Exception;



	/**
	 * 
	 * @Description:门户判断手机或者邮箱是否已经存在
	 * @param emailMob
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> checkUserExitByEmiMob(String emailMob) throws Exception;



	/**
	 * 
	 * @Description:发送手机或者邮箱
	 * @param emailMob
	 *            手机或者邮箱
	 * @param type
	 *            email or mobile
	 * @param loginAccount
	 *            用户名校验
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> sendEmiMob(String emailMob, String type, String loginAccount) throws Exception;



	/**
	 * 
	 * @Description:根据门户标识模糊查询手机或者登陆名或者邮箱是否存在
	 * @param emLogMob
	 * @return
	 */
	Boolean checkUserExistByUserType(String emLogMob) throws Exception;



	/**
	 * 
	 * @Description:用户登录验证
	 * @param loginAccout
	 * @param password
	 * @param userType
	 * @return
	 * @throws Exception
	 */

	RpcResponse userAuthz(String loginAccout, String password) throws Exception;



	/**
	 * 
	 * @Description:用户登录验证
	 * @param loginAccout
	 * @param password
	 * @param userType
	 * @return
	 * @throws Exception
	 */

	RpcResponse appUserAuthz(String loginAccout, String password) throws Exception;



	/**
	 * 
	 * @Description:检查登录名
	 * @param userInfo
	 * @return
	 */
	RpcResponse checkloginname(String userInfo) throws Exception;



	/**
	 * 
	 * @Description:退出登录
	 * @param loginInfo
	 * @return
	 */
	RpcResponse loginout(String loginInfo);



	/***
	 * 
	 * @Description:根据用户标识分页查询所有用户信息
	 * @param getAllUserByKey
	 * @param pageInfo
	 *            分页信息
	 * @return
	 */
	RpcResponse getPageAllUserByKey(JSONObject pageInfo) throws Exception;



	/***
	 * 
	 * @Description:根据token得到用户信息
	 * @param queryUserParam
	 * @param id
	 * @return
	 */
	RpcResponse getUserByToken(String tokenId) throws Exception;



	/***
	 * 
	 * @Description:根据用户id得到用户信息
	 * @param userId
	 *            用户id
	 * @return
	 */
	RpcResponse getUserByUserId(String userId) throws Exception;



	/**
	 * 
	 * @Description:根据登录名模糊查询用户列表
	 * @param loginAccount
	 * @return
	 */
	RpcResponse queryuserByKey(String userKey, String tokenId) throws Exception;



	/**
	 * 
	 * @Description:根据父类资源查询子类资源集合
	 * @param sourceId
	 *            资源id
	 * @return
	 */
	RpcResponse<List<String>> querySourceChild(String sourceId);



	/**
	 * 
	 * @Description:获取该组织下能或不能接收短信的用户
	 * @param paramInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<List<Map>> getUserByUserIds(JSONObject paramInfo) throws Exception;



	/**
	 * 
	 * @Description:通过登录名密码接入方密钥获取用户id
	 * @param loginAccount
	 * @param password
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<String> getUserIdOrAccessByLogin(String loginAccount, String password, String accessSecret);



	/**
	 * 
	 * @Description:通过接入方查询最近一个月的使用率
	 * @param accessId
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> getUsageRate(String accessId) throws Exception;



	/**
	 * 
	 * @Description:通过用户id查询该用户id对应的厂家id
	 * @param userIds
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> findFactoryByIds(List<String> userIds);



	/**
	 * 
	 * @Description:通过用户ids 查询这些用户id的信息
	 * @param userIds
	 * @return
	 */
	RpcResponse<List> findUserInfoByIds(List<String> userIds);



	/**
	 * 
	 * @Description:根据接入方密钥以及关键字模糊查询用户 返回用户id
	 * @param keyWord
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<List<String>> findUserIdsByKey(String keyWord, String accessSecret);
	
	/**
	 * 
	 * @Description:获取密码加密的公钥
	 * @return
	 */
	RpcResponse<String> findPublicKeyForLoginEncode();

}
