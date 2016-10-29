package com.community.yuequ.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.community.yuequ.Contants;
import com.community.yuequ.R;
import com.community.yuequ.Session;
import com.community.yuequ.YQApplication;
import com.community.yuequ.imple.DialogConfListener;
import com.community.yuequ.modle.MessageBean;
import com.community.yuequ.modle.OrderTip;
import com.community.yuequ.modle.OrderTipsDao;
import com.community.yuequ.modle.RProgram;
import com.community.yuequ.modle.RProgramDetail;
import com.community.yuequ.modle.RProgramDetailDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.AESUtil;
import com.community.yuequ.util.HtmlUtil;
import com.community.yuequ.view.TitleBarLayout;
import com.community.yuequ.widget.GoChargDialog;
import com.community.yuequ.widget.InputPhoneNumberDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class PicDetailActivity extends AppCompatActivity implements View.OnClickListener, InputPhoneNumberDialog.PhoneNumberCallBack, DialogConfListener {
    public static final String TAG = VideoDetailActivity.class.getSimpleName();

    private TitleBarLayout mTitleBarLayout;

    private RProgram mRProgram;
    private Session mSession;
    private RProgramDetailDao programDetailDao;
    private RProgramDetail programDetail;

    WebView mWebView;
//    private TextView tv_content;
    TextView tvErrorMsg;
    ProgressBar progressBar;
    private WebSettings settings;
    private TextView tv_second_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);
        mSession = Session.get(this);
        Intent intent = getIntent();
        mRProgram = (RProgram) intent.getSerializableExtra("program");
        mTitleBarLayout = new TitleBarLayout(this)
                .setLeftButtonVisibility(true)
                .setLeftButtonClickListener(this);
        mWebView = (WebView) findViewById(R.id.detail_web_view);
//        tv_content = (TextView) findViewById(R.id.tv_content);
        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_second_title = (TextView) findViewById(R.id.tv_second_title);

        mTitleBarLayout.setText(getString(R.string.detail));
        tv_second_title.setText(mRProgram.name);
//        initView();
        getData();
    }

    private void getData() {
        if (mRProgram == null) {
            Toast.makeText(YQApplication.getAppContext(), R.string.load_data_fail, Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("program_id", mRProgram.id);
        hashMap.put("imei", mSession.getIMEI());
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
                .url(Contants.URL_PROGRAMDETAIL)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<RProgramDetailDao>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(RProgramDetailDao response, int id) {
                        programDetailDao = response;
                        programDetail = response.result;
                        if (response.errorCode == Contants.HTTP_NO_PERMISSION) {
                            GoChargDialog chargDialog = GoChargDialog.newInstance();
                            chargDialog.show(getSupportFragmentManager(), "charg");


                        } else if (response.errorCode == Contants.HTTP_OK) {
//                            GoChargDialog chargDialog = GoChargDialog.newInstance();
//                            chargDialog.show(getSupportFragmentManager(),"charg");
                            if (!TextUtils.isEmpty(programDetail.contents)) {
//                                mWebView.loadData(body, "text/html; charset=UTF-8", null);

                                //设置web内容加载
                                String htmlData = HtmlUtil.createHtmlData(programDetail);
//                                Spanned spanned = Html.fromHtml(programDetail.getBody(), new URLImageParser(PicDetailActivity.this, tv_content), null);
//                                tv_content.setText(spanned);

                                mWebView.loadData(htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);
                            }
                        } else if (!TextUtils.isEmpty(programDetailDao.errorMessage)) {
                            Toast.makeText(YQApplication.getAppContext(), programDetailDao.errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(YQApplication.getAppContext(), R.string.unknow_erro, Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void toBuy() {
        String phoneNumber = mSession.getPhoneNumber();
        if (TextUtils.isEmpty(phoneNumber)) {
            InputPhoneNumberDialog phoneNumberDialog = InputPhoneNumberDialog.newInstance();
            phoneNumberDialog.show(getSupportFragmentManager(), "phoneNumber");
        } else {
            toBuyStep2();

        }
    }

    private void setphoneNumber(String phoneNumber) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("tel", phoneNumber);
        hashMap.put("imei", mSession.getIMEI());
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
                .url(Contants.URL_UPDATEUSER)
                .tag(TAG)
                .build()
                .execute(new MyUpdateUserCallBack(this));
    }



    @Override
    public void conf() {
        toBuy();
    }

    @Override
    public void cancel() {
        finish();
    }

//    @Override
//    public Drawable getDrawable(String source) {
//        InputStream is = null;
//
//        try {
//            is = (InputStream) new URL(source).openStream();
//            Drawable d = Drawable.createFromStream(is, "");
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            is.close();
//            return d;
//        } catch (Exception e) {
//            return null;
//        }
//
//    }

    private static class MyUpdateUserCallBack extends JsonCallBack<MessageBean> {
        private WeakReference<PicDetailActivity> mWeakReference;

        public MyUpdateUserCallBack(PicDetailActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            PicDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, "设置手机号错误！", Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onResponse(MessageBean response, int id) {
            if (response.errorCode == Contants.HTTP_OK) {
                PicDetailActivity activity = mWeakReference.get();
                if (activity != null) {
                    activity.toBuyStep2();
                }
            } else {
                Toast.makeText(YQApplication.getAppContext(), "设置手机号错误！", Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void onBefore(Request request, int id) {
            PicDetailActivity activity = mWeakReference.get();
            if (activity != null) {

            }
        }
    }

    private void toBuyStep2() {

        if (mSession.haveOrderTips()) {
            goPayPage(mSession.getOrderTips());
        } else {
            OkHttpUtils
                    .post()
                    .url(Contants.URL_ORDERTIPS)
                    .tag(TAG)
                    .build()
                    .execute(new MyOrderTipsCallBack(this));
        }
    }

    private void setOrderTips(ArrayList<OrderTip> result) {
        if (result != null && !result.isEmpty()) {
            mSession.setOrderTips(result);
            goPayPage(result);
        } else {
            Toast.makeText(YQApplication.getAppContext(), "计费信息获取失败，请重试！", Toast.LENGTH_SHORT).show();
        }
    }

    private void goPayPage(ArrayList<OrderTip> result) {
        Intent intent = new Intent(this, PayListActivity.class);
        intent.putExtra("programId", mRProgram.id);
        intent.putParcelableArrayListExtra("ordertips", result);
        startActivity(intent);
    }

    protected static class MyOrderTipsCallBack extends JsonCallBack<OrderTipsDao> {
        private WeakReference<PicDetailActivity> mWeakReference;

        public MyOrderTipsCallBack(PicDetailActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            PicDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, "获取信息失败！", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onResponse(OrderTipsDao response, int id) {
            PicDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                if (response.errorCode == Contants.HTTP_OK) {
                    activity.setOrderTips(response.result);
                } else {
                    Toast.makeText(activity, "获取信息失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


  private void initView() {
        settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true); //如果访问的页面中有Javascript，则WebView必须设置支持Javascript
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(false); //支持缩放
        settings.setBuiltInZoomControls(false); //支持手势缩放
        settings.setDisplayZoomControls(false); //是否显示缩放按钮
        // >= 19(SDK4.4)启动硬件加速，否则启动软件加速
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            settings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        } else {
//            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            settings.setLoadsImagesAutomatically(true);
//        }
//        settings.setTextZoom(200);
//        settings.setUseWideViewPort(true); //将图片调整到适合WebView的大小
//        settings.setLoadWithOverviewMode(true); //自适应屏幕
//        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setSaveFormData(true);
//        settings.setSupportMultipleWindows(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //优先使用缓存
//        settings.setDefaultTextEncodingName("utf-8");

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //可使滚动条不占位
//        mWebView.setHorizontalScrollbarOverlay(true);
//        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.requestFocus();

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                mWebView.setLayerType(View.LAYER_TYPE_NONE, null);
//                if (!settings.getLoadsImagesAutomatically()) {
//                    settings.setLoadsImagesAutomatically(true);
//                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressBar.setVisibility(View.GONE);
//                toolbar.setTitle("加载失败");
                if (!TextUtils.isEmpty(description)) {
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText("errorCode: " + errorCode + "\ndescription: " + description);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress >= 100) {
//                    progressBar.setVisibility(View.GONE);
//                } else {
//                    if (progressBar.getVisibility() == View.GONE) {
//                        progressBar.setVisibility(View.VISIBLE);
//                    }
//                    progressBar.setProgress(newProgress);
//                }
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                toolbar.setTitle(title);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
//            mWebView.goBack();//返回上一页面
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_play:

                break;
            default:
                break;
        }
    }

    @Override
    public void phoneNumber(String phoneNumber) {
        setphoneNumber(phoneNumber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }
}
