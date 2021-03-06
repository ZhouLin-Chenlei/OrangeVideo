package com.community.yuequ.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.community.yuequ.gui.adapter.VideoListAdapter;
import com.community.yuequ.modle.PicListDao;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class VideoListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private static final String TAG = VideoListActivity.class.getSimpleName();
//    public  static final int CODE_REFRESH = 17;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private PageStatuLayout mStatuLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private VideoListAdapter mListAdapter;
    private int lastVisibleItem;
    private int mPage = 1;
    private boolean isLoading = false;
    private String type = "1";
    private int column_id;
    private String column_name;
    private int from;


    private PicListDao.PicListBean mPicListBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_list_layout);
        Intent intent = getIntent();
        column_id = intent.getIntExtra("column_id",0);
        from = intent.getIntExtra("from",0);
        type = intent.getStringExtra("type");
        column_name = intent.getStringExtra("column_name");
        mStatuLayout = new PageStatuLayout(this)
                .setReloadListener(this)
                .hide();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView.setText(column_name);
        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mListAdapter = new VideoListAdapter(this);
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
            hashMap.put("col_id",column_id);//默认为视频ID，值=2
            url = Contants.URL_PROGRAMLIST;
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
                            if(mListAdapter.getItemCount()<=1){
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

                        if(response!=null && response.result!=null){
                            mPage = page;
                            mPicListBean = response.result;
                            if(mPage==1){
                                mListAdapter.setData(mPicListBean.list);

                            }else{
                                mListAdapter.addData(mPicListBean.list);
                            }
                            if(mPage >= mPicListBean.total_page){
                                mListAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mListAdapter.setLoadMoreViewText(getString(R.string.load_data_adequate));
                            }else{
                                mListAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mListAdapter.setLoadMoreViewText(getString(R.string.loading_data));
                            }
                        }


                        if (mStatuLayout != null) {
                            if(mListAdapter.getItemCount()<=1){
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
                        if(mListAdapter.getItemCount()<=1){
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


    //上拉加载数据
    protected void onScrollLast(){
//        Toast.makeText(this, "加载更多...", Toast.LENGTH_SHORT).show();
        getdata(mPage+1);
    }






    @Override
    public void onRefresh() {
        getdata(1);
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_status:
                if(!isLoading){
                    getdata(1);
                }
                break;
            default:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==17){
            if(resultCode==18){
                int id = data.getIntExtra("id",0);
                String isCollection = data.getStringExtra("isCollection");

                if(mListAdapter!=null ){
                    List<RProgram> list = mListAdapter.getList();
                    if(list!=null){
                        for(int i = 0;i<list.size();i++){
                            RProgram program = list.get(i);
                            if(program.id==id){
                                program.isCollection = isCollection;
                            }


                        }
                    }

                }
            }

        }

    }
}
