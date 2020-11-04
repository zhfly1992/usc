/*
 * File name: TestQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2018年6月14日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.TestQueryService;
import com.run.usc.service.util.MongoTemplateUtil;

/**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2018年6月14日
 */

public class TestQueryServiceImpl implements TestQueryService {

	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;



	/**
	 * @see com.run.usc.base.query.TestQueryService#getTransactionById(java.lang.String)
	 */
	@Override
	public RpcResponse<JSONObject> getTransactionById(String id) {
		// 查询所有租户集合
		try {
			if (StringUtils.isEmpty(id)) {
				logger.error("[getTenementByPage()->error:id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("id不能为空！！！");
			}
			return tenementTemplateUtil.getModelById(logger, "getTransactionById", "testTransaction", id);
		} catch (Exception e) {
			logger.error("[getTenementByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
