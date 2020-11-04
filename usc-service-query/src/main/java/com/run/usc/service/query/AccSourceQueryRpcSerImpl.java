/*
 * File name: AccSourceQueryRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月4日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.authz.api.constants.AuthzConstants;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.governance.service.query.GovernanceServices;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.AccSourceBaseQueryService;
import com.run.usc.service.util.MongoPageUtil;
import com.run.usc.service.util.MongoTemplateUtil;

/**
 * @Description: 接入方资源查询rpc
 * @author: zhabing
 * @version: 1.0, 2017年7月4日
 */

public class AccSourceQueryRpcSerImpl implements AccSourceBaseQueryService {

	@Autowired
	private MongoTemplate		tenementTemplate;

	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;

	@Autowired
	private GovernanceServices	goverNance;

	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getAccSourceInfoByPage(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccSourceInfoByPage(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccSourceInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.get(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccSourceInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.get(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccSourceInfoByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 组装查询条件
			String sourceName = pageInfo.get(TenementConstant.ACCESS_SOURCE_NAME);
			String accessType = pageInfo.get(TenementConstant.ACCESS_TYPE);
			String sourceType = pageInfo.get(TenementConstant.SOURCE_TYPE);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + sourceName + UscConstants.REGX_RIGHT);

			Query query = new Query();

			// 基础查询条件，时间排序，可用标识
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_CREATE_DATE)));
			query.addCriteria(
					Criteria.where(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(TenementConstant.TENEMENT_STATE).is(TenementConstant.STATE_NORMAL_ONE));

			if (!StringUtils.isEmpty(sourceName)) {
				query.addCriteria(Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).regex(patternTenement));
			}
			if (!StringUtils.isEmpty(accessType)) {
				query.addCriteria(Criteria.where(TenementConstant.ACCESS_TYPE).is(accessType));
			}
			if (!StringUtils.isEmpty(sourceType)) {
				query.addCriteria(Criteria.where(TenementConstant.SOURCE_TYPE).is(sourceType));
			}
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			page.setDatas(dataBuild(page));
			logger.debug(String.format("[getAccessInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccessInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	private List<Map<String, Object>> dataBuild(Pagination<Map<String, Object>> page) {
		List<Map<String, Object>> datas = page.getDatas();
		Map<String, Object> mapTenement = new HashMap<>();
		Map<String, Object> mapAccess = new HashMap<>();
		Map<String, Object> mapRes = new HashMap<>();
		for (Map<String, Object> map : datas) {
			// 封装租户accesssTenementId
			String telId = (String) map.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID);
			if (mapTenement.get(telId) == null) {
				Map<String, Object> tenementInfo = getTenementInfo(telId);
				map.put("tenementInfo", tenementInfo);
				mapTenement.put(telId, tenementInfo);
			} else {
				map.put("tenementInfo", mapTenement.get(telId));
			}

			// 封装接入方
			String accId = (String) map.get(TenementConstant.TENEMENT_ACCESS_ID);
			if (mapAccess.get(accId) == null) {
				Map<String, Object> accInfo = getAccessInfo(accId);
				map.put("accessInfo", accInfo);
				mapAccess.put(accId, accInfo);
			} else {
				map.put("accessInfo", mapAccess.get(accId));
			}
			// 封装资源类型
			String resName = (String) map.get(TenementConstant.SOURCE_TYPE);
			if (mapRes.get(resName) == null) {
				Map<String, Object> resInfo = getResInfo(resName);
				map.put("resInfo", resInfo);
				mapAccess.put(resName, resInfo);
			} else {
				map.put("resInfo", mapRes.get(accId));
			}
		}
		return datas;
	}



	@SuppressWarnings("unchecked")
	public Map<String, Object> getTenementInfo(String id) {
		return tenementTemplate.findById(id, (new HashMap<String, Object>()).getClass(),
				MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
	}



	@SuppressWarnings("unchecked")
	public Map<String, Object> getAccessInfo(String id) {
		return tenementTemplate.findById(id, (new HashMap<String, Object>()).getClass(),
				MongodbConstants.MONGODB_ACCESS_INFO_COLL);
	}



	@SuppressWarnings("unchecked")
	public Map<String, Object> getResInfo(String name) {
		Criteria criteria = Criteria.where(TenementConstant.RES_NAME).is(name);
		Query query = new Query();
		query.addCriteria(criteria);
		return tenementTemplate.findOne(query, (new HashMap<String, Object>()).getClass(),
				MongodbConstants.MONGODB_DICTIONARY);
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getAllSourceType()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<List<Map>> getAllSourceType(String dicName) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(dicName)) {
				logger.error(String.format("[getAllSourceType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 查询资源分类字典表
			Query query = new Query(Criteria.where(UscConstants.NAME).is(dicName));
			List<String> type = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_DICTIONARY, query,
					UscConstants.ID_);

			RpcResponse<List<Map>> res = tenementTemplateUtil.getModelByParentId(logger, "getAllSourceType",
					MongodbConstants.MONGODB_DICTIONARY, UscConstants.PARENT_ID, type.get(0));
			logger.debug(String.format("[getAllSourceType()->success:%s]", res));
			return res;
		} catch (Exception e) {
			logger.error("[getAllSourceType()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getListMenuByAccessId(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getListMenuByAccessId(String accessType, String applicationType) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[getListMenuByAccessId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 查询接入方下面所有的菜单资源信息
			Query queryUser = new Query();
			queryUser.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(UscConstants.SOURCE_TYPE_MENU));
			queryUser.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			queryUser.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			queryUser.addCriteria(Criteria.where(TenementConstant.ACCESS_TYPE).is(accessType));
			// 判断是否为null
			if (StringUtils.isEmpty(applicationType)) {
				applicationType = "PC";
			}
			// 增加应用类型条件筛选
			queryUser.addCriteria(Criteria.where(UscConstants.APPLICATIONTYPE).is(applicationType));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.find(queryUser, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL));
		} catch (Exception e) {
			logger.error("[checkUserRoleName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getListButtonByAccessId(String accessId, String buttonMenu) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getListButtonByAccessId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESSID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(buttonMenu)) {
				logger.error(String.format("[getListButtonByAccessId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.BUTTONMENU));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 查询接入方下面所有的按钮资源信息
			Query queryButton = new Query();

			queryButton.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(UscConstants.SOURCE_TYPE_BUTTON));
			queryButton.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			queryButton.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			queryButton.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));
			queryButton.addCriteria(Criteria.where(UscConstants.BUTTONMENU).is(buttonMenu));

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.find(queryButton, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL));
		} catch (Exception e) {
			logger.error("[getListButtonByAccessId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getListButtonByid(ArrayList<String> buttonIds) {
		try {
			// 参数校验
			if (null == buttonIds) {
				logger.error(String.format("[getListButtonByid()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESSID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 根据id集合查询按钮资源
			Query queryButton = new Query();

			queryButton.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(UscConstants.SOURCE_TYPE_BUTTON));
			queryButton.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			queryButton.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			queryButton.addCriteria(Criteria.where(UscConstants.ID_).in(buttonIds));

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.find(queryButton, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL));
		} catch (Exception e) {
			logger.error("[getListButtonByid()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getListMenuByAccessId(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getListMenuByIds(List menuIds) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(menuIds)) {
				logger.error(String.format("[getListMenuByIds()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.MENU_IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 查询接入方下面所有的菜单资源信息
			Query queryUser = new Query();

			queryUser.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			queryUser.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			queryUser.addCriteria(Criteria.where(UscConstants.ID_).in(menuIds));

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.find(queryUser, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL));
		} catch (Exception e) {
			logger.error("[getListMenuByIds()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getListMenuByAccessId(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getListMenuByIds(List menuIds, String applicationType) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(menuIds)) {
				logger.error(String.format("[getListMenuByIds()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.MENU_IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 参数校验
			if (StringUtils.isEmpty(applicationType)) {
				logger.error(String.format("[getListMenuByIds()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						AuthzConstants.APPLICATION_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 查询接入方下面所有的菜单资源信息
			Query queryUser = new Query(Criteria.where(AuthzConstants.APPLICATION_TYPE).is(applicationType));
			// TODO
			queryUser.with(new Sort(Direction.ASC, "sourceNum"));
			queryUser.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			queryUser.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			queryUser.addCriteria(Criteria.where(UscConstants.ID_).in(menuIds));

			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.find(queryUser, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL));
		} catch (Exception e) {
			logger.error("[getListMenuByIds()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getSourceMessById(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Map getSourceMessById(String id) {
		Query querySource = new Query();
		querySource.addCriteria(Criteria.where(UscConstants.ID_).is(id));
		return tenementTemplate.findOne(querySource, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getAccSourceInfoByType(java.util.Map)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Map> getSourceMessageById(String id) throws Exception {
		try {
			// 是否存在资源类型
			if (StringUtils.isEmpty(id)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ID_));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ID_));
			}

			Map<String, Object> mapMess = new HashMap<>();

			// 查询资源
			Query querySource = new Query();
			querySource.addCriteria(Criteria.where(UscConstants.ID_).is(id));
			Map<String, Object> map = tenementTemplate.findOne(querySource, Map.class,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			if (map != null) {
				mapMess.put(UscConstants.SOURCE_INFO, map);

				Object parentId = map.get(UscConstants.PARENT_ID);

				Query queryParentSource = new Query();
				queryParentSource.addCriteria(Criteria.where(UscConstants.ID_).is(parentId));
				Map<String, Object> parentMap = tenementTemplate.findOne(queryParentSource, Map.class,
						MongodbConstants.MONGODB_TEN_RES_INFO_COLL);

				mapMess.put(UscConstants.SOURCE_PARENT_INFO, parentMap);
			}
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, mapMess);
		} catch (Exception e) {
			logger.error("[getAccSourceInfoByType()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getAccSourceInfoByType(java.util.Map)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getAccSourceInfoByType(JSONObject pageInfo) throws Exception {
		try {
			// 资源类型,接入方秘钥
			String sourceType = pageInfo.getString(UscConstants.SOURCE_TYPE);
			String accessSecret = pageInfo.getString(UscConstants.ACCESS_SECRET);
			String accessId = null;

			// 是否存在资源类型
			if (StringUtils.isEmpty(sourceType)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.SOURCE_TYPE));
			}
			// 是否存在接入方秘钥
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESS_SECRET));
			} else {
				// 根据接入方秘钥查询接入方id
				Query query = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
				Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
				accessId = (String) map.get(UscConstants.ID_);
			}

			Query query = new Query();

			// 组装查询条件
			String sourceName = pageInfo.getString(TenementConstant.ACCESS_SOURCE_NAME);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + sourceName + UscConstants.REGX_RIGHT);

			// 基础查询条件，时间排序，可用标识
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
			query.addCriteria(
					Criteria.where(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(TenementConstant.TENEMENT_STATE).is(TenementConstant.STATE_NORMAL_ONE));
			// 是否存在资源名称查询条件
			if (!StringUtils.isEmpty(sourceName)) {
				query.addCriteria(Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).regex(patternTenement));
			}
			// 接入方id和接入方类型
			query.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));
			query.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(sourceType));
			List<Map> sourceList = tenementTemplate.find(query, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			// 是否存在资源名称查询条件
			if (!StringUtils.isEmpty(sourceName)) {
				List<String> allSouce = new ArrayList<>();
				// 进行数据的组装
				for (Map map : sourceList) {
					String id = (String) map.get(UscConstants.ID_);
					String parentId = (String) map.get(UscConstants.PARENT_ID);
					// 根据id查询当前id所有的父类以及子类
					getListParentSourceById(id, parentId, allSouce);
				}

				// 去除重复的数据
				List<String> listWithoutDup = new ArrayList(new HashSet(allSouce));

				Query queryInfo = new Query(Criteria.where(UscConstants.ID_).in(listWithoutDup));
				queryInfo.addCriteria(
						Criteria.where(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE));
				queryInfo.addCriteria(
						Criteria.where(TenementConstant.TENEMENT_STATE).is(TenementConstant.STATE_NORMAL_ONE));
				sourceList = tenementTemplate.find(queryInfo, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			}

			logger.debug(String.format("[getAccSourceInfoByType()->success:%s]", sourceList));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, sourceList);
		} catch (Exception e) {
			logger.error("[getAccSourceInfoByType()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getListParentSourceById(String id, String parentId, List<String> allSouce) {
		if (StringUtils.isEmpty(parentId)) {
			allSouce.add(id);
		} else {
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.PARENT_ID).is(parentId));

			List<String> listIds = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_TEN_RES_INFO_COLL, query,
					UscConstants.ID_);
			allSouce.addAll(listIds);

			Query queryParent = new Query(Criteria.where(UscConstants.ID_).is(parentId));
			Map mapMess = tenementTemplate.findOne(queryParent, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);

			if (null != mapMess && !mapMess.isEmpty()) {
				id = (String) mapMess.get(UscConstants.ID_);
				parentId = (String) mapMess.get(UscConstants.PARENT_ID);
			} else {
				parentId = null;
			}
			getListParentSourceById(id, parentId, allSouce);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getAccSourceInfoByType(java.util.Map)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccSourcePageInfoByType(JSONObject pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 资源id和资源类型,接入方秘钥
			String sourceId = pageInfo.getString(UscConstants.SOURCE_ID);
			String sourceType = pageInfo.getString(UscConstants.SOURCE_TYPE);
			String accessSecret = pageInfo.getString(UscConstants.ACCESS_SECRET);
			String accessId = null;

			// 是否存在资源类型
			if (StringUtils.isEmpty(sourceType)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.SOURCE_TYPE));
			}
			// 是否存在接入方秘钥
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESS_SECRET));
			} else {
				// 根据接入方秘钥查询接入方id
				Query query = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
				Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
				accessId = (String) map.get(UscConstants.ID_);
			}

			// 组装查询条件
			String sourceName = pageInfo.getString(TenementConstant.ACCESS_SOURCE_NAME);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + sourceName + UscConstants.REGX_RIGHT);

			Query query = new Query();

			// 基础查询条件，时间排序，可用标识
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_CREATE_DATE)));
			query.addCriteria(
					Criteria.where(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE));
			// 接入方id和接入方类型
			query.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));
			query.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(sourceType));

			// 是否存在资源名称查询条件
			if (!StringUtils.isEmpty(sourceName)) {
				query.addCriteria(Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).regex(patternTenement));
			}

			// 查询资源下面所有的资源信息
			List<String> sourceList = null;
			if (!StringUtils.isEmpty(sourceId)) {
				sourceList = new ArrayList<String>();
				sourceList.add(sourceId);
				// 查询该资源下面的子资源信息，采用递归查询
				sourceList.addAll(selectChildSource(sourceId));
				query.addCriteria(Criteria.where(UscConstants.ID_).in(sourceList));
			}

			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			logger.debug(String.format("[getAccSourceInfoByType()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccSourceInfoByType()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getParentAccSourcePageByType(JSONObject pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 资源id和资源类型,接入方秘钥
			String sourceId = pageInfo.getString(UscConstants.SOURCE_ID);
			String sourceType = pageInfo.getString(UscConstants.SOURCE_TYPE);
			String accessSecret = pageInfo.getString(UscConstants.ACCESS_SECRET);
			String state = pageInfo.getString("state");
			String accessId = null;

			// 是否存在资源类型
			if (StringUtils.isEmpty(sourceType)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.SOURCE_TYPE));
			}
			// 是否存在接入方秘钥
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESS_SECRET));
			} else {
				// 根据接入方秘钥查询接入方id
				Query query = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
				Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
				accessId = (String) map.get(UscConstants.ID_);
			}

			// 组装查询条件
			String sourceName = pageInfo.getString(TenementConstant.ACCESS_SOURCE_NAME);

			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + sourceName + UscConstants.REGX_RIGHT);

			Query query = new Query();

			// 基础查询条件，时间排序，可用标识
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_CREATE_DATE)));
			query.addCriteria(
					Criteria.where(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE));

			// 接入方id和接入方类型
			query.addCriteria(Criteria.where(UscConstants.ACCESSID).is(accessId));
			query.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(sourceType));
			if (!org.apache.commons.lang3.StringUtils.isBlank(state)) {
				query.addCriteria(Criteria.where("state").is(state));
			}

			// 是否存在资源名称查询条件
			if (!StringUtils.isEmpty(sourceName)) {
				query.addCriteria(Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).regex(patternTenement));
			}

			// 查询资源下面所有的资源信息
			List<String> sourceList = null;
			if (!org.apache.commons.lang3.StringUtils.isBlank(sourceId)) {
				sourceList = new ArrayList<String>();
				// sourceList.add(sourceId);
				// 查询该资源下面的子资源信息
				// sourceList.addAll(selectChildSource(sourceId));

				Query queryOrg = new Query(Criteria.where(UscConstants.PARENT_ID).is(sourceId));
				sourceList = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_TEN_RES_INFO_COLL, queryOrg,
						UscConstants.ID_);

				query.addCriteria(Criteria.where(UscConstants.ID_).in(sourceList));
			} else {
				query.addCriteria(Criteria.where(UscConstants.PARENT_ID).in("", null));
			}

			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			logger.debug(String.format("[getAccSourceInfoByType()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccSourceInfoByType()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("unchecked")
	public List<String> selectChildSource(String sourceId) {
		List<String> allSource = new ArrayList<>();
		Query queryOrg = new Query(Criteria.where(UscConstants.PARENT_ID).is(sourceId));
		List<String> sourcelist = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_TEN_RES_INFO_COLL,
				queryOrg, UscConstants.ID_);
		if (sourcelist != null && sourcelist.size() != 0) {
			allSource.addAll(sourcelist);
			for (int i = 0; i < sourcelist.size(); i++) {
				List<String> orgs = selectChildSource(sourcelist.get(i));
				if (orgs != null && orgs.size() != 0) {
					allSource.addAll(orgs);
				}
			}
		}

		return allSource;
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getSourceMessByIds(java.util.List)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<List<Map<String, Object>>> getSourceMessByIds(List<String> sourceIds) {
		try {
			if (sourceIds == null || sourceIds.size() == 0) {
				logger.error(String.format("[getSourceMessByIds()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getSourceMessByIds()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.SOURCE_ID));
			}
			Query query = new Query(Criteria.where(UscConstants.ID_).in(sourceIds));
			List<Map> map = tenementTemplate.find(query, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			if (map != null && !map.isEmpty()) {
				// 针对线上强制转换为HashMap<String,Object>打包有问题。所以有问题
				List<Map<String, Object>> resultMap = new ArrayList<>();
				for (Map map2 : map) {
					resultMap.add(map2);
				}
				logger.debug(String.format("[getAccSourceInfoByType()->success:%s]", resultMap));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, resultMap);
			} else {
				logger.error(String.format("[getAccSourceInfoByType()->error:%s]", UscConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(
						String.format("[getAccSourceInfoByType()->error:%s]", UscConstants.GET_FAIL));
			}
		} catch (Exception e) {
			logger.error("[getAccSourceInfoByType()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getAccSourceUrlByPage(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccSourceUrlByPage(JSONObject pageInfo) throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccSourceUrlByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.getString(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccSourceUrlByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.getString(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccSourceUrlByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 资源id和资源类型,接入方秘钥
			String sourceId = pageInfo.getString(UscConstants.SOURCE_ID);
			String accessId = pageInfo.getString(UscConstants.ACCESSID);

			// 是否存在接入方id
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getAccSourceUrlByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESSID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESSID));
			}

			if (StringUtils.isEmpty(sourceId)) {
				logger.error(String.format("[getAccSourceUrlByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccSourceInfoByType()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.SOURCE_ID));
			}

			// 查询资源id所关联的url集合
			Query queryUrl = new Query(Criteria.where(UscConstants.SOURCE_ID).is(sourceId));
			List<String> urlAddress = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_URL_SOURCE_COLL,
					queryUrl, UscConstants.URLID);
			pageInfo.put(UscConstants.URL_ADDRESS, urlAddress);

			RpcResponse<Pagination<Map<String, Object>>> page = goverNance.getInterfaceByAccessId(pageInfo);
			logger.debug(String.format("[getAccSourceUrlByPage()->success:%s]", page));
			return page;
		} catch (Exception e) {
			logger.error("[getAccSourceUrlByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getListUrlBySourceId(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<List<Map>> getListUrlBySourceId(String sourceId) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(sourceId)) {
				logger.error(String.format("[getListUrlBySourceId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 查询接入方下面所有的菜单资源信息
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.SOURCE_ID).is(sourceId));

			List<String> urlIds = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_URL_SOURCE_COLL, query,
					UscConstants.URLID);
			// 根据urlIds查询接口信息
			RpcResponse<List<Map>> res = goverNance.getInterfaceByUrlIds(urlIds);

			return res;
		} catch (Exception e) {
			logger.error("[getListUrlBySourceId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#checkSourceName(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkSourceName(String sourceName, String accessType) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(sourceName)) {
				logger.error(String.format("[checkSourceName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.ACCESS_SOURCE_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[checkSourceName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 查询接入方下面所有的菜单资源信息
			Query query = new Query(Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).is(sourceName));
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(TenementConstant.ACCESS_TYPE).is(accessType));

			// 校验名称是否重复
			Boolean check = tenementTemplate.exists(query, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			logger.debug(String.format("[checkSourceName()->success:%s]", check));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, check);

		} catch (Exception e) {
			logger.error("[checkSourceName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#checkOrgName(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Boolean> checkOrgName(String sourceName, String accessSecret, String parentId, String sourceType,
			String accessType, String id) throws Exception {
		try {
			// 参数校验
			// TODO 加入accessType校验
			if (StringUtils.isEmpty(sourceName)) {
				logger.error(String.format("[checkOrgName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.ACCESS_SOURCE_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 参数校验
			if (StringUtils.isEmpty(sourceType)) {
				logger.error(String.format("[checkOrgName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			String accessId = null;

			// 是否存在接入方秘钥
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[checkOrgName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[checkSourceName()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESS_SECRET));
			} else {
				// 根据接入方秘钥查询接入方id
				Query query = new Query(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
				Map map = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
				accessId = (String) map.get(UscConstants.ID_);
			}

			// 封装查询条件
			Query query = new Query(Criteria.where(UscConstants.ACCESSID).is(accessId));
			// 适应前端修改后台
			if (!StringUtils.isEmpty(parentId)) {
				query.addCriteria(Criteria.where(UscConstants.PARENT_ID).is(parentId));
			}
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.SOURCE_TYPE).is(sourceType));
			query.addCriteria(Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).is(sourceName));
			if (!StringUtils.isEmpty(id)) {
				query.addCriteria(Criteria.where(UscConstants.ID_).nin(id));
			}
			// 校验名称是否重复
			Boolean check = tenementTemplate.exists(query, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			logger.debug(String.format("[checkOrgName()->success:%s]", check));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, check);

		} catch (Exception e) {
			logger.error("[checkOrgName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#checkOrgHasChild(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkOrgHasChild(String id) throws Exception {
		try {
			// 参数校验
			if (StringUtils.isEmpty(id)) {
				logger.error(String.format("[checkOrgHasChild()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[checkOrgHasChild()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ID));
			}

			List<String> listChildIds = selectChildSource(id);

			Query query = new Query(Criteria.where(UscConstants.ID_).in(listChildIds));
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));

			Boolean check = tenementTemplate.exists(query, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);

			logger.debug(String.format("[checkOrgHasChild()->success:%s]", check));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, check);

		} catch (Exception e) {
			logger.error("[checkOrgHasChild()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#findOrgParentId(java.lang.String)
	 */
	@Override
	public RpcResponse<String> findOrgParentId(String orgId) throws Exception {

		try {
			// 校验组织id
			if (StringUtils.isEmpty(orgId)) {
				logger.error(String.format("[findOrgParentId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ORGANIZED_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[findOrgParentId()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ORGANIZED_ID));
			}
			// 递归使用集合
			ArrayList<String> orgList = Lists.newArrayList();
			orgList.add(orgId);

			// 获取最上级的组织id
			tenementTemplateUtil.findParentId(logger, "findOrgParentId", orgList,
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL);

			logger.debug(String.format("[findOrgParentId()->success:%s]", orgList));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, orgList.get(0));
		} catch (Exception e) {

			logger.error("[findOrgParentId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);

		}

	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#getModelById(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getModelByParentId(String resourceId) throws Exception {

		try {

			// 校验业务参数
			if (StringUtils.isEmpty(resourceId)) {
				logger.error(String.format("[getModelByParentId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ORGANIZED_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[findOrgParentId()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ORGANIZED_ID));
			}

			return tenementTemplateUtil.getModelByParentId(logger, "getModelByParentId",
					MongodbConstants.MONGODB_TEN_RES_INFO_COLL, UscConstants.PARENT_ID, resourceId);

		} catch (Exception e) {
			logger.error("[getModelByParentId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#findSourceInfo(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Map> findSourceInfo(List<String> sourcesIds) throws Exception {

		try {

			// 校验组织id数组
			if (sourcesIds == null || sourcesIds.size() == 0) {
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[findSourceInfo()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ORGANIZED_ID));
			}

			// 创建map封装source信息
			Map<String, Object> sourceMap = Maps.newHashMap();
			for (String orgId : sourcesIds) {
				Query querySource = new Query();
				querySource.addCriteria(Criteria.where(UscConstants.ID_).is(orgId));
				Map orgInfo = tenementTemplate.findOne(querySource, Map.class,
						MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
				if (orgInfo != null) {
					String sourceId = (String) orgInfo.get(UscConstants.ID_);
					String sourceName = (String) orgInfo.get(UscConstants.SOURCE_NAME);
					sourceMap.put(sourceId, sourceName);
				}

			}

			logger.debug(String.format("[findSourceInfo()->success:%s]", sourceMap));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, sourceMap);

		} catch (Exception e) {
			logger.error("[findSourceInfo()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#findAccInfoById(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Map> findAccInfoById(String accessId) throws Exception {

		try {
			if (org.apache.commons.lang3.StringUtils.isBlank(accessId)) {
				logger.error(String.format("[findAccInfoById()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESSID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[findAccInfoById()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESSID));
			}
			Query query = new Query();
			query.addCriteria(Criteria.where(UscConstants.ID_).is(accessId));
			Map findOne = tenementTemplate.findOne(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);

			logger.info(String.format("[findAccInfoById()->info:%s-->%s]", UscConstants.GET_SUCC, findOne));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, findOne);
		} catch (Exception e) {
			logger.error("findAccInfoById()->error:" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#checkSourceIdsHaveChildId(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<Boolean> checkSourceIdsHaveChildId(List<String> ids) {
		try {

			if (StringUtils.isEmpty(ids) || ids.isEmpty()) {
				logger.error(String.format("[checkSourceIdsHaveChildId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.IDS));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 查询子资源数据
			List<Map> childLists = Lists.newArrayList();
			for (String parentValue : ids) {
				if (StringUtils.isEmpty(parentValue)) {
					continue;
				}
				tenementTemplateUtil.recursiveSourceInfos(UscConstants.PARENT_ID, parentValue,
						MongodbConstants.MONGODB_TEN_RES_INFO_COLL, childLists);
			}
			for (Map map : childLists) {
				ids.add(map.get(UscConstants.ID_) + "");
			}

			logger.info(String.format("[checkSourceIdsHaveChildId()->suc:%s--%s]", UscConstants.GET_SUCC, childLists));
			return childLists.isEmpty() ? RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, false)
					: RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, true);

		} catch (Exception e) {
			logger.error("checkSourceIdsHaveChildId()->error:" + e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.AccSourceBaseQueryService#findAllOrgParentId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<String>> findAllOrgParentId(String orgId) throws Exception {
		try {
			// 校验组织id
			if (StringUtils.isEmpty(orgId)) {
				logger.error(String.format("[findOrgParentId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ORGANIZED_ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[findOrgParentId()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ORGANIZED_ID));
			}
			// 递归使用集合
			ArrayList<String> orgList = Lists.newArrayList();
			orgList.add(orgId);

			// 获取所有的父组织id
			tenementTemplateUtil.findAllParentId(logger, "findAllOrgParentId", orgList,MongodbConstants.MONGODB_TEN_RES_INFO_COLL);
			logger.debug(String.format("[findOrgParentId()->success:%s]", orgList));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, orgList);
		} catch (Exception e) {

			logger.error("[findOrgParentId()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);

		}
		
	}

}
