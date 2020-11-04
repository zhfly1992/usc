/*
 * File name: TenementConstant.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月26日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.api.constants;

/**
 * @Description: 租户信息常量类
 * @author: zhabing
 * @version: 1.0, 2017年6月26日
 */

public class TenementConstant {
	/** 租户编号 */
	public static final String	TENEMENT_NUM						= "tenementNum";

	/** 租户名 */
	public static final String	TENEMENT_NAME						= "tenementName";

	/** 租户id */
	public static final String	TEN_ID								= "tenementId";

	/** 1:租户名全匹配 */
	public static final String	TENEMENT_MATCH						= "match";

	/** 租户地址 */
	public static final String	TENEMENT_ADDRESS					= "tenementAddress";

	/** 租户联系电话 */
	public static final String	TENEMENT_PHONE						= "tenementPhone";

	/** 租户id */
	public static final String	TENEMENT_ID							= "_id";
	/** 租户code */
	public static final String	TENEMENT_CODE						= "tenementCode";
	/** 租户状态 */
	public static final String	TENEMENT_STATE						= "state";
	/** 租户删除状态 0删除 1正常 */
	public static final String	TENEMENT_DELETE_STATE				= "isDelete";
	/** 租户创建时间 */
	public static final String	TENEMENT_CREATE_DATE				= "createTime";
	/** 租户更新时间 */
	public static final String	TENEMENT_UPDATE_DATE				= "updateTime";

	/** 租户状态正常1 标识启用或者正常 */
	public static final String	STATE_NORMAL_ONE					= "valid";
	/** 租户状态不正常0 表示停用或者删除 */
	public static final String	STATE_STOP_ZERO						= "invalid";
	/** 租户查询信息 */
	public static final String	TENEMENTINFO						= "tenementInfo";

	/** 删除ids集合 */
	public static final String	IDS									= "ids";

	/** 接入方名称 */
	public static final String	TENEMENT_ACCESS_NAME				= "accessName";

	/** 接入方id */
	public static final String	TENEMENT_ACCESS_ID					= "accessId";

	/** 主键id */
	public static final String	ACCESS_ID							= "_id";

	/** 接入方根域 */
	public static final String	TENEMENT_ACCESS_ROOT_DOMAIN			= "accessRootDomain";

	/** 接入方code */
	public static final String	TENEMENT_ACCESS_CODE				= "accessCode";

	/** 接入方所属租户id */
	public static final String	TENEMENT_ACCESS_TENEMENT_ID			= "accessTenementId";

	/** 接入方所属租户name */
	public static final String	TENEMENT_ACCESS_TENEMENT_NAME		= "accessTenementName";

	/** 接入方资源备注 */
	public static final String	ACCESS_SOURCE_DESC					= "sourceDecs";

	/** 资源名称 */
	public static final String	ACCESS_SOURCE_NAME					= "sourceName";

	/** 资源名称 */
	public static final String	USER_ID								= "userId";
	/** 资源名 */
	public static final String	RES_NAME							= "name";

	/** 接入方信息 */
	public static final String	ACC_INFO							= "accInfo";

	/** 接入方信息 */
	public static final String	TEN_INFO							= "tenInfo";

	/** 自身id */
	public static final String	SELF_ID								= "selfId";
	/** 资源类型 */
	public static final String	SOURCE_TYPE							= "sourceType";
	
	/** 接入方类型*/
	public static final String  ACCESS_TYPE                         = "accessType";

	/** 正则左匹配 */
	public static final String	REGX_LEFT							= "^.*";
	public static final String	REGX_RIGHT							= ".*$";

	/** 正则左匹配 */
	public static final String	REGX_LEFT_ALL						= "^";
	public static final String	REGX_RIGHT_ALL						= "$";

	public static final String	ACCESS_ID_NOTEXIST					= "接入方id不能为空！";
	public static final String	ADD_TENEMENT_SUCC					= "添加租户成功！";
	public static final String	ADD_TENEMENT_FAIL					= "添加租户失败！";

	public static final String	GET_TENEMENT_SUCC					= "查询租户成功！";
	public static final String	GET_TENEMENT_FAIL					= "查询租户失败！";

	public static final String	UPDATE_TENEMENT_SUCC				= "修改租户成功！";
	public static final String	UPDATE_TENEMENT_FAIL				= "租户不存在，修改租户失败！";

	public static final String	DELETE_TENEMENT_SUCC				= "删除租户成功！";
	public static final String	DELETE_TENEMENT_FAIL				= "租户不存在，删除租户失败！";
	public static final String	DELETE_TENEMENT_EXI_FAIL			= "租户下面存在接入方，请先删除接入方！";

	public static final String	TENEMENT_SAVE_FAIL_NAME_EXITES		= "租户重名保存失败！";
	public static final String	TENEMENT_ACC_SAVE_FAIL_NAME_EXITES	= "接入方重名保存失败！";

	public static final String	UPDATE_TENEMENT_ACC_SUCC			= "修改接入方成功！";
	public static final String	UPDATE_TENEMENT_ACC_FAIL			= "接入方不存在，修改失败！";

	public static final String	DELETE_TENEMENT_ACC_SUCC			= "删除接入方成功！";
	public static final String	DELETE_TENEMENT_ACC_FAIL			= "接入方不存在，删除失败！";
	public static final String	DELETE_TENEMENT_ACC_EXI_FAIL		= "接入方下面存在资源，请先删除资源！";
	public static final String	DELETE_ACC_USER_EXI_FAIL			= "接入方下面存在用户，请先删除用户！";

	public static final String	ACC_SOURCE_SAVE_FAIL_NAME_EXITES	= "接入方资源重名保存失败！";
	public static final String	ACC_USER_SAVE_FAIL_NAME_EXITES		= "接入方用户重名保存失败！";
	public static final String	ACC_USER_SAVE_FAIL_MOBILE_EXITES	= "电话已存在保存失败！";

	public static final String	ADD_SOURCE_SUCC						= "添加资源成功！";
	public static final String	ADD_SOURCE_FAIL						= "添加资源失败！";

	public static final String	GET_ACC_USER_SUCC					= "查询用户成功！";
	public static final String	GET_ACC_USER_FAIL					= "查询用户失败！";

	public static final String	GET_ACC_SOURCE_SUCC					= "查询接入方资源成功！";

}
