package com.community.yuequ.util;

import android.text.TextUtils;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.MessageBean;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/11/23.
 */

public class PlayCountHelper {

    public interface PlayCountListener{
        void onCountError();
        void onCountBefore();
        void onCountResponse(MessageBean response);
    }

    /**
     * 统计播放次数
     * @param listener
     * @param programId 节目id
     */
    public static void count(PlayCountListener listener, int programId){

        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("program_id", programId);
        String url = Contants.URL_PLAYCNT;

        String content;
        try {
            content = AESUtil.encode(new Gson().toJson(hashMap));
        } catch (Exception e) {
            throw new RuntimeException("加密错误！");
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(YQApplication.getAppContext(), R.string.unknow_erro, Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpUtils
                .postString()
                .content(content)
                .url(url)
                .tag(listener.getClass().getSimpleName())
                .build()
                .execute(new PlayCountCallback(listener));

    }


    public static class PlayCountCallback extends JsonCallBack<MessageBean> {
        private WeakReference<PlayCountListener> mWeakReference;

        public PlayCountCallback(PlayCountListener activity) {
            mWeakReference = new WeakReference<>(activity);

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            PlayCountListener activity = mWeakReference.get();
            if (activity != null) {
                activity.onCountError();
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            PlayCountListener activity = mWeakReference.get();
            if (activity != null) {
                activity.onCountBefore();
            }
        }

        @Override
        public void onResponse(MessageBean response, int id) {
            PlayCountListener activity = mWeakReference.get();
            if (activity != null) {
                activity.onCountResponse(response);
            }
        }
    }
}
