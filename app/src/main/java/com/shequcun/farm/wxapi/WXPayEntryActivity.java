package com.shequcun.farm.wxapi;


import android.content.Intent;
import android.os.Bundle;

import com.shequcun.farm.BaseFragmentActivity;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.LocalParams;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends BaseFragmentActivity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, LocalParams.getWxAppId());
        //api.registerApp(LocalParams.getWxAppId());
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        IntentUtil.sendWxPayResultMsg(WXPayEntryActivity.this, resp.errCode);
        finish();


//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
////			AlertDialog.Builder builder = new AlertDialog.Builder(this);
////			builder.setTitle(R.string.app_tip);
////			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
////			builder.show();
//        }
    }
}