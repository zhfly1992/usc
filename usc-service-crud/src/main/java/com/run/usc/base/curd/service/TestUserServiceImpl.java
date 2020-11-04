/*
 * File name: TestUserServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2018年5月30日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.base.curd.service;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.reliableNews.server.NewsService;
import com.run.usc.api.base.crud.TestUserService;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.util.MongoTemplateUtil;

/**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2018年5月30日
 */

public class TestUserServiceImpl implements TestUserService {

	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;
	@Autowired
	private NewsService			newService;



	/**
	 * @see com.run.usc.api.base.crud.TestUserService#testSave()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void testSave() {
		// 发送待确认消息
		JSONObject json = new JSONObject();
		String id = UUID.randomUUID().toString();
		json.put("id", id);
		json.put("newsTitle", "测试保存数据事务一直性");
		json.put("newsState", "0");
		RpcResponse res = newService.sendNewsState(json);

		// 确认可靠消息已经保存状态消息，然后执行本地业务代码
		if (res.isSuccess()) {
			JSONObject testS = new JSONObject();
			String userId = UUID.randomUUID().toString();
			testS.put("_id", userId);
			testS.put("message", "这是用户中心一条废弃测试数据!!");
			RpcResponse<JSONObject> result = tenementTemplateUtil.insertId(logger, "testSave", testS, "testSave",
					userId);
			// 业务保存成功
			if (result.isSuccess()) {

				// 修改可靠消息的状态为已发送
				JSONObject paramJson = new JSONObject();
				paramJson.put("userId", userId);
				paramJson.put("queueName", 1111122222);

				// 存储本地业务库
				tenementTemplateUtil.insertId(logger, "testSave", paramJson, "testTransaction", id);

				newService.updateNewsState(id, "1", paramJson);
			}
		}
	}



	/**
	 * @see com.run.usc.api.base.crud.TestUserService#saveTransactionTest()
	 */
	@Transactional
	@Override
	public void saveTransactionTest() {

		try {
			String id = UUID.randomUUID().toString();
			JSONObject tran1 = new JSONObject();
			tran1.put("_id", id);
			tran1.put("key", "保存1");

			JSONObject tran2 = new JSONObject();
			tran2.put("_id", id);
			tran2.put("key", "保存1");
			tenementTemplateUtil.insertId(logger, "testSave", tran1, "testTransaction1", id);
			tenementTemplateUtil.insertId(logger, "testSave", tran2, "testTransaction2", id);
		} catch (Exception e) {
			System.out.println("开始事务混滚！！！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
		}
	}

}
