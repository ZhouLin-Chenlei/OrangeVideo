package com.community.yuequ.modle.callback;

import com.community.yuequ.util.AESUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;

import okhttp3.Response;

/**
 *
 */
public abstract class JsonCallBack<T> extends Callback<T> {

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Class<T> clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        String string = response.body().string();
        String result = AESUtil.decrypt(string);
        T obj = new Gson().fromJson(result, clazz);
        return obj;
    }
}
