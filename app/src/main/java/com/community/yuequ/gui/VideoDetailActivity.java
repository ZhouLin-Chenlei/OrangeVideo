package com.community.yuequ.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.imple.ActionBarShowHideListener;
import com.community.yuequ.imple.DialogConfListener;
import com.community.yuequ.imple.PlayData;
import com.community.yuequ.modle.MessageBean;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.modle.OrderTipsDao;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.modle.RProgramDetail;
import com.community.yuequ.modle.RProgramDetailDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.player.VideoViewActivity;
import com.community.yuequ.player.WhtVideoView;
import com.community.yuequ.provider.History;
import com.community.yuequ.share.ShareFragmentDialog;
import com.community.yuequ.share.ShareHelper;
import com.community.yuequ.transformations.BlurTransformation;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.PlayCountHelper;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.widget.DialogManager;
import com.community.yuequ.widget.GoChargDialog;
import com.community.yuequ.widget.InputPhoneNumberDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class VideoDetailActivity extends AppCompatActivity implements View.OnClickListener,ActionBarShowHideListener,ShareFragmentDialog.ShareListener{
    public static final String TAG = VideoDetailActivity.class.getSimpleName();

    private ActionBar mActionBar;
    private TextView mTitleView;
    private PageStatuLayout mStatuLayout;

    private WhtVideoView mVideoView;
    private ImageView iv_video_cover;
    private ImageButton iv_play_cc;
    private TextView tv_detail;

    private ImageView iv_collect;
    private ImageView iv_share;

    private RProgram mRProgram;
    private Session mSession;
    private RProgramDetail programDetail;
    private boolean isLoading = false;

    private boolean mCreated = false;

    private ProgressDialog mProgressDialog;
    private ShareFragmentDialog shareFragmentDialog;
    private ShareHelper mShareHelper;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mShareHelper.onNewIntent(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        mSession = Session.get(this);
        mShareHelper = new ShareHelper(this,savedInstanceState);
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
        iv_play_cc = (ImageButton) findViewById(R.id.iv_play_cc);
        tv_detail = (TextView) findViewById(R.id.tv_detail);

        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        iv_share = (ImageView) findViewById(R.id.iv_share);

        iv_play_cc.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        iv_share.setOnClickListener(this);

        mVideoView.setVDVideoViewContainer((ViewGroup) mVideoView
                .getParent());

        if (mRProgram != null) {
            mTitleView.setText(mRProgram.name);

            tv_detail.setText(mRProgram.remark);
            setCollectButton("true".equalsIgnoreCase(mRProgram.isCollection));
//            ImageManager.getInstance().loadUrlImage(this, mRProgram.img_path, iv_video_cover);

            Glide
                .with(this)
                .load(mRProgram.img_path)
                .bitmapTransform(new BlurTransformation(this))
                .into(iv_video_cover);

            YQApplication.runBackground(new Runnable() {
                @Override
                public void run() {
                    History.deleteHistory(VideoDetailActivity.this,mRProgram.id);
                    History.addHistory(VideoDetailActivity.this,mRProgram);
                }
            });

        }
        getData();
    }


    private void display() {
        if (programDetail != null) {
            mTitleView.setText(programDetail.name);

            tv_detail.setText(programDetail.remark);
//            ImageManager.getInstance().loadUrlImage(this, programDetail.img_path, iv_video_cover);

            Glide
                    .with(this)
                    .load(programDetail.img_path)
                    .bitmapTransform(new BlurTransformation(this))
                    .into(iv_video_cover);

        }
    }


    private void getData() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("program_id", mRProgram.id);
        hashMap.put("imei", mSession.getIMEI());
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
                .url(Contants.URL_PROGRAMDETAIL)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<RProgramDetailDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
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
                    public void onBefore(Request request,int id) {
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_play_cc:
                if(programDetail==null){
                    Toast.makeText(VideoDetailActivity.this, "获取视频信息失败！", Toast.LENGTH_SHORT).show();
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
                    ArrayList<PlayData> programDetails = new ArrayList<>();
                    programDetails.add(programDetail);
                    mVideoView.open(this, false, programDetails);
                    mVideoView.play(0);
                    //统计播放次数
                    PlayCountHelper.count(null,programDetail.id);
                }
                break;
            case R.id.ll_status:
                if(!isLoading){
                    getData();
                }
                break;
            case R.id.iv_collect:

                if(mSession.isLogin()){
                    boolean collect = false;

                    if(mRProgram!=null){
                        collect = !"true".equalsIgnoreCase(mRProgram.isCollection);
                    }
                    collect(collect);

                }else{
//                    Toast.makeText(this, "请先登录后再操作！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this,LoginActivity.class));
                }
                break;
            case R.id.iv_share:

                shareFragmentDialog = ShareFragmentDialog.newInstance();
                shareFragmentDialog.show(getSupportFragmentManager(),"dialog");


                break;
            default:
                break;
        }
    }

    /**
     *
     * @param collect :true表示执行收藏操作,false取消收藏
     */
    private void collect(boolean collect) {
        if(mRProgram==null)

            return;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("program_id", mRProgram.id);

        String url;
        if(collect){
            url = Contants.URL_COLLECTPROGRAM;
//            hashMap.put("type", "1");//1：视频，2：图文，3：直播
        }else{
            url = Contants.URL_DELCOLLECTPROGRAM;
        }


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
                .tag(TAG)
                .build()
                .execute(new CollectCallback(this,collect));
    }

    @Override
    public void share(int code) {
        if(code==ShareFragmentDialog.SHARE_SINA){
            mShareHelper.sendMessage(getString(R.string.app_name)+"视频["+mRProgram.name+"]:"+mRProgram.img_path);

        }else if(code ==ShareFragmentDialog.SHARE_WEIXIN){
            mShareHelper.sendTextToWX(false,getString(R.string.app_name)+"视频["+mRProgram.name+"]:"+mRProgram.img_path);
        }else{
            mShareHelper.sendTextToWX(true,getString(R.string.app_name)+"视频["+mRProgram.name+"]:"+mRProgram.img_path);
        }
    }


    public static class CollectCallback extends JsonCallBack<MessageBean> {
        private WeakReference<VideoDetailActivity> mWeakReference;
        private final boolean mCollect;

        public CollectCallback(VideoDetailActivity activity, boolean collect) {
            mWeakReference = new WeakReference<>(activity);
            this.mCollect = collect;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            VideoDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onCollectError(mCollect);
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            VideoDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onCollectBefore(mCollect);
            }
        }

        @Override
        public void onResponse(MessageBean response, int id) {
            VideoDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onCollectResponse(mCollect,response);
            }
        }
    }

    private void onCollectError(boolean collect) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        Toast.makeText(this, collect ?"收藏失败！":"取消收藏失败！", Toast.LENGTH_SHORT).show();
    }

    private void onCollectBefore(boolean collect) {
        String message = "收藏中...";
        if(!collect){
            message = "取消收藏...";
        }
        mProgressDialog = DialogManager.getProgressDialog(this,message);
        mProgressDialog.show();
    }

    private void onCollectResponse(boolean collect,MessageBean response) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }

        if(response.errorCode==200){
            Toast.makeText(this, collect ? "收藏成功！" : "取消收藏！", Toast.LENGTH_SHORT).show();
            if(mRProgram!=null){
                mRProgram.isCollection = String.valueOf(collect);
                setCollectButton(collect);

                //跟新列表数据
                Intent intent = new Intent();
                intent.putExtra("id",mRProgram.id);
                intent.putExtra("isCollection",mRProgram.isCollection);
                setResult(18,intent);
            }
        }else{
            if(!TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, collect ?"收藏失败！":"取消收藏失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void  setCollectButton(boolean collect){

        if(collect){
            iv_collect.setImageResource(R.mipmap.icon_collect);
        }else{
            iv_collect.setImageResource(R.drawable.btn_collect_selector);
        }
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

    @Override
    public void showActionBar() {
        if(mActionBar!=null && !mActionBar.isShowing()){
            mActionBar.show();
        }
    }

    @Override
    public void hideActionBar() {
        if(mActionBar!=null && mActionBar.isShowing()){
            mActionBar.hide();
        }
    }
}
