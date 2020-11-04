/*
 * File name: AccUserQueryRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月5日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.authz.api.base.util.ExceptionChecked;
import com.run.authz.api.constants.AuthzConstants;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.base.util.RSAUtil;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.AccUserBaseQueryService;
import com.run.usc.base.query.TenementBaseQueryService;
import com.run.usc.service.util.MongoPageUtil;
import com.run.usc.service.util.MongoTemplateUtil;

/**
 * @Description: 用户中心crud-rpc
 * @author: zhabing
 * @version: 1.0, 2017年7月5日
 */

public class AccUserQueryRpcSerImpl implements AccUserBaseQueryService {

	private static final Logger			logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private MongoTemplate				tenementTemplate;

	@Autowired
	private MongoTemplateUtil			tenementTemplateUtil;

	@Autowired
	private UserRoleBaseQueryService	userRoleQuery;

	@Autowired
	private TenementBaseQueryService	tenement;



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getAccUserInfoByPage(java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccUserInfoByPage(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.get(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.get(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccUserInfoByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 组装查询条件
			String loginAccount = pageInfo.get(UscConstants.LOGIN_ACCOUNT);
			String tenementNames = pageInfo.get(AuthzConstants.TENEMENT_NAME);
			String accessId = pageInfo.get(AuthzConstants.TENEMENT_ACCESS_ID);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + loginAccount + UscConstants.REGX_RIGHT);

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(loginAccount)) {
				criteria.and(UscConstants.LOGIN_ACCOUNT).regex(patternTenement);
			}
			if (!StringUtils.isEmpty(tenementNames)) {
				// 模糊查询接入方集合
				List<String> accessIdList = tenement.getAccessListByTenementName(tenementNames, accessId);
				// 根据接入方id查询用户集合
				Query querIds = new Query(Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).in(accessIdList));

				List<String> userIds = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL,
						querIds, UscConstants.USER_ID);

				query.addCriteria(Criteria.where(UscConstants.ID_).in(userIds));
			}

			query.addCriteria(criteria);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_USERINFO_COLL);
			logger.debug(String.format("[getAccUserInfoByPage()->success:%s]", page));
			List<Map<String, Object>> dataMap = page.getDatas();
			// 根据查询出来的用户信息查询关联的接入方信息
			for (Map<String, Object> map : dataMap) {
				// 移出用户表里面存在的接入方id
				map.remove(TenementConstant.TENEMENT_ACCESS_ID);

				String id = (String) map.get(UscConstants.ID_);
				// 根据用户id查询接入方id集合
				Query queryAccs = new Query(Criteria.where(UscConstants.USER_ID).is(id));
				List<Map> maps = tenementTemplate.find(queryAccs, Map.class,
						MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
				List listAccs = new ArrayList<>();
				if (maps != null && !maps.isEmpty()) {
					for (Map mapAcc : maps) {
						listAccs.add(mapAcc.get(UscConstants.ACCESSID));
					}

					// 根据集合id查询这个用户所属于的接入方集合
					Query accQ = new Query(Criteria.where(UscConstants.ID_).in(listAccs));
					query.with(new Sort(new Order(UscConstants.ID_)));
					List<Map> accs = tenementTemplate.find(accQ, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
					String accessNames = "";
					String tenementName = "";
					if (accs != null && accs.size() != 0 && listAccs.size() != 0) {
						for (int i = 0; i < listAccs.size(); i++) {
							for (int j = 0; j < accs.size(); j++) {
								if (listAccs.get(i).toString()
										.equals((String) accs.get(j).get(TenementConstant.ACCESS_ID))) {
									tenementName = (String) accs.get(j)
											.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME);
									accessNames += (","
											+ (String) accs.get(j).get(TenementConstant.TENEMENT_ACCESS_NAME));
								}
							}
						}
						if ("" != accessNames) {
							accessNames = accessNames.substring(1, accessNames.length());
						}
					}
					map.put(TenementConstant.TENEMENT_ACCESS_NAME, accessNames);
					map.put(TenementConstant.TENEMENT_ACCESS_ID, listAccs);
					map.put(TenementConstant.TENEMENT_NAME, tenementName);
				}
			}

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccUserInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getAccUserInfoByPage(java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getUnUserInfoPageByRoleId(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getUnUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.get(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getUnUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.get(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getUnUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String roleId = pageInfo.get(UscConstants.ROLE_ID);
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getUnUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 根据角色id查询该角色已经关联的用户id
			RpcResponse<List<Map>> res = userRoleQuery.getUserIdByRoleId(roleId);
			List<String> userIds = new ArrayList<>();
			if (res.isSuccess()) {
				// 组装用户id
				List<Map> map = res.getSuccessValue();
				for (Map userMap : map) {
					String userId = userMap.get(UscConstants.USER_ID) + "";
					userIds.add(userId);
				}

			} else {
				logger.error(String.format("[getUnUserInfoPageByRoleId()->exception:%s]", res.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(res.getMessage());
			}

			String accessId = null;
			// 根据角色id查询角色信息
			RpcResponse<Map<String, Object>> resMap = userRoleQuery.getRoleMessById(roleId);
			if (res.isSuccess()) {
				accessId = resMap.getSuccessValue().get(UscConstants.ACCESSID) + "";

			} else {
				logger.error(String.format("[getUnUserInfoPageByRoleId()->exception:%s]", res.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(res.getMessage());
			}

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE);

			// 组装查询条件
			String loginAccount = pageInfo.get(UscConstants.LOGIN_ACCOUNT);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + loginAccount + UscConstants.REGX_RIGHT);
			if (!StringUtils.isEmpty(loginAccount)) {
				criteria.and(UscConstants.LOGIN_ACCOUNT).regex(patternTenement);
			}
			query.addCriteria(criteria);

			// 查询该接入方下面所有的用户id
			Query queryUser = new Query();
			queryUser.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));
			List<String> listUser = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL,
					queryUser, UscConstants.USER_ID);

			// 排除该角色已经有的用户id
			listUser.removeAll(userIds);

			query.addCriteria(Criteria.where(UscConstants.ID_).in(listUser));

			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_USERINFO_COLL);
			logger.debug(String.format("[getUnUserInfoPageByRoleId()->success:%s]", page));

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getUnUserInfoPageByRoleId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getUserInfoPageByRoleId(java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getUserInfoPageByRoleId(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.get(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.get(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String roleId = pageInfo.get(UscConstants.ROLE_ID);
			if (StringUtils.isEmpty(roleId)) {
				logger.error(String.format("[getUserInfoPageByRoleId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 根据角色id查询该角色已经关联的用户id
			RpcResponse<List<Map>> res = userRoleQuery.getUserIdByRoleId(roleId);
			List<String> userIds = new ArrayList<>();
			if (res.isSuccess()) {
				// 组装用户id
				List<Map> map = res.getSuccessValue();
				for (Map userMap : map) {
					String userId = userMap.get(UscConstants.USER_ID) + "";
					userIds.add(userId);
				}
			} else {
				logger.error(String.format("[getUserInfoPageByRoleId()->exception:%s]", res.getMessage()));
				return RpcResponseBuilder.buildErrorRpcResp(res.getMessage());
			}

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE);

			// 组装查询条件
			String loginAccount = pageInfo.get(UscConstants.LOGIN_ACCOUNT);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + loginAccount + UscConstants.REGX_RIGHT);
			if (!StringUtils.isEmpty(loginAccount)) {
				criteria.and(UscConstants.LOGIN_ACCOUNT).regex(patternTenement);
			}
			query.addCriteria(criteria);

			query.addCriteria(Criteria.where(UscConstants.ID_).in(userIds));

			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_USERINFO_COLL);
			logger.debug(String.format("[getUserInfoPageByRoleId()->success:%s]", page));

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getUserInfoPageByRoleId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#checkAccUserName(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Boolean> checkAccUserName(String accessId, String name) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[checkAccUserName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(name)) {
				logger.error(String.format("[checkAccUserName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 根据接入方id查询该接入方下面所有的用户id集合
			// 查询条件
			DBObject dbCondition = new BasicDBObject();
			dbCondition.put(TenementConstant.TENEMENT_ACCESS_ID, accessId);

			// 查询显示的列
			DBObject dbObjectClum = new BasicDBObject();
			dbObjectClum.put(TenementConstant.USER_ID, 1);
			dbObjectClum.put(UscConstants.ID_, 0);

			Query querymodule = new BasicQuery(dbCondition, dbObjectClum);

			List<Map> userListId = tenementTemplate.find(querymodule, Map.class,
					MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);

			List<Object> usersList = null;
			// 针对map封装成list String userId
			if (null != userListId && !userListId.isEmpty()) {
				usersList = new ArrayList<>();
				for (Map map : userListId) {
					usersList.add(map.get(TenementConstant.USER_ID));
				}
			}

			// 判断该接入方下面所有的正常用户是否存在该名称
			Query queryUser = new Query();
			Criteria cr = Criteria.where(UscConstants.ID_).in(usersList);
			Criteria crDel = Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE);
			Criteria crName = Criteria.where(UscConstants.LOGIN_ACCOUNT).is(name);
			queryUser.addCriteria(cr);
			queryUser.addCriteria(crDel);
			queryUser.addCriteria(crName);

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.exists(queryUser, MongodbConstants.MONGODB_USERINFO_COLL));
		} catch (Exception e) {
			logger.error("[checkAccUserName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getAddUserInfoByPage(java.util.Map)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override

	public RpcResponse<Pagination<Map<String, Object>>> getAddUserInfoByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 租户id
			String tenementId = pageInfo.getString(TenementConstant.TEN_ID);

			if (StringUtils.isEmpty(tenementId)) {
				logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TEN_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 接入方id
			String accessId = pageInfo.getString(TenementConstant.TENEMENT_ACCESS_ID);

			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ACCESS_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 查询该接入方下面所有的用户id
			Query userQuery = new Query();
			userQuery.addCriteria(Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).is(accessId));
			List<Map> find = tenementTemplate.find(userQuery, Map.class, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			List<String> userId = Lists.newArrayList();
			if (find != null) {
				for (Map maps : find) {
					userId.add((String) maps.get(UscConstants.USER_ID));
				}
			}

			// 查询租户下面所有的接入方
			Query accQuery = new Query();
			accQuery.addCriteria(Criteria.where(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID).is(tenementId));
			List<Map> accInfos = tenementTemplate.find(accQuery, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			List allAccessId = Lists.newArrayList();
			if (allAccessId != null) {
				for (Map maps : accInfos) {
					allAccessId.add(maps.get(UscConstants.ID_));
				}
			}

			Query queryUser = new Query();
			if (null != userId && userId.size() != 0) {
				// 排除当前接入方查询其他接入方下面所有的用户
				Criteria cr = Criteria.where(UscConstants.USER_ID).nin(userId);
				queryUser.addCriteria(cr);
			}
			Criteria cr1 = Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).in(allAccessId);
			queryUser.addCriteria(cr1);

			// 查询所有的可添加用户id
			List<Map> lis = tenementTemplate.find(queryUser, Map.class, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
			List userIds = new ArrayList<>();
			if (null != lis && lis.size() != 0) {
				for (Map li : lis) {
					userIds.add(li.get(UscConstants.USER_ID));
				}
			}

			// 组装查询条件
			String loginAccount = pageInfo.getString(UscConstants.LOGIN_ACCOUNT);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + loginAccount + UscConstants.REGX_RIGHT);

			// 分页查询可添加的用户信息
			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE);
			Criteria criteriaUser = Criteria.where(UscConstants.ID_).in(userIds);
			if (!StringUtils.isEmpty(loginAccount)) {
				criteria.and(UscConstants.LOGIN_ACCOUNT).regex(patternTenement);
			}
			query.addCriteria(criteria);
			query.addCriteria(criteriaUser);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_USERINFO_COLL);
			logger.debug(String.format("[getAccUserInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccUserInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getUserRsPageByCode(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getUserRsPageByCode(JSONObject pageInfo) throws Exception {
		if (null == pageInfo) {
			logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
					UscConstants.EMPTYOBJECT));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
		}

		// 分页页数
		String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
		if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
			logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
					UscConstants.PAGENUMBER));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
		}

		// 分页大小
		String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
		if (StringUtils.isEmpty(pageSize)) {
			pageSize = UscConstants.PAGESIZEDEFAULT;
		} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
			logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
					UscConstants.PAGESIZE));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
		}

		// 接入方id
		String accessId = pageInfo.getString(TenementConstant.TENEMENT_ACCESS_ID);

		if (StringUtils.isEmpty(accessId)) {
			logger.error(String.format("[getAddUserInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
					TenementConstant.TENEMENT_ACCESS_ID));
			return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
		}

		// 查询该接入方下面所有的用户id
		Query queryUser = new Query();
		Criteria cr1 = Criteria.where(TenementConstant.TENEMENT_ACCESS_ID).is(accessId);
		queryUser.addCriteria(cr1);

		List<Map> lis = tenementTemplate.find(queryUser, Map.class, MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL);
		List userIds = new ArrayList<>();
		if (null != lis && lis.size() != 0) {
			for (Map li : lis) {
				userIds.add(li.get(UscConstants.USER_ID));
			}
		}

		// 组装查询条件
		String loginAccount = pageInfo.getString(UscConstants.LOGIN_ACCOUNT);

		Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + loginAccount + UscConstants.REGX_RIGHT);
		// 分页查询可添加的用户信息
		Query query = new Query();
		query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
		Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
				.is(TenementConstant.STATE_NORMAL_ONE);
		Criteria criteriaUser = Criteria.where(UscConstants.ID_).in(userIds);
		if (!StringUtils.isEmpty(loginAccount)) {
			criteria.and(UscConstants.LOGIN_ACCOUNT).regex(patternTenement);
		}
		query.addCriteria(criteria);
		query.addCriteria(criteriaUser);
		Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(tenementTemplate,
				Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
				MongodbConstants.MONGODB_USERINFO_COLL);

		logger.debug(String.format("[getAccUserInfoByPage()->success:%s]", page));
		return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);

	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getListAccessByUserId(java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<List<Map>> getListAccessByUserId(String userId) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(userId)) {
				logger.error(String.format("[getListAccessByUserId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 根据用户id查询该接入方id集合
			Query query = new Query(Criteria.where(UscConstants.USER_ID).is(userId));
			List<String> list = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL, query,
					UscConstants.ACCESSID);

			Query queryAcc = new Query(Criteria.where(UscConstants.ID_).in(list));
			List<Map> map = tenementTemplate.find(queryAcc, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, map);
		} catch (Exception e) {
			logger.error("[getListAccessByUserId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getListAccessByUserId(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getListUserByUserIds(List<String> userIds) {
		try {
			// 参数校验
			if (null == userIds || userIds.size() == 0) {
				logger.error(String.format("[getListUserByUserIds()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.USER_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 根据用户id查询该接入方id集合
			Query query = new Query(Criteria.where(UscConstants.ID_).in(userIds));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			List<Map> map = tenementTemplate.find(query, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
			if (null != map && !map.isEmpty()) {
				logger.debug(String.format("[getListUserByUserIds()->success:%s]", map));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, map);
			} else {
				logger.error(String.format("[getListUserByUserIds()->error:%s]", UscConstants.GET_FAIL));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[getListUserByUserIds()->error:%s]", UscConstants.GET_FAIL));
			}

		} catch (Exception e) {
			logger.error("[getListUserByUserIds()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#checkUserState(java.lang.String)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Boolean checkUserState(String id) {
		// 根据用户id查询用户状态
		Query query = new Query(Criteria.where(UscConstants.ID_).is(id));
		query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
		query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
		Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_USERINFO_COLL);
		if (null != map && map.size() != 0) {
			return true;
		}
		return false;
	}



	/**
	 * @see com.run.usc.base.query.AccUserBaseQueryService#getListAccessByUserInfo(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getListAccessByUserInfo(String userInfo) {
		try {
			// 校验json
			RpcResponse<List<Map>> checkRequestParam = ExceptionChecked.checkRequestParam(userInfo);
			if (checkRequestParam != null) {
				return checkRequestParam;
			}

			// 校验必填参数
			RpcResponse<List<Map>> rs = ExceptionChecked.checkRequestKey(logger, "getListAccessByUserInfo", userInfo,
					UscConstants.LOGIN_ACCOUNT, UscConstants.PASSWORD);
			if (rs != null) {
				return rs;
			}

			JSONObject userInfoJson = JSONObject.parseObject(userInfo);
			String decrypt = RSAUtil.decrypt(userInfoJson.getString(UscConstants.PASSWORD));
			logger.info("getListAccessByUserInfo()->密码解密为:" + decrypt);
			String password = DigestUtils.md5Hex(decrypt);
			// 根据用户名和密码查询接入方密钥
			Query query = new Query(
					Criteria.where(UscConstants.LOGIN_ACCOUNT).is(userInfoJson.getString(UscConstants.LOGIN_ACCOUNT))
							.and(UscConstants.PASSWORD).is(password));

			// 获取用户名和密码相同的用户id
			List<String> listUsersId = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_USERINFO_COLL, query,
					UscConstants.ID_);

			// 通过接入方id查询信息
			Query queryUser = new Query(Criteria.where(UscConstants.USER_ID).in(listUsersId));

			// 获取所有接入方的id
			List<String> list = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCUSERINFO_RS_COLL,
					queryUser, UscConstants.ACCESSID);

			// 通过接入方id查询信息
			Query queryAcc = new Query(Criteria.where(UscConstants.ID_).in(list));

			List<Map> map = tenementTemplate.find(queryAcc, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, map);
		} catch (Exception e) {
			logger.error("[getListAccessByUserId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
