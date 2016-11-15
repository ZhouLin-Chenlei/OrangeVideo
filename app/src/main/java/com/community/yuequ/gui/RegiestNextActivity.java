package com.community.yuequ.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.InitDao;
import com.community.yuequ.modle.UserInfoDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.AndroidDevices;
import com.community.yuequ.util.Validator;
import com.community.yuequ.widget.DialogManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class RegiestNextActivity extends AppCompatActivity implements View.OnClickListener{
    private final static String TAG = "RegiestNextActivity";
    private String phone;

    private Toolbar mToolbar;
    private TextView mTitleView;
    private Session mSession;

    private TextView mPhoneView;
    private EditText et_password;
    private EditText et_password_verify;
    private Button register_button;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiest_next);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mSession = Session.get(this);
        mTitleView.setText(getString(R.string.action_register_in));

        mPhoneView = (TextView) findViewById(R.id.tv_phone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password_verify = (EditText) findViewById(R.id.et_password_verify);
        register_button = (Button) findViewById(R.id.register_button);

        register_button.setOnClickListener(this);

        mPhoneView.setText(phone);




    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_button:
                register();
                break;

        }
    }

    private void register(){


        String password = et_password.getText().toString();
        String password2 = et_password_verify.getText().toString();

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
        }else if(!Validator.isPassword(password)){
            et_password.setError(getString(R.string.error_incorrect_password));

        }else if(!password.equals(password2)){
            Toast.makeText(this, R.string.error_discord_password, Toast.LENGTH_SHORT).show();
        }else{
            requset(phone,password);
        }

    }

    private void requset(String phone, String password) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("login_name",phone);
        hashMap.put("login_password", password);

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
                .url(Contants.URL_REGISTER)
                .content(content)
                .tag(TAG)
                .build()
                .execute(new RegisterCallBack(this));
    }

    public static class RegisterCallBack extends JsonCallBack<UserInfoDao> {
        private WeakReference<RegiestNextActivity> mWeakReference;

        public RegisterCallBack(RegiestNextActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            RegiestNextActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onError();
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            RegiestNextActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onBefore();
            }
        }

        @Override
        public void onResponse(UserInfoDao response, int id) {
            RegiestNextActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onResponse(response);
            }
        }
    }

    private void onResponse(UserInfoDao response) {
        if(mProgressDialog!=null)
            mProgressDialog.dismiss();
        if(response!=null&&response.result!=null){
            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
            mSession.setUserInfo(response.result);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Contants.ACTION_LOGIN));
            finish();

        }else{
            if(!TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "注册失败！", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void onBefore() {
        mProgressDialog = DialogManager.getProgressDialog(this,getString(R.string.register_wiat));
        mProgressDialog.show();
    }

    private void onError() {
        if(mProgressDialog!=null)
            mProgressDialog.dismiss();
        Toast.makeText(this, "注册失败！", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();

    }
}
