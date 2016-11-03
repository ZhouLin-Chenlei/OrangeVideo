package com.community.yuequ.util;

import android.app.Activity;

import com.community.yuequ.R;

/**
 * Created by iiro on 7.6.2016.
 */
public class TabMessage {
    public static String get(Activity activity,int menuItemId) {
        String message = activity.getString(R.string.main_tab_home);

        switch (menuItemId) {
            case R.id.tab_home:
                message = activity.getString(R.string.main_tab_home);
                break;
            case R.id.tab_online:
                message = activity.getString(R.string.main_tab_online);
                break;
            case R.id.tab_videos:
                message = activity.getString(R.string.main_tab_videos);
                break;
            case R.id.tab_myprofile:
                message = activity.getString(R.string.main_tab_myprofile);
                break;

        }

        return message;
    }
}
