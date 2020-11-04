package com.run.usc.api.base.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.log4j.Logger;



public class RSAUtil {
	   private static final Logger logger = Logger.getLogger(RSAUtil.class);
	    /**
	     * 放置公钥与私钥
	     *
	     * volatile 保证当前的map在主内存中保存最新值
	     *
	     */
	    private static volatile Map<String, String> keyMap = new HashMap<String, String>(2);

//	     public static void main(String[] args) throws Exception {
////	         生成公钥和私钥
////	         genKeyPair();
////	         加密字符串
////
////	         System.out.println("随机生成的公钥为:" + getPublicKey());
////	         System.out.println("随机生成的私钥为:" + getPrivateKey());
//
//	             String message = "Aa123456*";
//	             System.out.println("原字符串为:" + message);
//	             String messageEn = encrypt(message, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCePs+67FkUZzaEmnhGYJitDcdGRzrIifZ/eZzMHtdISJAeDudgDWqwk7SE8WbkDEeZq+M8+ae/oXfgIIUbz5Wk6+BgP8sDTF7pPTtHD8k6viTuv0zHNZChKdiso2ahDuD9ptsNXeWxHem++rrbmE0yg+863x+z/UEzheztDoD4oQIDAQAB");
//	             System.out.println(message + "加密后的字符串为:" + messageEn);
//	             String messageDe = decrypt(messageEn, "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ4+z7rsWRRnNoSaeEZgmK0Nx0ZHOsiJ9n95nMwe10hIkB4O52ANarCTtITxZuQMR5mr4zz5p7+hd+AghRvPlaTr4GA/ywNMXuk9O0cPyTq+JO6/TMc1kKEp2KyjZqEO4P2m2w1d5bEd6b76utuYTTKD7zrfH7P9QTOF7O0OgPihAgMBAAECgYB3YeeRm3DwxZUJoQeQAJvuInDuAhrE4+BE91hlXfcIH0Yqhw/jFPwiTqxnhz8aWHFD67s5axc0qPoo6h/BhW/clWPnHpOUi9IVtKQ2+ngISfipC/ChTaPV6wTv1SlNu1icfPPOqXMjA1+yyLp1T/Wd1kuwk2U1kaoi5xsPWT7NYQJBANhSJgUwdpvLWPgoN/M2sqW2jw42EdvB+0P2HhQrmMs/t9mMQ8regQJT5hOfVN3kbYlIXlGjs9Ge8uorHmVbvO0CQQC7RZdCx+6la5KjMlW2W5IZURqLZWd/CjnVTSHIuquese+FisoghGpYRpBxUR27G87T5LNSUFOElEzs1LwiDmgFAkB10sjCqtLvqKKdB8rxvikv+W+R3gF8IPwq2DMp6VUf32TUKYIDTY4XJnt61J30/iZrT++PqAqZcmn9Ad9cdctBAkAMApOH0eCpNTPrSXAbyNWe0Ae+xtsTV6mL122CPQ6Cl/C/hL4fxb44ORZMXiJk/IyQJEunzMT+bLBeQ+po2LmtAkBxX1gp/ZbysgVlhX33ITp1KSzjJzK3d+EtIdelDlcAi2+OI712uphkdQdHsS6rfRBrULUlohEur6R8WUS51ZHW");
//	             System.out.println("还原后的字符串为:" + messageDe);
////	             String md5Hex = DigestUtils.md5Hex("Aa1234..");
////	             System.out.println("md5加密后的密码:" + md5Hex);
//	    	 
//	     }
	     
	     
	     /**
	      * 
	      * @Description:测试方法
	      * @param str
	      * @param privateKey
	      * @return
	      * @author :zh
	      * @version 2020年10月20日
	      */
//	     public static String decrypt1(String str,String privateKey) {
//		        String outStr = "";
//		        //64位解码加密后的字符串
//		        byte[] inputByte = new byte[0];
//		        try {
//		            inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
//
//		            //base64编码的私钥
//		            byte[] decoded = Base64.decodeBase64(privateKey);
//		            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
//		            //RSA解密
//		            Cipher cipher = Cipher.getInstance("RSA");
//		            cipher.init(Cipher.DECRYPT_MODE, priKey);
//		            outStr = new String(cipher.doFinal(inputByte));
//		        } catch (UnsupportedEncodingException e) {
//		            logger.error("不支持的编码", e);
//		        } catch (NoSuchAlgorithmException e) {
//		            e.printStackTrace();
//		            logger.error("未找到找到指定算法", e);
//		        } catch (InvalidKeyException e) {
//		            logger.error("无效的秘钥", e);
//		        } catch (NoSuchPaddingException e) {
//		            logger.error("请求特定填充机制, 但该环境中未提供时", e);
//		        } catch (BadPaddingException e) {
//		            logger.error("预期对输入数据使用特定填充机制, 但未正确填充数据", e);
//		        } catch (InvalidKeySpecException e) {
//		            logger.error("无效的密钥规范", e);
//		        } catch (IllegalBlockSizeException e) {
//		            logger.error("非法的块大小", e);
//		        }
//		        return outStr;
//		    }

	    /**
	     * 获取公钥
	     *
	     * @return
	     */
	    public static String getPublicKey() {
	        String publicKey = keyMap.get("public");
	        if (publicKey == null) {
	            // 加锁 防止多线程生产多个密钥
	            synchronized (keyMap.getClass()) {
	                if (publicKey == null) {
	                    try {
	                        genKeyPair();
	                        publicKey = keyMap.get("public");
	                    } catch (NoSuchAlgorithmException e) {
	                        logger.error("获取随机公私钥错误", e);
	                    }
	                }
	            }
	        }
	        return publicKey;
	    }

	    /**
	     * 获取私钥
	     *
	     * @return 私钥
	     */
	    public static String getPrivateKey() {
	        String privateKey = keyMap.get("private");
	        if (privateKey == null) {
	            logger.error("未获取私钥!!!");
	        }
	        return privateKey;
	    }

	    /**
	     * 随机生成密钥对
	     *
	     * @throws NoSuchAlgorithmException
	     */
	    private static void genKeyPair() throws NoSuchAlgorithmException {
	        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象

	        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
	        // 初始化密钥对生成器，密钥大小为96-1024位
	        keyPairGen.initialize(1024);
	        // 生成一个密钥对，保存在keyPair中
	        KeyPair keyPair = keyPairGen.generateKeyPair();
	        // 得到私钥
	        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	        // 得到公钥
	        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
	        // 得到私钥字符串
	        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
	        // 将公钥和私钥保存到Map
	        keyMap.put("public", publicKeyString);
	        keyMap.put("private", privateKeyString);
	    }

	    /**
	     * RSA公钥加密
	     *
	     * @param str       加密字符串
	     * @param publicKey 公钥
	     * @return 密文
	     * @throws Exception 加密过程中的异常信息
	     */
	    public static String encrypt(String str, String publicKey) throws Exception {
	        //base64编码的公钥
	        byte[] decoded = Base64.decodeBase64(publicKey);
	        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
	        //RSA加密
	        Cipher cipher = Cipher.getInstance("RSA");
	        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
	        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
	        return outStr;
	    }

	    /**
	     * RSA私钥解密
	     *
	     * @param str 加密字符串
	     * @return 铭文
	     * @throws Exception 解密过程中的异常信息
	     */
	    public static String decrypt(String str) {
	        String outStr = "";
	        //64位解码加密后的字符串
	        byte[] inputByte = new byte[0];
	        try {
	            inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));

	            //base64编码的私钥
	            byte[] decoded = Base64.decodeBase64(getPrivateKey());
	            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
	            //RSA解密
	            Cipher cipher = Cipher.getInstance("RSA");
	            cipher.init(Cipher.DECRYPT_MODE, priKey);
	            outStr = new String(cipher.doFinal(inputByte));
	        } catch (UnsupportedEncodingException e) {
	            logger.error("不支持的编码", e);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	            logger.error("未找到找到指定算法", e);
	        } catch (InvalidKeyException e) {
	            logger.error("无效的秘钥", e);
	        } catch (NoSuchPaddingException e) {
	            logger.error("请求特定填充机制, 但该环境中未提供时", e);
	        } catch (BadPaddingException e) {
	            logger.error("预期对输入数据使用特定填充机制, 但未正确填充数据", e);
	        } catch (InvalidKeySpecException e) {
	            logger.error("无效的密钥规范", e);
	        } catch (IllegalBlockSizeException e) {
	            logger.error("非法的块大小", e);
	        }
	        return outStr;
	    }
	    
	    
	    
	    /**
	     * RSA私钥解密
	     *
	     * @param str 加密字符串, privateKey从redis读取到的私钥
	     * @return 铭文
	     * @throws Exception 解密过程中的异常信息
	     */
	    public static String decrypt(String str,String privateKey) {
	        String outStr = "";
	        //64位解码加密后的字符串
	        byte[] inputByte = new byte[0];
	        try {
	            inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));

	            //base64编码的私钥
	            byte[] decoded = Base64.decodeBase64(privateKey);
	            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
	            //RSA解密
	            Cipher cipher = Cipher.getInstance("RSA");
	            cipher.init(Cipher.DECRYPT_MODE, priKey);
	            outStr = new String(cipher.doFinal(inputByte));
	        } catch (UnsupportedEncodingException e) {
	            logger.error("不支持的编码", e);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	            logger.error("未找到找到指定算法", e);
	        } catch (InvalidKeyException e) {
	            logger.error("无效的秘钥", e);
	        } catch (NoSuchPaddingException e) {
	            logger.error("请求特定填充机制, 但该环境中未提供时", e);
	        } catch (BadPaddingException e) {
	            logger.error("预期对输入数据使用特定填充机制, 但未正确填充数据", e);
	        } catch (InvalidKeySpecException e) {
	            logger.error("无效的密钥规范", e);
	        } catch (IllegalBlockSizeException e) {
	            logger.error("非法的块大小", e);
	        }
	        return outStr;
	    }
	    
	    

}
