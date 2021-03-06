package com.community.yuequ.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.imple.ActionBarShowHideListener;
import com.community.yuequ.imple.PlayData;
import com.community.yuequ.modle.MessageBean;
import com.community.yuequ.modle.OnlineProgram;
import com.community.yuequ.modle.OnlineProgramDao;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.player.WhtVideoView;
import com.community.yuequ.share.ShareFragmentDialog;
import com.community.yuequ.share.ShareHelper;
import com.community.yuequ.transformations.BlurTransformation;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.DateUtil;
import com.community.yuequ.util.PlayCountHelper;
import com.community.yuequ.verticaltablayout.TabAdapter;
import com.community.yuequ.verticaltablayout.VerticalTabLayout;
import com.community.yuequ.verticaltablayout.widget.QTabView;
import com.community.yuequ.verticaltablayout.widget.TabView;
import com.community.yuequ.view.DividerItemDecoration;
import com.community.yuequ.view.PageStatuLayout;
import com.community.yuequ.widget.DialogManager;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class LiveVideoActivity extends AppCompatActivity implements View.OnClickListener,ActionBarShowHideListener,
        VerticalTabLayout.OnTabSelectedListener,ShareFragmentDialog.ShareListener {
    private static final String TAG = LiveVideoActivity.class.getSimpleName();
    private ActionBar mActionBar;
    private TextView mTitleView;
    private PageStatuLayout mStatuLayout;

    private WhtVideoView mVideoView;
    private ImageView iv_video_cover;
    private ImageView iv_play_cc;

    private TextView tv_live_pro;
    private ImageView iv_collect;
    private ImageView iv_share;
    private ImageView iv_load;
    private VerticalTabLayout mTabLayout;
    private ViewPager mViewPager;
    private Session mSession;
    private RProgram mRProgram;

    private OnlineProgramDao.OnlineProgramDaoResult mOnlineProgramDaoResult;
    private boolean isLoading = false;

    private boolean mCreated = false;
    private ProgressDialog mProgressDialog;
    private ShareFragmentDialog shareFragmentDialog;
    private ShareHelper mShareHelper;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mShareHelper.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);

        mSession = Session.get(this);
        mShareHelper = new ShareHelper(this,savedInstanceState);
        Intent intent = getIntent();
        mRProgram = (RProgram) intent.getSerializableExtra("program");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleView = (TextView) toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if(mActionBar!=null){
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,R.color.transparent));
            mActionBar.setHomeAsUpIndicator(R.mipmap.video_back_img);
        }

        mCreated = true;

        mStatuLayout = new PageStatuLayout(this)
                .setReloadListener(this)
                .hide();
        mVideoView = (WhtVideoView) findViewById(R.id.whtvideoview);
        iv_video_cover = (ImageView) findViewById(R.id.iv_video_cover);
        iv_play_cc = (ImageView) findViewById(R.id.iv_play_cc);


        tv_live_pro = (TextView) findViewById(R.id.tv_live_pro);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_load = (ImageView) findViewById(R.id.iv_load);
        mTabLayout = (VerticalTabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

//        mViewPager.addOnPageChangeListener(this);
        mTabLayout.addOnTabSelectedListener(this);
        iv_play_cc.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_load.setOnClickListener(this);

        mVideoView.setVDVideoViewContainer((ViewGroup) mVideoView
                .getParent());


        if (mRProgram != null) {
            mTitleView.setText(mRProgram.remark);
            tv_live_pro.setText(mRProgram.name);
            setCollectButton("true".equalsIgnoreCase(mRProgram.isCollection));
            Glide
                    .with(this)
                    .load(mRProgram.img_path)
                    .bitmapTransform(new BlurTransformation(this))
                    .into(iv_video_cover);


        }

        getData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_play_cc:
                if(mRProgram==null){
                    Toast.makeText(LiveVideoActivity.this, "获取视频信息失败！", Toast.LENGTH_SHORT).show();
                }else{

                    if(mActionBar!=null && mActionBar.isShowing()){
                        mActionBar.hide();
                    }
                    iv_video_cover.setVisibility(View.GONE);
                    iv_play_cc.setVisibility(View.GONE);
                    ArrayList<PlayData> programDetails = new ArrayList<>();
                    programDetails.add(mRProgram);
                    mVideoView.open(this, false, programDetails);
                    mVideoView.play(0);
                    PlayCountHelper.count(null,mRProgram.id);
                }
                break;
            case R.id.ll_status:
                if(!isLoading){
                    getData();
                }
                break;
            case R.id.iv_collect:

                if(mSession.isLogin()){
                    boolean collect = false;

                    if(mRProgram!=null){
                        collect = !"true".equalsIgnoreCase(mRProgram.isCollection);
                    }
                    collect(collect);

                }else{
//                    Toast.makeText(this, "请先登录后再操作！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this,LoginActivity.class));
                }
                break;
            case R.id.iv_share:

                shareFragmentDialog = ShareFragmentDialog.newInstance();
                shareFragmentDialog.show(getSupportFragmentManager(),"dialog");


                break;
            case R.id.iv_load:

                break;
            default:
                break;
        }
    }

    /**
     *
     * @param collect :true表示执行收藏操作,false取消收藏
     */
    private void collect(boolean collect) {
        if(mRProgram==null)

            return;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("program_id", mRProgram.id);
        String url;
        if(collect){
            url = Contants.URL_COLLECTPROGRAM;
//            hashMap.put("type", "1");//1：视频，2：图文，3：直播
        }else{
            url = Contants.URL_DELCOLLECTPROGRAM;
        }
        String content;
        try {
            content = AESUtil.encode(new Gson().toJson(hashMap));
        } catch (Exception e) {
            throw new RuntimeException("加密错误！");
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(YQApplication.getAppContext(), R.string.unknow_erro, Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpUtils
                .postString()
                .content(content)
                .url(url)
                .tag(TAG)
                .build()
                .execute(new CollectCallback(this,collect));
    }

    @Override
    public void share(int code) {
        if(code==ShareFragmentDialog.SHARE_SINA){
            mShareHelper.sendMessage(getString(R.string.app_name)+"正在播出["+mRProgram.remark+"]:"+mRProgram.img_path);

        }else if(code ==ShareFragmentDialog.SHARE_WEIXIN){
            mShareHelper.sendTextToWX(false,getString(R.string.app_name)+"正在播出["+mRProgram.remark+"]:"+mRProgram.img_path);
        }else{
            mShareHelper.sendTextToWX(true,getString(R.string.app_name)+"正在播出["+mRProgram.remark+"]:"+mRProgram.img_path);
        }
    }


    public static class CollectCallback extends JsonCallBack<MessageBean> {
        private WeakReference<LiveVideoActivity> mWeakReference;
        private final boolean mCollect;

        public CollectCallback(LiveVideoActivity activity, boolean collect) {
            mWeakReference = new WeakReference<>(activity);
            this.mCollect = collect;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            LiveVideoActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onCollectError(mCollect);
            }
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            LiveVideoActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onCollectBefore(mCollect);
            }
        }

        @Override
        public void onResponse(MessageBean response, int id) {
            LiveVideoActivity activity = mWeakReference.get();
            if (activity != null) {
                activity.onCollectResponse(mCollect,response);
            }
        }
    }

    private void onCollectError(boolean collect) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        Toast.makeText(this, collect ?"收藏失败！":"取消收藏失败！", Toast.LENGTH_SHORT).show();
    }

    private void onCollectBefore(boolean collect) {
        String message = "收藏中...";
        if(!collect){
            message = "取消收藏...";
        }
        mProgressDialog = DialogManager.getProgressDialog(this,message);
        mProgressDialog.show();
    }

    private void onCollectResponse(boolean collect,MessageBean response) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }

        if(response.errorCode==200){
            Toast.makeText(this, collect ? "收藏成功！" : "取消收藏！", Toast.LENGTH_SHORT).show();
            if(mRProgram!=null){
                mRProgram.isCollection = String.valueOf(collect);
                setCollectButton(collect);

                //跟新列表数据
                Intent intent = new Intent();
                intent.putExtra("id",mRProgram.id);
                intent.putExtra("isCollection",mRProgram.isCollection);
                setResult(18,intent);
            }
        }else{
            if(!TextUtils.isEmpty(response.errorMessage)){
                Toast.makeText(this, response.errorMessage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, collect ?"收藏失败！":"取消收藏失败！", Toast.LENGTH_SHORT).show();
            }
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
    protected void onPause() {
        super.onPause();
        if (!mCreated)
            return;
        if (mVideoView != null) {
            mVideoView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mCreated)
            return;
        if (mVideoView != null) {
            mVideoView.releaseSurface();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mCreated)
            return;

        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mVideoView.setIsFullScreen(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mVideoView.setIsFullScreen(false);
        }
    }

    @Override
    public void onBackPressed() {

        if (mVideoView != null && mVideoView.onBackPressed()) {

        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(TAG);
        if (mVideoView != null){
            mVideoView.releaseSurface();
            mVideoView.release(false);

        }
        super.onDestroy();
    }


    protected void  setCollectButton(boolean collect){

        if(collect){
            iv_collect.setImageResource(R.mipmap.icon_collect);
        }else{
            iv_collect.setImageResource(R.drawable.btn_collect_selector);
        }
    }
    private void display() {

        if (mOnlineProgramDaoResult != null && mOnlineProgramDaoResult.orderMap!=null) {

            List<Pair<String,ArrayList<OnlineProgram>>> pairList = new ArrayList<>();
            LinkedHashMap<String, ArrayList<OnlineProgram>> result = mOnlineProgramDaoResult.orderMap;
            Iterator<Map.Entry<String, ArrayList<OnlineProgram>>> iterator = result.entrySet().iterator();
            int pos = 0;
            int index = 0;
            String today = DateUtil.formatDate(mOnlineProgramDaoResult.currTime);
            while (iterator.hasNext()) {
                Map.Entry<String, ArrayList<OnlineProgram>> next = iterator.next();
                String key = next.getKey();
                ArrayList<OnlineProgram> val = next.getValue();

                if(today.equals(key)){
                    index = pos;
                    key = "今天";
                }

                Pair<String,ArrayList<OnlineProgram>> programPair = new Pair<>(key,val);
                pairList.add(programPair);

                pos++;
            }
            mViewPager.setOffscreenPageLimit(pairList.size()-1);
            mViewPager.setAdapter(new MyPagerAdapter(this,pairList,mOnlineProgramDaoResult.livingOrderId));
            mTabLayout.setupWithViewPager(mViewPager);
            final int tabSelected = index;
            mTabLayout.post(new Runnable() {
                @Override
                public void run() {
                    mTabLayout.setTabSelected(tabSelected);
                }
            });

        }
    }
    private void getData() {
        if(mRProgram==null)
            return;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("program_id", mRProgram.id);
//        hashMap.put("order_date", DateUtil.getTodayDate());
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

        OkHttpUtils
                .postString()
                .content(content)
                .url(Contants.URL_LIVEPROGRAMORDERLIST)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<OnlineProgramDao>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        isLoading = false;

                        if (mStatuLayout != null) {
                            mStatuLayout.show()
                                    .setProgressBarVisibility(false)
                                    .setText(getString(R.string.load_data_fail));
                        }
                    }

                    @Override
                    public void onResponse(OnlineProgramDao response,int id) {
                        isLoading = false;
                        mOnlineProgramDaoResult = response.result;
                        display();
                        if (mStatuLayout != null) {
                            if(response.errorCode!=Contants.HTTP_OK){
                                mStatuLayout.show()
                                        .setProgressBarVisibility(false)
                                        .setText(response.errorMessage);
                            }else{
                                mStatuLayout.hide()
                                        .setProgressBarVisibility(false)
                                        .setText(null);
                            }

                        }

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        isLoading = true;
                        mStatuLayout.show()
                                .setProgressBarVisibility(true)
                                .setText(null);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        isLoading = false;
                    }
                });
    }


    @Override
    public void onTabSelected(TabView tab, int position) {

    }

    @Override
    public void onTabReselected(TabView tab, int position) {

    }


    class MyPagerAdapter extends PagerAdapter implements TabAdapter {
        Activity mActivity;
        List<Pair<String,ArrayList<OnlineProgram>>> mList;
        private int playId;

        public MyPagerAdapter(Activity activity, List<Pair<String, ArrayList<OnlineProgram>>> list, int id) {
            mActivity = activity;
            mList = list;
            this.playId = id;
        }

        @Override
        public int getCount() {
            if(mList!=null){
                return mList.size();
            }else{
                return  0;
            }
        }

        @Override
        public int getBadge(int position) {
            return 0;
        }

        @Override
        public QTabView.TabIcon getIcon(int position) {

            return null;
        }

        @Override
        public QTabView.TabTitle getTitle(int position) {
            String tabTitle = mList.get(position).first;
            return new QTabView.TabTitle.Builder(LiveVideoActivity.this)
                    .setContent(tabTitle)
                    .setTextColor(ContextCompat.getColor(LiveVideoActivity.this,R.color.white),ContextCompat.getColor(LiveVideoActivity.this,R.color.grey700))
                    .build();
        }

        @Override
        public int getBackground(int position) {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = View.inflate(container.getContext(),R.layout.live_prolist_page_item,null);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            TextView textView = (TextView) view.findViewById(R.id.tv_text);
            Pair<String, ArrayList<OnlineProgram>> pair = mList.get(position);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));
            OnLineProgramListAdapter lineProgramListAdapter = new OnLineProgramListAdapter(mActivity,pair.second,playId);
//            recyclerView.addOnScrollListener(mScrollListener);

            recyclerView.setAdapter(lineProgramListAdapter);
            final int selectedPosition = lineProgramListAdapter.getSelectedPosition();

            if(selectedPosition !=0){
                final RecyclerView rview = recyclerView;
                rview.post(new Runnable() {
                    @Override
                    public void run() {
                        rview.scrollToPosition(selectedPosition);
                    }
                });

            }
            if(pair.second!=null && !pair.second.isEmpty()){
                textView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }else{
                textView.setVisibility(View.VISIBLE);
                textView.setText("没有节目单！");
                recyclerView.setVisibility(View.GONE);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }



    public class OnLineProgramListAdapter extends RecyclerView.Adapter<PViewHolder>{
        private Activity mContext;
        private ArrayList<OnlineProgram> mOnlinePrograms;
        private int playId;
        private int selectedPosition = 0;

        public OnLineProgramListAdapter(Activity activity, ArrayList<OnlineProgram> list, int id){
            this.mContext = activity;
            this.mOnlinePrograms = list;
            this.playId = id;
//            setHasStableIds(true);
            if(mOnlinePrograms!=null){
                for (int i = 0;i<mOnlinePrograms.size();i++){
                    if(playId==mOnlinePrograms.get(i).id){
                        selectedPosition = i;
                        break;
                    }
                }

            }
        }


        @Override
        public PViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_prolist_item,parent,false);
            return new PViewHolder(view);


        }

        @Override
        public void onBindViewHolder(PViewHolder holder, int position) {
            final OnlineProgram onlineProgram = mOnlinePrograms.get(position);
            if(onlineProgram.id==playId){
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                holder.tv_time.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                holder.tv_play.setVisibility(View.VISIBLE);

            }else{
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.grey900));
                holder.tv_time.setTextColor(ContextCompat.getColor(mContext,R.color.grey900));
                holder.tv_play.setVisibility(View.GONE);
            }
            holder.tv_title.setText(onlineProgram.name);
            holder.tv_time.setText(onlineProgram.begin_time);
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    playProgram = onlineProgram;
//
//                     if(mActionBar!=null && mActionBar.isShowing()){
//                        mActionBar.hide();
//                    }
//                    iv_video_cover.setVisibility(View.GONE);
//                    iv_play_cc.setVisibility(View.GONE);
//                    ArrayList<PlayData> programDetails = new ArrayList<>();
//                    programDetails.add(playProgram);
//                    mVideoView.open(mContext, false, programDetails);
//                    mVideoView.play(0);
//
//                    for (OnLineProgramListAdapter adapter : mOnLineProgramListAdapters){
//
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            if(mOnlinePrograms==null)return 0;
            return mOnlinePrograms.size();
        }



        public int getSelectedPosition() {
            return selectedPosition;
        }
    }

    public class PViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_title;
        public TextView tv_time;
        public TextView tv_play;

        public PViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_play = (TextView) itemView.findViewById(R.id.tv_play);
        }
    }



    @Override
    public void showActionBar() {
        if(mActionBar!=null && !mActionBar.isShowing()){
            mActionBar.show();
        }
    }

    @Override
    public void hideActionBar() {
        if(mActionBar!=null && mActionBar.isShowing()){
            mActionBar.hide();
        }
    }


}
