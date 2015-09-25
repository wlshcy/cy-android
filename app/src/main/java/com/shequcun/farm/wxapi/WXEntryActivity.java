package com.shequcun.farm.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.shequcun.farm.BaseFragmentActivity;
import com.shequcun.farm.util.Constrants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends BaseFragmentActivity implements IWXAPIEventHandler {
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(this, "WXEntryActivity onCreate", Toast.LENGTH_LONG).show();
        api = WXAPIFactory.createWXAPI(this, Constrants.APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        Toast.makeText(this, "onNewIntent", Toast.LENGTH_LONG).show();
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
//        Toast.makeText(this, "onReq", Toast.LENGTH_LONG).show();
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        finish();
        String result = "";
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送失败";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                break;
            default:
                result = "发送被返回";
                break;
        }
//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private void goToGetMsg() {
//        Intent intent = new Intent(this, GetFromWXActivity.class);
//        intent.putExtras(getIntent());
//        startActivity(intent);
//        finish();
    }
}
