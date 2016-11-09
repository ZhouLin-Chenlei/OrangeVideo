package com.community.yuequ.gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.UserInfoDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.widget.DialogManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Request;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnCancelListener, Handler.Callback {
    private final static String TAG = RegisterActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TextView mTitleView;

    private EditText mEmailView;
    private EditText mPasswordView;
    private Session mSession;
    private ProgressDialog mProgressDialog;

    //此APPKEY仅供测试使用，且不定期失效，请到mob.com后台申请正式APPKEY
    private static String APPKEY = "f3fc6baa9ac4";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "7f3dedcb36d92deebcb373af921d635a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initSDK();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mSession = Session.get(this);
        mTitleView.setText(getString(R.string.action_register_in));
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        findViewById(R.id.email_register_button).setOnClickListener(this);
    }

    private void initSDK() {

        // 初始化短信SDK

        SMSSDK.initSDK(this, APPKEY, APPSECRET, true);


        final Handler handler = new Handler(this);

        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);

            }
        };

        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_register_button:
                register();
                break;

        }
    }

    private void register() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("login_name", email);
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
    }

    @Override
    public boolean handleMessage(Message msg) {
        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;

        if (result == SMSSDK.RESULT_COMPLETE) {
            //回调完成
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功

            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                //获取验证码成功

            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                //返回支持发送验证码的国家列表


            }
        } else {
            Throwable throwable = (Throwable) data;
            throwable.printStackTrace();
            Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    public static class RegisterCallBack extends JsonCallBack<UserInfoDao> {
        private WeakReference<RegisterActivity> mWeakReference;

        public RegisterCallBack(RegisterActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            RegisterActivity registerActivity = mWeakReference.get();
            if (registerActivity != null) {
                registerActivity.onError();
            }
        }

        @Override
        public void onResponse(UserInfoDao response, int id) {
            RegisterActivity registerActivity = mWeakReference.get();
            if (registerActivity != null) {
                registerActivity.onResponse(response);
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            RegisterActivity registerActivity = mWeakReference.get();
            if (registerActivity != null) {
                registerActivity.onBefore();
            }
        }
    }

    protected void onResponse(UserInfoDao response) {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
        mSession.setUserInfo(response.result);
    }

    protected void onBefore() {
        mProgressDialog = DialogManager.getProgressDialog(this, "注册中...", this);
        mProgressDialog.show();

    }

    protected void onError() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        // 销毁回调监听接口
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

