package com.run.usc.api.base.util;


import java.io.IOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.UscConstants;


/**
 * @Description: 发送短信消息
 * @author: 张贺
 * @version: 2018年9月14日
 */

public class SendMessage {
 private static final Logger logger = Logger.getLogger(SendMessage.class);
    
 /**
  * 
  * @param phonenumber 发送手机号
  * @param content 发送内容
  * @param url 授权地址
  * @return
  */
	public static RpcResponse<String> send(String phonenumber, String content, String url){
		logger.info(String.format("[sendVerificationCode()->request params-mobile:%s,code:%s,url:%s]", phonenumber,content,url));
		String extras = "";
		String key = UscConstants.SENDMESSAGE_KEY;
		String str = content + extras + phonenumber + key;//拼接顺序参考文档中心
		String md5 = DigestUtils.md5Hex(str);//md5加密
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		method.getParams().setContentCharset("utf-8"); //指定HttpClient请求的编码方式未"utf-8",解决POST中的参数中有中文的问题
		method.setRequestHeader("ContentType", "application/X-WWW-FORM-URLENCODED");
		NameValuePair[] data = { new NameValuePair("target", phonenumber), new NameValuePair("content", content),
				new NameValuePair("extras", extras), new NameValuePair("md5", md5) };
		method.setRequestBody(data);
		try{
			client.executeMethod(method);
			String submitResult = method.getResponseBodyAsString();
			if (submitResult == null) {
				logger.error("sendMessage()->fail:无法收到返回信息");
				return RpcResponseBuilder.buildErrorRpcResp("网关无响应");
			}
			JSONObject jsonObject = JSONObject.parseObject(submitResult);
			if (jsonObject.getInteger("code") == 1) {
				logger.error("sendMessage()->fail:key验证不通过");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			if (jsonObject.getInteger("code") == 2) {
				logger.error("sendMessage()->fail:参数不能为空");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			if (jsonObject.getInteger("code") == 3) {
				logger.error("sendMessage()->fail:接口错误或者提交失败");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			if(jsonObject.getInteger("code") == 4){
				logger.error("sendMessage()->fail:接口受限");
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("msg"));
			}
			logger.info("sendMessage->:发送短信成功");
			return RpcResponseBuilder.buildSuccessRpcResp("发送成功", content);
			}
		catch(HttpException e){
			logger.error(String.format("sendMessage()->error:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		catch (IOException  e) {
			logger.error(String.format("sendMessage()->error:%s", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
