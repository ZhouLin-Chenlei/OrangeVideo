package com.community.yuequ.gui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.imple.DialogConfListener;
import com.community.yuequ.modle.InitMsg;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.AESUtil3;
import com.community.yuequ.util.DisplayUtil;
import com.community.yuequ.widget.WelComeTipsDialog;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,ViewPager.OnPageChangeListener,DialogConfListener {

    private ViewPager mViewPager;
    private SimpleFragmentPagerAdapter pagerAdapter;

    private RelativeLayout rl_recommend;
    private RelativeLayout rl_video;
    private RelativeLayout rl_channel;
    private RelativeLayout rl_tuwen;
    private long lastTime = 0;
    private MyHandler mMyHandler;
    private Session mSession;
    public static final int SHOW_WELCOME_DIALOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mViewPager = ((ViewPager) findViewById(R.id.viewpager));
        rl_recommend = ((RelativeLayout) findViewById(R.id.rl_recommend));
        rl_video = ((RelativeLayout) findViewById(R.id.rl_video));
        rl_channel = ((RelativeLayout) findViewById(R.id.rl_channel));
        rl_tuwen = ((RelativeLayout) findViewById(R.id.rl_tuwen));

        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(pagerAdapter);

        mViewPager.addOnPageChangeListener(this);
        rl_recommend.setOnClickListener(this);
        rl_video.setOnClickListener(this);
        rl_channel.setOnClickListener(this);
        rl_tuwen.setOnClickListener(this);

        mViewPager.setCurrentItem(0);
        rl_recommend.setSelected(true);
        mSession = Session.get(this);
        mMyHandler = new MyHandler(this);
        if(savedInstanceState==null){

            InitMsg initMsg = mSession.getInitMsg();
            if(initMsg!=null && initMsg.orderTips!=null && !initMsg.orderTips.isEmpty()){
                OrderTip orderTip = initMsg.orderTips.get(0);
                if(!TextUtils.isEmpty(orderTip.gust_tips)){
                    Message message = mMyHandler.obtainMessage(SHOW_WELCOME_DIALOG);
                    message.obj = orderTip.gust_tips;
                    mMyHandler.sendMessageDelayed(message,500);
                }
            }


        }
    }

    @Override
    public void conf() {

    }

    @Override
    public void cancel() {

    }


    private static class MyHandler extends Handler {
            private WeakReference<MainActivity> activityWeakReference;

            public MyHandler(MainActivity activity) {
                activityWeakReference = new WeakReference<>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                MainActivity activity = activityWeakReference.get();
                if (activity != null) {
                    switch (msg.what){
                        case SHOW_WELCOME_DIALOG:
                            activity.showWelcomeDialog(((String)msg.obj));
                            break;
                        default:
                            break;


                    }
                }
            }
    }

    private void showWelcomeDialog(String tips) {
        WelComeTipsDialog tipsDialog = WelComeTipsDialog.newInstance(tips);
        tipsDialog.show(getSupportFragmentManager(),"dialog");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_recommend:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rl_video:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.rl_channel:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.rl_tuwen:
                mViewPager.setCurrentItem(3);

                break;

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        rl_recommend.setSelected(position==0);
        rl_video.setSelected(position==1);
        rl_channel.setSelected(position==2);
        rl_tuwen.setSelected(position==3);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 4;

        private Context context;

        public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return new RecommendFragment();
            }else if(position==1){
                return new YQVideoFragment();
            }else if(position==2){
                return new ChannelFragment();
            }else {
                return new YQImageFragment();
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - lastTime < 2000) {
            super.onBackPressed();
        } else {
            lastTime = System.currentTimeMillis();
            Toast.makeText(YQApplication.getAppContext(), R.string.toast_exit_tip, Toast.LENGTH_SHORT).show();
        }
    }

}
