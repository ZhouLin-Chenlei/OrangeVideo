package com.community.yuequ.gui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.PicListAdapter;
import com.community.yuequ.modle.PicListDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.community.yuequ.view.TitleBarLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class PicListActivity extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = PicListActivity.class.getSimpleName();
    private TitleBarLayout mTitleBarLayout;
    private PageStatuLayout mStatuLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItem;
    private int mPage = 1;
    private boolean isLoading = false;
    private PicListAdapter mListAdapter;

    private String type = "1";
    private int column_id;
    private String column_name;
    private int from;

    private PicListDao.PicListBean mPicListBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_group);
        Intent intent = getIntent();
        column_id = intent.getIntExtra("column_id",0);
        from = intent.getIntExtra("from",0);
        type = intent.getStringExtra("type");
        column_name = intent.getStringExtra("column_name");

        mStatuLayout = new PageStatuLayout(this)
                .hide();

        mTitleBarLayout = new TitleBarLayout(this)
                .setText(column_name)
                .setLeftButtonVisibility(true)
                .setLeftButtonClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink900);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mListAdapter = new PicListAdapter(this);
        mRecyclerView.setAdapter(mListAdapter);
        getdata(1);
    }

    private void getdata(final int page) {
        String url = null;
        HashMap<String,Integer> hashMap  =new HashMap<>();
        hashMap.put("pageIdx",page);
        if(from==4){
            hashMap.put("subject_id",column_id);//from==4，是专题过来的
            url = Contants.URL_SPECPROGRAMLIST;
        }else{
            url = Contants.URL_PROGRAMLIST;
            hashMap.put("col_id",column_id);//默认为视频ID，值=2
        }
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

        OkHttpUtils
                .postString()
                .content(content)
                .url(url)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<PicListDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        mListAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                        mListAdapter.setLoadMoreViewText(getString(R.string.load_data_fail));
                        if (mStatuLayout != null) {
                            if(mListAdapter.getItemCount()==0){
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
                    public void onResponse(PicListDao response,int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        if(response.result!=null){
                            mPage = page;
                            mPicListBean = response.result;
                            if(mPage >= mPicListBean.total_page){
                                mListAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mListAdapter.setLoadMoreViewText(getString(R.string.load_data_adequate));
                            }else{
                                mListAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mListAdapter.setLoadMoreViewText(getString(R.string.loading_data));
                            }

                            if(mPage==1){
                                mListAdapter.setData(mPicListBean.list);

                            }else{
                                mListAdapter.addData(mPicListBean.list);
                            }

                        }


                        if (mStatuLayout != null) {
                            if(mListAdapter.getItemCount()==0){
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
                        if(mListAdapter.getItemCount()==0){
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
                        super.onAfter(id);
                        isLoading = false;
                    }
                });
    }


    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    int size = recyclerView.getAdapter().getItemCount();
                    if (lastVisibleItem + 1 == size && mListAdapter.isLoadMoreShown() &&
                            !mListAdapter.getLoadMoreViewText().equals(getString(R.string.load_data_adequate))&&!isLoading) {
                        onScrollLast();
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            int topRowVerticalPosition =
                    (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
            mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
        }
    };

    private void onScrollLast() {
        getdata(mPage+1);
    }

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
       getdata(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }
}
