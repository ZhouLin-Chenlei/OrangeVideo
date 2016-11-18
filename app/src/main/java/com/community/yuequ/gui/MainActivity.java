package com.community.yuequ.gui;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.bottombar.BottomBar;
import com.community.yuequ.bottombar.OnTabReselectListener;
import com.community.yuequ.bottombar.OnTabSelectListener;
import com.community.yuequ.imple.DialogConfListener;
import com.community.yuequ.modle.InitMsg;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.modle.UserInfo;
import com.community.yuequ.util.TabMessage;
import com.community.yuequ.widget.WelComeTipsDialog;

import java.lang.ref.WeakReference;

/**
 * 主页Activity，装载4个Fragment
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnTabSelectListener,OnTabReselectListener,DialogConfListener {

    protected SharedPreferences mSettings;
    private long lastTime = 0;
    private MyHandler mMyHandler;
    private Session mSession;
    public static final int SHOW_WELCOME_DIALOG = 1;

    private Toolbar mToolbar;
    private TextView mTitleView;
    private  BottomBar bottomBar;
    private int mCurrentFragmentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        /* Get settings */
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        mSession = Session.get(this);
        mMyHandler = new MyHandler(this);
        if(savedInstanceState==null){

            InitMsg initMsg = mSession.getInitMsg();
            if(initMsg!=null && initMsg.orderTips!=null && !initMsg.orderTips.isEmpty()){
                OrderTip orderTip = initMsg.orderTips.get(0);
                if(!TextUtils.isEmpty(orderTip.gust_tips)){
                    Message message = mMyHandler.obtainMessage(SHOW_WELCOME_DIALOG);
                    message.obj = orderTip.gust_tips;
                    mMyHandler.sendMessageDelayed(message,500);
                }
            }
        }else{

            mCurrentFragmentId = savedInstanceState.getInt("current");
            if (mCurrentFragmentId > 0){
                bottomBar.selectTabWithId(mCurrentFragmentId);
                setToolbar(mCurrentFragmentId);

            }
        }

        /* Reload the latest preferences */
        reloadPreferences();
    }

    private void reloadPreferences() {
        mCurrentFragmentId = mSettings.getInt("fragment_id", R.id.tab_home);
    }

    private void setToolbar(int id){
        mTitleView.setText(TabMessage.get(this,id));
        String tag = getTag(id);
        if(ID_MYPROFILE.equals(tag)){
            setMyProfileToolbar();

        }else{
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && !actionBar.isShowing()) {
                actionBar.show();
            }
        }

    }

    public void setMyProfileToolbar(){
        UserInfo userInfo = mSession.getUserInfo();
        if(userInfo!=null){
            mTitleView.setText("");
        }else{

        }

//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null && actionBar.isShowing()) {
//                actionBar.hide();
//            }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.


    }
    @Override
    protected void onResume() {
        super.onResume();
        bottomBar.selectTabWithId(mCurrentFragmentId);
        setToolbar(mCurrentFragmentId);
        mCurrentFragmentId = mSettings.getInt("fragment_id",R.id.tab_home);
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        // Figure out if currently-loaded fragment is a top-level fragment.
        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placeholder);

        /**
         * Restore the last view.
         *
         * Replace:
         * - null fragments (freshly opened Activity)
         * - Wrong fragment open AND currently displayed fragment is a top-level fragment
         *
         * Do not replace:
         * - Non-sidebar fragments.
         * It will try to remove() the currently displayed fragment
         * (i.e. tracks) and replace it with a blank screen. (stuck menu bug)
         */
        if (current == null) {
            bottomBar.selectTabWithId(mCurrentFragmentId);
            setToolbar(mCurrentFragmentId);
            Fragment ff = getFragment(mCurrentFragmentId);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, ff, getTag(mCurrentFragmentId));
            ft.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Deactivated for now
//        createExtensionServiceConnection();

        bottomBar.setOnTabSelectListener(this);
        bottomBar.setOnTabReselectListener(this);


    }

    @Override
    protected void onStop() {
        super.onStop();
        bottomBar.setOnTabSelectListener(null);
        bottomBar.setOnTabReselectListener(null);

    }

    /**
     * Stop audio player and save opened tab
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (getChangingConfigurations() == 0) {
//            /* Check for an ongoing scan that needs to be resumed during onResume */
//            mScanNeeded = mMediaLibrary.isWorking();
//            /* Stop scanning for files */
//            mMediaLibrary.stop();
        }
        /* Save the tab status in pref */
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("fragment_id", mCurrentFragmentId);
        editor.apply();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current", mCurrentFragmentId);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /* Reload the latest preferences */
        reloadPreferences();
    }

    protected static final String ID_HOME = "home";
    protected static final String ID_ONLINE = "online";
    protected static final String ID_VIDEOS = "videos";
    protected static final String ID_MYPROFILE = "myprofile";

    private String getTag(int id){
        switch (id) {
            case R.id.tab_home:
                return ID_HOME;
            case R.id.tab_online:
                return ID_ONLINE;
            case R.id.tab_videos:
                return ID_VIDEOS;
            case R.id.tab_myprofile:
                return ID_MYPROFILE;
            default:
                return ID_HOME;
        }
    }

    private Fragment getFragment(int id)
    {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(getTag(id));
        if (frag != null)
            return frag;
        switch (id) {
            case R.id.tab_home:
                return new RecommendFragment();
            case R.id.tab_online:
                return new OnLinePageFragment();
            case R.id.tab_videos:
                return new VideoPageFragment();
            case R.id.tab_myprofile:
                return new MyProfilePageFragment();
            default:
                return new RecommendFragment();
        }
    }
    private void clearBackstackFromClass(Class clazz) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_placeholder);
        while (clazz.isInstance(current)) {
            if (!fm.popBackStackImmediate())
                break;
            current = getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_placeholder);
        }
    }
    @Override
    public void conf() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        int id = tabId;

        FragmentManager fm = getSupportFragmentManager();
        Fragment current = fm.findFragmentById(R.id.fragment_placeholder);
        if (current == null) {
            return;
        }
        if(mCurrentFragmentId == id) { /* Already selected */
            // Go back at root level of current browser
//            if (current instanceof BaseBrowserFragment && !((BaseBrowserFragment) current).isRootDirectory()) {
//                clearBackstackFromClass(current.getClass());
//            } else {

                return;
//            }
        }
        String tag = getTag(id);


        /* Switch the fragment */
        Fragment fragment = getFragment(id);
        if (fragment instanceof BaseTabFragment)
            ((BaseTabFragment)fragment).setReadyToDisplay(false);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment, tag);
        ft.addToBackStack(getTag(mCurrentFragmentId));
        ft.commit();
        mCurrentFragmentId = id;
        bottomBar.selectTabWithId(mCurrentFragmentId);
        setToolbar(mCurrentFragmentId);
    }

    @Override
    public void onTabReSelected(@IdRes int tabId) {

    }


    private static class MyHandler extends Handler {
            private WeakReference<MainActivity> activityWeakReference;

            public MyHandler(MainActivity activity) {
                activityWeakReference = new WeakReference<>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                MainActivity activity = activityWeakReference.get();
                if (activity != null) {
                    switch (msg.what){
                        case SHOW_WELCOME_DIALOG:
                            activity.showWelcomeDialog(((String)msg.obj));
                            break;
                        default:
                            break;


                    }
                }
            }
    }

    private void showWelcomeDialog(String tips) {
        WelComeTipsDialog tipsDialog = WelComeTipsDialog.newInstance(tips);
        tipsDialog.show(getSupportFragmentManager(),"dialog");
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - lastTime < 2000) {
            // If it's the directory view, a "backpressed" action shows a parent.
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_placeholder);
//            if (fragment instanceof BaseBrowserFragment){
//                ((BaseBrowserFragment)fragment).goBack();
//                return;
//            } else if (fragment instanceof ExtensionBrowser) {
//                ((ExtensionBrowser) fragment).goBack();
//                return;
//            }
            finish();
        } else {
            lastTime = System.currentTimeMillis();
            Toast.makeText(YQApplication.getAppContext(), R.string.toast_exit_tip, Toast.LENGTH_SHORT).show();
        }
    }

}
