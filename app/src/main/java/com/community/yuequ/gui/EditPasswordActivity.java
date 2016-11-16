package com.community.yuequ.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.community.yuequ.util.Validator;
import com.community.yuequ.widget.DialogManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class EditPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "EditPasswordActivity";
    private Toolbar mToolbar;
    private TextView mTitleView;

    private EditText old_password;
    private EditText new_password;
    private EditText new_password_verify;
    private Button edit_button;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView .setText(R.string.edit_password);

        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        new_password_verify = (EditText) findViewById(R.id.new_password_verify);
        edit_button = (Button) findViewById(R.id.edit_button);
        edit_button.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_button:
                edit();
                break;
        }
    }

    private void edit() {

        String oldPassword= old_password.getText().toString();
        String newPassword = new_password.getText().toString();
        String password2 = new_password_verify.getText().toString();

        if(TextUtils.isEmpty(oldPassword)){
            Toast.makeText(this, "请输入原始密码", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(newPassword)){
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
        }else if(!Validator.isPassword(newPassword)){
            new_password.setError(getString(R.string.error_incorrect_password));

        }else if(!newPassword.equals(password2)){
            Toast.makeText(this, R.string.error_discord_password, Toast.LENGTH_SHORT).show();
        }else{
            requset(oldPassword,newPassword);
        }
    }

    private void requset(String old_password,String password ) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("old_password",old_password);
        hashMap.put("password", password);

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
                .url(Contants.URL_UPDATEPASSWORD)
                .content(content)
                .tag(TAG)
                .build()
                .execute(new EditCallBack(this));
    }


    public static class EditCallBack extends JsonCallBack<MessageBean> {
        private WeakReference<EditPasswordActivity> mWeakReference;

        public EditCallBack(EditPasswordActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            EditPasswordActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onError();
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            EditPasswordActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onBefore();
            }
        }

        @Override
        public void onResponse(MessageBean response, int id) {
            EditPasswordActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onResponse(response);
            }
        }
    }

    private void onResponse(MessageBean response) {
        if(mProgressDialog!=null)
            mProgressDialog.dismiss();
        if(response!=null&&response.errorCode==200){
            Toast.makeText(this, "密码修改成功！", Toast.LENGTH_LONG).show();
//            mSession.setUserInfo(response.result);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Contants.ACTION_EDIT_PASSOWRD));
            finish();

        }else{
            if(!TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "密码修改失败！", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void onBefore() {
        mProgressDialog = DialogManager.getProgressDialog(this,"修改密码...");
        mProgressDialog.show();
    }

    private void onError() {
        if(mProgressDialog!=null)
            mProgressDialog.dismiss();
        Toast.makeText(this, "密码修改失败！", Toast.LENGTH_SHORT).show();
    }
}
