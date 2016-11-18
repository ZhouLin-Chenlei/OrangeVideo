package com.community.yuequ.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.VideoOrPicGroupAdapter;
import com.community.yuequ.modle.OrVideoGroupDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerGridItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class VideoSecondGroupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    public static final String TAG = VideoSecondGroupActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TextView mTitleView;
    private  PageStatuLayout mStatuLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private VideoOrPicGroupAdapter mGroupAdapter;

    private OrVideoGroupDao mYQVideoDao;

    private String type = "1";
    private int column_id;
    private String column_name;
    protected volatile boolean isLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_list_layout);

        Intent intent = getIntent();
        column_id = intent.getIntExtra("column_id",0);
        type = intent.getStringExtra("type");
        column_name = intent.getStringExtra("column_name");

        mStatuLayout = new PageStatuLayout(this)
                .setReloadListener(this)
                .hide();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView .setText(column_name);


        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mGroupAdapter = new VideoOrPicGroupAdapter(this);
        mRecyclerView.setAdapter(mGroupAdapter);
        mGroupAdapter.setType(type);
        getdata();
    }

    private void getdata() {
        HashMap<String,Integer> hashMap  =new HashMap<>();
        hashMap.put("level",2);//默认一级栏目，值=1；二级栏目，值=2
        hashMap.put("col_id",column_id);//默认为视频ID，值=2
        String content = "";
        try {
            content = AESUtil.encode(new Gson().toJson(hashMap));
        } catch (Exception e) {
            throw new RuntimeException("加密错误！");
        }
        if (TextUtils.isEmpty(content)){
            Toast.makeText(YQApplication.getAppContext(), R.string.unknow_erro, Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Contants.URL_VIDEOLIST;
        if("2".equals(type)){
            url = Contants.URL_PICTURELIST;
        }
        OkHttpUtils
                .postString()
                .content(content)
                .url(url)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<OrVideoGroupDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (mStatuLayout != null) {
                            if(mGroupAdapter.getItemCount()==0){
                                mStatuLayout.show()
                                        .setProgressBarVisibility(false)
                                        .setText(getString(R.string.load_data_fail));
                            }else {
                                mStatuLayout.hide()
                                        .setProgressBarVisibility(false)
                                        .setText(null);
                            }
                        }
                    }

                    @Override
                    public void onResponse(OrVideoGroupDao response, int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        mYQVideoDao = response;
                        if(mYQVideoDao!=null && mYQVideoDao.result!=null){
                            mGroupAdapter.setData(mYQVideoDao.result);
                        }

                        if (mStatuLayout != null) {
                            if(mGroupAdapter.getItemCount()==0){
                                mStatuLayout.show()
                                        .setProgressBarVisibility(false)
                                        .setText(getString(R.string.no_data));
                            }else {
                                mStatuLayout.hide()
                                        .setProgressBarVisibility(false)
                                        .setText(null);
                            }
                        }

                    }

                    @Override
                    public void onBefore(Request request,int id) {
                        isLoading = true;
                        if(mGroupAdapter.getItemCount()==0){
                            mStatuLayout.show()
                                    .setProgressBarVisibility(true)
                                    .setText(null);
                        }else {
                            mStatuLayout.hide()
                                    .setProgressBarVisibility(false)
                                    .setText(null);
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        isLoading = false;
                    }
                });
    }


    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int topRowVerticalPosition =
                    (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
            mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
        }
    };


    @Override
    public void onRefresh() {
       getdata();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_status:
                if(!isLoading){
                    getdata();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
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
