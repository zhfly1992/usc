/*
 * File name: UscConstants.java
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
 * @Description: 用户中心静态类
 * @author: zhabing
 * @version: 1.0, 2017年6月21日
 */

public class UscConstants {
	/** 统一用户访问信息 */
	public static final String	USC_INFO						= "uscInfo";
	public static final String	CHECK_INFO						= "checkInfo";
	public static final String	PASSWORD						= "password";
	public static final String	USERTYPE						= "userType";
	public static final String	ACCESSCODE						= "accessCode";
	public static final String	ACCESSID						= "accessId";
	public static final String	ACCESSTYPE						= "accessType";
	public static final String	BUTTONMENU						= "buttonMenu";
	public static final String	LOGIN_ACCOUNT					= "loginAccount";
	public static final String	EMAIL							= "email";
	public static final String	USERNAME						= "userName";
	public static final String	EMAIL_ACTIVETION_SIGN			= "emailActivationSign";
	public static final String	MOBILE							= "mobile";
	public static final String	ID_								= "_id";
	public static final String	ID								= "id";
	public static final String	USER_CODE						= "userCode";
	public static final String	REGISTER_TIME					= "registerTime";
	public static final String	IS_DELETE						= "isDelete";
	public static final String	STATE							= "state";
	public static final String	USER_STETE						= "userState";
	public static final String	SOURCE_ID						= "sourceId";
	public static final String	SOURCE_NAME						= "sourceName";
	public static final String	USER_ID							= "userId";
	public static final String	ROLE_ID							= "roleId";
	public static final String	ROLE_INFO						= "roleInfo";
	public static final String	NAME							= "name";
	public static final String	SOURCE_INFO						= "sourceInfo";
	public static final String	SOURCE_PARENT_INFO				= "sourceParentInfo";
	public static final String	UNDEFINED						= "undefined";
	public static final String	LOGIN_TIME						= "loginTime";
	public static final String	FACTORY							= "factory";
	public static final String	USC_IDS							= "uscIds";
	public static final String	USC_KEY_WORD					= "keyWord";
	public static final String	USC_USER_IDS					= "userIds";
	public static final String	USC_ORG_IDS						= "orgIds";
	public static final String	USC_TYPE_BIND					= "bind";
	/** 组织id */
	public static final String	ORGANIZED_ID					= "organizedId";
	public static final String	ORG_ID							= "organizeId";
	/** 组织信息 */
	public static final String	ORGANIZED_INFO					= "orgInfo";
	/** 父类id */
	public static final String	PARENT_ID						= "parentId";
	/** 查询关键字 */
	public static final String	SELECT_KEY						= "nameKey";
	/** 正常 */
	public static final String	STATE_NORMAL_ONE				= "valid";
	/** 已删除 */
	public static final String	STATE_STOP_ZERO					= "invalid";
	public static final String	API_PAGE_CREATE_TIME			= "createTime";
	/** 分页页数 */
	public static final String	PAGENUMBER						= "pageNo";
	/** 分页大小 */
	public static final String	PAGESIZE						= "pageSize";
	/** 分页大小 */
	public static final String	PAGESIZEDEFAULT					= "10";

	/** token */
	public static final String	TOKEN							= "token";

	/** token */
	public static final String	APP_TOKEN						= "appToken";

	/** 接入方秘钥 */
	public static final String	ACCESS_SECRET					= "accessSecret";

	/** 验证信息 */
	public static final String	AUTHZ_INFO						= "authzInfo";

	/** 注册信息 */
	public static final String	REGISTER_INFO					= "registerInfo";

	/** 分页信息 */
	public static final String	PAGE_INFO						= "pageInfo";

	/** 门户个人注册 */
	public static final String	USERTYPE_INDIVIDUAL				= "individual";

	/** 门户企业注册 */
	public static final String	USERTYPE_COMPANY				= "company";

	/** 资源分类名称 */
	public static final String	SOURCE_TYPE						= "sourceType";

	/** 菜单资源类型 */
	public static final String	SOURCE_TYPE_MENU				= "sourceTypeMenu";

	/** 按钮资源类型 */
	public static final String	SOURCE_TYPE_BUTTON				= "sourceTypeButton";

	/** 激活状态 */
	public static final String	ACTIVATE_STATE					= "activateState";

	/** 激活秘钥 */
	public static final String	REGISTER_SECRET					= "registerSecret";

	/** 用户邮箱或者电话 */
	public static final String	EMAIL_MOBILE					= "emailMob";

	/** 验证类型 email or phone */
	public static final String	TYPE							= "type";

	/** 验证码 email or phone */
	public static final String	SEND_NUM						= "sendNum";

	/** 手机验证码 */
	public static final String	MOBILE_ACT_SIGN					= "mobileActivationSign";

	/** 菜单ids */
	public static final String	MENU_IDS						= "menuIds";

	/** 接口地址 */
	public static final String	URL_ADDRESS						= "urlAddress";

	/** 接口ids */
	public static final String	URLIDS							= "urlIds";
	public static final String	URLID							= "urlId";

	/** 应用类型 */
	public static final String	APPLICATIONTYPE					= "applicationType";

	public static final String	APP								= "APP";

	public static final String	PC								= "PC";

	/** 人员类型 */
	public static final String	PEOPLE_TYPE						= "peopleType";
	/** 是否具有接受短信的能力 */
	public static final String	RECEIVESMS						= "是否接受短信";
	/** 接受短信参数 */
	public static final String	RECEIVESMS_KEY					= "receiveSms";
	/**日志记录key*/
	public static final String	LOGKEY							= "usc";
	/** 密码到期时间*/
	public static final String	ExpiredDate						= "expiredDate";

	// 选填,s000->初始,s001->正常,s002->强制修改密码,s003->锁定,s004->删除,接入方根据自身需求传入
	public static final String	USER_STETE_DEFUALT_S000			= "s000";
	public static final String	USER_STETE_NORMAL_S001			= "s001";
	public static final String	USER_STETE_MODIFY_PASSWORD_S002	= "s002";
	public static final String	USER_STETE_LOCK_S003			= "s003";
	public static final String	USER_STETE_DELETE_S004			= "s004";

	// 正则左匹配
	public static final String	REGX_LEFT						= "^.*";
	public static final String	REGX_RIGHT						= ".*$";

	public static final String	USER_ADD_SUCCESS				= "用户信息添加成功!";
	public static final String	USER_MESS_GET_SUCCESS			= "查询用户信息成功！";

	public static final String	USER_SOURCE_SUCCESS				= "根据接入方资源id查询用户列表成功!";
	/** 提示信息 */
	public static final String	NO_BUSINESS						= "没有业务数据！";
	public static final String	CHECK_BUSINESS					= "参数不合法！";
	public static final String	TOKEN_GET_USER_FAIL				= "token查询用户信息失败！";
	public static final String	TOKEN_CREATE_FAIL				= "token生成失败！";
	public static final String	LOGIN_ACOOUNT_INVALID			= "用户注册失败:登录名非法，登录名由英文大小写字母与数字组成,长度大于或等于5位";
	public static final String	PHONE_INVALID					= "用户注册失败:手机号格式错误！";
	public static final String	EMIL_INVALID					= "用户注册失败:emil错误！";
	public static final String	USER_USER_NAME_EXIT				= "用户名已经存在！";
	public static final String	PHONE_EXIT						= "手机已经存在！";
	public static final String	EMIL_EXIT						= "邮箱已经存在！";
	public static final String	EMPTYOBJECT						= "对象为空！";

	public static final String	SAVE_SUCC						= "保存成功！";
	public static final String	ADD_SUCC						= "添加成功！";
	public static final String	SAVE_FAIL						= "保存失败！";

	public static final String	GET_SUCC						= "查询成功！";
	public static final String	GET_FAIL						= "查询失败！";

	public static final String	UPDATE_SUCC						= "修改成功！";
	public static final String	UPDATE_FAIL						= "数据不存在，修改失败！";

	public static final String	ACTIVATE_SUCC					= "激活成功！";
	public static final String	ACTIVATE_FAIL					= "激活失败！";
	public static final String	ACTIVATE_ALREADY				= "用户已激活，不能重复激活！";

	public static final String	DEL_SUCC						= "删除成功！";
	public static final String	DEL_FAIL						= "数据不存在，删除失败！";

	public static final String	NAME_NOT_EXIT					= "名称不存在";
	public static final String	NAME_EXIT						= "名称已存在";

	public static final String	USER_LOGIN_SUCC					= "登录成功！";
	public static final String	USER_LOGIN_FAIL					= "用户不存在，登录失败！";

	public static final String	ACCESS_LOGIN_FAIL				= "接入方不存在或者已停用";
	public static final String	LOGIN_PASS_FAIL					= "密码错误，请输入正确的密码！";
	public static final String	PASSWORD_TIMEOUT				= "用户密码已过期,请修改密码!";
	public static final String	LOGIN_AUTH_FAIL					= "用户权限不可用，请联系管理员核实！";
	public static final String  PASSWORD_FORMAT_ERROR           = "密码格式不正确，请确认密码";

	public static final String	GATWAY_REGISTER_SUCC			= "用户注册成功！";
	public static final String	GATWAY_REGISTER_FAIL			= "用户注册失败！";

	public static final String	SEND_SUCC						= "发送成功！";
	public static final String	SEND_FAIL						= "发送失败！";

	public static final String	EMA_MOB_LOG_EXIST				= "登录名，密码，邮箱不能全部为空！";

	public static final String	SIGN_CHECK_ERRO					= "验证码输入错误！";

	public static final String	EMAIL_SEND_SUCC					= "邮件发送成功！";
	public static final String	ID_IS_NULL						= "用户id不能为空！";
	public static final String	SWATE_FAIL						= "修改失败，该%s存在关联岗位信息";

	public static final String	RECURSION_ID					= "递归查询！";
	// 短信发送密匙key,用于生成md5
	public static final String	SENDMESSAGE_KEY					= "31538f53c6cc6d8dab6fa83a79bce059";
	
	// 密码格式正则表达式
	public static final String  PASSWOR_FORMAT                 ="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,30}";
	
	// 非对称加密私钥key
	public static final String  PRIVATE_KEY                    = "privateKey";
}
