package com.shequcun.farm.platform;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.shequcun.farm.R;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Constrants;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by cong on 15/9/24.
 */
public class ShareManager {
    private IWXAPI api;
    private static final int THUMB_SIZE = 150;

    private void shareToWx(Context context, ShareContent shareContent, boolean circle) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getTargetUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();
        if (shareContent.getImageId() > 0) {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), shareContent.getImageId());
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            msg.thumbData = bmpToByteArray(thumbBmp, true);
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = circle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        if (api == null)
            api = WXAPIFactory.createWXAPI(context, Constrants.APP_ID);
        api.sendReq(req);
    }

    private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * //     * 自定义分享面板
     * //     * @param context
     * //     * @param shareContent
     * //
     */
    public void popShareFrame(final Context context, final ShareContent shareContent) {
        final AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.show();
//        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.getWindow().setContentView(R.layout.prompt_share_frame);
        alert.getWindow().findViewById(R.id.wxTv).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                shareToWx(context, shareContent, false);
                alert.dismiss();
            }
        });
        alert.getWindow().findViewById(R.id.wxCircleTv).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                shareToWx(context, shareContent, true);
                alert.dismiss();
            }
        });
        if (api == null)
            api = WXAPIFactory.createWXAPI(context, Constrants.APP_ID);
    }

    public static void shareByFrame(Context context,ShareContent shareContent) {
        ShareManager shareManager = new ShareManager();
        shareManager.popShareFrame(context, shareContent);
    }
}
