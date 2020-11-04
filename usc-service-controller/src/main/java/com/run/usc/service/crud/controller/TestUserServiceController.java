/*
* File name: TestUserServiceController.java								
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
* 1.0			zhabing		2018年5月30日
* ...			...			...
*
***************************************************/

package com.run.usc.service.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.run.usc.api.base.crud.TestUserService;

/**
* @Description:	
* @author: zhabing
* @version: 1.0, 2018年5月30日
*/
@CrossOrigin(maxAge = 3600,origins = "*")
@RestController
@RequestMapping(value = "/user")
public class TestUserServiceController {
	
	@Autowired
	private TestUserService testSave;
	
	@RequestMapping(value = "/saveTest", method = RequestMethod.POST)
	public void save(){
		testSave.testSave();
	}
	
	
	/**
	 * 测试事务回滚
	* @Description:
	 */
	@RequestMapping(value = "/saveTransactionTest", method = RequestMethod.POST)
	public void saveTransactionTest(){
		testSave.saveTransactionTest();
	}
	
}
