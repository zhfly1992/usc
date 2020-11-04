/*
 * File name: UscUrlConstants.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月22日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.api.constants;

/**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2017年6月22日
 */

public class UscUrlConstants {

	/** ------------------------用户中心访问url------------------------------- */
	/** 用户路径 **/
	public static final String	API_USER_BASE_PATH				= "/user";
	/** 用户字段 **/
	public static final String	API_USER						= "/user";
	// 用户管理
	/** 门户用户注册 **/
	public static final String	API_CODE_REGISTER				= "/register";

	/** 接入方用户注册 **/
	public static final String	API_ACCESS_REGISTER				= "/accessRegister";

	/** 用户认证 **/
	public static final String	API_CODE_USERAUTH				= "/oauth";

	/** 注册信息 */
	public static final String	USER_REGISTER_INFO				= "registerInfo";

	/** 验证用户名 */
	public static final String	CHEACK_LOGIN_NAME				= "/checkloginname";

	/** 用户退出 **/
	public static final String	API_CODE_LOGINOUT				= "/logout";

	/** 修改密码 **/
	public static final String	API_UPDATE_PASS					= "/updatePass";

	/** 修改密码 **/
	public static final String	API_CODE_UPDATEPSD				= "/updatepassword";

	/** 通过验证码修改密码 */
	public static final String	API_CODE_RESETPSD_AUTH			= "/updatepasswordbyauth";

	/** 通过用户标识查询当前用户所在的接入方所有信息 **/
	public static final String	QUERY_ACCESS_USER_PAGE			= "/getAccPageUser";

	/** 通过token查询用户信息 **/
	public static final String	QUERY_USER_TOKEN				= "/queryUserByToken";

	/** 通过用户id查询用户信息 **/
	public static final String	QUERY_USER_BYID					= "/queryUserById";

	/** 更新用户 **/
	public static final String	API_CODE_UPDATEUSER				= "/updateuser";

	/** 修改手机号 */
	public static final String	UPDATE_MOBILE					= "/updatemobile";

	/** 修改邮箱 */
	public static final String	UPDATE_EMIL						= "/updateemail";

	/** 修改用户信息 */
	public static final String	UPDATE_ACCESS_USER				= "/updateuser";

	/** 通过用户名称/邮箱/电话模糊查询用户信息 **/
	public static final String	QUERY_USER_BYKEY				= "/queryUserByKey";

	/** 激活用户 **/
	public static final String	USER_ACTIVATE					= "/activate";

	/** 检查用户手机号或者邮箱是否存在 **/
	public static final String	CHECK_USER_EXIST_EMIMOB			= "/checkUserExitByEmiMob";

	/** 发送手机或者邮箱验证 **/
//	public static final String	SEND_EMIMOB						= "sendEmiMob";

	/***
	 * ---------------------------租户url地址----------------------------------------
	 */
	/** 租户根接口 */
	public static final String	TENEMENT						= "/tenement";

	/** 保存租户信息 */
	public static final String	TENEMENT_SAVE					= "/tenementSave";

	/** 更新租户信息 */
	public static final String	TENEMENT_UPDATE					= "/tenementUpdate";

	/** 删除租户信息 */
	public static final String	TENEMENT_DELETE					= "/tenementDelete";

	/** 分页查询租户 */
	public static final String	GET_TENEMENT_BY_PAGE			= "/getTenementByPage";

	/** 获取所有租户 */
	public static final String	GET_TENEMENT_ALL				= "/getTenementAll";

	/** 检查租户是否重名 */
	public static final String	TENEMENT_CHECK_NAME				= "/checkTenementName";

	/** 通过租户名获取租户信息 */
	public static final String	TENEMENT_BY_TENEMENT_NAME		= "/tenementByTenementName";

	/** 更新租户信息字段 */
	public static final String	TENEMENT_UPDATE_FILED			= "/tenementUpdateFiled";

	/*** --------------------------接入方url地址---------------------------------- */
	/** 接入方根接口 */
	public static final String	TENEMENT_ACCESS_INFO			= "/tenementAccessInfo";

	/** 接入方数据保存 */
	public static final String	TENEMENT_ACCESS_SAVE			= "/tenementAccessSave";

	/** 接入方数据更新 */
	public static final String	TENEMENT_ACCESS_UPDATE			= "/tenementAccessUpdate";

	/** 接入方数据删除 */
	public static final String	TENEMENT_ACCESS_DELETE			= "/tenementAccessDelete";

	/** 接入方根域 */
	public static final String	TENEMENT_AND_ACCESS_RS			= "/tenementAndAccessRs";

	/** 接入方信息分页查询 */
	public static final String	TENEMENT_AND_ACCESS_RS_SEARCH	= "/tenementAndAccessRsSearch";

	/** 通过租户id获取所有接入方信息 */
	public static final String	TENEMENT_ACCESS_BY_TENEMTN_ID	= "/tenementAccessByTenementId";

	/** 根据接入方id查询所有的资源id */
	public static final String	TENEMENT_RESOURCE_GET_ALL		= "/getAllAccSouByAccId";

	/** 查询资源分类 */
	public static final String	RESOURCE_TYPE_GET_ALL			= "/getAllSourceType";

	/** 查询接入方菜单资源 */
	public static final String	GET_MENU_BY_ACCESSID			= "/getListMenuByAccessId";

	/** 查询接入方按钮资源 */
	public static final String	GET_BUTTON_BY_ACCESSID			= "/getListButtonByAccessId";

	/** 根据资源id查询资源所绑定的url地址 */
	public static final String	GET_URL_BY_SOURCEID				= "/getListUrlBySourceId";

	/** 校验当前接入方下面是否重名 */
	public static final String	CHECK_ACCESS_NAME				= "/checkAccessName";

	/** 检测资源是否重名 */
	public static final String	TENEMENT_RESOURCE_CHECK_NAME	= "/checkTeneResoName";

	/** 检测组织是否重名 */
	public static final String	CHECK_ORG_NAME					= "/checkOrgName";

	/** 检测组织下面是否存在子类 */
	public static final String	CHECK_ORG_HAS_CHILD				= "/checkOrgHasChild";

	/***
	 * ----------------------------接入方资源地址----------------------------------------
	 */
	/** 接入方资源根域 */
	public static final String	TENEMENT_RESOURCE				= "/tenmentResource";
	/** 添加接入方资源 */
	public static final String	TENEMENT_RESOURCE_ADD			= "/addTenementResoInfo";
	/** 根据接入方秘钥添加接入方资源 */
	public static final String	RESOURCE_ADD_BY_SECRET			= "/addResourceBySecret";
	/** 修改接入方资源 */
	public static final String	TENEMENT_RESOURCE_UPDATE		= "/updateTenementResoInfo";
	/** 根据接入方秘钥修改接入方资源 */
	public static final String	RESOURCE_UPDATE_BYSECRET		= "/updateSourceBySecret";
	/** 删除接入方资源 */
	public static final String	TENEMENT_RESOURCE_DELETE		= "/delTenementResoInfo";
	/** 修改接入方资源状态 */
	public static final String	SWATE_SOURCE_STATE				= "/swateSourceState";
	/** 接入方资源授权 */
	public static final String	SOURCE_AUTHORIZ					= "/sourceAuthoriz";
	/** 删除接入方资源授权 */
	public static final String	DEL_SOURCE_AUTHORIZ				= "/delSourceAuthoriz";
	/** 分页查询接入方资源信息 */
	public static final String	TENEMENT_RESOURCE_GET_PAGE		= "/getTenementResourceByPage";
	/** 分页查询接入方资源信息 */
	public static final String	ACC_RESOURCE_GET_PAGE_BYTYPE	= "/getAccSourcePageInfoByType";
	/** 分页查询接入方资源信息 */
	public static final String	GET_PARENT_ACCSOURCE_BYPAGE		= "/getParentAccSourcePageByType";
	/** 查询接入方资源信息 */
	public static final String	ACC_RESOURCE_GET_BYTYPE			= "/getAccSourceInfoByType";
	/** 查询接入方资源已拥有或者未拥有的url */
	public static final String	ACC_RESOURCE_URL_BYPAGE			= "/getAccSourceUrlByPage";
	/** 根据資源id查詢資源信息 */
	public static final String	ACC_RESOURCE_BY_ID				= "/getAccSourceById";

	/***
	 * ------------------------------接入方用户地址------------------------------------------------
	 */
	/** 接入方用户根 */
	public static final String	ACCESS_AND_USER_RS				= "/accessUserRs";

	/** 接入方用户接口保存 */
	public static final String	ACCESS_AND_USER_RS_SAVE			= "/accessUserRsSave";
	/** 接入方用户接口修改 */
	public static final String	ACCESS_AND_USER_RS_UPDATE		= "/accessUserRsUpdate";
	/** 接入方用户接口修改 */
	public static final String	ACCESS_AND_USER_RS_DELETE		= "/accessUserRsDelete";

	/** 保存用户关系(关联组织，关联角色)RS */
	public static final String	SAVE_USER_RS					= "saveUserRs";

	/** 修改用户关系(关联组织，关联角色)RS */
	public static final String	UPDATE_USER_RS					= "updateUserRs";

	/** 启用或者禁用用户 */
	public static final String	SWATE_USER_STATE				= "swateUserState";

	/** 用户接入方接口 */
	public static final String	ACCESS_AND_USER_RS_QUERY_PAGE	= "/accessUserRsQueryPage";

	/** 根据角色id分页查询可添加的用户信息 */
	public static final String	GET_UN_USERINFO_BY_ROLE_PAGE	= "/getUnUserInfoPageByRoleId";

	/** 根据角色id分页查询该角色下面所有的用户信息 */
	public static final String	GET_USERINFO_BY_ROLE_PAGE		= "/getUserInfoPageByRoleId";

	/** 分页查询其他接入方所在的用户信息 */
	public static final String	ACCESS_AND_USER_PAGE_QUERY_INFO	= "/getUserRsPageByExitCode";

	/** 查询该接入方下面所有用户信息 */
	public static final String	ACCESS_AND_USER_PAGE_RS_INFO	= "/getUserRsPageByCode";

	/** 检测用户是否重名 */
	public static final String	TENEMENT_ACCUSER_CHECK_NAME		= "/checkAccUserName";

	/** 禁用启用接入方 */
	public static final String	TENEMENT_ACCESS_STATE_EDIT		= "/tenementAccessStateEdit";

	/** 保存用户与接入方关系 */
	public static final String	ACCESS_SAVE_USER_RS				= "/saveAccessUserRs";

	/** 删除接入方用户关系 */
	public static final String	ACCESS_DEL_RS					= "/delAccessUserRs";

	/** 根据用户id查询接入方信息 */
	public static final String	GET_ACCINFO_BY_USERID			= "/getAccinfoByUserId";

	/** 刷新登录时间 */
	public static final String	REFRESH_LOGIN_TIME				= "/refreshLoginTime";

	/** 通过人员ids查询这些人员的厂家id */
	public static final String	FIND_FACTORY_IDS				= "/findFactoryIds";

	/** 通过关键字模糊查询用户名返回id */
	public static final String	FIND_USER_KEYWORD				= "/findUserKeyword";

	/** 查询用户的名称->ids */
	public static final String	FIND_USER_NAME					= "/findUserName";

	/** 查询组织信息名称 */
	public static final String	FIND_ORG_NAME					= "/findOrgName";

	/** 查询该组织id下所有的组织id */
	public static final String	FIND_ORG_ID						= "/findOrgId";

	/** 判断该组织数组下 是否存在子组织 */
	public static final String	CHECK_SOURCE_ID					= "/checkSourceIds";

}
