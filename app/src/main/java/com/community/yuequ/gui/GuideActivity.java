package com.community.yuequ.gui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.community.yuequ.R;
import com.community.yuequ.util.AndroidDevices;
import com.community.yuequ.util.AndroidUtil;

public class GuideActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private MyPagerAdapter mMyPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mViewPager = ((ViewPager)findViewById(R.id.viewpager));
        mMyPagerAdapter = new MyPagerAdapter(this);
        mViewPager.setAdapter(mMyPagerAdapter);

//        dimStatusBar(false);
    }

    /**
     * Dim the status bar and/or navigation icons when needed on Android 3.x.
     * Hide it on Android 4.0 and later
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void dimStatusBar(boolean dim) {
        if (!AndroidUtil.isHoneycombOrLater())
            return;
        int visibility = 0;
        int navbar = 0;

        if (!AndroidDevices.hasCombBar() && AndroidUtil.isJellyBeanOrLater()) {
            visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            navbar = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (dim) {
            navbar |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            if (!AndroidDevices.hasCombBar()) {
                navbar |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                if (AndroidUtil.isKitKatOrLater())
                    visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
                visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
        }
        if (!dim) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
        }

        if (AndroidDevices.hasNavBar())
            visibility |= navbar;
        getWindow().getDecorView().setSystemUiVisibility(visibility);
    }


    @Override
    public void onBackPressed() {

    }

    private static class MyPagerAdapter extends PagerAdapter{
        private static int imgResId[] = {R.mipmap.guide1,R.mipmap.guide2};
        private AppCompatActivity mActivity;

        public MyPagerAdapter(AppCompatActivity activity) {
            mActivity = activity;
        }

        @Override
        public int getCount() {
            return imgResId.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View view = inflater.inflate(R.layout.guide_item,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_img);
            final int index = position;
            Button button = (Button) view.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(index==1){
                        ((GuideActivity)mActivity).startMainActivity();
                    }else if(index==0){
                        ((GuideActivity)mActivity).goNextPage();
                    }
                }
            });

            imageView.setImageResource(imgResId[position]);

            container.addView(view);
            return view;
        }
    }

    private void goNextPage() {
        mViewPager.setCurrentItem(1,true);
    }

    protected void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
