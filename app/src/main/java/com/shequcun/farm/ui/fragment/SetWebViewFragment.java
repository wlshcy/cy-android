package com.shequcun.farm.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shequcun.farm.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by apple on 15/8/25.
 */
public class SetWebViewFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.webview_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        ((TextView) v.findViewById(R.id.title_center_text)).setText(buildTitleId());
    }


    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    @Override
    protected void setWidgetLsn() {
        setWebViewValue();
    }

    String buildUrl() {
        Bundle bundle = getArguments();
        return bundle != null ? bundle.getString("Url") : null;
    }

    int buildTitleId() {
        Bundle bundle = getArguments();
        return bundle != null ? bundle.getInt("TitleId") : R.string.about;
    }


    @SuppressLint("SetJavaScriptEnabled")
    void setWebViewValue() {
        WebSettings settings = mWebView.getSettings();
        // 如果访问的页面中有Javascript，则webview必须设置支持Javascript
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        String htmlUrl = buildUrl();
        if (htmlUrl == null) return;
//      mWebView.loadData(htmlUrl, "text/html", "UTF-8");
        mWebView.loadUrl(htmlUrl);
        // 滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上
        mWebView.setScrollBarStyle(0);
        // android4.2以前
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                return true;
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
    final int PROGRESS_LOADING = 1;
    final int PROGRESS_SUCCESS = 2;
    @Bind(R.id.mWebView)
    WebView mWebView;
    @Bind(R.id.seekbar)
    SeekBar mProgressBar;
}
