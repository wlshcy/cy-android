package com.shequcun.farm.platform;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Constrants;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cong on 15/9/24.
 */
public class ShareManager {
    private IWXAPI api;
    /**
     * 150就会导致>32k
     */
    private static final int THUMB_SIZE = 120;
    private Bitmap mThumbBmp;
    private static ShareManager instance;
    private ShareContent shareContent;
    private Context context;
    private ProgressDlg progressDlg;

    private ShareManager() {
    }

    public static ShareManager getInstance() {
        if (instance == null)
            instance = new ShareManager();
        return instance;
    }

    private void shareToWx(Context context, ShareContent shareContent, boolean circle) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getTargetUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();
        if (shareContent.getImageId() > 0) {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), shareContent.getImageId());
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            msg.thumbData = bmpToByteArray(thumbBmp, false);
            bmp.recycle();
        } else if (!TextUtils.isEmpty(shareContent.getUrlImage())) {
            if (mThumbBmp != null && !mThumbBmp.isRecycled()) {
                /**一定要小于32k，否则分享失败*/
                msg.thumbData = bmpToByteArray(mThumbBmp, false);
            }
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = circle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        if (api == null)
            api = WXAPIFactory.createWXAPI(context, Constrants.APP_ID);
        api.sendReq(req);
        clear();
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
     * * 自定义分享面板
     * * @param context
     * * @param shareContent
     */
    public void popShareFrame(Context context, final ShareContent shareContent) {
        this.context = context;
        this.shareContent = shareContent;
        final AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.show();
//        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.getWindow().setContentView(R.layout.prompt_share_frame);
        alert.getWindow().findViewById(R.id.wxTv).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                alert.dismiss();
                executeBitmapTask(Platform.WX);
            }
        });
        alert.getWindow().findViewById(R.id.wxCircleTv).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                alert.dismiss();
                executeBitmapTask(Platform.WXCricle);
            }
        });
        if (api == null)
            api = WXAPIFactory.createWXAPI(context, Constrants.APP_ID);
    }

    private void executeBitmapTask(Platform platform) {
        if (shareContent == null) return;
//            加载本地图片
        if (shareContent.getImageId() > 0) {
            shareToPlatform(platform);
            //加载网络图片
        } else if (!TextUtils.isEmpty(shareContent.getUrlImage())) {
            new BitmapAsyncTask(platform).execute(shareContent.getUrlImage() + "?imageView2/1/w/" + THUMB_SIZE);
        }
    }

    private void clear() {
        if (mThumbBmp != null && !mThumbBmp.isRecycled())
            mThumbBmp.isRecycled();
        mThumbBmp = null;
    }

    public static void shareByFrame(Context context, ShareContent shareContent) {
        ShareManager shareManager = ShareManager.getInstance();
        shareManager.popShareFrame(context, shareContent);
    }

    enum Platform {
        WX, WXCricle
    }

    class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private Platform platform;

        public BitmapAsyncTask(Platform platform) {
            this.platform = platform;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDlg == null)
                progressDlg = new ProgressDlg(context, "分享中");
            progressDlg.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imgUrl = params[0];
            Bitmap bitmap = null;
            try {
                /**单纯的网络加载会出现大容量的bitmap，导致分享没反应*/
                bitmap = ImageLoader.getInstance().loadImageSync(imgUrl);
            } catch (Exception e) {
                return bitmap;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                if (progressDlg != null)
                    progressDlg.dismiss();
                Toast.makeText(context, "网络超时，分享失败", Toast.LENGTH_LONG).show();
                return;
            }
            mThumbBmp = bitmap;
            progressDlg.dismiss();
            shareToPlatform(platform);
        }
    }

    private void shareToPlatform(Platform platform) {
        switch (platform) {
            case WX:
                shareToWx(context, shareContent, false);
                break;
            case WXCricle:
                shareToWx(context, shareContent, true);
                break;
        }
    }
}
