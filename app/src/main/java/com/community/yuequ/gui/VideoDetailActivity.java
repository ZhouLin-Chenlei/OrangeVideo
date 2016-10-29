package com.community.yuequ.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.imple.DialogConfListener;
import com.community.yuequ.modle.MessageBean;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.modle.OrderTipsDao;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.modle.RProgramDetail;
import com.community.yuequ.modle.RProgramDetailDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.player.VideoViewActivity;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.TitleBarLayout;
import com.community.yuequ.widget.GoChargDialog;
import com.community.yuequ.widget.InputPhoneNumberDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class VideoDetailActivity extends AppCompatActivity implements View.OnClickListener,InputPhoneNumberDialog.PhoneNumberCallBack,DialogConfListener {
    public static final String TAG = VideoDetailActivity.class.getSimpleName();

    private TitleBarLayout mTitleBarLayout;
    private PageStatuLayout mStatuLayout;
    private ImageView iv_img;
    private TextView tv_dec;
    private Button btn_play;
    private TextView tv_detail;
    private RProgram mRProgram;
    private Session mSession;
    private RProgramDetail programDetail;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        mSession = Session.get(this);
        Intent intent = getIntent();
        mRProgram = (RProgram) intent.getSerializableExtra("program");
        mTitleBarLayout = new TitleBarLayout(this)
                .setLeftButtonVisibility(true)
                .setLeftButtonClickListener(this);
        mStatuLayout = new PageStatuLayout(this)
                .setReloadListener(this)
                .hide();
        iv_img = (ImageView) findViewById(R.id.iv_img);
        tv_dec = (TextView) findViewById(R.id.tv_dec);
        btn_play = (Button) findViewById(R.id.btn_play);
        tv_detail = (TextView) findViewById(R.id.tv_detail);

        btn_play.setOnClickListener(this);

        if (mRProgram != null) {
            mTitleBarLayout.setText(mRProgram.name);
            tv_dec.setText(mRProgram.name);
            tv_detail.setText(mRProgram.remark);
            ImageManager.getInstance().loadUrlImage(this, mRProgram.img_path, iv_img);
        }
        getData();
    }


    private void display() {
        if (programDetail != null) {
            mTitleBarLayout.setText(programDetail.name);
            tv_dec.setText(programDetail.name);
            tv_detail.setText(programDetail.remark);
            ImageManager.getInstance().loadUrlImage(this, programDetail.img_path, iv_img);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_play:
                if(programDetail==null){
                    Toast.makeText(VideoDetailActivity.this, "获取视频信息失败！", Toast.LENGTH_SHORT).show();
                }else{
                    //视频鉴权
                    if("1".equals(programDetail.is_cost)){
                        playAccess();
                    }else{
                        Intent intent = new Intent(this,VideoViewActivity.class);
                        intent.putExtra("programDetail",programDetail);
                        startActivity(intent);
                    }
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

    private void playAccess(){

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
                .url(Contants.URL_PLAYACCESS)
                .tag(TAG)
                .build()
                .execute(new MyPlayAccessCallback(this));

    }
    private void toBuyStep2() {

        if(mSession.haveOrderTips()){
            goPayPage(mSession.getOrderTips());
        }else{
            OkHttpUtils
                    .post()
                    .url(Contants.URL_ORDERTIPS)
                    .tag(TAG)
                    .build()
                    .execute(new MyOrderTipsCallBack(this));
        }
    }
    protected static class MyOrderTipsCallBack extends JsonCallBack<OrderTipsDao> {
        private WeakReference<VideoDetailActivity> mWeakReference;
        public MyOrderTipsCallBack(VideoDetailActivity activity){
            mWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void onError(Call call, Exception e,int id) {
            VideoDetailActivity activity = mWeakReference.get();
            if(activity!=null){
                Toast.makeText(activity, "获取信息失败！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onResponse(OrderTipsDao response,int id) {
            VideoDetailActivity activity = mWeakReference.get();
            if(activity!=null){
                if(response.errorCode==Contants.HTTP_OK){
                    activity.setOrderTips(response.result);
                }else{
                    Toast.makeText(activity, "获取信息失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void setOrderTips(ArrayList<OrderTip> result) {
        if(result!=null && !result.isEmpty()){
            mSession.setOrderTips(result);
            goPayPage(result);
        }else{
            Toast.makeText(YQApplication.getAppContext(), "计费信息获取失败，请重试！", Toast.LENGTH_SHORT).show();
        }
    }
    private void goPayPage(ArrayList<OrderTip> result){
        Intent intent = new Intent(this,PayListActivity.class);
        intent.putExtra("programId",mRProgram.id);
        intent.putParcelableArrayListExtra("ordertips",result);
        startActivity(intent);
    }
    @Override
    public void conf() {
        toBuy();
    }

    @Override
    public void cancel() {
        finish();
    }
    private void toBuy() {
        String phoneNumber = mSession.getPhoneNumber();
        if(TextUtils.isEmpty(phoneNumber)){
            InputPhoneNumberDialog phoneNumberDialog = InputPhoneNumberDialog.newInstance();
            phoneNumberDialog.show(getSupportFragmentManager(),"phoneNumber");
        }else{
            toBuyStep2();

        }
    }
    @Override
    public void phoneNumber(String phoneNumber) {
        setphoneNumber(phoneNumber);
    }
    private void setphoneNumber(String phoneNumber) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("tel",phoneNumber);
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
                .url(Contants.URL_UPDATEUSER)
                .tag(TAG)
                .build()
                .execute(new MyUpdateUserCallBack(this));
    }
    private static class MyUpdateUserCallBack extends JsonCallBack<MessageBean> {
        private WeakReference<VideoDetailActivity> mWeakReference;

        public MyUpdateUserCallBack(VideoDetailActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e,int id) {
            VideoDetailActivity activity = mWeakReference.get();
            if(activity!=null){
                Toast.makeText(activity, "设置手机号错误！", Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onResponse(MessageBean response,int id) {
            if(response.errorCode==Contants.HTTP_OK){
                VideoDetailActivity activity = mWeakReference.get();
                if(activity!=null){
                    activity.toBuyStep2();
                }
            }else{
                Toast.makeText(YQApplication.getAppContext(), "设置手机号错误！", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onBefore(Request request,int id) {
            VideoDetailActivity activity = mWeakReference.get();
            if(activity!=null){

            }
        }
    }
    public static class MyPlayAccessCallback extends JsonCallBack<MessageBean> {
        private WeakReference<VideoDetailActivity> mWeakReference;
        public MyPlayAccessCallback(VideoDetailActivity activity){
            mWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void onError(Call call, Exception e,int id) {
            Toast.makeText(YQApplication.getAppContext(), R.string.load_data_fail, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(MessageBean response,int id) {
            VideoDetailActivity activity = mWeakReference.get();
            if(activity!=null){

                activity.PlayAccess(response);
            }
        }
    }

    private void PlayAccess(MessageBean response) {
        if(response.errorCode==Contants.HTTP_NO||response.errorCode==Contants.HTTP_NO_PERMISSION){
            GoChargDialog chargDialog = GoChargDialog.newInstance();
            chargDialog.show(getSupportFragmentManager(),"charg");

        }else if(response.errorCode==Contants.HTTP_VIP||response.errorCode==Contants.HTTP_ONECE){
            Intent intent = new Intent(this,VideoViewActivity.class);
            intent.putExtra("programDetail",programDetail);
            startActivity(intent);
        }else{
            Toast.makeText(YQApplication.getAppContext(), "数据获取异常！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();
    }
}
