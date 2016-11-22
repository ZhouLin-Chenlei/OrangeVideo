package com.community.yuequ.gui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import com.community.yuequ.YQApplication;
import com.community.yuequ.modle.AboutDao;
import com.community.yuequ.modle.callback.JsonCallBack;
import com.community.yuequ.util.HtmlUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import okhttp3.Request;

public class AboutActivity extends AppCompatActivity {
    public static final String TAG = VideoDetailActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TextView mTitleView;

    WebView mWebView;
    TextView tvErrorMsg;
    ProgressBar progressBar;
    private WebSettings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitleView.setText(getString(R.string.about));

        mWebView = (WebView) findViewById(R.id.detail_web_view);

        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


//        initView();
        getData();
    }

    private void getData() {



        OkHttpUtils
                .post()
                .url(Contants.URL_ABOUT)
                .tag(TAG)
                .build()
                .execute(new JsonCallBack<AboutDao>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(AboutDao response, int id) {

                        if (response.errorCode == Contants.HTTP_OK) {
                            if (!TextUtils.isEmpty(response.result)) {
//                                mWebView.loadData(body, "text/html; charset=UTF-8", null);

                                //设置web内容加载
                                String htmlData = HtmlUtil.createHtmlTag(response.result);
//                                Spanned spanned = Html.fromHtml(programDetail.getBody(), new URLImageParser(PicDetailActivity.this, tv_content), null);
//                                tv_content.setText(spanned);

                                mWebView.loadData(htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);
                            }else{
                                tvErrorMsg.setText(R.string.no_data);
                            }
                        } else if (!TextUtils.isEmpty(response.errorMessage)) {
                            Toast.makeText(YQApplication.getAppContext(), response.errorMessage, Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
                finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
