package com.shequcun.farm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.http.HttpAction;

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
        if (TextUtils.isEmpty(xsrf))
            new HttpAction(context).init();
    }
}
