/*
 * File name: OracleTestServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年6月4日 ...
 * ... ...
 *
 ***************************************************/

package com.run.usc.base.curd.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.http.client.util.HttpClientUtil;
import com.run.usc.api.base.crud.OracleTestService;
import com.run.usc.api.base.crud.TenAccBaseCrudService;
import com.run.usc.test.repository.OracleRepository;

/**
 * @Description:
 * @author: zwz
 * @version: 1.0, 2018年6月4日
 */

public class OracleTestServiceImpl implements OracleTestService {

	@Autowired
	private OracleRepository		oracleRepository;
	@Autowired
	private MongoTemplate			tenementTemplate;

	@Autowired
	private TenAccBaseCrudService	tenaccCrud;



	/**
	 * @throws Exception
	 * @see com.run.usc.api.base.crud.OracleTestService#getAllUserInfo()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<String> getAllUserInfo(Map maps) {

		try {
			// 1.获取oracle中user的数据
			List<Map<String, Object>> allUserInfo = oracleRepository.getAllUserInfo(maps);

			for (Map<String, Object> map : allUserInfo) {
				// 2.封装数据
				JSONObject userObj = new JSONObject();
				userObj.put("_id", map.get("ID"));
				userObj.put("userName", map.get("USERNAME"));
				userObj.put("mobile", map.get("USERPHONE"));
				userObj.put("password", map.get("PASSWORD").toString().toUpperCase());
				userObj.put("createTime", map.get("CREATETIME"));
				userObj.put("remark", map.get("REMARK"));

				// 启用或者停用判断
				String state = map.get("STATE").toString();
				if ("1".equals(state)) {
					userObj.put("state", "valid"); // 0 停用 1启用
				} else if ("0".equals(state)) {
					userObj.put("state", "invalid"); // 0 停用 1启用
				}
				userObj.put("loginAccount", map.get("LOGINNAME"));
				userObj.put("activateState", "valid");
				userObj.put("isDelete", "valid");

				// 厂家判断
				String factory = map.get("FACTORYID") + "";
				if (!StringUtils.isBlank(factory)) {
					userObj.put("factory", factory);
					userObj.put("peopleType", "12d36d753fa14cd29cfaedfc5f1177a4");
				}

				// 人员类型
				String userType = map.get("USERTYPE") + "";
				if ("9".equals(userType)) {
					userObj.put("peopleType", "66c58d57c51f46b884438c4406405c73");
				}

				if ("0".equals(userType)) {
					userObj.put("peopleType", "1f2e480c9c794adc85b0dd220d23b300");
				}

				// 不接收短信
				userObj.put("receiveSms", "false");

				// 3.插入mongodb数据
				// 为了删除
				userObj.put("z_delete", maps.get("accessSecret"));
				tenementTemplate.insert(userObj, "UserInfo");

				// 4.人与接入方关系

				JSONObject userAsAccess = new JSONObject();
				userAsAccess.put("_id", UUID.randomUUID().toString());
				userAsAccess.put("userId", map.get("ID"));
				userAsAccess.put("accessId", map.get("COMPANYID"));
				// userAsAccess.put("accessId", "a8f5d1bb1f8b8780");
				userAsAccess.put("z_delete", maps.get("accessSecret"));

				tenementTemplate.insert(userAsAccess, "AccessUserRs");

			}

		} catch (Exception e) {
			e.printStackTrace();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

		return RpcResponseBuilder.buildSuccessRpcResp("成功：001", "true");
	}



	/**
	 * @see com.run.usc.api.base.crud.OracleTestService#getAccessInfo()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<String> getAccessInfo(Map maps) {

		try {
			// 获取所有的公司 对应3.0的接入方 默认在上面创建一层租户
			List<Map<String, Object>> accessSecret = oracleRepository.getAccessSecret(maps);

			for (Map<String, Object> map : accessSecret) {

				// 创建租户
				JSONObject tenementInfo = new JSONObject();
				UUID randomUUID = UUID.randomUUID();
				tenementInfo.put("_id", randomUUID.toString());
				tenementInfo.put("createTime", DateUtils.formatDate(new Date()).toString());
				tenementInfo.put("tenementCode", randomUUID.toString());
				tenementInfo.put("isDelete", "valid");
				tenementInfo.put("tenementAddress", map.get("COMPANYADDRESS"));
				tenementInfo.put("tenementName", map.get("COMPANYNAME"));
				tenementInfo.put("state", "valid");
				tenementInfo.put("tenementNum", randomUUID.toString());
				tenementInfo.put("tenementPhone", map.get("COMPANYPHONE"));

				// 测试数据作方便删除
				tenementInfo.put("z_delete", maps.get("accessSecret"));
				tenementTemplate.insert(tenementInfo, "TenementInfo");

				// 创建接入方 并且建立于租户的关系
				JSONObject accessSecretInfo = new JSONObject();
				accessSecretInfo.put("_id", map.get("ID"));
				accessSecretInfo.put("accessTenementName", map.get("COMPANYNAME"));
				accessSecretInfo.put("accessName", map.get("COMPANYNAME") + "-locman3.0");
				accessSecretInfo.put("accessRootDomain", "/locman");

				// 格式化时间
				accessSecretInfo.put("createTime", DateUtils.formatDate(new Date()).toString());
				accessSecretInfo.put("isDelete", "valid");
				accessSecretInfo.put("accessTenementId", randomUUID.toString());
				accessSecretInfo.put("updateTime", DateUtils.formatDate(new Date()).toString());
				accessSecretInfo.put("state", "valid");
				accessSecretInfo.put("accessSecret", map.get("ID"));
				accessSecretInfo.put("accessType", "LOCMAN");
				accessSecretInfo.put("longitude", map.get("LONGITUDE"));
				accessSecretInfo.put("latitude", map.get("LATITUDE"));
				// 测试数据作方便删除
				accessSecretInfo.put("z_delete", maps.get("accessSecret"));
				tenaccCrud.saveAccessInfo(accessSecretInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		return RpcResponseBuilder.buildSuccessRpcResp("成功：001", "true");
	}



	/**
	 * @see com.run.usc.api.base.crud.OracleTestService#getOrgInfo()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<String> getOrgInfo(Map maps) {

		try {

			List<Map<String, Object>> orgInfo = oracleRepository.getOrgInfo(maps);
			for (Map<String, Object> map : orgInfo) {
				JSONObject orgObj = new JSONObject();
				orgObj.put("_id", map.get("ID"));
				orgObj.put("accessId", map.get("COMPANYID"));
				orgObj.put("accessType", "LOCMAN");
				orgObj.put("accessName", map.get("COMPANYNAME")); // 接入方名称
				orgObj.put("sourceType", "sourceTypeOrganize");
				orgObj.put("sourceDecs", map.get("REMARK"));

				orgObj.put("createTime", DateUtils.formatDate(new Date()));
				orgObj.put("updateTime", DateUtils.formatDate(new Date()));
				orgObj.put("isDelete", "valid");
				orgObj.put("sourceName", map.get("ORGANIZATIONALNAME")); // 组织名
				orgObj.put("state", "valid");
				orgObj.put("accessSecret", map.get("COMPANYID"));
				orgObj.put("z_delete", maps.get("accessSecret"));
				if (map.get("PARID") != null) {
					orgObj.put("parentId", map.get("PARID").toString());
				} else {
					orgObj.put("parentId", "");
				}

				// 通过组织id查询辖区
				maps.put("orgId", map.get("ID"));
				List<Map<String, Object>> jurisdiction = oracleRepository.getJurisdiction(maps);

				for (Map<String, Object> jurisdictionMap : jurisdiction) {

					// 循环保存辖区， parentId为当前组织id

					JSONObject jurObj = new JSONObject();
					jurObj.put("_id", jurisdictionMap.get("ID"));
					jurObj.put("accessId", map.get("COMPANYID"));
					jurObj.put("accessType", "LOCMAN");
					jurObj.put("accessName", map.get("COMPANYNAME"));
					jurObj.put("sourceType", "sourceTypeOrganize");
					jurObj.put("sourceDecs", jurisdictionMap.get("REMARK"));

					jurObj.put("createTime", DateUtils.formatDate(new Date()));
					jurObj.put("updateTime", DateUtils.formatDate(new Date()));
					jurObj.put("isDelete", "valid");
					jurObj.put("sourceName", jurisdictionMap.get("MARKETINGCENTERNAME")); // 组织名
					jurObj.put("state", "valid");
					jurObj.put("accessSecret", jurisdictionMap.get("COMPANYID"));
					jurObj.put("z_delete", maps.get("accessSecret"));
					jurObj.put("parentId", map.get("ID"));
					tenementTemplate.insert(jurObj, "ResourceInfo");
				}

				// 同步组织 -> 辖区作为最下级组织
				tenementTemplate.insert(orgObj, "ResourceInfo");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
		return RpcResponseBuilder.buildSuccessRpcResp("成功：001", "true");

	}



	/**
	 * @see com.run.usc.api.base.crud.OracleTestService#getRoleAsuser(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public RpcResponse<String> getRoleAsuser(Map maps) {

		String ip = "193.168.0.94";
		String isflag = maps.get("isFlag") + "";

		if ("FAT".equals(isflag)) {
			ip = "131.10.11.102";
		} else if ("ONLINE".equals(isflag)) {
			ip = "api.locman.cn";
		}

		try {
			// 创建超级管理员用户->
			JSONObject userInfo = new JSONObject();
			userInfo.put("accessId", maps.get("accessSecret"));
			String loginAccount = UUID.randomUUID().toString().replace("-", "").substring(0, 7);
			userInfo.put("loginAccount", loginAccount);
			userInfo.put("mobile", "13585858588");
			userInfo.put("password", "96E79218965EB72C92A549DD5A330112");
			userInfo.put("userType", "accManagUser");
			userInfo.put("z_delete", maps.get("accessSecret"));
			String doPost1 = HttpClientUtil.getInstance().doPost("http://" + ip + ":8002/usc/user/register",
					JSONObject.toJSONString(userInfo), maps.get("token").toString());
			JSONObject resultJson1 = JSON.parseObject(doPost1);
			String userId = resultJson1.getString("value");
			JSONObject jsonObject = resultJson1.getJSONObject("resultStatus");
			String resultCode = jsonObject.getString("resultCode");
			if (!"0000".equals(resultCode)) {
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("resultMessage"));
			}

			// 创建角色
			JSONObject roleInfo = new JSONObject();
			roleInfo.put("accessId", maps.get("accessSecret"));
			roleInfo.put("remark", "admin");
			roleInfo.put("roleName", "admin");
			roleInfo.put("z_delete", maps.get("accessSecret"));
			String doPost2 = HttpClientUtil.getInstance().doPost("http://" + ip + ":8002/authz/userRole/saveUserRole",
					JSONObject.toJSONString(roleInfo), maps.get("token").toString());
			JSONObject resultJson2 = JSON.parseObject(doPost2);
			String roleId = resultJson2.getString("value");

			jsonObject = resultJson2.getJSONObject("resultStatus");
			resultCode = jsonObject.getString("resultCode");
			if (!"0000".equals(resultCode)) {
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getString("resultMessage"));
			}

			// 角色授权

			JSONObject roleAsPer = new JSONObject();
			List<Object> roleList = Lists.newArrayList();
			roleList.add(JSON.parseObject(roleId).getString("_id"));
			List<Object> perList = Lists.newArrayList();
			perList.add("958e5ae3ab2b420abadbd240605037f5");
			perList.add("7a37115612b741ebafa2370f42345e51");
			perList.add("79da811fbde24b45b366840a68ff25dc");
			perList.add("0185407bfd304c28bccf16d86b062808");
			perList.add("54317fb015314006886160f0bf3fae6e");
			perList.add("7e425ca9ebc14553b8b4d4f15a5de542");
			perList.add("389d3925720a463f84e403d16feca15d");
			perList.add("6cf8d84bc7de4790b6987da796fa799c");
			perList.add("ebc551f391f8416c9a3bf3260290ab0c");
			perList.add("43fea6ff175842029489a6fc71c2da14");
			perList.add("e94f71a0924b49a09c42b1c7978a6933");
			perList.add("784df000cd8b4817bba03d18e1409be6");
			perList.add("32c28759dae44c6792377765f89e0b4e");
			perList.add("20d42a8595d841ea9c86e64f69bad0a4");
			perList.add("1a13439fdc3b402f9ac752a699892442");
			perList.add("4c88bef7687f4304b239894f1670279f");
			perList.add("277093eb043844aa8eb6e85b753db68e");
			perList.add("a889d71279c544ceadbebc2622c41842");
			perList.add("0f8aa68f962f4d2f86798f3e63e659fe");
			perList.add("0f8aa68f962f4d2f86798f3e63e659fe");
			perList.add("49a7c3e9ddd342eca0037f51b110b5ba");
			perList.add("d51094ea9ea4414e9c864f142b391cdf");
			perList.add("ca47f1026d514d389a22d6352f6660ea");
			perList.add("5e74a31665ae4662929584c41d53336c");
			perList.add("551078b05b734fddb6121fc5140d90d8");
			perList.add("eb23fca9a38540ada08a97ffe97afa95");
			perList.add("501cae11010e4a658ed1c4ed1bdec29c");
			perList.add("b2c5d30aa3f14e9eb9742ac6b961abc6");
			perList.add("bf2b7515e8e34a08ba60c5fa867d8fe5");
			perList.add("5d029c9e197f441db60f1926cee1e1ee");
			perList.add("fdd01e95189648acab9460eb721a7b4a");

			roleAsPer.put("roleArray", roleList);
			roleAsPer.put("permiArray", perList);

			HttpClientUtil.getInstance().doPost("http://" + ip + ":8002/authz/permi/addPermiRsRole",
					JSONObject.toJSONString(roleAsPer), maps.get("token").toString());

			// 用户绑定角色
			JSONObject userAsRole = new JSONObject();
			List<String> userLists = new ArrayList<>();
			userLists.add(userId);
			List<String> roleLists = new ArrayList<>();
			roleLists.add(JSON.parseObject(roleId).getString("_id"));
			userAsRole.put("userId", userLists);
			userAsRole.put("roleId", roleLists);
			HttpClientUtil.getInstance().doPost("http://" + ip + ":8002/authz/userRole/addRoleRsUser",
					JSONObject.toJSONString(userAsRole), maps.get("token").toString());

			// 用户登录授权
			JSONObject login = new JSONObject();
			login.put("activateState", "valid");
			HttpClientUtil.getInstance().doPost("http://" + ip + ":8002/usc/user/activate/user/" + userId,
					JSONObject.toJSONString(login), maps.get("token").toString());

			// 返回账号密码
			return RpcResponseBuilder.buildSuccessRpcResp(loginAccount, loginAccount);
		} catch (Exception e) {
			return RpcResponseBuilder.buildErrorRpcResp("token" + e.getMessage());
		}

	}

}
