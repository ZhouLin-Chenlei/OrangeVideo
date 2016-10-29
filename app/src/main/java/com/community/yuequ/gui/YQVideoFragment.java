package com.community.yuequ.gui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.YQVideoAdapter;
import com.community.yuequ.modle.VideoOrPicGroup;
import com.community.yuequ.modle.YQVideoOrPicGroupDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.Log;
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
 * 视频页
 */
public class YQVideoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    public static final String TAG = YQVideoFragment.class.getSimpleName();
    protected RecyclerView mRecyclerView;
    protected PageStatuLayout mStatuLayout;
    private YQVideoAdapter mVideoAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private YQVideoOrPicGroupDao mVideoPrograma;
    private final List<VideoOrPicGroup> mProgramas = new ArrayList<>();

    public YQVideoFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoAdapter = new YQVideoAdapter(this, mProgramas);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        mStatuLayout = new PageStatuLayout(convertView);
        mRecyclerView = findView(android.R.id.list);
        mSwipeRefreshLayout = findView(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink900);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mVideoAdapter);

    }

//    String testUrl = "http://image.baidu.com/channel/listjson?fr=channel&tag1=美女&tag2=泳装&sorttype=0&pn=1&rn=100&ie=utf8&oe=utf-8&8339397110145592";
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
                .content(content)
                .url(Contants.URL_VIDEOLIST)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<YQVideoOrPicGroupDao>() {
                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if(isAdded()){
                            getDataFail();
                        }
                    }

                    @Override
                    public void onResponse(YQVideoOrPicGroupDao response,int id) {
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
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView-----------");
        OkHttpUtils.getInstance().cancelTag(TAG);
    }
}
