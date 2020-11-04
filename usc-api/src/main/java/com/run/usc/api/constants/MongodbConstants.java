/*
 * File name: MongodbConstants.java
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

package com.run.usc.api.constants;

/**
 * @Description:mongodb 实体类
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

public class MongodbConstants {
	/* 用户信息集合 */
	public static final String	MONGODB_USERINFO_COLL			= "UserInfo";

	/* 用户与接入方资源集合 */
	public static final String	MONGODB_USERINFO_SOURCE_COLL	= "SourceUserRs";

	/* 租户信息集合 */
	public static final String	MONGODB_TENEMENT_INFO_COLL		= "TenementInfo";

	/* 租户接入方信息集合 */
	public static final String	MONGODB_ACCESS_INFO_COLL		= "AccessInfo";

	/* 租户接入方资源集合 */
	public static final String	MONGODB_TEN_RES_INFO_COLL		= "ResourceInfo";

	/* 接入方用户信息集合 */
	public static final String	MONGODB_ACCUSERINFO_RS_COLL		= "AccessUserRs";

	/* 用户中心字典表 */
	public static final String	MONGODB_DICTIONARY				= "Dictionary";

	/* url与接入方资源集合 */
	public static final String	MONGODB_URL_SOURCE_COLL			= "SourceUrlRs";

}
