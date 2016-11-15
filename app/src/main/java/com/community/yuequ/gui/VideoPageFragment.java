package com.community.yuequ.gui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.VideoPageAdapter;
import com.community.yuequ.modle.OrVideoGroup;
import com.community.yuequ.modle.OrVideoGroupDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
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
 * 点播一级界面
 */
public class VideoPageFragment extends BaseTabFragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private final static String TAG = VideoPageFragment.class.getSimpleName();
    protected PageStatuLayout mStatuLayout;
    protected RecyclerView mRecyclerView;
    private VideoPageAdapter mListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private OrVideoGroupDao mVideoGroupDao;
    public final List<OrVideoGroup> mProgramas =new ArrayList<>();
    public VideoPageFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new VideoPageAdapter(this, mProgramas);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.comm_list_layout, container, false);

        mStatuLayout = new PageStatuLayout(convertView)
                .setProgressBarVisibility(true)
                .setReloadListener(this)
                .setText(null)
                .show();
        mRecyclerView = (RecyclerView) convertView.findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mListAdapter);
        return convertView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mVideoGroupDao!=null){
            if(mVideoGroupDao.result!=null&&!mVideoGroupDao.result.isEmpty()){
                mProgramas.clear();
                mProgramas.addAll(mVideoGroupDao.result);
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
        hashMap.put("col_id",2);//默认为视频ID，值=2

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
                .url(Contants.URL_VIDEOLIST)
                .content(content)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<OrVideoGroupDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if(isAdded()){
                            getDataFail();
                        }

                    }

                    @Override
                    public void onResponse(OrVideoGroupDao response, int id) {
                        mVideoGroupDao  = response;
                        if(isAdded()){
                            if(mVideoGroupDao.result==null||mVideoGroupDao.result.isEmpty()){
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

        if(mListAdapter.getItemCount()==0){
            mStatuLayout.show().setProgressBarVisibility(true).setText(null);
        }else {
            mStatuLayout.hide();
        }
    }

    protected void getDataFail() {
        super.getDataFail();
        mListAdapter.notifyDataSetChanged();
        completeRefresh();
    }
    protected void getDataAfter() {
        super.getDataAfter();

    }

    //数据为空
    public void getDataEmpty() {
        mProgramas.clear();
        mListAdapter.notifyDataSetChanged();
        completeRefresh();
    }

    //数据足够
    public void getDataAdequate() {
        mProgramas.clear();
        mProgramas.addAll(mVideoGroupDao.result);
        mListAdapter.notifyDataSetChanged();
        completeRefresh();

    }


    private void completeRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mStatuLayout != null) {
            mStatuLayout.setProgressBarVisibility(false);
            if(mVideoGroupDao==null && mListAdapter.getItemCount()==0){
                mStatuLayout.show().setText(getString(R.string.load_data_fail));
            }else if(mVideoGroupDao!=null && mListAdapter.getItemCount()==0){
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
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_status:
                if(!isLoading){
                    initData();
                }
                break;
            default:
                break;
        }
    }
}
