package com.shequcun.farm.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.LocalParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by cong on 15/10/27.
 */
public class HttpAction {
    private Context context;

    public HttpAction(Context context) {
        this.context = context;
    }

    public void init() {
        HttpRequestUtil.getHttpClient(context).get(
                LocalParams.getBaseUrl() + "auth/init",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onSuccess(int sCode, Header[] headers, byte[] data) {
                        for (cz.msebera.android.httpclient.Header h : headers) {
                            if (h.getName().equals("X-Xsrftoken")) {
                                PersistanceManager.saveCookieValue(context, h.getValue());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                    }
                });
    }
}
