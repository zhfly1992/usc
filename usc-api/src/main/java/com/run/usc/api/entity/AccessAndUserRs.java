/*
 * File name: AccessAndUserRs.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年4月24日
 * ... ... ...
 *
 ***************************************************/

package com.run.usc.api.entity;

/**
 * @Description: 接入方和用户关系
 * @author: lkc
 * @version: 1.0, 2017年4月24日
 */

public class AccessAndUserRs {

	private String	id;
	private String	userId;
	/** 接入方资源id */
	private String	accessId;
	private String	accessSourceId;



	public String getAccessId() {
		return accessId;
	}



	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}



	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}



	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}



	/**
	 * @return the accessSourceId
	 */
	public String getAccessSourceId() {
		return accessSourceId;
	}



	/**
	 * @param accessSourceId
	 *            the accessSourceId to set
	 */
	public void setAccessSourceId(String accessSourceId) {
		this.accessSourceId = accessSourceId;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccessAndUserRs [_id=" + id + ", userId=" + userId + ", accessId=" + accessId + ", accessSourceId="
				+ accessSourceId + "]";
	}



	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}




}
