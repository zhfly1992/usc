/*
 * File name: MongoPageUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年2月22日 ... ...
 * ...
 *
 ***************************************************/

package com.run.usc.service.util;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.run.entity.common.Pagination;

/**
 * @Description: mongodb分页查询工具类
 * @author: zhabing
 * @version: 1.0, 2017年2月22日
 */

public class MongoPageUtil<T> {
	/**
	 * 通过条件查询,查询分页结果
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param query
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Pagination getPage(MongoTemplate ucsTemplate, int pageNo, int pageSize, Query query,
			String collectionName) {
		long totalCount = ucsTemplate.count(query, collectionName);
		Pagination page = new Pagination(pageNo, pageSize, totalCount);
		query.skip(page.getFirstResult());// skip相当于从那条记录开始
		query.limit(page.getPageSize());// 从skip开始,取多少条记录
		List datas = find(ucsTemplate, query, collectionName);
		page.setDatas(datas);
		return page;
	}



	/**
	 * 通过条件查询实体(集合)
	 * 
	 * @param query
	 */

	@SuppressWarnings("rawtypes")
	public static List<Map> find(MongoTemplate ucsTemplate, Query query, String collectionName) {
		return ucsTemplate.find(query, Map.class, collectionName);
	}

}
