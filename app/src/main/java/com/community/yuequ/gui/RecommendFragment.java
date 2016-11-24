package com.community.yuequ.gui;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.gui.adapter.RecommendAdapter;
import com.community.yuequ.imple.HomeData;
import com.community.yuequ.modle.Advert;
import com.community.yuequ.modle.HomeItem;
import com.community.yuequ.modle.HomeOnline;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.modle.RecommendDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.view.NetworkImageHolderView;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.view.SwipeRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 推荐页
 */
public class RecommendFragment extends BaseTabFragment implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener ,View.OnClickListener{
    public static final String TAG = RecommendFragment.class.getSimpleName();
    protected PageStatuLayout mStatuLayout;
    private RecommendAdapter mRecommendAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private View headView;
    private ConvenientBanner mConvenientBanner;
    protected RecommendDao mRecommendDao;


    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecommendAdapter = new RecommendAdapter(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_recommend, container, false);

        mStatuLayout = new PageStatuLayout(convertView)
                .setReloadListener(this);
        mRecyclerView = (RecyclerView) convertView.findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) convertView.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        headView = inflater.inflate(R.layout.recommed_banner_layout, null);
        mConvenientBanner = (ConvenientBanner) headView.findViewById(R.id.convenientBanner);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mRecommendAdapter);
        mRecyclerView.setHasFixedSize(true);

        return convertView;
    }



    private void display() {
        if(mRecommendDao!=null && mRecommendDao.result!=null) {
            if (mRecommendDao.result.advert != null) {
                //本地图片例子
                mConvenientBanner.setPages(
                        new CBViewHolderCreator<NetworkImageHolderView>() {
                            @Override
                            public NetworkImageHolderView createHolder() {
                                return new NetworkImageHolderView();
                            }
                        }, mRecommendDao.result.advert)
                        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                        .setPageIndicator(new int[]{R.drawable.circular_indicator_white, R.drawable.circular_indicator_red})
                        //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
//                .setOnPageChangeListener(this)//监听翻页事件
                        .setOnItemClickListener(this);
                mRecommendAdapter.addHeadView(headView);
            }


                mRecommendAdapter.setData(mRecommendDao.result);

        }

        if (mStatuLayout != null) {
            if(mRecommendAdapter.getItemCount()==0){
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
    protected void initData() {
        OkHttpUtils
                .post()
                .url(Contants.URL_RECOMMEND)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<RecommendDao>() {

                    @Override
                    public void onError(Call call, Exception e,int id) {
                        if(isAdded()){
                            getDataFail();
                        }

                    }

                    @Override
                    public void onResponse(RecommendDao response,int id) {
                        mRecommendDao = response;
                        if(isAdded()){
                            display();
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
        if (mStatuLayout != null) {
           if(mRecommendAdapter.getItemCount()==0){
                mStatuLayout.show()
                        .setProgressBarVisibility(true)
                        .setText(null);
            }else {
                mStatuLayout.hide()
                        .setProgressBarVisibility(true)
                        .setText(null);
            }
        }

    }

    protected void getDataFail() {
        super.getDataFail();
        if (mStatuLayout != null) {
           if(mRecommendAdapter.getItemCount()==0){
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

    protected void getDataAfter() {
        super.getDataAfter();
        mSwipeRefreshLayout.setRefreshing(false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mRecommendDao!=null && mRecommendDao.result!=null) {
            if (mRecommendDao.result.advert != null) {
                //本地图片例子
                mConvenientBanner.setPages(
                        new CBViewHolderCreator<NetworkImageHolderView>() {
                            @Override
                            public NetworkImageHolderView createHolder() {
                                return new NetworkImageHolderView();
                            }
                        }, mRecommendDao.result.advert)
                        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                        .setPageIndicator(new int[]{R.drawable.circular_indicator_white, R.drawable.circular_indicator_red})
                        //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
//                .setOnPageChangeListener(this)//监听翻页事件
                        .setOnItemClickListener(this);
                mRecommendAdapter.addHeadView(headView);
            }


            mRecommendAdapter.setData(mRecommendDao.result);


            mStatuLayout.setProgressBarVisibility(false)
                    .setText(null)
                    .hide();

        }else{
//            mStatuLayout .setProgressBarVisibility(false)
//                    .setText(getString(R.string.no_data))
//                    .show();
        }

    }


    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
//        mConvenientBanner.startTurning(4000);
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
//        mConvenientBanner.stopTurning();
    }


    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onItemClick(int position) {
        if(mRecommendDao!=null && mRecommendDao.result!=null) {
            if (mRecommendDao.result.advert != null) {

                Advert advert = mRecommendDao.result.advert.get(position);

                if("1".equals(advert.link_type)){
                    Intent intent = new Intent(getContext(),AvdWebActivity.class);
                    intent.putExtra("title",advert.title);
                    intent.putExtra("link_url",advert.link_url);
                    startActivity(intent);
                }else{
                    try {
                        String openurl = advert.link_url;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openurl));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public void onDestroyView() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroyView();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==17){
            if(resultCode== 18){
                int id = data.getIntExtra("id",0);
                String isCollection = data.getStringExtra("isCollection");

                if(mRecommendAdapter!=null ){
                    List<HomeData> list = mRecommendAdapter.getList();
                    if(list!=null){
                        for(int i = 0;i<list.size();i++){
                            HomeData homeData = list.get(i);
                            if(homeData instanceof HomeItem){
                                HomeItem homeItem = (HomeItem) homeData;
                                if(homeItem.mPrograms!=null){
                                    for (int j = 0;j < homeItem.mPrograms.size();j++){
                                        RProgram program = homeItem.mPrograms.get(j);
                                        if(program.id==id){
                                            program.isCollection = isCollection;
                                        }
                                    }
                                }
                            }else if(homeData instanceof HomeOnline){
                                HomeOnline homeOnline = (HomeOnline) homeData;
                                if(homeOnline.mProgram!=null){
                                    if(homeOnline.mProgram.id==id){
                                        homeOnline.mProgram.isCollection = isCollection;
                                    }
                                }

                            }

                        }
                    }

                }
            }

        }

    }
}
