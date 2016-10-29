package com.community.yuequ.util;

import android.os.Environment;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class FileTools {

	public static final String ROOT_DIR = "yq/";//根目录
	public static final String VIDEO_DIR = "video/";
	public static final String IMAGE_DIR = "image/";
	public static final String TEMP_DIR = ".temp/";
	public static final String APP_DIR = "app/";
	public static final String LOG_DIR = "log/";

	/**
	 * 检查是否安装SD卡
	 * @return
	 */
	public static boolean checkSDCard() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status = sDCardStatus.equals(Environment.MEDIA_MOUNTED);
		return status;
	}

	 public static String getSdDirectory() {
	        return Environment.getExternalStorageDirectory().getPath();
	    }
	
	/**
	 * 判断目录是否存在，如果不存在，则创建
	 * @param path
	 */
	public static void createDir(String path){
		File filePath = new File(path);
			
		if (!filePath.exists()){
			filePath.mkdir();			
		}	
			
	}
	
	/**
	 * 获取根目录
	 * @return
	 */
	public static String getRootDir() {
		if (checkSDCard()) {
			String str = getSdDirectory()+ File.separator + ROOT_DIR;
			createDir(str);
			return str;
		} else {
			return null;
		}
	}

    /**
	 * 　　* 获取错误的信息 　　* @param arg1 　　* @return 　　
	 */
	public String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}
    
	/**
	 * 获取视频文件的目录
	 * @return
	 */
	public static String getVideoFileDir() {
		String basePath = getRootDir();
		if (basePath != null) {
			String str = basePath + VIDEO_DIR;
			createDir(str);
			return str;
					
		} else {
			return null;
		}
	}
	
	/**
	 * 获取图片文件的目录
	 * @return
	 */
	public static String getImageFileDir() {
		String basePath = getRootDir();
		if (basePath != null) {
			String str = basePath + IMAGE_DIR;
			createDir(str);
			return str;
					
		} else {
			return null;
		}
	}
	
	/**
	 * 获取临时文件的目录
	 * @return
	 */
	public static String getTempFileDir() {
		String basePath = getRootDir();
		if (basePath != null) {
			String str = basePath + TEMP_DIR;
			createDir(str);
			return str;
		} else {
			return null;
		}
	}
	
	/**
	 * 获取应用文件的目录
	 * @return
	 */
	public static String getAppFileDir() {
		String basePath = getRootDir();
		if (basePath != null) {
			String str = basePath + APP_DIR;
			createDir(str);
			return str;
		} else {
			return null;
		}
	}
	
	/**
	 * 获取日志文件的目录
	 * @return
	 */
	public static String getLogFileDir() {
		String basePath = getRootDir();
		if (basePath != null) {
			String str = basePath + LOG_DIR;
			createDir(str);
			return str;
		} else {
			return null;
		}
	}

	public static File getImageLocalPath(String picPath) {
		File file = null;
		String imageFileDir = FileTools.getImageFileDir();
		if(imageFileDir!=null){
			String path = imageFileDir + picPath.hashCode();
			file = new File(path);
		}
		return 	file;
	}
}
