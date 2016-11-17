package com.community.yuequ.gui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.UserInfoDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.Validator;
import com.community.yuequ.widget.DialogManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import okhttp3.Request;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener{
    private static final String TAG = "LoginActivity";
    // UI references.
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private TextView tv_register_account;
    private TextView tv_forget_password;
    private Button mSignInButton;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerReceiver();
        mSession = Session.get(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.color.white));
            actionBar.setHomeAsUpIndicator(R.mipmap.login_page_back);
        }

        mPhoneView = (AutoCompleteTextView) findViewById(R.id.atv_phone);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tv_register_account = (TextView) findViewById(R.id.tv_register_account);
        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);

        tv_register_account.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.ACTION_LOGIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,filter);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Contants.ACTION_LOGIN.equals(action)){
                finish();

            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();


        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, R.string.input_phone, Toast.LENGTH_SHORT).show();
        }else if(!Validator.isMobile(phone)){
            mPhoneView.setError(getString(R.string.txt_phonenumber_erro));

        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, R.string.prompt_password, Toast.LENGTH_SHORT).show();
        }else{
            login(phone,password);
        }

    }


        private void login(String phone, String password) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("login_name",phone);
            hashMap.put("login_password", password);
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
                    .url(Contants.URL_LOGIN)
                    .content(content)
                    .tag(TAG)
                    .build()
                    .execute(new LoginCallBack(this));
        }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                attemptLogin();
                break;
            case R.id.tv_register_account:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
            case R.id.tv_forget_password:
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
                break;

        }
    }


    public static class LoginCallBack extends JsonCallBack<UserInfoDao> {
        private WeakReference<LoginActivity> mWeakReference;

        public LoginCallBack(LoginActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            LoginActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onError();
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            LoginActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onBefore();
            }
        }

        @Override
        public void onResponse(UserInfoDao response, int id) {
            LoginActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onResponse(response);
            }
        }
    }

    private void onResponse(UserInfoDao response) {
        showProgress(false);
        mSignInButton.setEnabled(true);
        if(response!=null && response.result!=null){
            Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
            mSession.setUserInfo(response.result);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Contants.ACTION_LOGIN));
            finish();

        }else{
            if(response!=null && !TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "登录失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onBefore() {
        showProgress(true);
        mSignInButton.setEnabled(false);
    }

    private void onError() {
        showProgress(false);
        mSignInButton.setEnabled(true);
        Toast.makeText(this, "登录失败！", Toast.LENGTH_SHORT).show();
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
}

