package com.shequcun.farm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class HttpRequestUtil {

    private static AsyncHttpClient hClient = null;
    private static PersistentCookieStore myCookieStore = null;
    private static Context mContext;

    public static void setContext(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (hClient == null) {
            initHttpClient();
        }
    }

    public static void initHttpClient() {
        hClient = new AsyncHttpClient();
        hClient.addHeader("Accept-Encoding", "gzip");
        hClient.addHeader("User-Agent", "youcai/" + DeviceInfo.getVersion(mContext)
                + " android/" + DeviceInfo.getReleseVersion() + " device/"
                + DeviceInfo.getModelName());
        SSLSocketFactory.getSocketFactory().setHostnameVerifier(
                new AllowAllHostnameVerifier());
        hClient.setTimeout(10000);
        myCookieStore = new PersistentCookieStore(mContext);
        hClient.setCookieStore(myCookieStore);
    }

    public static void httpGet(String urlString, AsyncHttpResponseHandler res) {
        if (hClient != null)
            hClient.get(urlString, res);

    }

    public static void httpGet(String urlString, RequestParams params,
                               AsyncHttpResponseHandler res) {
        if (hClient != null)
            hClient.get(urlString, params, res);
    }

    public static void httpGet(String urlString, JsonHttpResponseHandler res) {
        if (hClient != null)
            hClient.get(urlString, res);
    }

    public static void httpGet(String urlString, RequestParams params,
                               JsonHttpResponseHandler res) {
        if (hClient != null)
            hClient.get(urlString, params, res);

    }

    public static void httpGet(String uString,
                               BinaryHttpResponseHandler bHandler) {

        if (hClient != null)
            hClient.get(uString, bHandler);

    }

    public static void httpPost(String urlString, AsyncHttpResponseHandler res) {
        if (hClient != null)
            hClient.post(urlString, res);
    }

    public static void httpPost(String urlString, RequestParams params,
                                AsyncHttpResponseHandler res) {
        if (hClient != null)
            hClient.post(urlString, params, res);

    }

    public static void httpPost(String urlString, JsonHttpResponseHandler res) {
        if (hClient != null)
            hClient.post(urlString, res);
    }

    public static AsyncHttpClient getClient() {
        return hClient;
    }

    public static PersistentCookieStore getCookieObj() {
        if (myCookieStore == null) {
            myCookieStore = new PersistentCookieStore(mContext);
        }

        return myCookieStore;
    }

    /**
     * @param context
     * @return
     */
    public static boolean netWorkIsValid(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.isConnectedOrConnecting() == false) {
            return false;
        }
        return true;
    }

    public static final void addGzipHeader() {
        if (hClient != null)
            hClient.addHeader("Accept-Encoding", "gzip, deflate");
    }

    /**
     * 上传多张图片Http
     *
     * @param mContext
     * @return
     */
    public static AsyncHttpClient getHttpClient(Context mContext) {
        AsyncHttpClient hClient = new AsyncHttpClient();
        hClient.addHeader("Accept-Encoding", "gzip");
        hClient.addHeader("User-Agent", "youcai/" + DeviceInfo.getVersion(mContext)
                + " android/" + DeviceInfo.getReleseVersion() + " device/"
                + DeviceInfo.getModelName());
        SSLSocketFactory.getSocketFactory().setHostnameVerifier(
                new AllowAllHostnameVerifier());
        hClient.setTimeout(10000);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(
                mContext);
        hClient.setCookieStore(myCookieStore);
        return hClient;
    }

    public static void release() {
        if (hClient != null) {
            hClient.cancelAllRequests(true);
            hClient = null;
        }
        myCookieStore = null;
        mContext = null;
    }

}
