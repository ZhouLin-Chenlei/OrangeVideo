package com.community.yuequ.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.gui.adapter.CollectPagerAdapter;
import com.community.yuequ.gui.adapter.MyCenterCollectAdapter;
import com.community.yuequ.modle.CollectListDao;
import com.community.yuequ.modle.OrOnlineListDao;
import com.community.yuequ.modle.UserInfo;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerGridItemDecoration;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Request;


public class MyCollectActivity extends AppCompatActivity implements View.OnClickListener{


    private Button bt_edit;
    private TextView tv_name;
    private ImageView iv_gender;
    private TextView tv_desc;
    private CircleImageView mImageView;

    private UserInfo mUserInfo;
    private Session mSession;


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CollectPagerAdapter mCollectAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        setContentView(R.layout.activity_my_collect);
        Intent intent = getIntent();
        if(intent!=null){
            mUserInfo = (UserInfo) intent.getSerializableExtra("user");
        }
        mSession = Session.get(this);
        if(mUserInfo==null){
            mUserInfo = mSession.getUserInfo();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.activity_main_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.activity_main_pager);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

        }

        bt_edit = (Button) findViewById(R.id.bt_edit);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_gender = (ImageView) findViewById(R.id.iv_gender);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        mImageView = (CircleImageView) findViewById(R.id.iv_head);

        bt_edit.setOnClickListener(this);

        mCollectAdapter = new CollectPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager.setAdapter(mCollectAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        displayUser();


    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.ACTION_EDIT_USERINFO);
        filter.addAction(Contants.ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,filter);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
           if(Contants.ACTION_EDIT_USERINFO.equals(action)){
               mUserInfo = mSession.getUserInfo();
               displayUser();

            }else if(Contants.ACTION_LOGOUT.equals(action)){
               if(isFinishing()){
                   finish();
               }
           }
        }
    };


    private void displayUser() {

        if(mUserInfo!=null){

            ImageManager.getInstance().loadUrlImage(this,mUserInfo.head_img_path,mImageView);
            tv_name.setText(mUserInfo.nick_name);
            if("女".equals(mUserInfo.sex)){
                iv_gender.setImageResource(R.mipmap.icon_female);
            }else{
                iv_gender.setImageResource(R.mipmap.icon_male);
            }
//            String idStr = "ID："+userInfo.id;
//            tv_userid.setText(idStr);
            tv_desc.setText(mUserInfo.person_sign);


        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_edit:
                Intent intent_myprofile = new Intent(this, MyprofileActivity.class);
                intent_myprofile.putExtra("user", mUserInfo);
                startActivity(intent_myprofile);

                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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
