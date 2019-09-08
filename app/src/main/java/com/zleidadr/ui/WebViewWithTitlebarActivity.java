package com.zleidadr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zleidadr.R;
import com.zleidadr.common.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewWithTitlebarActivity extends Activity {

    /**
     * Intent intent = new Intent(this, WebViewActivity.class);
     * intent.putExtra(WebViewActivity.BUNDLE_WEB_TYPE, WebViewActivity.WEB_URL);
     * intent.putExtra(WebViewActivity.BUNDLE_WEB_URL, "https://m.jimu.com/user/withdraw/rule");
     * startActivity(intent);
     */

    public static final String BUNDLE_TITLE = "title";
    public static final String BUNDLE_WEB_URL = "url";
    private static final String TAG = WebViewWithTitlebarActivity.class.getName();
   @BindView(R.id.webView)
    WebView mWebView;
   @BindView(R.id.tv_back)
    FrameLayout mIvTitleBack;
   @BindView(R.id.tv_title)
    TextView mTvTitleCenterText;

    private String mWebUrl;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_with_titlebar);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            mTitle = intent.getStringExtra(BUNDLE_TITLE);
            mWebUrl = intent.getStringExtra(BUNDLE_WEB_URL);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        initWebView();
    }

    private void initWebView() {

        mIvTitleBack.setVisibility(View.VISIBLE);
        mTvTitleCenterText.setVisibility(View.VISIBLE);
        mTvTitleCenterText.setText(mTitle);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Logger.d(TAG, "onPageFinished url: " + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // 设置缓存模式
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAppCachePath(getCacheDir() + "");
//
        mWebView.loadUrl(mWebUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 检查是否为返回事件，如果有网页历史记录
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // 如果不是返回键或没有网页浏览历史，保持默认
        // 系统行为（可能会退出该活动）
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.tv_back)
    public void onClick() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }
}
