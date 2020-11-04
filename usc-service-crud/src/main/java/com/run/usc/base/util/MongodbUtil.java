package com.run.usc.base.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.run.entity.tool.DateUtils;

public class MongodbUtil {
	private static final Logger	logger			= Logger.getLogger(MongodbUtil.class);

	private static MongodbUtil	uniqueInstance	= new MongodbUtil();



	public static MongodbUtil getInstance() {
		return uniqueInstance;
	}



	public MongodbUtil() {
	}



	/**
	 * 实体类封装进mongodb的Update对象中
	 * 
	 * @param jsonString
	 * @return
	 */
	public static final Update entityReflectionToUpdate(Object obj) {
		Update update = new Update();
		try {
			Class<? extends Object> entityClass = obj.getClass();
			Field[] fields = entityClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();
				field.setAccessible(true);
				Object value = null;
				String type = field.getType().toString();// 得到此属性的类型
				if (type.endsWith("String")) {
					value = field.get(obj);
					String fieldValue = value == null ? "" : value.toString();
					update.set(fieldName, fieldValue);
				} else if (type.endsWith("int")) {
					update.set(fieldName, field.getInt(obj));
				} else if (type.endsWith("Integer")) {
					// 如果类型是Integer
					Method m = (Method) obj.getClass().getMethod("get" + getMethodName(field.getName()));
					Integer val = (Integer) m.invoke(obj);
					if (val != null) {
						update.set(fieldName, val);
					}
				} else {
					if (!"serialVersionUID".equals(fieldName)) {
						value = field.get(obj);
						String fieldValue = value == null ? "" : value.toString();
						update.set(fieldName, fieldValue);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		} catch (SecurityException e) {
			logger.error("", e);
		} catch (NoSuchMethodException e) {
			logger.error("", e);
		} catch (Exception e) {
			logger.error("", e);
		}
		return update;
	}



	/**
	 * 字符串首字母大写
	 * 
	 * @param fildeName
	 * @return
	 * @throws Exception
	 */
	private static String getMethodName(String fildeName) throws Exception {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}



	/**
	 * json封装进mongodb的Update对象中
	 * 
	 * @param jsonString
	 * @return
	 */
	public static final Update jsonStringToUpdate(String jsonString) {
		Update update = new Update();
		try {
			if (StringUtils.isNotBlank(jsonString)) {
				JSONObject parseObject = JSON.parseObject(jsonString);

				for (String key : parseObject.keySet()) {
					if (parseObject.get(key) != null) {
						update.set(key, parseObject.get(key));
					}
				}
			}
		} catch (IllegalArgumentException e) {
			logger.error("", e);
		} catch (SecurityException e) {
			logger.error("", e);
		}
		return update;
	}



	/**
	 * 封装条件查询
	 * 
	 * @param value
	 *            条件查询json
	 * @param pageIndex
	 *            第几页
	 * @param pageSize
	 *            每页记录数
	 * @return
	 */
	public static final Query jsonStringToQuery(String jsonString, int pageIndex, int pageSize) {
		Query query = new Query();
		Criteria c = new Criteria();
		List<Criteria> listC = new ArrayList<Criteria>();
		try {
			if (StringUtils.isNotBlank(jsonString)) {
				JSONObject value = JSON.parseObject(jsonString);
				for (String key : value.keySet()) {
					if (value.get(key) != null && !StringUtils.isBlank(value.get(key).toString())) {
						listC.add(Criteria.where(key).is(value.get(key)));
					}
				}
				if (listC.size() > 0) {
					Criteria[] cs = new Criteria[listC.size()];
					c.andOperator(listC.toArray(cs));
				}
				if (c != null) {
					query.addCriteria(c);
				}
			}
			if (pageSize > 0) {
				query.skip((pageIndex - 1) * pageSize);
				query.limit(pageSize);
			}
		} catch (Exception e) {
			logger.error("条件查询封装异常", e);
		}
		return query;
	}



	/**
	 * 封装条件查询
	 * 
	 * @param value
	 *            条件查询map
	 * @param pageIndex
	 *            第几页
	 * @param pageSize
	 *            每页记录数
	 * @return
	 */
	public static final Query mapToQuery(Map<String, Object> value, int pageIndex, int pageSize) {
		Query query = new Query();
		Criteria c = new Criteria();
		List<Criteria> listC = new ArrayList<Criteria>();
		try {
			if (value != null) {
				for (String key : value.keySet()) {
					if (value.get(key) != null && !StringUtils.isBlank(value.get(key).toString())) {
						listC.add(Criteria.where(key).is(value.get(key).toString()));
					}
				}
				if (listC.size() > 0) {
					Criteria[] cs = new Criteria[listC.size()];
					c.andOperator(listC.toArray(cs));
				}
				if (c != null) {
					query.addCriteria(c);
				}
			}
			if (pageSize > 0) {
				query.skip((pageIndex - 1) * pageSize);
				query.limit(pageSize);
			}
		} catch (Exception e) {
			logger.error("条件查询封装异常", e);
		}

		return query;
	}



	public static final String jsonResult(boolean success, int resultCode, String resultMessage, Object resultData) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", success);
		map.put("resultCode", resultCode);
		map.put("resultMessage", resultMessage);
		map.put("result", resultData != null ? resultData : "");
		String jsonString = JSON.toJSONString(map);
		logger.info("返回结果：" + jsonString);
		return jsonString;
	}



	public static final String jsonResult(Boolean success, String resultCode, String resultMessage, Object resultData) {
		JSONObject map = new JSONObject();
		if (resultData != null) {
			map.put("userInfo", formatDate(resultData));
		}
		map.put("resultCode", resultCode);
		map.put("resultMessage", resultMessage);

		String jsonString = JSON.toJSONString(map);
		logger.info("返回结果：" + jsonString);
		return jsonString;
	}



	public static final String jsonResult(String resultCode, String resultMessage, String dataKey, Object dataValue) {
		JSONObject resultJson = new JSONObject();
		JSONObject srvrtJson = new JSONObject();

		srvrtJson.put("resultCode", resultCode);
		srvrtJson.put("resultMessage", resultMessage);
		srvrtJson.put("timeStamp", DateUtils.stampToDate(System.currentTimeMillis() + ""));
		resultJson.put("status", srvrtJson);
		if (StringUtils.isNotBlank(dataKey)) {
			JSONObject bizrtJson = new JSONObject();
			bizrtJson.put(dataKey, formatDate(dataValue));
			resultJson.put("data", bizrtJson);
		}
		String jsonString = resultJson.toJSONString();
		logger.info("返回结果：" + jsonString);
		return jsonString;
	}



	public static final String jsonResult(String resultCode, String resultMessage, String dataKey, Object dataValue,
			String dataKey2, Object dataValue2, String token) {
		JSONObject resultJson = new JSONObject();
		JSONObject srvrtJson = new JSONObject();

		srvrtJson.put("resultCode", resultCode);
		srvrtJson.put("resultMessage", resultMessage);
		srvrtJson.put("timeStamp", DateUtils.stampToDate(System.currentTimeMillis() + ""));
		resultJson.put("status", srvrtJson);
		JSONObject bizrtJson = new JSONObject();
		if (StringUtils.isNotBlank(dataKey)) {
			bizrtJson.put(dataKey, formatDate(dataValue));
			resultJson.put("data", bizrtJson);
		}
		if (StringUtils.isNotBlank(dataKey2)) {
			bizrtJson.put(dataKey2, formatDate(dataValue2));
			resultJson.put("data", bizrtJson);
		}
		if (StringUtils.isNotBlank(token)) {
			bizrtJson.put("token", token);
			resultJson.put("data", bizrtJson);
		}
		String jsonString = resultJson.toJSONString();
		logger.info("返回结果：" + jsonString);
		return jsonString;
	}



	/**
	 * 查询属性封装
	 * 
	 * @param query
	 *            mongodb查询类
	 * @param queryType
	 *            oauthFields=用户认证。
	 * @return
	 */
	public Query queryFields(Query query, String queryType, String[] appCodeFields) {
		org.springframework.data.mongodb.core.query.Field fields = query.fields();
		if (appCodeFields != null) {
			for (String field : appCodeFields) {
				fields.include(field);
			}
			fields.include("tenantCode").include("appCode");
		}
		fields.exclude("_id");
		return query;
	}



	public void closeMongoDb(MongoTemplate mongoTemplate) {
		DB db = mongoTemplate.getDb();
		Mongo mongo = db.getMongo();
		if (mongo != null) {
			mongo.close();
		}
	}



	public static final Object formatDate(Object json) {
		return json;
	}
}
