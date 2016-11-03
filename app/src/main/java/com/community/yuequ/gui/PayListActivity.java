package com.community.yuequ.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.community.yuequ.R;
import com.community.yuequ.gui.adapter.PayListAdapter;
import com.community.yuequ.imple.OrderTipsOneListener;
import com.community.yuequ.imple.OrderTipsTowListener;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.pay.SPUtils;

import java.util.ArrayList;

public class PayListActivity extends AppCompatActivity implements OrderTipsOneListener,OrderTipsTowListener{
    private Toolbar mToolbar;
    private TextView mTitleView;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private PayListAdapter mListAdapter;
    private ArrayList<OrderTip> mOrderTips;
    private int programId;
    SPUtils mSmsPayUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_list);
        mSmsPayUtils = new SPUtils(this);
        mOrderTips = getIntent().getParcelableArrayListExtra("ordertips");
        programId = getIntent().getIntExtra("programId",0);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView.setText("购买");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mListAdapter = new PayListAdapter(this,mOrderTips,mSmsPayUtils);
        mRecyclerView.setAdapter(mListAdapter);

    }

    public int getProgramId(){
        return programId;
    }


    @Override
    public void one_confirm(int programId,OrderTip orderTip) {
        mSmsPayUtils.showConfirmOrderTips(programId,orderTip);
    }

    @Override
    public void one_cancel(int programId,OrderTip orderTip) {

    }

    @Override
    public void tow_confirm(int programId,OrderTip orderTip) {
        mSmsPayUtils.sendSms(programId,orderTip);
    }

    @Override
    public void tow_cancel(int programId,OrderTip orderTip) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!getSupportFragmentManager().popBackStackImmediate())
                finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
