package com.run.metadata.api.er.crud;

import com.run.entity.common.RpcResponse;

/**
 * 
 * 租户与元数据包的关系写入管理Rpc接口
 * 
 * @author: jayden
 * @version: 1.0, 2017年6月13日
 */
public interface MetaPackageTenmentErCrudRpcService {
	/**
	 * 
	 * 删除租户下面的某个包
	 *
	 * @param packageCode
	 *            元数据的包信息唯一编号
	 * @return
	 * 
	 */
	RpcResponse<Boolean> deleteMetaPackageTenment(String packageCode) throws Exception;
}
