/*
* File name: TimeUtil.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			guofeilong		2020年10月19日
* ...			...			...
*
***************************************************/

package com.run.usc.api.base.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.run.usc.api.constants.UscConstants;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2020年10月19日
*/

public class TimeUtil {

	private static final Logger			logger	= Logger.getLogger(UscConstants.LOGKEY);
	/**
	 * 返回当前时间毫秒数
	 */
	public static long getNowTime() {
		try {
			logger.info("即将访问http://www.baidu.com获取时间");
			// 中国科学院国家授时中心
			// URL url = new URL("http://www.ntsc.ac.cn");
			URL url = new URL("http://www.baidu.com");

			URLConnection conn = url.openConnection();
			//conn.setConnectTimeout(3000);
			conn.connect();
			long dateL = conn.getDate();
			logger.info("时间:" + dateL);
			return dateL;
		} catch (MalformedURLException e) {
			logger.error("queryTime()-->e", e);
		} catch (IOException e) {
			logger.error("queryTime()-->e", e);
		}
		Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
		logger.error("查询网络时间失败,获取服务器时间:" + time);
		return time;
	}
	
	/**
	 * 返回过期时间毫秒数
	 */
	public static String getExpiredDate() {
		long nowTime = getNowTime();
		logger.info("nowTime时间:" + nowTime);
		Long Interval=(long) (30L*24L*60L*60L*1000L);
		long expiredDate = nowTime + Interval;
		logger.info("过期时间:" + expiredDate);
		return expiredDate + "" ;
	}
}
