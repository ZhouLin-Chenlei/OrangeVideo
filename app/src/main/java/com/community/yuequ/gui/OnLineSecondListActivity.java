package com.community.yuequ.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.community.yuequ.gui.adapter.OnLineListAdapter;
import com.community.yuequ.modle.OrOnlineListDao;
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

public class OnLineSecondListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    public static final String TAG = OnLineSecondListActivity.class.getSimpleName();
//    public static final int CODE_REFRESH = 17;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private  PageStatuLayout mStatuLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private OnLineListAdapter mGroupAdapter;

    private OrOnlineListDao.OrOnlineListBean mOnlineListBean;

    private int lastVisibleItem;
    private int mPage = 1;
    private boolean isLoading = false;

//    private String type = "1";//1:视频;2:图文
    private int column_id;
    private String column_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_list_layout);

        Intent intent = getIntent();
        column_id = intent.getIntExtra("column_id",0);
//        type = intent.getStringExtra("type");
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

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mGroupAdapter = new OnLineListAdapter(this);
        mRecyclerView.setAdapter(mGroupAdapter);

        getdata(1);
    }

    private void getdata(final int page) {
        HashMap<String,Integer> hashMap  =new HashMap<>();
        hashMap.put("pageIdx",page);//当前页数，默认是1
        hashMap.put("col_id",column_id);//栏目id
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
        String url = Contants.URL_LIVEPROGRAMLIST;

        OkHttpUtils
                .postString()
                .content(content)
                .url(url)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<OrOnlineListDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        mGroupAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                        mGroupAdapter.setLoadMoreViewText(getString(R.string.load_data_fail));
                        if (mStatuLayout != null) {
                            if(mGroupAdapter.getItemCount()<=1){
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
                    public void onResponse(OrOnlineListDao response,int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if(response!=null && response.result!=null){
                            mPage = page;
                            mOnlineListBean = response.result;

                            if(mPage==1){
                                mGroupAdapter.setData(mOnlineListBean.list);

                            }else{
                                mGroupAdapter.addData(mOnlineListBean.list);
                            }
                            if(mPage >= mOnlineListBean.total_page){
                                mGroupAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mGroupAdapter.setLoadMoreViewText(getString(R.string.load_data_adequate));
                            }else{
                                mGroupAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mGroupAdapter.setLoadMoreViewText(getString(R.string.loading_data));
                            }
                        }

                        if (mStatuLayout != null) {
                            if(mGroupAdapter.getItemCount()<=1){
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
                        if(mGroupAdapter.getItemCount()<=1){
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
                    if (lastVisibleItem + 1 == size && mGroupAdapter.isLoadMoreShown() &&
                            !mGroupAdapter.getLoadMoreViewText().equals(getString(R.string.load_data_adequate))&&!isLoading) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==17){
            if(resultCode==18){
                int id = data.getIntExtra("id",0);
                String isCollection = data.getStringExtra("isCollection");

                if(mGroupAdapter!=null ){
                    List<RProgram> list = mGroupAdapter.getList();
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
