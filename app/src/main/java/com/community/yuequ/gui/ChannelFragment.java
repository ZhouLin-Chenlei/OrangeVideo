package com.community.yuequ.gui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.ChannelListAdapter;
import com.community.yuequ.modle.Channel;
import com.community.yuequ.modle.ChannelDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 专题
 */
public class ChannelFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private final static String TAG = ChannelFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected PageStatuLayout mStatuLayout;
    private ChannelListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private ChannelDao mChannelDao;

    public final List<Channel> mChannels =  new ArrayList<>();
    public ChannelFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ChannelListAdapter(this,mChannels);

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
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mAdapter);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mChannelDao!=null){
            if(mChannelDao.result!=null&&!mChannelDao.result.isEmpty()){
                mChannels.clear();
                mChannels.addAll(mChannelDao.result);
                mAdapter.notifyDataSetChanged();
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
        OkHttpUtils
                .post()
                .url(Contants.URL_SPECIALSUBJECTLIST)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<ChannelDao>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(isAdded()){
                            getDataFail();
                        }
                    }
                    @Override
                    public void onResponse(ChannelDao response, int id) {
                        mChannelDao = response;
                        if(isAdded()){
                            if(mChannelDao.result==null||mChannelDao.result.isEmpty()){
                                getDataEmpty();
                            }else{
                                getDataAdequate();
                            }
                        }
                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        getDataBefore();
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
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
        mChannels.clear();
        mAdapter.notifyDataSetChanged();
        completeRefresh();
    }

    //数据足够
    public void getDataAdequate() {
        mChannels.clear();
        mChannels.addAll(mChannelDao.result);
        mAdapter.notifyDataSetChanged();
        completeRefresh();

    }
    private void completeRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mStatuLayout != null) {
            mStatuLayout.setProgressBarVisibility(false);
            if(mChannelDao==null && mAdapter.getItemCount()==0){
                mStatuLayout.show().setText(getString(R.string.load_data_fail));
            }else if(mChannelDao!=null && mAdapter.getItemCount()==0){
                mStatuLayout.show().setText(getString(R.string.no_data));
            }else {
                mStatuLayout.hide();
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    public void onRefresh() {
        initData();
    }
}
