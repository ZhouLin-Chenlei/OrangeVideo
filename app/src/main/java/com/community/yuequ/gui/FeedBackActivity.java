package com.community.yuequ.gui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.MessageBean;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.AndroidUtil;
import com.community.yuequ.util.Validator;
import com.community.yuequ.widget.DialogManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class FeedBackActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "FeedBackActivity";
    private Toolbar mToolbar;
    private TextView mTitleView;
    private EditText et_phone;
    private EditText et_desc;
    private Button feedback_button;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView.setText(R.string.feedback);


        et_phone = (EditText) findViewById(R.id.et_phone);
        et_desc = (EditText) findViewById(R.id.et_desc);
        feedback_button = (Button) findViewById(R.id.feedback_button);
        feedback_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.feedback_button:
                AndroidUtil.hideSoftInput(this);
                feedback();
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
    private void feedback() {
        // Store values at the time of the login attempt.
        String phone = et_phone.getText().toString();
        String desc = et_desc.getText().toString();


        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, R.string.input_phone, Toast.LENGTH_SHORT).show();
        }else if(!Validator.isMobile(phone)){
            et_phone.setError(getString(R.string.txt_phonenumber_erro));

        }else if(TextUtils.isEmpty(desc)){
            Toast.makeText(this, R.string.feedback_describe, Toast.LENGTH_SHORT).show();
        }else{
            send(phone,desc);
        }
    }

    private void send(String phone, String desc) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("smark",desc);
        hashMap.put("user_phone", phone);


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
                .url(Contants.URL_ADDFEEDBACK)
                .content(content)
                .tag(TAG)
                .build()
                .execute(new FeedBackCallBack(this));
    }


    public static class FeedBackCallBack extends JsonCallBack<MessageBean> {
        private WeakReference<FeedBackActivity> mWeakReference;

        public FeedBackCallBack(FeedBackActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            FeedBackActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onError();
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            FeedBackActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onBefore();
            }
        }

        @Override
        public void onResponse(MessageBean response, int id) {
            FeedBackActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onResponse(response);
            }
        }
    }

    private void onResponse(MessageBean response) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }

        if(response!=null && response.errorCode==200){
            Toast.makeText(this, "发送成功！", Toast.LENGTH_SHORT).show();
            finish();

        }else{
            if(response!=null && !TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "发送失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onBefore() {
        mProgressDialog = DialogManager.getProgressDialog(this,"发送中...");
        mProgressDialog.show();

    }

    private void onError() {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        Toast.makeText(this, "发送失败！", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();

    }
}
