/*
 * File name: UserOracleRepostory.java
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

package com.run.usc.test.repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: zwz
 * @version: 1.0, 2018年6月4日
 */

public interface OracleRepository {

	@SuppressWarnings("rawtypes")
	List getAllUserInfo(Map maps) throws Exception;



	@SuppressWarnings("rawtypes")
	List getAccessSecret(Map maps) throws Exception;



	@SuppressWarnings("rawtypes")
	List getOrgInfo(Map maps) throws Exception;



	@SuppressWarnings("rawtypes")
	List getJurisdiction(Map maps) throws Exception;
}
