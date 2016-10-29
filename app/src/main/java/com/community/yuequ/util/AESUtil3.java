package com.community.yuequ.util;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



/**
 * 数据传输加密解密
 * @author Administrator
 *
 */

public class AESUtil3 {
	
	public static final String COMMON_KEY = "8h%Sj(Il3_3P!l+a";
	
	public static String decryptByCBC(String input, byte[] key, byte[] iv) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			byte[] outText = cipher.doFinal(Base64.decode(input,Base64.DEFAULT));
			
			return new String(outText);
		} catch (Exception e) {
			return "";
		}
	}

	public static String encodeByCBC(String input, byte[] key, byte[] iv) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			
			byte[] outText = cipher.doFinal(input.getBytes("utf-8"));
			byte[] encode = Base64.encode(outText, Base64.DEFAULT);
			return new String(encode);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }  
	
	
	public static String encode(String encodeStr, String key){
		StringBuilder builder = new StringBuilder();
		
		String iv = getRandomString(16);
		String encodeResult = "";
		try {
			encodeResult = encodeByCBC(encodeStr, key.getBytes("utf-8"), iv.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
//		builder.append(key);
		builder.append(iv);
		builder.append(encodeResult);
		return builder.toString();
	}
	
	
	
	public static String decode(String encodeString, String key){
		String iv = encodeString.substring(0, 16);
		String encodeStr = encodeString.substring(16);
		String decodeResult = "";
		try {
			decodeResult = decryptByCBC(encodeStr,key.getBytes("utf-8"),iv.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		return decodeResult;
	}
	public static void main(String[] args) {
		
//		String line = "{\"a\":1,\"b\":2,\"number\":\"123\",\"string\":\"Hello World\"}";
		String lin2 = "我爱你中国@#！！";
		String encodeResult = encode(lin2 , AESUtil3.COMMON_KEY);
		
		System.out.println("encodeResult:" + encodeResult);
		
		String decodeResult = "";
		decodeResult = decode(encodeResult, AESUtil3.COMMON_KEY);
		System.out.println("decodeResult:" + decodeResult);
		Log.d("TAG",decodeResult);
	}
	
}
