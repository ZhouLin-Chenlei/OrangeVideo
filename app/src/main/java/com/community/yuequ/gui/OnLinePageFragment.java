package com.community.yuequ.gui;

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
import com.community.yuequ.gui.adapter.OnLinePageAdapter;
import com.community.yuequ.modle.OrOnlineGroup;
import com.community.yuequ.modle.OrOnlineDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.Log;
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
 * 直播一级界面
 */
public class OnLinePageFragment extends BaseTabFragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    public static final String TAG = OnLinePageFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected PageStatuLayout mStatuLayout;
    private OnLinePageAdapter mVideoAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private OrOnlineDao mVideoPrograma;
    private final List<OrOnlineGroup> mProgramas = new ArrayList<>();

    public OnLinePageFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoAdapter = new OnLinePageAdapter(this, mProgramas);

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.comm_list_layout, container, false);

        mStatuLayout = new PageStatuLayout(convertView)
                .setReloadListener(this);
        mRecyclerView = (RecyclerView) convertView.findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mVideoAdapter);
    return convertView;
    }

    @Override
    protected void initData() {
        HashMap<String,Integer> hashMap  =new HashMap<>();

        hashMap.put("level",1);//默认一级栏目，值=1；二级栏目，值=2
        hashMap.put("col_id",3);//默认为直播ID，值=3
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
                .url(Contants.URL_LIVELIST)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<OrOnlineDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if(isAdded()){
                            getDataFail();
                        }
                    }

                    @Override
                    public void onResponse(OrOnlineDao response, int id) {
                        mVideoPrograma = response;
                        if(isAdded()){
                            if(mVideoPrograma.result==null||mVideoPrograma.result.isEmpty()){
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



    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int topRowVerticalPosition =
                    (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
            mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0 && !isLoading);
        }
    };


    private void completeRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mStatuLayout != null) {
            mStatuLayout.setProgressBarVisibility(false);
            if(mVideoPrograma==null && mVideoAdapter.getItemCount()==0){
                mStatuLayout.show().setText(YQApplication.getAppResources().getString(R.string.load_data_fail));
            }else if(mVideoPrograma!=null && mVideoAdapter.getItemCount()==0){
                mStatuLayout.show().setText(YQApplication.getAppResources().getString(R.string.no_data));
            }else {
                mStatuLayout.hide();
            }
        }
    }


    protected void getDataBefore() {
        super.getDataBefore();
        if(mVideoAdapter.getItemCount()==0){
            mStatuLayout.show().setProgressBarVisibility(true).setText(null);
        }else {
            mStatuLayout.hide();
        }
    }

    protected void getDataFail() {
        super.getDataFail();
        mVideoAdapter.notifyDataSetChanged();
        completeRefresh();
    }
    protected void getDataAfter() {
        super.getDataAfter();
    }


    //数据为空
    public void getDataEmpty() {
        mProgramas.clear();
        mVideoAdapter.notifyDataSetChanged();
        completeRefresh();
    }

    //数据足够
    public void getDataAdequate() {
        mProgramas.clear();
        mProgramas.addAll(mVideoPrograma.result);
        mVideoAdapter.notifyDataSetChanged();
        completeRefresh();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mVideoPrograma!=null){
            if(mVideoPrograma.result!=null&&!mVideoPrograma.result.isEmpty()){
                mProgramas.clear();
                mProgramas.addAll(mVideoPrograma.result);
                mVideoAdapter.notifyDataSetChanged();
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView-----------");
        OkHttpUtils.getInstance().cancelTag(TAG);
    }
}
