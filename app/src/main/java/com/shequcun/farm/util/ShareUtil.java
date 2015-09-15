package com.shequcun.farm.util;

import android.app.Activity;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
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
        qqZoneShareContent(sc);
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
        UMImage urlImage;
        if (sc.getImageId() <= 0)
            urlImage = new UMImage(act, sc.getUrlImage());
        else
            urlImage = new UMImage(act, sc.getImageId());
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(sc.getContent());
        qqShareContent.setTitle(sc.getTitle());
        qqShareContent.setTargetUrl(sc.getTargetUrl());
        qqShareContent.setShareMedia(urlImage);
        mController.setShareMedia(qqShareContent);
    }

    public void postShare(SnsPostListener snsPostListener) {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE);
        mController.openShare(act, snsPostListener);
    }
    public void postShareAll(SnsPostListener snsPostListener) {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QZONE);
        mController.openShare(act, snsPostListener);
    }
}
