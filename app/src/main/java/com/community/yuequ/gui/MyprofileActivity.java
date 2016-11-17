package com.community.yuequ.gui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.modle.MessageBean;
import com.community.yuequ.modle.UploadHeadDao;
import com.community.yuequ.modle.UserInfo;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.DateUtil;
import com.community.yuequ.util.ImageUtils;
import com.community.yuequ.widget.ConfirmOrderTipsDialog;
import com.community.yuequ.widget.DialogManager;
import com.community.yuequ.widget.GenderChoiceDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class MyprofileActivity extends AppCompatActivity implements View.OnClickListener,
        MediaScannerConnection.OnScanCompletedListener,DialogInterface.OnCancelListener,
        GenderChoiceDialog.ChoiceGenderListener {
    private final  String TAG = "MyprofileActivity";
    public final static int requestCode_nickname = 101;
    public final static int requestCode_desc = 102;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private Button commit;
    private ViewGroup rl_head;
    private ImageView iv_image;

    private ViewGroup rl_nickname;
    private TextView tv_nickname;

    private ViewGroup rl_gender;
    private TextView tv_gender;

    private ViewGroup rl_birthday;
    private TextView tv_birthday;

    private ViewGroup rl_describe;
    private TextView tv_describe;

    private ViewGroup rl_account_security;

    private Button btn_logout;
    private UserInfo mUserInfo;
    private Session mSession;
    private ProgressDialog mProgressDialog;

    private int mYear;
    private int mMonth;
    private int mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        mSession = Session.get(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        Intent intent = getIntent();
        if(intent!=null){
            mUserInfo = (UserInfo) intent.getSerializableExtra("user");
        }

        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        commit = (Button) mToolbar.findViewById(R.id.commit);
        mTitleView.setText(R.string.my_profile);

        rl_head = (ViewGroup) findViewById(R.id.rl_head);
        iv_image = (ImageView) findViewById(R.id.iv_image);

        rl_nickname = (ViewGroup) findViewById(R.id.rl_nickname);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);

        rl_gender = (ViewGroup) findViewById(R.id.rl_gender);
        tv_gender = (TextView) findViewById(R.id.tv_gender);

        rl_birthday = (ViewGroup) findViewById(R.id.rl_birthday);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);

        rl_describe = (ViewGroup) findViewById(R.id.rl_describe);
        tv_describe = (TextView) findViewById(R.id.tv_describe);

        rl_account_security = (ViewGroup) findViewById(R.id.rl_account_security);
        btn_logout = (Button) findViewById(R.id.btn_logout);


        commit.setOnClickListener(this);
        rl_head.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        rl_gender.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_describe.setOnClickListener(this);
        rl_account_security.setOnClickListener(this);
        btn_logout.setOnClickListener(this);


        display();
    }

    private void display() {

        if(mUserInfo!=null){

            ImageManager.getInstance().loadUrlImage(this,mUserInfo.head_img_path,iv_image);
            tv_nickname.setText(mUserInfo.nick_name);
            if("女".equals(mUserInfo.sex)){
                tv_gender.setText(R.string.female);
            }else if("男".equals(mUserInfo.sex)){
                tv_gender.setText(R.string.male);
            }else{
                tv_gender.setText(R.string.gender_unknow);
            }
            tv_birthday.setText(mUserInfo.birthday);
            tv_describe.setText(mUserInfo.person_sign);
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_head:
                if (mUserInfo == null) {
                    Toast.makeText(this,"请登录后操作", Toast.LENGTH_SHORT).show();
                } else {
                    ImageUtils.showImagePickDialog(this);
                }

                break;
            case R.id.rl_nickname:
                Intent intent = new Intent(this,EditInfoActivity.class);
                if(mUserInfo!=null){
                    intent.putExtra("nickname",mUserInfo.nick_name);
                }
                intent.putExtra("code",requestCode_nickname);
                startActivityForResult(intent,requestCode_nickname);
                break;
            case R.id.rl_gender:
                GenderChoiceDialog choiceDialog = GenderChoiceDialog.newInstance();
                choiceDialog.show(getSupportFragmentManager(),"dialog");

                break;
            case R.id.rl_birthday:

                if(mUserInfo!=null){
                    String text = tv_birthday.getText().toString();
                    long time= System.currentTimeMillis();
                    if(!TextUtils.isEmpty(text)){
                        try {
                            time = DateUtil.stringToLong(text,"yyyy-MM-dd");
                        } catch (ParseException e) {
                            e.printStackTrace();
                            time= System.currentTimeMillis();
                        }

                    }
                    final Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(time);
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(this,mDateSetListener,mYear, mMonth, mDay);
                    datePickerDialog.show();
                }

                break;
            case R.id.rl_describe:
                Intent intent2 = new Intent(this,EditInfoActivity.class);
                if(mUserInfo!=null){
                    intent2.putExtra("desc",mUserInfo.person_sign);
                }
                intent2.putExtra("code",requestCode_desc);
                startActivityForResult(intent2,requestCode_desc);
                break;
            case R.id.rl_account_security:
                startActivity(new Intent(this,SecurityCenterActivity.class));
                break;
            case R.id.btn_logout:
                mSession.setUserInfo(null);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Contants.ACTION_LOGOUT));
                finish();
                break;
            case R.id.commit:
                edit();
                break;

        }
    }

    private void edit() {

        String nickname = tv_nickname.getText().toString();
        String gender = tv_gender.getText().toString();
        String birthday = tv_birthday.getText().toString();
        String desc = tv_describe.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("nick_name", nickname);
        hashMap.put("sex", gender);
        hashMap.put("birthday", birthday);
        hashMap.put("person_sign", desc);

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

        OkHttpUtils.postString()
                .content(content)
                .url(Contants.URL_UPDATEUSERINFO)
                .tag(TAG)
                .build()
                .execute(new MyEditUserInfoCallBack(this,nickname,gender,birthday,desc));

    }



    public static class MyEditUserInfoCallBack extends JsonCallBack<UploadHeadDao> {
        private WeakReference<MyprofileActivity> activityWeakReference;
        private String nickname;
        private String gender;
        private String birthday;
        private String desc;

        public MyEditUserInfoCallBack(MyprofileActivity activity, String nickname, String gender, String birthday, String desc) {
            activityWeakReference = new WeakReference<>(activity);
            this.nickname = nickname;
            this.gender = gender;
            this.birthday = birthday;
            this.desc = desc;
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request,id);
            MyprofileActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onEditBefore();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            MyprofileActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onEditError();
            }
        }

        @Override
        public void onResponse(UploadHeadDao response,int id) {
            MyprofileActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onEditResponse(response,nickname,gender,birthday,desc);
            }
        }
    }

    private void onEditResponse(UploadHeadDao response,String nickname, String gender, String birthday, String desc) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if(response.errorCode==200){

            Toast.makeText(this, "资料修改成功！", Toast.LENGTH_SHORT).show();
            if(mUserInfo!=null){
                mUserInfo.nick_name = nickname;
                mUserInfo.sex = gender;
                mUserInfo.birthday = birthday;
                mUserInfo.person_sign = desc;
                mSession.setUserInfo(mUserInfo);
                //资料修改成功通知其他界面变化
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Contants.ACTION_EDIT_USERINFO));
                finish();
            }

        }else{
            if(!TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "资料修改失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onEditError() {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        Toast.makeText(this, "资料修改失败！", Toast.LENGTH_SHORT).show();
    }

    private void onEditBefore() {
        mProgressDialog = DialogManager.getProgressDialog(this,"修改资料...",this);
        mProgressDialog.show();
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    private void updateDisplay() {
        tv_birthday.setText(
                new StringBuilder()
                        .append(mYear).append("-")
                        // Month is 0 based so add 1
                        .append(mMonth + 1).append("-")
                        .append(mDay));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                ImageUtils.copeImage(this, data.getData());

                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (null != ImageUtils.mPhotoFile) {
                    if (resultCode == Activity.RESULT_OK) {
                        ImageUtils.scanMediaJpegFile(this, ImageUtils.mPhotoFile, this);
                    } else {
                        ImageUtils.mPhotoFile.delete();
                    }
                    ImageUtils.mPhotoFile = null;
                } else {
                    Toast.makeText(this, R.string.pic_notfound, Toast.LENGTH_SHORT).show();
                }
                break;
            case ImageUtils.REQUEST_CODE_FROM_CUT:
                // 从剪切图片返回的数据
                if (null != ImageUtils.mPhotoFile) {
                    if (resultCode == Activity.RESULT_OK) {

                        ImageUtils.scanMediaJpegFile(this, ImageUtils.mPhotoFile, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(final String path, Uri uri) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (path == null) {
                                            Toast.makeText(MyprofileActivity.this, R.string.pic_notfound, Toast.LENGTH_SHORT).show();
                                        } else {
                                            uploadHead(path);
                                        }
                                    }
                                });
                            }
                        });
                        ImageUtils.mPhotoFile = null;
                    } else {
                        ImageUtils.mPhotoFile.delete();
                    }
                } else {
                    Toast.makeText(MyprofileActivity.this, R.string.pic_notfound, Toast.LENGTH_SHORT).show();
                }
                break;

            case requestCode_nickname:
                if(resultCode==RESULT_OK){
                    String nickname = data.getStringExtra("nickname");
                    tv_nickname.setText(nickname);
                }


                break;
            case requestCode_desc:
                if(resultCode==RESULT_OK){
                    String desc = data.getStringExtra("desc");
                    tv_describe.setText(desc);
                }

                break;
            default:
                break;
        }
    }


    @Override
    public void onScanCompleted(String s, final Uri uri) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (uri != null) {
                    ImageUtils.copeImage(MyprofileActivity.this, uri);
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.pic_notfound, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isExists(String url) {
        if(url==null)return false;
        return new File(url).exists();
    }
    private void uploadHead(String path) {
        if (!isExists(path)) {
            Toast.makeText(getApplicationContext(),
                    R.string.pic_notfound, Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(path);
        OkHttpUtils.post()
                .addFile("upload_file", file.getName(), file)
                .url(Contants.URL_UPDATEHEAD)
                .tag(TAG)
                .build()
                .execute(new MyUploadHeadCallBack(this));


    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();

    }

    @Override
    public void getGender(String gender) {
        tv_gender.setText(gender);
    }

    public static class MyUploadHeadCallBack extends JsonCallBack<UploadHeadDao> {
        private WeakReference<MyprofileActivity> activityWeakReference;

        public MyUploadHeadCallBack(MyprofileActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request,id);
            MyprofileActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onBefore();
            }

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            MyprofileActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onError();
            }
        }

        @Override
        public void onResponse(UploadHeadDao response,int id) {
            MyprofileActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onResponse(response);
            }
        }
    }

    private void onResponse(UploadHeadDao response) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if(response.errorCode==200){
            mUserInfo.head_img_path =response.result;
            mSession.setUserInfo(mUserInfo);
            ImageManager.getInstance().loadUrlImage(this,response.result,iv_image);
            //资料修改成功通知其他界面变化
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Contants.ACTION_EDIT_USERINFO));
            Toast.makeText(this, "头像上传成功！", Toast.LENGTH_SHORT).show();

        }else{
            if(!TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "头像上传失败！", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void onError() {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    private void onBefore() {
        mProgressDialog = DialogManager.getProgressDialog(this,"上传头像中...",this);
        mProgressDialog.show();
    }


}
