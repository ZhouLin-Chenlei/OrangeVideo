/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.community.yuequ;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.request.Request;
import com.community.yuequ.modle.InitMsg;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.modle.StartUpImg;
import com.community.yuequ.modle.UpgradeInfo;
import com.community.yuequ.util.FileTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;

/**
 * 
 * The Client Seesion Object for YueQu, contains some necessary
 * information.
 * 
 */
public class Session {

    /** Log tag */
    private final static String TAG = "Session";
    
    /** Application Context */
    private Context mContext;

    /** The mobile device screen size */
    private String screenSize;

    /** The version of OS */
    private int osVersion;

    /** The channel id */
    private String cid;

    /** The Application Version Code */
    private int versionCode;

    /** The Application package name */
    private String packageName;

    /** The Application version name */
    private String versionName;

    /** The Application version name */
    private String appName;

    /** The mobile IMEI code */
    private String imei;

    /** The mobile IMSI code */
    private String imsi;



    private String phoneNumber;
    
    /** The mobile mac address */
    private String macAddress;

    /**
     * The mobile model such as "Nexus One" Attention: some model type may have
     * illegal characters
     */
    private String model;

    /** The user-visible version string. E.g., "1.0" */
    private String buildVersion;
    private int screenWidth;
    private int screenHeight;



    /** Indicate whether new version is available */
    private boolean isUpdateAvailable;

    /** The new version name */
    private String updateVersionName;

    /** The new version code */
    private int updateVersionCode;

    /** The new version description */
    private String updateVersionDesc;

    /** The new version update uri */
    private String updateUri;

    /** The new version update level(Force Update/Option Update) */
    private int updateLevel;
    
    /** The new version APK download task id*/
    private long updateId;


    /** The apps update check time */
    private long updataCheckTime;

    /** The current version */
    private int lastVersion;

    /** 上次更新splash的时间戳 */
    private long splashTime;

    /** 上次更新splash的id */
    private long splashId;

    /** The singleton instance */
    private static Session mInstance;

    private InitMsg mInitMsg;

    /**
     * default constructor
     * @param context
     */
    private Session(Context context) {
        
        synchronized (this) {
            mContext = context;

            osVersion = Build.VERSION.SDK_INT;
            buildVersion = Build.VERSION.RELEASE;
            try {
                model = URLEncoder.encode(Build.MODEL, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }

        }
    }
    


    public static Session get(Context context) {
        if (mInstance == null) {
            mInstance = new Session(context.getApplicationContext());
        }
        return mInstance;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Activity activity) {

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        this.screenSize = screenWidth < screenHeight ? screenWidth + "*" + screenHeight : screenHeight + "*" + screenWidth;

    }

    public int getOsVersion() {
        return osVersion;
    }

    public void getApplicationInfo() {

        final PackageManager pm = (PackageManager) mContext.getPackageManager();
        try {
            final PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;

            final ApplicationInfo ai = pm.getApplicationInfo(mContext.getPackageName(),
                    PackageManager.GET_META_DATA);
            cid = ai.metaData.get("YUEQU_CHANNEL").toString();

            appName = String.valueOf(ai.loadLabel(pm));
            packageName = mContext.getPackageName();

            TelephonyManager telMgr = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telMgr.getDeviceId();
            phoneNumber = telMgr.getLine1Number();
            if(phoneNumber!=null && phoneNumber.startsWith("+86")){
                phoneNumber = phoneNumber.substring(3);
            }
            imsi = telMgr.getSubscriberId();
        } catch (NameNotFoundException e) {
            Log.d(TAG, "met some error when get application info");
        }
    }

    public String getCid() {
        if (TextUtils.isEmpty(cid)) {
            getApplicationInfo();
        }
        return cid;
    }


    public String getVersionName() {
        if (TextUtils.isEmpty(versionName)) {
            getApplicationInfo();
        }
        return versionName;
    }

    public int getVersionCode() {
        if (versionCode <= 0) {
            getApplicationInfo();
        }
        return versionCode;
    }

    public String getIMEI() {
        if (TextUtils.isEmpty(imei)) {
            getApplicationInfo();
        }
        return imei;
    }

    public String getPackageName() {
        if (TextUtils.isEmpty(packageName)) {
            getApplicationInfo();
        }
        return packageName;
    }

    public String getImsi() {
        if (TextUtils.isEmpty(imsi)) {
            getApplicationInfo();
        }
        if(imsi ==null){
            imsi = "";
        }
        return imsi;
    }

    public String getMac() {
        if (TextUtils.isEmpty(macAddress)) {
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }



    public String getAppName() {
        return appName;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }
    

    public String getUpdateVersionName() {
        return updateVersionName;
    }

    public int getUpdateVersionCode() {
        return updateVersionCode;
    }

    public String getUpdateVersionDesc() {
        return updateVersionDesc;
    }

    public String getUpdateUri() {
        return updateUri;
    }

    public int getUpdateLevel() {
        return updateLevel;
    }

    public void setUpdateInfo(String versionName, int versionCode, String description, String url,
            int level) {
        
        this.isUpdateAvailable = true;
        this.updateVersionName = versionName;
        this.updateVersionCode = versionCode;
        this.updateVersionDesc = description;
        this.updateUri = url;
        this.updateLevel = level;

    }

    public long getUpdateId() {
        return updateId;
    }

    public void setUpdateID(long updateId) {
        
        if(this.updateId == updateId) {
            return;
        }
        this.updateId = updateId;

    }

    public String getModel() {
        if(model==null){
            model = "";
        }
        return model;
    }

    public String getBuildVersion() {
        return buildVersion;
    }


    public long getUpdataCheckTime() {
        return updataCheckTime;
    }

    public void setUpdataCheckTime(long updataCheckTime) {

        // there is no need to update for [same] value
        if (this.updataCheckTime == updataCheckTime) {
            return;
        }
        this.updataCheckTime = updataCheckTime;

    }


    public void close() {
        mInstance = null;
    }

    public long getSplashTime() {
        return splashTime;
    }

    public void setSplashTime(long splashTime) {
        this.splashTime = splashTime;

    }
    
    public int getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(int currentVersion) {

        if (currentVersion == this.lastVersion) {
            return;
        }
        clearData();
        this.lastVersion = currentVersion;

    }
    
    /**
     * 清除上一个版本数据
     */
    public void clearData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        sp.edit().clear().commit();
    }

    public long getSplashId() {
        return splashId;
    }

    public void setSplashId(long splashId) {
        this.splashId = splashId;

    }


    public int getScreenHeight() {
        return screenHeight;
    }
    public int getScreenWidth() {
        return screenWidth;
    }
    public String getPhoneNumber() {
        if (phoneNumber==null) {
            phoneNumber = "";
        }
        return phoneNumber;
    }

    public void setInitMsg(InitMsg msg) {
       this.mInitMsg = msg;
        if(mInitMsg!=null && mInitMsg.startupImages!=null){
            saveStartUpImg(mInitMsg.startupImages);

        }
    }
    public InitMsg getInitMsg() {
        return mInitMsg;
    }

    public boolean haveOrderTips() {
        return mInitMsg!=null && mInitMsg.orderTips!=null && !mInitMsg.orderTips.isEmpty();
    }

    public boolean isShowUpgradeDialog() {
        boolean show = false;
        if(mInitMsg!=null){
            UpgradeInfo upgradeInfo = mInitMsg.upgrade;
            if(upgradeInfo != null && versionCode < upgradeInfo.version && !TextUtils.isEmpty(upgradeInfo.app_path)){
                show = true;
            }
        }
        return show;
    }

    public ArrayList<OrderTip> getOrderTips() {
        if(mInitMsg!=null){
           return mInitMsg.orderTips;
        }
        return null;
    }

    public void setOrderTips(ArrayList<OrderTip> tips) {
        if(mInitMsg!=null){
             mInitMsg.orderTips = tips;
        }

    }

    public void saveStartUpImg(final StartUpImg upImg) {
        if(upImg != null){
            long currentTimeMillis = System.currentTimeMillis();
            if(!TextUtils.isEmpty(upImg.img_path) && currentTimeMillis >= upImg.begin_time && currentTimeMillis < upImg.end_time) {

                final File localPath = FileTools.getImageLocalPath(upImg.img_path);
                if(localPath==null || !localPath.exists()){
                    OkHttpUtils
                            .get()
                            .url(upImg.img_path)
                            .build()//
                            .execute(new FileCallBack(FileTools.getImageFileDir(),String.valueOf(upImg.img_path.hashCode()))
                            {
                                @Override
                                public void onError(Call call, Exception e,int id) {

                                }

                                @Override
                                public void onResponse(File response,int id) {

                                }

                                @Override
                                public void inProgress(float progress, long total,int id) {

                                }
                            });
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("img_path", upImg.img_path);
                edit.putLong("begin_time", upImg.begin_time);
                edit.putLong("end_time", upImg.end_time);
                edit.apply();
            }
        }
    }
}