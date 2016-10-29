package com.community.yuequ.gui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.YQImageAdapter;
import com.community.yuequ.modle.RTextImage;
import com.community.yuequ.modle.YQImageDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 图文
 */
public class YQImageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private final static String TAG = YQImageFragment.class.getSimpleName();
    protected PageStatuLayout mStatuLayout;
    protected RecyclerView mRecyclerView;
    private YQImageAdapter mListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private YQImageDao imageDao;
    public final List<RTextImage> rTextImages=new ArrayList<>();
    public YQImageFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new YQImageAdapter(this,rTextImages);

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
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        mStatuLayout = new PageStatuLayout(convertView)
                .setProgressBarVisibility(true)
                .setText(null)
                .show();
        mRecyclerView = findView(android.R.id.list);
        mSwipeRefreshLayout = findView(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink900);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mListAdapter);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(imageDao!=null){
            if(imageDao.result!=null&&!imageDao.result.isEmpty()){
                rTextImages.clear();
                rTextImages.addAll(imageDao.result);
                mListAdapter.notifyDataSetChanged();
                if (mStatuLayout != null) {
                    mStatuLayout.setProgressBarVisibility(false).setText(null).hide();
                }
            }else{
                mStatuLayout.setProgressBarVisibility(false)
                        .setText(YQApplication.getAppResources().getString(R.string.no_data))
                        .show();
            }

        }

    }

    @Override
    protected void initData() {

        HashMap<String,Integer> hashMap  =new HashMap<>();
        hashMap.put("level",1);//默认一级栏目，值=1；二级栏目，值=2
        hashMap.put("col_id",4);//默认为视频ID，值=4
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
                .url(Contants.URL_PICTURELIST)
                .content(content)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<YQImageDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if(isAdded()){
                            getDataFail();
                        }

                    }

                    @Override
                    public void onResponse(YQImageDao response,int id) {
                        imageDao  = response;
                        if(isAdded()){
                            if(imageDao.result==null||imageDao.result.isEmpty()){
                                getDataEmpty();
                            }else{
                                getDataAdequate();
                            }
                        }



                    }

                    @Override
                    public void onBefore(Request request,int id) {
                        getDataBefore();
                    }
                    @Override
                    public void onAfter(int id) {
                        if(isAdded()){
                            getDataAfter();
                        }

                    }
                });
    }

    protected void getDataBefore() {
        super.getDataBefore();
    }

    protected void getDataFail() {
        super.getDataFail();
    }
    protected void getDataAfter() {
        super.getDataAfter();

    }

    //数据为空
    public void getDataEmpty() {
        rTextImages.clear();
        mListAdapter.notifyDataSetChanged();
        completeRefresh();
    }

    //数据足够
    public void getDataAdequate() {
        rTextImages.clear();
        rTextImages.addAll(imageDao.result);
        mListAdapter.notifyDataSetChanged();
        completeRefresh();

    }


    private void completeRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mStatuLayout != null) {
            mStatuLayout.setProgressBarVisibility(false);
            if(imageDao==null && mListAdapter.getItemCount()==0){
                mStatuLayout.show().setText(getString(R.string.load_data_fail));
            }else if(imageDao!=null && mListAdapter.getItemCount()==0){
                mStatuLayout.show().setText(getString(R.string.no_data));
            }else {
                mStatuLayout.hide();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }



    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onRefresh() {
        initData();
    }

}
