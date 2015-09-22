package com.shequcun.farm.util;

import android.content.Context;
import android.os.AsyncTask;

import com.shequcun.farm.R;
import com.shequcun.farm.data.WxPayResEntry;
import com.shequcun.farm.dlg.ProgressDlg;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 微信支付工具类
 * Created by apple on 15/8/3.
 */
public class WxPayUtils {
    private IWXAPI iwxapi;
    /**
     * 应用唯一标识,在微信开放平台提交应用审核通
     */
    /**
     * 应用密钥,在微信开放平台提交应用审核通过后获得
     */
    private String app_secret;
    /**
     * 长度为 128 的字符串,用于支付过程中生成app_signature
     */
    private String app_key;
    /**
     * 微信公众平台商户模块生成的商户密钥
     */
    private String partner_key;
    private String partner_id;
    private Context mContext;

    /**
     * 初始化微信支付 API
     *
     * @param mContext
     */
    public void initWxAPI(Context mContext) {
        this.mContext = mContext;
        iwxapi = WXAPIFactory.createWXAPI(mContext, LocalParams.getWxAppId());
    }

    /**
     * 是否支持微信支付
     *
     * @return
     */
    public boolean isSupportWxPay() {
        return iwxapi != null && iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }

    public void doWxPay() {
        new GetAccessTokenTask().execute();
    }

    public void doWxPay(WxPayResEntry wxPay) {
        iwxapi.sendReq(wxPay);
    }

    /**
     * 获取 Token
     */
    private class GetAccessTokenTask extends AsyncTask<Void, Void, GetAccessTokenResult> {
        private ProgressDlg pDlg;

        @Override
        protected void onPreExecute() {
            pDlg = new ProgressDlg(mContext, "加载中...");
            pDlg.show();
        }

        @Override
        protected void onPostExecute(GetAccessTokenResult result) {
            if (pDlg != null) {
                pDlg.dismiss();
            }


            if (result.localRetCode == LocalRetCode.ERR_OK) {
                GetPrepayIdTask getPrepayId = new GetPrepayIdTask(result.accessToken);
                getPrepayId.execute();
            } else {
                ToastHelper.showShort(mContext, mContext.getString(R.string.get_access_token_fail, result.localRetCode.name()));
                //Toast.makeText(mContext, getString(R.string.get_access_token_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected GetAccessTokenResult doInBackground(Void... params) {
            GetAccessTokenResult result = new GetAccessTokenResult();

            String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                    LocalParams.getWxAppId(), app_secret);
            //Log.d(TAG, "get access token, url = " + url);

            byte[] buf = Util.httpGet(url);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }

            String content = new String(buf);
            result.parseFrom(content);
            return result;
        }
    }

    private enum LocalRetCode {
        ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
    }

    private class GetAccessTokenResult {
        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String accessToken;
        public int expiresIn;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {

            if (content == null || content.length() <= 0) {
//                Log.e(TAG, "parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try {
                JSONObject json = new JSONObject(content);
                if (json.has("access_token")) { // success case
                    accessToken = json.getString("access_token");
                    expiresIn = json.getInt("expires_in");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    errCode = json.getInt("errcode");
                    errMsg = json.getString("errmsg");
                    localRetCode = LocalRetCode.ERR_JSON;
                }

            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }


    /**
     * 生成预支付订单
     */
    private class GetPrepayIdTask extends AsyncTask<Void, Void, GetPrepayIdResult> {

        //        private ProgressDialog dialog;
        private String accessToken;

        ProgressDlg pDlg;

        public GetPrepayIdTask(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        protected void onPreExecute() {
            pDlg = new ProgressDlg(mContext, "加载中...");
            pDlg.show();

//            dialog = ProgressDialog.show(mContext, getString(R.string.app_tip), getString(R.string.getting_prepayid));
        }

        @Override
        protected void onPostExecute(GetPrepayIdResult result) {
            if (pDlg != null) {
                pDlg.dismiss();
            }

            if (result.localRetCode == LocalRetCode.ERR_OK) {
                //Toast.makeText(mContext, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
                sendPayReq(result);
            } else {
                ToastHelper.showShort(mContext, mContext.getString(R.string.get_prepayid_fail, result.localRetCode.name()));
                //Toast.makeText(mContext, getString(R.string.get_prepayid_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected GetPrepayIdResult doInBackground(Void... params) {

            String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", accessToken);
            String entity = genProductArgs();

//            Log.d(TAG, "doInBackground, url = " + url);
//            Log.d(TAG, "doInBackground, entity = " + entity);

            GetPrepayIdResult result = new GetPrepayIdResult();

            byte[] buf = Util.httpPost(url, entity);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }

            String content = new String(buf);
//            Log.d(TAG, "doInBackground, content = " + content);
            result.parseFrom(content);
            return result;
        }
    }

    private class GetPrepayIdResult {
        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String prepayId;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {

            if (content == null || content.length() <= 0) {
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try {
                JSONObject json = new JSONObject(content);
                if (json.has("prepayid")) { // success case
                    prepayId = json.getString("prepayid");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    localRetCode = LocalRetCode.ERR_JSON;
                }

                errCode = json.getInt("errcode");
                errMsg = json.getString("errmsg");

            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private String genProductArgs() {
        JSONObject json = new JSONObject();

        try {
            json.put("appid", LocalParams.getWxAppId());
            String traceId = getTraceId();  // traceId
            json.put("traceid", traceId);
            nonceStr = genNonceStr();
            json.put("noncestr", nonceStr);

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            /**银行类型,在微信中使用WX*/
            packageParams.add(new BasicNameValuePair("bank_type", "WX"));
            /**商品描述*/
            packageParams.add(new BasicNameValuePair("body", "SheQuCun"));
            /**支付币种,目前只支 持人民币,默认值是 1*/
            packageParams.add(new BasicNameValuePair("fee_type", "1"));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));
            /**商户系统的订单号*/
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            /**商户号,也即之前步骤的 partnerid*/
            packageParams.add(new BasicNameValuePair("partner", partner_id));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));
            /**支付金额,单位为分*/
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            packageValue = genPackage(packageParams);
            json.put("package", packageValue);
            timeStamp = genTimeStamp();
            json.put("timestamp", timeStamp);
            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", LocalParams.getWxAppId()));
            signParams.add(new BasicNameValuePair("appkey", app_key));
            signParams.add(new BasicNameValuePair("noncestr", nonceStr));
            signParams.add(new BasicNameValuePair("package", packageValue));
            signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
            signParams.add(new BasicNameValuePair("traceid", traceId));
            json.put("app_signature", genSign(signParams));
            json.put("sign_method", "sha1");
        } catch (Exception e) {
//            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

        return json.toString();
    }

    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * ￼商家对用户的唯一标识,如果用微信 SSO,此处建议填写 授权用户的 openid
     *
     * @return
     */
    private String getTraceId() {
        return "crestxu_" + genTimeStamp();
    }

    /**
     * 时间戳
     *
     * @return
     */
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 订单详情
     *
     * @param params
     * @return
     */
    private String genPackage(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(partner_key);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();

        return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
    }

    private void sendPayReq(GetPrepayIdResult result) {
        PayReq req = new PayReq();
        req.appId = LocalParams.getWxAppId();
        req.partnerId = partner_id;
        req.prepayId = result.prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = String.valueOf(timeStamp);
        req.packageValue = "Sign=" + packageValue;

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("appkey", app_key));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genSign(signParams);
        // IWXMsg.registerApp
        iwxapi.sendReq(req);
    }

    /**
     * 随机串 防止重发
     *
     * @return
     */
    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 签名
     *
     * @param params
     * @return
     */
    private String genSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (; i < params.size() - 1; i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());
        String sha1 = Util.sha1(sb.toString());
        return sha1;
    }

    private long timeStamp;
    private String nonceStr, packageValue;
}
