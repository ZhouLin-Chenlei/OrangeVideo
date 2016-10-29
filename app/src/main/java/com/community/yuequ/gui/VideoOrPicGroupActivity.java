package com.community.yuequ.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.VideoOrPicGroupAdapter;
import com.community.yuequ.modle.YQVideoOrPicGroupDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerGridItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.community.yuequ.view.TitleBarLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class VideoOrPicGroupActivity extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = VideoOrPicGroupActivity.class.getSimpleName();
    private TitleBarLayout mTitleBarLayout;
    private  PageStatuLayout mStatuLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private VideoOrPicGroupAdapter mGroupAdapter;

    private YQVideoOrPicGroupDao mYQVideoDao;

    private String type = "1";
    private int column_id;
    private String column_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_group);

        Intent intent = getIntent();
        column_id = intent.getIntExtra("column_id",0);
        type = intent.getStringExtra("type");
        column_name = intent.getStringExtra("column_name");

        mStatuLayout = new PageStatuLayout(this)
                .hide();

        mTitleBarLayout = new TitleBarLayout(this)
                .setLeftButtonVisibility(true)
                .setLeftButtonClickListener(this);



        mTitleBarLayout .setText(column_name);


        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink900);
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
                .execute(new JsonCallBack<YQVideoOrPicGroupDao>() {
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
                    public void onResponse(YQVideoOrPicGroupDao response,int id) {
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
       getdata();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }
}
