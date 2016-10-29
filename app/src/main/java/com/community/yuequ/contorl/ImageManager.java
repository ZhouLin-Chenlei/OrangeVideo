package com.community.yuequ.contorl;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.widget.GlideCircleTransform;

/**
 * Created by sunfusheng on 16/4/6.
 */
public class ImageManager {

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    private ImageManager() {}

    private static class ImageManagerHolder {
        private static ImageManager instance = new ImageManager();
    }

    public static ImageManager getInstance() {
        return ImageManagerHolder.instance;
    }

    // 将资源ID转为Uri
    public Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    // 加载网络图片
    public void loadUrlImage(Fragment myFragment, String url, ImageView imageView) {

        Glide
            .with(myFragment)
            .load(url)
            .centerCrop()
            .placeholder(R.mipmap.jiazai)
            .dontAnimate()
            .into(imageView);


    }
    // 加载网络图片
    public void loadUrlImage(Context context, String url, ImageView imageView) {
        Glide
                .with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.mipmap.jiazai)
                .dontAnimate()
                .into(imageView);

    }

    // 加载drawable图片
    public void loadResImage(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resourceIdToUri(context, resId))
                .placeholder(R.mipmap.jiazai)
//                .error(R.mipmap.qowu)
                .crossFade()
                .into(imageView);
    }

    // 加载本地图片
    public void loadLocalImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load("file://" + path)
                .placeholder(R.mipmap.jiazai)
//                .error(R.mipmap.qowu)
                .crossFade()
                .into(imageView);
    }

    // 加载网络圆型图片
    public void loadCircleImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.jiazai)
//                .error(R.mipmap.qowu)
                .crossFade()
                .transform(new GlideCircleTransform(context))
                .into(imageView);
    }

    // 加载drawable圆型图片
    public void loadCircleResImage(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resourceIdToUri(context, resId))
                .placeholder(R.mipmap.jiazai)
//                .error(R.mipmap.qowu)
                .crossFade()
                .transform(new GlideCircleTransform(context))
                .into(imageView);
    }

    // 加载本地圆型图片
    public void loadCircleLocalImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load("file://" + path)
                .placeholder(R.mipmap.jiazai)
//                .error(R.mipmap.qowu)
                .crossFade()
                .transform(new GlideCircleTransform(context))
                .into(imageView);
    }

}
