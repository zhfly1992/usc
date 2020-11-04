/*
 * File name: ExceptionChecked.java
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

package com.run.usc.api.base.util;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.UscConstants;

/**
 * @Description: 参数验证工具类
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

public class ExceptionChecked {
	@SuppressWarnings("rawtypes")
	public static RpcResponse checkRequestParamHasKey(String requestJson, String checkKey) {
		try {
			// 判断是否为空
			if (ParamChecker.isBlank(requestJson)) {
				return RpcResponseBuilder.buildErrorRpcResp("传入参数为空！");
			}
			// 参数非法是否是json格式
			if (ParamChecker.isNotMatchJson(requestJson)) {
				return RpcResponseBuilder.buildErrorRpcResp("传入参数json参数非法！");
			}
			JSONObject json = JSON.parseObject(requestJson);
			if (!json.containsKey(checkKey)) {
				return RpcResponseBuilder.buildErrorRpcResp("没有业务数据！");
			}
		} catch (Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

		return null;
	}



	@SuppressWarnings("rawtypes")
	public static RpcResponse checkRequestParam(String requestJson) {
		try {
			// 判断是否为空
			if (ParamChecker.isBlank(requestJson)) {
				return RpcResponseBuilder.buildErrorRpcResp("传入参数为空！");
			}
			// 参数非法是否是json格式
			if (ParamChecker.isNotMatchJson(requestJson)) {
				return RpcResponseBuilder.buildErrorRpcResp("传入参数json参数非法！");
			}
		} catch (Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		return null;
	}



	/**
	 * 
	 * @Description:判断必填参数是否存在
	 * @param json
	 *            json字符串
	 * @param key
	 *            包含的key
	 * @return
	 */
	public static <T> RpcResponse<T> checkRequestKey(Logger logger, String methodName, JSONObject json,
			String... keys) {
		for (String key : keys) {
			if (isEmpty(json.getString(key))) {
				logger.error(String.format("[%s()->error:%s:%s]", methodName, UscConstants.NO_BUSINESS, key));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
		}
		return null;
	}



	/**
	 * 
	 * @Description:判断必填参数是否存在
	 * @param json
	 *            json字符串
	 * @param key
	 *            包含的key
	 * @return
	 */
	public static <T> RpcResponse<T> checkRequestKey(Logger logger, String methodName, String... keys) {
		for (String key : keys) {
			if (isEmpty(key)) {
				logger.error(String.format("[%s()->error:%s:%s]", methodName, UscConstants.NO_BUSINESS, key));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
		}
		return null;
	}



	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str));
	}

}
