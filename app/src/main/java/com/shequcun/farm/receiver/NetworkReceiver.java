package com.shequcun.farm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.LocalParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by cong on 15/10/23.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private NetworkListener networkListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            if (networkListener != null) {
                networkListener.onNetworkChanged(networkInfo.getType());
                requestAuthInit(context);
            }
        } else {
            if (networkListener != null) {
                networkListener.onNetworkChanged(-1);
            }
        }
    }

    public interface NetworkListener {
        void onNetworkChanged(int type);
    }

    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    /**
     * 每次有网络，且本地没有Xsrftoken的时候都请求一次
     */
    private void requestAuthInit(final Context context) {
        String xsrf = PersistanceManager.getCookieValue(context);
        if (!TextUtils.isEmpty(xsrf))
            return;
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
