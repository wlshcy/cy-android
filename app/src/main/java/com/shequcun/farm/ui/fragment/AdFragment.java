package com.shequcun.farm.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.util.AvoidDoubleClickListener;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 广告页
 * Created by apple check_turn_on 15/7/24.
 */
public class AdFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.webview_ly, container, false);
    }


    @Override
    protected void initWidget(View v) {
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.app_name);
    }

    @Override
    protected void setWidgetLsn() {
        setWebViewValue();
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setWebViewValue() {
        WebSettings settings = mWebView.getSettings();
        // 如果访问的页面中有Javascript，则webview必须设置支持Javascript
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl(buildAdUrl());
        // 滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上
//        mWebView.setScrollBarStyle(0);
        mWebView.setScrollBarStyle(0);

        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

        });
        // 判断页面加载过程
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                Message msg = mHandler.obtainMessage();
                msg.arg1 = newProgress;
                if (newProgress == 100) {
                    msg.what = PROGRESS_SUCCESS;
                } else {
                    msg.what = PROGRESS_LOADING;
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    final int PROGRESS_LOADING = 1;
    final int PROGRESS_SUCCESS = 2;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS_LOADING:
                    mProgressBar.setProgress(msg.arg1);
                    break;
                case PROGRESS_SUCCESS:
                    mProgressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    String buildAdUrl() {
        Bundle bundle = getArguments();
        return bundle != null ? bundle.getString("AdUrl") : null;
    }

    @Bind(R.id.mWebView)
    WebView mWebView;
    @Bind(R.id.back)
    View back;
    @Bind(R.id.seekbar)
    SeekBar mProgressBar;
}
