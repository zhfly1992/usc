/*
 * File name: OracleTestController.java
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

package com.run.usc.service.query.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.usc.crud.service.OracleTestServiceRest;

/**
 * @Description:
 * @author: zwz
 * @version: 1.0, 2018年6月4日
 */
@RequestMapping("/test/oracle")
@RestController
@CrossOrigin(origins = "*")
public class OracleTestController {

	@Autowired
	private OracleTestServiceRest oracleTestServiceRest;



	@SuppressWarnings("rawtypes")
	@PostMapping("/get2.0")
	public String test(@RequestBody Map maps) {

		if (maps == null || StringUtils.isBlank(maps.get("accessSecret").toString())) {
			return "参数不存在！";
		}

		String userInfo = oracleTestServiceRest.getTo3(maps);
		return userInfo;
	}

}
