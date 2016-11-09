package com.community.yuequ.gui;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.modle.RProgramDetail;
import com.community.yuequ.modle.RProgramDetailDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.player.WhtVideoView;
import com.community.yuequ.transformations.BlurTransformation;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.DateUtil;
import com.community.yuequ.verticaltablayout.TabAdapter;
import com.community.yuequ.verticaltablayout.VerticalTabLayout;
import com.community.yuequ.verticaltablayout.widget.QTabView;
import com.community.yuequ.view.PageStatuLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class LiveVideoActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = LiveVideoActivity.class.getSimpleName();
    private ActionBar mActionBar;
    private TextView mTitleView;
    private PageStatuLayout mStatuLayout;

    private WhtVideoView mVideoView;
    private ImageView iv_video_cover;
    private ImageView iv_play_cc;

    private TextView tv_live_pro;
    private ImageView iv_collect;
    private ImageView iv_share;
    private ImageView iv_load;
    private VerticalTabLayout mTabLayout;
    private ViewPager mViewPager;
    private Session mSession;
    private RProgram mRProgram;

    private RProgramDetail programDetail;
    private boolean isLoading = false;

    private boolean mCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);

        mSession = Session.get(this);
        Intent intent = getIntent();
        mRProgram = (RProgram) intent.getSerializableExtra("program");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if(mActionBar!=null){
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.color.transparent));
            mActionBar.setHomeAsUpIndicator(R.mipmap.video_back_img);
        }

        mCreated = true;

        mStatuLayout = new PageStatuLayout(this)
                .setReloadListener(this)
                .hide();
        mVideoView = (WhtVideoView) findViewById(R.id.whtvideoview);
        iv_video_cover = (ImageView) findViewById(R.id.iv_video_cover);
        iv_play_cc = (ImageView) findViewById(R.id.iv_play_cc);


        tv_live_pro = (TextView) findViewById(R.id.tv_live_pro);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_load = (ImageView) findViewById(R.id.iv_load);
        mTabLayout = (VerticalTabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);


        iv_play_cc.setOnClickListener(this);


        mVideoView.setVDVideoViewContainer((ViewGroup) mVideoView
                .getParent());


        if (mRProgram != null) {
            mTitleView.setText(mRProgram.name);

            Glide
                    .with(this)
                    .load(mRProgram.img_path)
                    .bitmapTransform(new BlurTransformation(this))
                    .into(iv_video_cover);


        }

        getData();


        mViewPager.setAdapter(new MyPagerAdapter());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_play_cc:
                if(programDetail==null){
                    Toast.makeText(LiveVideoActivity.this, "获取视频信息失败！", Toast.LENGTH_SHORT).show();
                }else{
                    //视频鉴权
//                    if("1".equals(programDetail.is_cost)){
//                        playAccess();
//                    }else{
//                        Intent intent = new Intent(this,VideoViewActivity.class);
//                        intent.putExtra("programDetail",programDetail);
//                        startActivity(intent);
//                    }

                    if(mActionBar!=null && mActionBar.isShowing()){
                        mActionBar.hide();
                    }
                    iv_video_cover.setVisibility(View.GONE);
                    iv_play_cc.setVisibility(View.GONE);
                    ArrayList<RProgramDetail> programDetails = new ArrayList<>();
                    programDetails.add(programDetail);
                    mVideoView.open(this, false, programDetails);
                    mVideoView.play(0);

                }
                break;
            case R.id.ll_status:
                if(!isLoading){
                    getData();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mCreated)
            return;
        if (mVideoView != null) {
            mVideoView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mCreated)
            return;
        if (mVideoView != null) {
            mVideoView.releaseSurface();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mCreated)
            return;

        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mVideoView.setIsFullScreen(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mVideoView.setIsFullScreen(false);
        }
    }

    @Override
    public void onBackPressed() {

        if (mVideoView != null && mVideoView.onBackPressed()) {

        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        if (mVideoView != null){
            mVideoView.releaseSurface();
            mVideoView.release(false);

        }
        super.onDestroy();
    }

    private void display() {
        if (programDetail != null) {
            mTitleView.setText(programDetail.name);

            Glide
                    .with(this)
                    .load(programDetail.img_path)
                    .bitmapTransform(new BlurTransformation(this))
                    .into(iv_video_cover);
        }
    }
    private void getData() {
        if(mRProgram==null)
            return;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("program_id", mRProgram.id);
        hashMap.put("order_date", DateUtil.getTodayDate());
        String content = "";
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
                .url(Contants.URL_LIVEPROGRAMORDERLIST)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<RProgramDetailDao>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        isLoading = false;

                        if (mStatuLayout != null) {
                            mStatuLayout.show()
                                    .setProgressBarVisibility(false)
                                    .setText(getString(R.string.load_data_fail));
                        }
                    }

                    @Override
                    public void onResponse(RProgramDetailDao response,int id) {
                        isLoading = false;
                        programDetail = response.result;
                        display();
                        if (mStatuLayout != null) {
                            if(response.errorCode!=Contants.HTTP_OK){
                                mStatuLayout.show()
                                        .setProgressBarVisibility(false)
                                        .setText(response.errorMessage);
                            }else{
                                mStatuLayout.hide()
                                        .setProgressBarVisibility(false)
                                        .setText(null);
                            }

                        }

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        isLoading = true;
                        mStatuLayout.show()
                                .setProgressBarVisibility(true)
                                .setText(null);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        isLoading = false;
                    }
                });
    }




    class MyPagerAdapter extends PagerAdapter implements TabAdapter {
        List<String> titles;

        {
            titles = new ArrayList<>();
            Collections.addAll(titles, "Android", "IOS", "Web", "JAVA", "C++",
                    ".NET", "JavaScript", "Swift", "PHP", "Python", "C#", "Groovy", "SQL", "Ruby");
        }

        public MyPagerAdapter() {

        }

        @Override
        public int getCount() {
            return 14;
        }

        @Override
        public int getBadge(int position) {
            return 0;
        }

        @Override
        public QTabView.TabIcon getIcon(int position) {

            return null;
        }

        @Override
        public QTabView.TabTitle getTitle(int position) {

            return new QTabView.TabTitle.Builder(LiveVideoActivity.this)
                    .setContent(titles.get(position))
                    .setTextColor(ContextCompat.getColor(LiveVideoActivity.this,R.color.white),ContextCompat.getColor(LiveVideoActivity.this,R.color.grey700))
                    .build();
        }

        @Override
        public int getBackground(int position) {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(LiveVideoActivity.this);
            tv.setText("" + position);
            container.addView(tv);
            return tv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }




}
