package com.community.yuequ.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.community.yuequ.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageUtils {
	
	public static final int REQUEST_CODE_FROM_CAMERA = 5001;
	public static final int REQUEST_CODE_FROM_ALBUM = 5002;
	public static final int REQUEST_CODE_FROM_CUT = 5003;

	/**
	 * 存放拍照图片的uri地址
	 */
//	public static Uri imageUriFromCamera;
	public static File mPhotoFile;
	/**
	 * 显示获取照片不同方式对话框
	 */
	public static void showImagePickDialog(final Activity activity) {
		String title = "选择获取图片方式";
		String[] items = new String[]{"拍照", "相册"};
		new AlertDialog.Builder(activity)
			.setTitle(title)
			.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					switch (which) {
					case 0:
						pickImageFromCamera(activity);
						break;
					case 1:
						pickImageFromAlbum(activity);
						break;
					default:
						break;
					}
				}
			})
			.show();
	}
	
	
	
	public static File getCameraPhotoFile() {
		File dir = new File(FileTools.getImageFileDir());
		if(!dir.exists()){
			dir.mkdirs();
		}
		return new File(dir, "pic_" + System.currentTimeMillis() + ".jpg");
	}

	public static boolean hasCamera(Context context) {
		PackageManager pm = context.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
				|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
	}

	public static void scanMediaJpegFile(final Context context,
			final File file, final OnScanCompletedListener listener) {
		MediaScannerConnection.scanFile(context,
				new String[] { file.getAbsolutePath() },
				new String[] { "image/jpg" }, listener);
	}
	
	 /**
     * Check whether the SD card is writable
     */
    public static boolean isSdcardWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
	/**
	 * 打开相机拍照获取图片
	 */
	public static void pickImageFromCamera(final Activity activity) {
		
		if (!hasCamera(activity)) {
			Toast.makeText(activity, R.string.no_camera, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!isSdcardWritable()) {
			Toast.makeText(activity, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
			
			return;
		}
		
//		imageUriFromCamera = createImageUri(activity);
		if(mPhotoFile==null){
			mPhotoFile = getCameraPhotoFile();
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
			activity.startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
		}
	}
	
	/**
	 * 打开本地相册选取图片
	 */
	public static void pickImageFromAlbum(final Activity activity) {

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
	}
	
	/**
	 * 打开本地相册选取图片2
	 */
	public static void pickImageFromAlbum2(final Activity activity) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
	}


	public static void copeImage(final Activity activity,Uri uri){
		if (!isSdcardWritable()) {
			Toast.makeText(activity, R.string.no_sdcard, Toast.LENGTH_SHORT).show();

			return;
		}
		if(mPhotoFile==null){
			mPhotoFile = getCameraPhotoFile();
		}
		String absolutePath19 = getImageAbsolutePath19(activity, uri);
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(new File(absolutePath19)), "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection

		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
		activity.startActivityForResult(intent, REQUEST_CODE_FROM_CUT);
	}

	/**
	 * 将图片保存到SD中
	 */
	public static void saveFile(Context context, Bitmap bm, String fileName) throws IOException {
		// 未安装SD卡时不做保存
		String storageState = Environment.getExternalStorageState();
		if(!storageState.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(context, "未检测到SD卡", Toast.LENGTH_SHORT).show();
			return;
		}
		

		File path = new File(FileTools.getImageFileDir());
		// 图片路径不存在创建之
		if (!path.exists()) {
			path.mkdirs();
		}
		// 图片文件如果不存在创建之
		File myCaptureFile = new File(path, fileName);
		if (!myCaptureFile.exists()) {
			myCaptureFile.createNewFile();
		}
		// 将图片压缩至文件对应的流里,即保存图片至该文件中
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}
	
	public static void saveBitmap(Bitmap bitmap, File file) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		int byteCount = bitmap.getByteCount();
		int quality = 100;
		if(byteCount > 800 * 1024){
			quality = 80;
		}else if(byteCount > 500 * 1024){
			quality = 90;
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
		bos.flush();
		bos.close();
	}
	
	
	/**
	 * 创建一条图片uri,用于保存拍照后的照片
	 */
//	private static Uri createImageUri(Context context) {
//		String name = "boreWbImg" + System.currentTimeMillis();
//		ContentValues values = new ContentValues();
//		values.put(MediaStore.Images.Media.TITLE, name);
//		values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
//		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//		Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//		return uri;
//	}
	
	/**
	 * 删除一条图片
	 */
//	public static void deleteImageUri(Context context, Uri uri) {
//		context.getContentResolver().delete(imageUriFromCamera, null, null);
//	}

	/**
	 * 获取图片文件路径
	 */
	public static String getImageAbsolutePath(Context context, Uri uri) {
		Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), uri, 
				new String[]{MediaStore.Images.Media.DATA});
		if(cursor.moveToFirst()) {
			return cursor.getString(0);
		}
		return null;
	}
	
	/////////////////////Android4.4以上版本特殊处理如下//////////////////////////////////////
	
	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * @param context
	 * @param imageUri
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getImageAbsolutePath19(Context context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} 
		
		// MediaStore (and general)
		if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	private static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}



	
}
