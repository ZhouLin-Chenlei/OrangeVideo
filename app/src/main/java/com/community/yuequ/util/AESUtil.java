package com.community.yuequ.util;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	public static final String COMMON_KEY = "8h%Sj(Il3_3P!l+a";
	public static String decryptByCBC(String input, byte[] key, byte[] iv) throws Exception{

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			byte[] outText = cipher.doFinal(Base64.decode(input, Base64.DEFAULT));
			return new String(outText);

	}

	public static String encodeByCBC(String input, byte[] key, byte[] iv) throws Exception {

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			byte[] outText = cipher.doFinal(input.getBytes("utf-8"));
			byte[] encode = Base64.encode(outText, Base64.DEFAULT);
			return new String(encode);

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
	
	public static String encode(String encodeStr) throws Exception {
		StringBuilder builder = new StringBuilder();
//		String pass = getRandomString(16);
		String iv = getRandomString(16);
		String encodeResult = encodeByCBC(encodeStr,COMMON_KEY.getBytes("utf-8"),iv.getBytes("utf-8"));

//		builder.append(pass);
		builder.append(iv);
		builder.append(encodeResult);
		return builder.toString();
	}
	
	public static String decrypt(String encodeString) throws Exception {
//		String pass = encodeString.substring(0, 16);
//		String iv = encodeString.substring(16, 32);
//		String encodeStr = encodeString.substring(32);
		String iv = encodeString.substring(0, 16);
		String encodeStr = encodeString.substring(16);
		String decodeResult = decryptByCBC(encodeStr,COMMON_KEY.getBytes("utf-8"),iv.getBytes("utf-8"));

		return decodeResult;
	}

	public static void main(String[] args) {
		
		String line = "我爱中国1243#@@@!!";
		String encodeResult = null;
		try {
			encodeResult = encode(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("encodeResult:" + encodeResult);
		encodeResult = "bgw9pcttyiusf14jLgs8km1tq4S75FTSq4iT5Tu9EtPz6vbxHSHSxAjvoAt6dG2d4/odT6MY28xlHhA5y4e+aXnXXQY5mvKhAdkqwy+lm20/CKNlyMw9rdgRIQg8r26tVFykapEh7Ej3UNTC";
		String decodeResult = "";
		try {
			decodeResult = decrypt(encodeResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("decodeResult:" + decodeResult);
	}
}
