package com.community.yuequ.gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
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
import com.community.yuequ.util.Validator;
import com.community.yuequ.widget.DialogManager;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnCancelListener, Handler.Callback {
    private final static String TAG = ResetPasswordActivity.class.getSimpleName();

    public static final long millisInFuture = 60*1000;
    public static final long countDownInterval = 1*1000;


    private Toolbar mToolbar;
    private TextView mTitleView;

    private EditText mPhoneView;
    private EditText mCodeView;
    private Button mButtonNext;
    private Button mButtonCode;


    private ProgressDialog mProgressDialog;




    private boolean countDown = false;
    private String phone;
    protected MyTimer myTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initSDK();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mTitleView.setText(getString(R.string.find_password));
        mPhoneView = (EditText) findViewById(R.id.et_phone);

        mCodeView = (EditText) findViewById(R.id.et_code);
        mButtonNext = (Button)findViewById(R.id.next_register_button);
        mButtonNext.setOnClickListener(this);
        mButtonCode = (Button) findViewById(R.id.code_register_button);
        mButtonCode.setOnClickListener(this);
    }

    private void initSDK() {

        // 初始化短信SDK

        SMSSDK.initSDK(this, Contants.APPKEY, Contants.APPSECRET, false);


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
            case R.id.next_register_button:
                next();
                break;
            case R.id.code_register_button:

                if(countDown){


                }else{
                    phone =  mPhoneView.getText().toString();
                    if(TextUtils.isEmpty(phone)){
                        Toast.makeText(this,  R.string.input_phone, Toast.LENGTH_SHORT).show();
                    }else if(!Validator.isMobile(phone)){
                        mPhoneView.setError(getString(R.string.txt_phonenumber_erro));
                    }else{

                        mProgressDialog = DialogManager.getProgressDialog(this, "获取验证码...", this);
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();
                        mButtonCode.setEnabled(false);
                        SMSSDK.getVerificationCode(Contants.COUNTRY_CODE, phone.trim(), null);
                    }
                }

                break;

        }
    }

    private void next() {

        // Reset errors.
        mPhoneView.setError(null);
        mCodeView.setError(null);

        phone = mPhoneView.getText().toString();
        String code = mCodeView.getText().toString();


                        Intent intent = new Intent(this,ResetPasswordNextActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("result",true);
                        startActivity(intent);
                        finish();


//        if(TextUtils.isEmpty(phone)){
//            Toast.makeText(this, R.string.input_phone, Toast.LENGTH_SHORT).show();
//        }else if(!Validator.isMobile(phone)){
//            mPhoneView.setError(getString(R.string.txt_phonenumber_erro));
//
//        }else if(TextUtils.isEmpty(code)){
//            Toast.makeText(this, R.string.input_code, Toast.LENGTH_SHORT).show();
//        }else{
//            smsVerifyRequset(phone,code);
//        }
    }

    private void smsVerifyRequset(String phone, String verificationCode) {

        mProgressDialog = DialogManager.getProgressDialog(this, "校验验证码...", this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        SMSSDK.submitVerificationCode(Contants.COUNTRY_CODE, phone, verificationCode);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;

        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
            /** 提交验证码 */
            afterSubmit(result, data);
        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
            /** 获取验证码成功后的执行动作 */
            afterGet(result, data);
        }else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {

        }

        return false;
    }

    private void afterGet(int result, Object data) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (result == SMSSDK.RESULT_COMPLETE) {
            countDown();
        }else{
            mButtonCode.setEnabled(true);
            ((Throwable) data).printStackTrace();
            Throwable throwable = (Throwable) data;
            // 根据服务器返回的网络错误，给toast提示
            int status = 0;
            try {
                JSONObject object = new JSONObject(throwable.getMessage());
                String des = object.optString("detail");
                status = object.optInt("status");
                if (!TextUtils.isEmpty(des)) {
                    Toast.makeText(this, des, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                SMSLog.getInstance().w(e);
            }

            Toast.makeText(this, "获取验证码失败！", Toast.LENGTH_SHORT).show();

        }


    }

    private void countDown() {
        //成功了才倒计时
        countDown = true;
        myTimer = new MyTimer(millisInFuture, countDownInterval);
        myTimer.start();
    }

    private void afterSubmit(int result, Object data) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (result == SMSSDK.RESULT_COMPLETE) {
            if(myTimer!=null){
                myTimer.cancel();
//                mButtonCode.setEnabled(true);
            }

//            Toast.makeText(this, "验证码校验成功", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this,ResetPasswordNextActivity.class);
            intent.putExtra("phone",phone);
            intent.putExtra("result",true);
            startActivity(intent);
            finish();

        }else{
            ((Throwable) data).printStackTrace();
            // 验证码不正确
            String message = ((Throwable) data).getMessage();
            int resId = 0;
            try {
                JSONObject json = new JSONObject(message);
                int status = json.getInt("status");
                Toast.makeText(this, "验证码不正确:"+status, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onDestroy() {

        // 销毁回调监听接口
        SMSSDK.unregisterAllEventHandler();
        if(myTimer!=null){
            countDown = false;
            myTimer.cancel();
        }
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


    /**
     *
     *  倒计时
     * */
    private class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            int s = (int) (millisUntilFinished/1000);
            mButtonCode.setText(getString(R.string.txt_later_retry,s));
        }


        @Override
        public void onFinish() {
            countDown = false;
            mButtonCode.setText(R.string.getcode);
            mButtonCode.setEnabled(true);
        }

    }
}
