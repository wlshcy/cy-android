package com.shequcun.farm.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareUtil {
    public static final String DESCRIPTOR = "com.umeng.share";
    private final static UMSocialService mController = UMServiceFactory
            .getUMSocialService(DESCRIPTOR);
    private Activity act;

    public ShareUtil(Activity act) {
        this.act = act;
        initShareUtil();
    }

    public void initShareUtil() {
        String appID = "wxedddf5c468bfd955";
        String appSecret = "ea6ca4bab29d7243bfa97a7f46a930a7";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(act, appID, appSecret);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(act, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
//        String appID1 = "wxedddf5c468bfd955";
//        String appSecret1 = "ea6ca4bab29d7243bfa97a7f46a930a7";
//        // qq空间
//        UMQQSsoHandler umqqSsoHandler = new UMQQSsoHandler(act, appID1, appSecret1);
//        umqqSsoHandler.addToSocialSDK();
    }

    public void shareAll(ShareContent sc) {
        wxShareContent(sc);
        circleShareContent(sc);
//        qqZoneShareContent(sc);
    }

    public void circleShareContent(ShareContent sc) {
        UMImage urlImage;
        if (sc.getImageId() <= 0)
            urlImage = new UMImage(act, sc.getUrlImage());
        else
            urlImage = new UMImage(act, sc.getImageId());
        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(sc.getContent());
        circleMedia.setTitle(sc.getTitle());
        circleMedia.setShareMedia(urlImage);
        // circleMedia.setShareMedia(uMusic);
        // circleMedia.setShareMedia(video);
        circleMedia.setTargetUrl(sc.getTargetUrl());
        mController.setShareMedia(circleMedia);
    }

    public void wxShareContent(ShareContent sc) {
        UMImage urlImage;
        if (sc.getImageId() <= 0)
            urlImage = new UMImage(act, sc.getUrlImage());
        else
            urlImage = new UMImage(act, sc.getImageId());
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(sc.getContent());
        weixinContent.setTitle(sc.getTitle());
        weixinContent.setTargetUrl(sc.getTargetUrl());
        weixinContent.setShareMedia(urlImage);
        mController.setShareMedia(weixinContent);
    }

    public void qqZoneShareContent(ShareContent sc) {
//        UMImage urlImage;
//        if (sc.getImageId() <= 0)
//            urlImage = new UMImage(act, sc.getUrlImage());
//        else
//            urlImage = new UMImage(act, sc.getImageId());
//        QQShareContent qqShareContent = new QQShareContent();
//        qqShareContent.setShareContent(sc.getContent());
//        qqShareContent.setTitle(sc.getTitle());
//        qqShareContent.setTargetUrl(sc.getTargetUrl());
//        qqShareContent.setShareMedia(urlImage);
//        mController.setShareMedia(qqShareContent);
    }

    public void postShare(SnsPostListener snsPostListener) {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE);
        mController.openShare(act, snsPostListener);
    }

    public void postShareAll(SnsPostListener snsPostListener) {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE);
        mController.openShare(act, snsPostListener);
    }

    private void post(Context context, SHARE_MEDIA share_media, SnsPostListener snsPostListener) {
        mController.postShare(context, share_media, snsPostListener);
    }

    /**
     * 自定义分享面板
     * @param context
     * @param shareContent
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
                wxShareContent(shareContent);
                post(context, SHARE_MEDIA.WEIXIN, mSnsPostListener);
                alert.dismiss();
            }
        });
        alert.getWindow().findViewById(R.id.wxCircleTv).setOnClickListener(new AvoidDoubleClickListener() {
            @Override
            public void onViewClick(View v) {
                circleShareContent(shareContent);
                post(context, SHARE_MEDIA.WEIXIN_CIRCLE, mSnsPostListener);
                alert.dismiss();
            }
        });
    }

    private SocializeListeners.SnsPostListener mSnsPostListener = new SocializeListeners.SnsPostListener() {
        @Override
        public void onStart() {
        }

        @Override
        public void onComplete(SHARE_MEDIA sm, int eCode,
                               SocializeEntity sEntity) {
            String showText = "分享成功";
            if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                showText = "分享失败";
            }
            ToastHelper.showShort(act, showText);
        }
    };
}
