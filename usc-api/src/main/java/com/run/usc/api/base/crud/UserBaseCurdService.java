/*
 * File name: UserBaseCurdService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月21日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.api.base.crud;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
 * @Description: 用户中心crud
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

public interface UserBaseCurdService {

	/**
	 * 
	 * @Description:注册用户
	 * @param registerInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<String> registerUser(JSONObject registerInfo) throws Exception;



	/**
	 * 
	 * @Description:接入方注册用户
	 * @param registerInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> accessRegisterUser(JSONObject registerInfo, JSONObject authzInfo) throws Exception;



	/**
	 * 
	 * @Description:退出登录
	 * @param userId
	 * @param tokenId
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> loginout(String tokenId) throws Exception;



	/**
	 * 
	 * @Description:修改密码
	 * @param updatepasswordInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updatePassword(String tokenId, String updatepasswordInfo) throws Exception;



	/**
	 * 
	 * @Description:绑定手机号
	 * @param updatepasswordInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<JSONObject> updatemobile(String token, String sendNum) throws Exception;



	/**
	 * 
	 * @Description:绑定手机号
	 * @param updatepasswordInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	RpcResponse<Map> activateUser(String registerSecret) throws Exception;



	/**
	 * 
	 * @Description:使用验证码修改密码
	 * @param resetpasswordInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<?> resetPasswordByAuthz(String newPass, String sendNum, String type) throws Exception;



	/**
	 * 
	 * @Description:根据用户标识修改用户信息
	 * @param updateUserParam
	 * @param id
	 * @return
	 * @throws Exception
	 */
	RpcResponse<?> updateUser(String updateUserParam, String id) throws Exception;



	/**
	 * 
	 * @Description:修改用户电话
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	RpcResponse<?> updatemobile(String userInfo) throws Exception;



	/**
	 * 
	 * @Description:修改用户邮箱
	 * @param userInfo
	 * @return
	 */
	RpcResponse<?> updateemail(String userInfo) throws Exception;



	/**
	 * 
	 * @Description:通过用户Id直接激活用户，平台使用
	 * @param userId
	 * @return
	 */
	RpcResponse<Boolean> activateUserByUserId(String userId, JSONObject state);



	/**
	 * 
	 * @Description:通过用户id刷新登录时间
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	RpcResponse<Boolean> refreshLoginTime(String userId) throws Exception;

}
