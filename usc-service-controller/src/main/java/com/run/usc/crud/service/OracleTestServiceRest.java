/*
 * File name: OracleTestServiceRest.java
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

package com.run.usc.crud.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.run.entity.common.RpcResponse;
import com.run.usc.api.base.crud.OracleTestService;

/**
 * @Description:
 * @author: zwz
 * @version: 1.0, 2018年6月4日
 */
@Service
public class OracleTestServiceRest {

	@Autowired
	private OracleTestService oracleTestService;



	/**
	 * 
	 * @Description:2.0数据到3.0数据
	 * @return
	 */
	@SuppressWarnings({ "rawtypes"})
	public String getTo3(Map maps) {
		// 2.0 人员
		RpcResponse<String> allUserInfo = oracleTestService.getAllUserInfo(maps);
		System.out.println(allUserInfo.getMessage());
		// 2.0 接入方以及租户
		RpcResponse<String> accessInfo = oracleTestService.getAccessInfo(maps);
		System.out.println(accessInfo.getMessage());
		// 2.0 组织
		RpcResponse<String> orgInfo = oracleTestService.getOrgInfo(maps);
		System.out.println(orgInfo.getMessage());
		// 角色必须绑定在组织下，人与角色有关系，与组织有关系，组织与角色无关系。2.0权限对应菜单无法直接导入
		// 2.0创建一个超级管理员
		RpcResponse<String> roleAsuser = oracleTestService.getRoleAsuser(maps);
		return roleAsuser.getMessage() + ":" + roleAsuser.getSuccessValue();
	}

}
