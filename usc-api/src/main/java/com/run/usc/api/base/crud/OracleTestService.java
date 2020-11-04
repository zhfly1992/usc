/*
 * File name: OracleTestService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年6月4日 ...
 * ... ...
 *
 ***************************************************/

package com.run.usc.api.base.crud;

import java.util.Map;

import com.run.entity.common.RpcResponse;

/**
 * @Description:
 * @author: zwz
 * @version: 1.0, 2018年6月4日
 */

public interface OracleTestService {

	@SuppressWarnings("rawtypes")
	RpcResponse<String> getAllUserInfo(Map maps);



	@SuppressWarnings("rawtypes")
	RpcResponse<String> getAccessInfo(Map maps);



	@SuppressWarnings("rawtypes")
	RpcResponse<String> getOrgInfo(Map maps);



	@SuppressWarnings("rawtypes")
	RpcResponse<String> getRoleAsuser(Map maps);
}
