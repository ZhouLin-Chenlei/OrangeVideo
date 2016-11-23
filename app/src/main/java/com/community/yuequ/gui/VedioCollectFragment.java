package com.community.yuequ.gui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.YQApplication;
import com.community.yuequ.gui.adapter.VedioCollectAdapter;
import com.community.yuequ.modle.CollectListDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;


public class VedioCollectFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private static final String TAG = VedioCollectFragment.class.getSimpleName();
    private FrameLayout ll_status;
    private ImageView iv_empty;
    private ProgressBar progressBar;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private VedioCollectAdapter mCollectAdapter;

    private int lastVisibleItem;
    private int mPage = 1;
    private boolean isLoading = false;
    public CollectListDao.CollectListBean mCollectListBean;

    public VedioCollectFragment() {

    }

    public static VedioCollectFragment newInstance() {
        VedioCollectFragment fragment = new VedioCollectFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vedio_collect, container, false);

        ll_status = (FrameLayout) view.findViewById(R.id.ll_status);
        iv_empty = (ImageView) view.findViewById(R.id.iv_empty);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mCollectAdapter = new VedioCollectAdapter(this);
        mRecyclerView.setAdapter(mCollectAdapter);

        getdata(1);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpUtils.getInstance().cancelTag(TAG);
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

    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    int size = recyclerView.getAdapter().getItemCount();
                    if (lastVisibleItem + 1 == size && mCollectAdapter.isLoadMoreShown() &&
                            !mCollectAdapter.getLoadMoreViewText().equals(getString(R.string.load_data_adequate)) && !isLoading) {
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

    private void getdata(final int page) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pageIdx", page);//当前页数，默认是1
        hashMap.put("type", "1");//1：视频，2：图文，3：直播
        String content = "";
        try {
            content = AESUtil.encode(new Gson().toJson(hashMap));
        } catch (Exception e) {
            throw new RuntimeException("加密错误！");
        }
        if (TextUtils.isEmpty(content)) {
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
                .execute(new JsonCallBack<CollectListDao>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        mCollectAdapter.setLoadMoreViewVisibility(View.GONE);
                        mCollectAdapter.setLoadMoreViewText(getString(R.string.load_data_fail));
                        if (ll_status != null) {
                            if (mCollectAdapter.getItemCount() == 1) {
                                ll_status.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.VISIBLE);
                            } else {
                                ll_status.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onResponse(CollectListDao response, int id) {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (response != null && response.result != null) {
                            mPage = page;
                            mCollectListBean = response.result;

                            if (mPage == 1) {
                                mCollectAdapter.setData(mCollectListBean.list);

                            } else {
                                mCollectAdapter.addData(mCollectListBean.list);
                            }
                            if (mPage >= mCollectListBean.total_page) {
                                mCollectAdapter.setLoadMoreViewVisibility(View.GONE);
                                mCollectAdapter.setLoadMoreViewText(getString(R.string.load_data_adequate));
                            } else {
                                mCollectAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                                mCollectAdapter.setLoadMoreViewText(getString(R.string.loading_data));
                            }
                        }

                        if (ll_status != null) {
                            if (mCollectAdapter.getItemCount() == 1) {
                                ll_status.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.VISIBLE);

                            } else {
                                ll_status.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                iv_empty.setVisibility(View.GONE);
                            }
                        }

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        isLoading = true;
                        if (mCollectAdapter.getItemCount() <= 1) {
                            ll_status.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            iv_empty.setVisibility(View.GONE);
                        } else {
                            ll_status.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            iv_empty.setVisibility(View.GONE);

                            mCollectAdapter.setLoadMoreViewVisibility(View.VISIBLE);
                            mCollectAdapter.setLoadMoreViewText(getString(R.string.loading_data));

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
    protected void onScrollLast() {
//        Toast.makeText(this, "加载更多...", Toast.LENGTH_SHORT).show();
        getdata(mPage + 1);
    }

    @Override
    public void onRefresh() {
        getdata(1);
    }
}
