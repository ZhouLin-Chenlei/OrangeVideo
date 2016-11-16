package com.community.yuequ.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.zhy.http.okhttp.OkHttpUtils;

public class SecurityCenterActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar mToolbar;
    private TextView mTitleView;
    private ViewGroup rl_edit_password;
    private ViewGroup rl_reset_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        setContentView(R.layout.activity_security_center);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView .setText(R.string.account_safety);

        rl_edit_password = (ViewGroup) findViewById(R.id.rl_edit_password);
        rl_reset_password = (ViewGroup) findViewById(R.id.rl_reset_password);
        rl_edit_password.setOnClickListener(this);
        rl_reset_password.setOnClickListener(this);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.ACTION_EDIT_PASSOWRD);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,filter);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Contants.ACTION_EDIT_PASSOWRD.equals(action)){
               finish();
            }
        }
    };


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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_edit_password:
                startActivity(new Intent(this,EditPasswordActivity.class));
                break;
            case R.id.rl_reset_password:
                startActivity(new Intent(this,ResetPasswordActivity.class));

                break;
        }
    }
}
