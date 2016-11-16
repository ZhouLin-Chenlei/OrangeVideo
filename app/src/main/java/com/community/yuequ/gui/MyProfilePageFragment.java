package com.community.yuequ.gui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.contorl.ImageManager;
import com.community.yuequ.modle.ChannelDao;
import com.community.yuequ.modle.UserInfo;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 我的
 */
public class MyProfilePageFragment extends BaseTabFragment implements View.OnClickListener {
    private final static String TAG = MyProfilePageFragment.class.getSimpleName();


    //登录显示的头部View
    private LinearLayout ll_ligin_head;
    private ImageView iv_head;
    private TextView tv_name;
    private ImageView iv_gender;
    private TextView tv_userid;
    private TextView tv_desc;


    //未登录显示的头部View
    private RelativeLayout rl_nologin_head;


    private RelativeLayout rl_group_history;
    private TextView iv_history_new;


    private RelativeLayout rl_group_collect;
    private TextView iv_collect_new;


    private RelativeLayout rl_group_feedback;


    private RelativeLayout rl_group_about;

    private Session mSession;
    public MyProfilePageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initData();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.ACTION_LOGIN);
        filter.addAction(Contants.ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,filter);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Contants.ACTION_LOGIN.equals(action)){
                display();

            }else if(Contants.ACTION_LOGOUT.equals(action)){
                display();

            }else if(Contants.ACTION_EDIT_USERINFO.equals(action)){
                display();

            }
        }
    };

    private void display() {

        if(mSession.isLogin()){
            UserInfo userInfo = mSession.getUserInfo();
            rl_nologin_head.setVisibility(View.GONE);
            ll_ligin_head.setVisibility(View.VISIBLE);
            ImageManager.getInstance().loadUrlImage(this,userInfo.head_img_path,iv_head);
            tv_name.setText(userInfo.nick_name);
            if("女".equals(userInfo.sex)){
                iv_gender.setImageResource(R.mipmap.icon_female);
            }else{
                iv_gender.setImageResource(R.mipmap.icon_male);
            }
            String idStr = "ID："+userInfo.id;
            tv_userid.setText(idStr);
            tv_desc.setText(userInfo.person_sign);

        }else{
            rl_nologin_head.setVisibility(View.VISIBLE);
            ll_ligin_head.setVisibility(View.GONE);

        }
        ((MainActivity)getActivity()).setMyProfileToolbar();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSession = Session.get(getActivity());

        registerReceiver();
        View convertView = inflater.inflate(R.layout.fragment_myprofile, container, false);


        ll_ligin_head = (LinearLayout) convertView.findViewById(R.id.ll_ligin_head);
        iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
        tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        iv_gender = (ImageView) convertView.findViewById(R.id.iv_gender);
        tv_userid = (TextView) convertView.findViewById(R.id.tv_userid);
        tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);


        rl_nologin_head = (RelativeLayout) convertView.findViewById(R.id.rl_nologin_head);


        rl_group_history = (RelativeLayout) convertView.findViewById(R.id.rl_group_history);
        iv_history_new = (TextView) convertView.findViewById(R.id.iv_history_new);


        rl_group_collect = (RelativeLayout) convertView.findViewById(R.id.rl_group_collect);
        iv_collect_new = (TextView) convertView.findViewById(R.id.iv_collect_new);


        rl_group_feedback = (RelativeLayout) convertView.findViewById(R.id.rl_group_feedback);


        rl_group_about = (RelativeLayout) convertView.findViewById(R.id.rl_group_about);
        iv_history_new.setVisibility(View.GONE);
        iv_collect_new.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ll_ligin_head.setOnClickListener(this);
        rl_nologin_head.setOnClickListener(this);

        rl_group_history.setOnClickListener(this);
        rl_group_collect.setOnClickListener(this);
        rl_group_feedback.setOnClickListener(this);
        rl_group_about.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        display();

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
                        if (isAdded()) {
                            getDataFail();
                        }
                    }

                    @Override
                    public void onResponse(ChannelDao response, int id) {


                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        getDataBefore();
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        if (isAdded()) {
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


    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        OkHttpUtils.getInstance().cancelTag(TAG);
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ll_ligin_head:
                if(mSession.isLogin()) {
                    Intent intent_myprofile = new Intent(getActivity(), MyprofileActivity.class);
                    intent_myprofile.putExtra("user", mSession.getUserInfo());
                    startActivity(intent_myprofile);
                }else{
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.rl_nologin_head:
                startActivity(new Intent(getActivity(),LoginActivity.class));
                break;
            case R.id.rl_group_history:
                break;
            case R.id.rl_group_collect:
                if(mSession.isLogin()){
                    Intent intent_collect = new Intent(getActivity(), MyCollectActivity.class);
                    intent_collect.putExtra("user",mSession.getUserInfo());
                    startActivity(intent_collect);

                }else{
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                break;
            case R.id.rl_group_feedback:
                break;
            case R.id.rl_group_about:
                startActivity(new Intent(getActivity(),MyCollectActivity.class));
                break;

        }
    }
}
