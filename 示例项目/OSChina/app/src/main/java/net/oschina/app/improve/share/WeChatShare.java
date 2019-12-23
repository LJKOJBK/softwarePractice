package net.oschina.app.improve.share;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.oschina.app.BuildConfig;
import net.oschina.app.R;
import net.oschina.app.improve.widget.SimplexToast;


import java.io.File;

/**
 * WeChatShare
 * Created by huanghaibin on 2017/6/12.
 */

public class WeChatShare extends BaseShare implements IWXAPIEventHandler {
    private static final String APP_ID = "wxa8213dc827399101";
    public static final String APP_SECRET = "5c716417ce72ff69d8cf0c43572c9284";
    private IWXAPI wxAPI;

    WeChatShare(Builder mBuilder) {
        super(mBuilder);
        wxAPI = WXAPIFactory.createWXAPI(mBuilder.mActivity, APP_ID, false);
        wxAPI.handleIntent(mBuilder.mActivity.getIntent(), this);
    }

    @Override
    public boolean share() {
        if (mBuilder.isShareImage && mBuilder.bitmap != null) {
            shareImage();
        } else {
            wechatShare(0);
        }
        return false;
    }

    @Override
    public void onReq(BaseReq baseReq) {
        wechatShare(0);
    }

    @Override
    public void onResp(BaseResp baseResp) {

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                SimplexToast.show(mBuilder.mActivity, "分享成功");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                SimplexToast.show(mBuilder.mActivity, "取消分享");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                SimplexToast.show(mBuilder.mActivity, "分享失败");
                break;
        }
    }

    void wechatShare(int flag) {

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mBuilder.url;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        if(mBuilder.thumbBitmap!= null && !mBuilder.thumbBitmap.isRecycled()){
            msg.setThumbImage(mBuilder.thumbBitmap);
        }else {
            msg.setThumbImage(BitmapFactory.decodeResource(mBuilder.mActivity.getResources(), R.mipmap.ic_share_app_logo));
        }
        msg.title = mBuilder.title;
        msg.description = mBuilder.content;

        final SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxAPI.sendReq(req);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void shareImage() {
        try {
            String url = saveImage(mBuilder.bitmap);
            if (TextUtils.isEmpty(url)) return;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(mBuilder.mActivity, APP_PROVIDER, new File(url));
            intent.putExtra(Intent.EXTRA_STREAM, uri);//uri为你要分享的图片的uri
            intent.setType("image/*");
            intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            mBuilder.mActivity.startActivityForResult(intent, 1);
        } catch (Exception e) {
            SimplexToast.show(mBuilder.mActivity, "分享失败");
            e.printStackTrace();
        }
    }
}
