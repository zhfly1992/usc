/*
 * File name: TenementInfoQueryRpcServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年6月29日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.query;

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

import com.run.entity.common.Pagination;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.usc.api.constants.MongodbConstants;
import com.run.usc.api.constants.TenementConstant;
import com.run.usc.api.constants.UscConstants;
import com.run.usc.base.query.TenementBaseQueryService;
import com.run.usc.service.util.MongoPageUtil;
import com.run.usc.service.util.MongoTemplateUtil;

/**
 * @Description: 租户查询rpc类
 * @author: zhabing
 * @version: 1.0, 2017年6月29日
 */

public class TenInfoQueryRpcSerImpl implements TenementBaseQueryService {

	private static final Logger	logger	= Logger.getLogger(UscConstants.LOGKEY);

	@Autowired
	private MongoTemplate		tenementTemplate;
	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;



	/**
	 * @see com.run.usc.base.query.TenementBaseQueryService#getTenementByPage(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RpcResponse<Pagination<Map<String, Object>>> getTenementByPage(Map<String, String> pageInfo)
			throws Exception {
		try {
			if (null == pageInfo) {
				logger.error(String.format("[getTenementByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.EMPTYOBJECT));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			// 分页页数
			String pageNumber = pageInfo.get(UscConstants.PAGENUMBER);
			if (StringUtils.isEmpty(pageNumber) || !org.apache.commons.lang3.StringUtils.isNumeric(pageNumber)) {
				logger.error(String.format("[getTenementByPage()->error:%s-->%s]", UscConstants.NO_BUSINESS,
						UscConstants.PAGENUMBER));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 分页大小
			String pageSize = pageInfo.get(UscConstants.PAGESIZE);
			if (StringUtils.isEmpty(pageSize)) {
				pageSize = UscConstants.PAGESIZEDEFAULT;
			} else if (!org.apache.commons.lang3.StringUtils.isNumeric(pageSize)) {
				logger.error(String.format("[getTenementByPage()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						UscConstants.PAGESIZE));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}

			// 查询条件
			String tenementName = pageInfo.get(TenementConstant.TENEMENT_NAME);

			// 组装查询条件
			Pattern patternTenement = Pattern.compile(UscConstants.REGX_LEFT + tenementName + UscConstants.REGX_RIGHT);

			Query query = new Query();
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE);
			if (!StringUtils.isEmpty(tenementName)) {
				criteria.and(TenementConstant.TENEMENT_NAME).regex(patternTenement);
			}
			query.addCriteria(criteria);
			Pagination<Map<String, Object>> page = (Pagination<Map<String, Object>>) MongoPageUtil.getPage(
					tenementTemplate, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), query,
					MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
			logger.debug(String.format("[getTenementByPage()->success:%s]", page));
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, page);
		} catch (Exception e) {
			logger.error("[getTenementByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenementBaseQueryService#getTenementAll()
	 */
	@Override
	@SuppressWarnings({ "rawtypes" })
	public RpcResponse<List> getTenementAll() throws Exception {
		// 查询所有租户集合
		try {
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE).and(TenementConstant.TENEMENT_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE);
			Query query = new Query(criteria);
			query.with(new Sort(new Order(Direction.DESC, TenementConstant.TENEMENT_UPDATE_DATE)));
			List<Map> tenementInfoList = tenementTemplate.find(query, Map.class,
					MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
			return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, tenementInfoList);
		} catch (Exception e) {
			logger.error("[getTenementByPage()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenementBaseQueryService#nameCheack(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> nameCheack(String tenementName) {
		try {
			// 参数校验
			if (StringUtils.isEmpty(tenementName)) {
				logger.error(String.format("[nameCheack()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						TenementConstant.TENEMENT_NAME));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			Query queryT = new Query();
			Criteria criteriaT = Criteria.where(TenementConstant.TENEMENT_NAME).is(tenementName)
					.and(TenementConstant.TENEMENT_DELETE_STATE).is(TenementConstant.STATE_NORMAL_ONE);
			queryT.addCriteria(criteriaT);
			boolean check = tenementTemplate.exists(queryT, MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
			return RpcResponseBuilder.buildSuccessRpcResp(null, check);
		} catch (Exception e) {
			logger.error("[nameCheack()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenementBaseQueryService#getTenementByTenementName(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public RpcResponse<List> getTenementByTenementName(String tenementName, String match) throws Exception {

		try {
			// 参数校验
			if (StringUtils.isEmpty(tenementName)) {
				tenementName = "";
			}
			if (StringUtils.isEmpty(match)) {
				logger.error(String.format("[getTenementByTenementName()->error:%s-->%s]", UscConstants.CHECK_BUSINESS,
						TenementConstant.TENEMENT_MATCH));
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.NO_BUSINESS);
			}
			Pattern pattern = null;
			// 1代表完全匹配方式查询
			if ("1".equals(match)) {
				pattern = Pattern
						.compile(TenementConstant.REGX_LEFT_ALL + tenementName + TenementConstant.REGX_RIGHT_ALL);
			} else {
				pattern = Pattern.compile(TenementConstant.REGX_LEFT + tenementName + TenementConstant.REGX_RIGHT);
			}
			// 根据名称查询租户信息
			Criteria criteria = Criteria.where(TenementConstant.TENEMENT_DELETE_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE).and(TenementConstant.TENEMENT_STATE)
					.is(TenementConstant.STATE_NORMAL_ONE).and(TenementConstant.TENEMENT_NAME).regex(pattern);
			Query query = new Query(criteria);
			List<Map> tenementInfoList = tenementTemplate.find(query, Map.class,
					MongodbConstants.MONGODB_TENEMENT_INFO_COLL);
			if (tenementInfoList == null || tenementInfoList.isEmpty()) {
				return RpcResponseBuilder.buildErrorRpcResp(UscConstants.GET_FAIL);
			} else {
				return RpcResponseBuilder.buildSuccessRpcResp(UscConstants.GET_SUCC, tenementInfoList);

			}
		} catch (Exception e) {
			logger.error("[getTenementByTenementName()->exception]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.usc.base.query.TenementBaseQueryService#getAccessListByTenementName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAccessListByTenementName(String tenementName, String accessId) {
		Pattern pattern = Pattern
				.compile(TenementConstant.REGX_LEFT+ tenementName + TenementConstant.REGX_RIGHT);
		// 根据名称查询租户信息
		Criteria criteria = Criteria.where(TenementConstant.TENEMENT_STATE).is(TenementConstant.STATE_NORMAL_ONE)
				.and(TenementConstant.TENEMENT_ACCESS_TENEMENT_NAME).regex(pattern);
		Query query = new Query(criteria);

		if (!StringUtils.isEmpty(accessId)) {
			query.addCriteria(Criteria.where(TenementConstant.ACCESS_ID).in(accessId));
		}

		List<String> accessIdList = tenementTemplateUtil.getListByKey(MongodbConstants.MONGODB_ACCESS_INFO_COLL, query,
				UscConstants.ID_);

		return accessIdList;
	}

}
