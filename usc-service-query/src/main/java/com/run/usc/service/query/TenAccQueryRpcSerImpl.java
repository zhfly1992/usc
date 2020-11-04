/*
 * File name: TenAccQueryRpcSerImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年7月3日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.query;

import java.util.HashMap;
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

import com.run.authz.api.constants.AuthzConstants;
import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.TenAccBaseQueryService;
import com.run.usc.service.util.MongoPageUtil;

/**
 * @Description: 接入方rpc类
 * @author: zhabing
 * @version: 1.0, 2017年7月3日
 */

public class TenAccQueryRpcSerImpl implements TenAccBaseQueryService {

	@Autowired
	private MongoTemplate		tenementTemplate;

	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#tenementAndAccessInfoByPage(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getAccessInfoByPage(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getAccessInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页页数
			String pageNumber = pageInfo.get(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getAccessInfoByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.get(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getAccessInfoByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 查询条件
			String accessName = pageInfo.get(TenementConstant.TENEMENT_ACCESS_NAME);

			// 组装查询条件
			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + accessName + UscConstants.REGX_RIGHT);

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_CREATE_DATE)));
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(accessName)) {
				criteria.and(TenementConstant.TENEMENT_ACCESS_NAME).regex(patternTenement);
			}
			query.addCriteria(criteria);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			logger.debug(String.format("[getAccessInfoByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getAccessInfoByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#getTenementAccessInfoByTenementId(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getAccessInfoByTenId(String tenementId) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(tenementId)) {
				logger.error(String.format("[getAccessInfoByTenId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			Query query = new Query();
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID).is(tenementId)
					.and(TenementConstant.TENEMENT_STATE).is(TenementConstant.STATE_NORMAL_ONE)
					.and(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE);
			query.addCriteria(criteria);
			List<Map> find = tenementTemplate.find(query, Map.class, MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			if (find != null && find.size() != 0) {
				logger.debug(String.format("[getAccessInfoByTenId()->error:%s]", UscConstants.GET_SUCC));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, find);
			} else {
				logger.debug(String.format("[getAccessInfoByTenId()->error:%s]", UscConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
			}
		} catch (Exception e) {
			logger.error("[getAccessInfoByTenId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#getAllAccSouByAccId(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<List<Map>> getAllAccSouByAccId(String accessType, String orgType, String applicationType) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[getAllAccSouByAccId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (org.apache.commons.lang3.StringUtils.isBlank(orgType)) {
				logger.error(String.format("[getAllAccSouByAccId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.SOURCE_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			Query query = new Query();
			Criteria criteria = Criteria.where(TenementConstant.ACCESS_TYPE).is(accessType)
					.and(TenementConstant.SOURCE_TYPE).is(orgType);
			query.addCriteria(criteria);
			query.addCriteria(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));

			// 如果是菜单资源才判断pc还是app
			if ("sourceTypeMenu".equals(orgType)) {
				if (org.apache.commons.lang3.StringUtils.isBlank(applicationType)) {
					logger.error(String.format("[getAllAccSouByAccId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
							UscConstants.APPLICATIONTYPE));
					return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
				}
				// 增加应用类型区分
				query.addCriteria(Criteria.where(UscConstants.APPLICATIONTYPE).is(applicationType));
			}

			List<Map> list = tenementTemplate.find(query, Map.class, MongodbConstants.MONGODB_TEN_RES_INFO_COLL);

			if (list != null && !list.isEmpty()) {
				logger.debug(String.format("[getAllAccSouByAccId()->success:%s]", list));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, list);
			} else {
				logger.debug(String.format("[getAllAccSouByAccId()->fail:%s]", UscConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
			}

		} catch (Exception e) {
			logger.error("[getAllAccSouByAccId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#checkAccessName(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkAccessName(String id, String accessName) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(id)) {
				logger.error(
						String.format("[checkAccessName()->error:%s-->%s]", UscConstants.NO_BUSINESS, UscConstants.ID));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(accessName)) {
				logger.error(String.format("[checkAccessName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.TENEMENT_ACCESS_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 重名条件
			Query query = new Query();
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID).is(id);
			Criteria criteria1 = Criteria.where(UscConstants.IS_DELETE).is(TenementConstant.STATE_NORMAL_ONE);
			Criteria criteria2 = Criteria.where(TenementConstant.TENEMENT_ACCESS_NAME).is(accessName);
			query.addCriteria(criteria);
			query.addCriteria(criteria1);
			query.addCriteria(criteria2);
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.exists(query, MongodbConstants.MONGODB_ACCESS_INFO_COLL));
		} catch (Exception e) {
			logger.error("[checkAccessName()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#checkSourceName(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> checkSourceName(String accessType, String applicationType, String sourceName) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessType)) {
				logger.error(String.format("[checkSourceName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.ACCESS_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(sourceName)) {
				logger.error(String.format("[checkSourceName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						TenementConstant.ACCESS_SOURCE_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			if (StringUtils.isEmpty(applicationType)) {
				logger.error(String.format("[checkSourceName()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						AuthzConstants.APPLICATION_TYPE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 重名条件(id。是否删除。资源名称)
			Query query = new Query(Criteria.where(AuthzConstants.APPLICATION_TYPE).is(applicationType));
			Criteria criteria = Criteria.where(TenementConstant.ACCESS_TYPE).is(accessType);
			Criteria criteria1 = Criteria.where(UscConstants.IS_DELETE).is(TenementConstant.STATE_NORMAL_ONE);
			Criteria criteria2 = Criteria.where(TenementConstant.ACCESS_SOURCE_NAME).is(sourceName);
			query.addCriteria(criteria2);
			query.addCriteria(criteria1);
			query.addCriteria(criteria);
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC,
					tenementTemplate.exists(query, MongodbConstants.MONGODB_TEN_RES_INFO_COLL));
		} catch (Exception e) {
			logger.error("[checkSourceName()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#getAccessIdBySecret(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<String> getAccessIdBySecret(String accessSecret) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getAccessIdBySecret()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[checkSourceName()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESS_SECRET));
			}
			Query query = new Query(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			Map<String, String> accessInfo = tenementTemplate.findOne(query, Map.class,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			if (accessInfo != null && !accessInfo.isEmpty()) {
				logger.debug(String.format("[getAccessIdBySecret()->success:%s]", accessInfo.get(UscConstants.ID_)));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, accessInfo.get(UscConstants.ID_));
			} else {
				logger.debug(String.format("[getAccessIdBySecret()->fail:%s]", UscConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
			}

		} catch (Exception e) {
			logger.error("[getAccessIdBySecret()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#getTenmentAccByAccId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Map<String, Object>> getTenmentAccByAccId(String accessId) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessId)) {
				logger.error(String.format("[getTenmentAccByAccId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESSID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getTenmentAccByAccId()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESSID));
			}

			Map<String, Object> mapMess = new HashMap<>();

			Query query = new Query(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ID_).is(accessId));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			Map<String, String> accessInfo = tenementTemplate.findOne(query, Map.class,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			if (accessInfo != null && !accessInfo.isEmpty()) {
				mapMess.put(TenementConstant.TENEMENT_ACCESS_NAME,
						accessInfo.get(TenementConstant.TENEMENT_ACCESS_NAME));
				mapMess.put(TenementConstant.TENEMENT_ACCESS_ID, accessInfo.get(UscConstants.ID_));
				mapMess.put(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME,
						accessInfo.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME));
				mapMess.put(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID,
						accessInfo.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID));
				mapMess.put(TenementConstant.ACCESS_TYPE, accessInfo.get(TenementConstant.ACCESS_TYPE));
				logger.debug(String.format("[getTenmentAccByAccId()->success:%s]", accessInfo.get(UscConstants.ID_)));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, mapMess);
			} else {
				logger.debug(String.format("[getTenmentAccByAccId()->fail:%s]", UscConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
			}

		} catch (Exception e) {
			logger.error("[getTenmentAccByAccId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#getTenmentAccByAccId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Map<String, Object>> getTenmentAccBySecret(String accessSecret) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(accessSecret)) {
				logger.error(String.format("[getTenmentAccByAccId()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ACCESS_SECRET));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getTenmentAccByAccId()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ACCESS_SECRET));
			}

			Map<String, Object> mapMess = new HashMap<>();

			Query query = new Query(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ACCESS_SECRET).is(accessSecret));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			Map<String, String> accessInfo = tenementTemplate.findOne(query, Map.class,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			if (accessInfo != null && !accessInfo.isEmpty()) {
				mapMess.put(TenementConstant.TENEMENT_ACCESS_NAME,
						accessInfo.get(TenementConstant.TENEMENT_ACCESS_NAME));
				mapMess.put(TenementConstant.TENEMENT_ACCESS_ID, accessInfo.get(UscConstants.ID_));
				mapMess.put(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME,
						accessInfo.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME));
				mapMess.put(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID,
						accessInfo.get(TenementConstant.TENEMENT_ACCESS_TENEMENT_ID));
				logger.debug(String.format("[getTenmentAccByAccId()->success:%s]", accessInfo.get(UscConstants.ID_)));
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, mapMess);
			} else {
				logger.debug(String.format("[getTenmentAccByAccId()->fail:%s]", UscConstants.GET_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
			}

		} catch (Exception e) {
			logger.error("[getTenmentAccByAccId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenAccBaseQueryService#getTenmentAccByAccId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Map<String, Object>> getAccessInfoById(String id) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(id)) {
				logger.error(String.format("[getAccessInfoById()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.ID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[getAccessInfoById()->error:%s-->%s]",
						UscConstants.NO_BUSINESS, UscConstants.ID));
			}

			Query query = new Query(Criteria.where(UscConstants.IS_DELETE).is(UscConstants.STATE_NORMAL_ONE));
			query.addCriteria(Criteria.where(UscConstants.ID_).is(id));
			query.addCriteria(Criteria.where(UscConstants.STATE).is(UscConstants.STATE_NORMAL_ONE));
			Map<String, Object> accessInfo = tenementTemplate.findOne(query, Map.class,
					MongodbConstants.MONGODB_ACCESS_INFO_COLL);
			logger.debug(String.format("[getAccessInfoById()->success:%s]", accessInfo.get(UscConstants.ID_)));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, accessInfo);
		} catch (Exception e) {
			logger.error("[getTenmentAccByAccId()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
