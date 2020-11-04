/*
* File name: TestQueryService.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			zhabing		2018年6月14日
* ...			...			...
*
***************************************************/

package com.run.usc.base.query;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;

/**
* @Description:	
* @author: zhabing
* @version: 1.0, 2018年6月14日
*/

public interface TestQueryService {
	/**
	 * 
	* @Description:根据id查询本地事务
	* @param id
	* @return
	 */
	RpcResponse<JSONObject> getTransactionById(String id);
}
