package com.shequcun.farm.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by apple on 15/8/20.
 */
public class WebViewFragment extends BaseFragment {
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
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.combo_introduce);
        ((TextView) v.findViewById(R.id.title_right_text)).setText(R.string.consultation);
    }

    @Override
    protected void setWidgetLsn() {
        setWebViewValue();
    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    @OnClick(R.id.title_right_text)
    void doClick() {
        ConsultationDlg.showCallTelDlg(getActivity());
    }

    String[] buildUrl() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return null;
        ComboEntry entry = (ComboEntry) bundle.getSerializable("ComboEntry");
        if (entry == null)
            return null;
        String tiles[] = entry.tiles;
        return tiles;
    }

    private String getHtml(String imgs[]) {
        if (imgs == null || imgs.length <= 0) {
            return null;
        }
        StringBuffer s = new StringBuffer();
        s.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>img列表页</title>\n" +
                "    <style>\n" +
                "        *{margin: 0;padding: 0;}\n" +
                "        /*html { overflow-y: hidden; }*/\n" +
                "        .container {width: 100%;}\n" +
                "        .container img.lazy {display: block;width: 100%;}\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"container\">\n");
        for (String img : imgs) {
            s.append("<img v-repeat=\"imgs\" class=\"lazy img-responsive\" src=\"");
            s.append(img);
            s.append("\"alt=\"\"/>");
        }
        s.append("</div>\n" +
                "</body>\n" +
                "</html>");
        return s.toString();
    }

    @SuppressLint("SetJavaScriptEnabled")
    void setWebViewValue() {
        WebSettings settings = mWebView.getSettings();
        // 如果访问的页面中有Javascript，则webview必须设置支持Javascript
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

//        settings.setBuiltInZoomControls(true);
        settings.setLoadsImagesAutomatically(true);

        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        String htmlUrl = getHtml(buildUrl());
        if (htmlUrl == null) return;
        mWebView.loadData(htmlUrl, "text/html", "UTF-8");

//        mWebView.loadUrl(buildUrl());
        // 滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上
        mWebView.setScrollBarStyle(0);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Bind(R.id.mWebView)
    WebView mWebView;
    @Bind(R.id.seekbar)
    SeekBar mProgressBar;
    final int PROGRESS_LOADING = 1;
    final int PROGRESS_SUCCESS = 2;
}
