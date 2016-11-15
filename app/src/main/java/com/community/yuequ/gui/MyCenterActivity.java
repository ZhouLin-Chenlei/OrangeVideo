package com.community.yuequ.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.gui.adapter.MyCenterCollectAdapter;
import com.community.yuequ.modle.OrOnlineListDao;
import com.community.yuequ.modle.UserInfo;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerGridItemDecoration;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Request;


public class MyCenterActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private static final String TAG = "MyCenterActivity";

    private ImageButton ib_back;
    private Button bt_edit;
    private TextView tv_name;
    private ImageView iv_gender;
    private TextView tv_desc;
    private CircleImageView mImageView;

    private FrameLayout ll_status;
    private ImageView iv_empty;
    private ProgressBar progressBar;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private MyCenterCollectAdapter mCollectAdapter;

    private int lastVisibleItem;
    private int mPage = 1;
    private boolean isLoading = false;
    private Session mSession;
    public OrOnlineListDao.OrOnlineListBean mOnlineListBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_center);
        mSession = Session.get(this);

        ib_back = (ImageButton) findViewById(R.id.ib_back);
        bt_edit = (Button) findViewById(R.id.bt_edit);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_gender = (ImageView) findViewById(R.id.iv_gender);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        mImageView = (CircleImageView) findViewById(R.id.iv_head);

        ll_status = (FrameLayout)findViewById(R.id.ll_status);
        iv_empty = (ImageView) findViewById(R.id.iv_empty);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mCollectAdapter = new MyCenterCollectAdapter(this);
        mRecyclerView.setAdapter(mCollectAdapter);


        bt_edit.setOnClickListener(this);
        ib_back.setOnClickListener(this);

        displayUser();
        getdata(1);

    }

    private void displayUser() {
        UserInfo userInfo = mSession.getUserInfo();
        if(userInfo!=null){

            ImageManager.getInstance().loadUrlImage(this,userInfo.head_img_path,mImageView);
            tv_name.setText(userInfo.nick_name);
            if("女".equals(userInfo.sex)){
                iv_gender.setImageResource(R.mipmap.icon_female);
            }else{
                iv_gender.setImageResource(R.mipmap.icon_male);
            }
//            String idStr = "ID："+userInfo.id;
//            tv_userid.setText(idStr);
            tv_desc.setText(userInfo.person_sign);


        }

    }

    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    int size = recyclerView.getAdapter().getItemCount();
                    if (lastVisibleItem + 1 == size && mCollectAdapter.isLoadMoreShown() &&
                            !mCollectAdapter.getLoadMoreViewText().equals(getString(R.string.load_data_adequate))&&!isLoading) {
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
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                finish();
                break;
            case R.id.bt_edit:

                break;
            case R.id.ll_status:
                if(!isLoading){
                    getdata(1);
                }
                break;
            default:
                break;
        }
    }

    private void getdata(final int page) {
        HashMap<String,Integer> hashMap  =new HashMap<>();
        hashMap.put("pageIdx",page);//当前页数，默认是1
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
        String url = Contants.URL_USERCOLLECTIONSLIST;

        OkHttpUtils
                .postString()
                .content(content)
                .url(url)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<OrOnlineListDao>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        mCollectAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                        mCollectAdapter.setLoadMoreViewText(getString(R.string.load_data_fail));
                        if (ll_status != null) {
                            if(mCollectAdapter.getItemCount()==1){
                                ll_status.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.VISIBLE);
                            }else {
                                ll_status.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.GONE);
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
                                mCollectAdapter.setData(mOnlineListBean.list);

                            }else{
                                mCollectAdapter.addData(mOnlineListBean.list);
                            }
                            if(mPage >= mOnlineListBean.total_page){
                                mCollectAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mCollectAdapter.setLoadMoreViewText(getString(R.string.load_data_adequate));
                            }else{
                                mCollectAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mCollectAdapter.setLoadMoreViewText(getString(R.string.loading_data));
                            }
                        }

                        if (ll_status != null) {
                            if(mCollectAdapter.getItemCount()==1){
                                ll_status.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.VISIBLE);

                            }else {
                                ll_status.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        isLoading = true;
                        if(mCollectAdapter.getItemCount()<=1){
                            ll_status.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            iv_empty.setVisibility(View.GONE);
                        }else {
                            ll_status.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            iv_empty.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        isLoading = false;
                    }

                });
    }

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
