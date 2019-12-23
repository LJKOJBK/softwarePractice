package net.oschina.app.improve.share;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;

import net.oschina.app.R;
import net.oschina.app.improve.widget.SimplexToast;


/**
 * sina
 * Created by huanghaibin on 2017/6/12.
 */
@SuppressWarnings("unused")
public class SinaShare extends BaseShare {

    private static final String APP_KEY = "3616966952";
    private static final String APP_SECRET = "fd81f6d31427b467f49226e48a741e28";
    private IWeiboShareAPI mAPI;

    SinaShare(Builder mBuilder) {
        super(mBuilder);
        mAPI = WeiboShareSDK.createWeiboAPI(mBuilder.mActivity, APP_KEY, false);
        mAPI.registerApp();
        mBuilder.isShareImage = false;
    }

    @Override
    public boolean share() {
        if (!mAPI.isWeiboAppInstalled()) {
            SimplexToast.show(mBuilder.mActivity, "请安装微博客户端");
            return false;
        }
        if (mBuilder.isShareImage) {
            shareImage();
            return true;
        }
        toShare();
        return true;
    }

    private void toShare() {
        WebpageObject webpageObject = new WebpageObject();
        webpageObject.identify = Utility.generateGUID();
        webpageObject.title = mBuilder.title;
        webpageObject.description = mBuilder.content;
        mBuilder.bitmap = BitmapFactory.decodeResource(mBuilder.mActivity.getResources(), R.mipmap.ic_share_app_logo);
        webpageObject.setThumbImage(mBuilder.bitmap);
        webpageObject.actionUrl = mBuilder.url;
        webpageObject.defaultText = mBuilder.content;

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        TextObject textObject = new TextObject();
        textObject.text = mBuilder.title;
        textObject.title = mBuilder.title;
        weiboMessage.textObject = textObject;

        weiboMessage.mediaObject = webpageObject;

        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mAPI.sendRequest(mBuilder.mActivity, request);
    }

    @Override
    public void shareImage() {
        String url = saveImage(mBuilder.bitmap);
        if (TextUtils.isEmpty(url)) return;
        WeiboMultiMessage msg = new WeiboMultiMessage();
        ImageObject img = new ImageObject();
        //img.setImageObject(bitmap);
        img.imagePath = url;
        msg.imageObject = img;
        SendMultiMessageToWeiboRequest multRequest = new SendMultiMessageToWeiboRequest();
        multRequest.multiMessage = msg;
        //以当前时间戳为唯一识别符
        multRequest.transaction = String.valueOf(System.currentTimeMillis());
        mAPI.sendRequest(mBuilder.mActivity, multRequest);
    }
}
