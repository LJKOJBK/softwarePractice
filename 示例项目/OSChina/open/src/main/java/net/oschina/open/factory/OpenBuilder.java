package net.oschina.open.factory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import net.oschina.open.bean.Share;
import net.oschina.open.utils.OpenUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by qiujuer
 * on 2016/11/1.
 */
public class OpenBuilder {
    private Activity activity;

    public static String saveShare(Bitmap bitmap) {
        FileOutputStream os = null;
        String url = null;
        try {
            File file = new File(url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/share/");
            if (!file.exists())
                file.mkdirs();
            url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/share/" +
                    System.currentTimeMillis() + ".jpg";
            os = new FileOutputStream(url);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();
            close(os);
        }
        return url;
    }

    public static OpenBuilder with(Activity activity) {
        OpenBuilder builder = new OpenBuilder();
        builder.activity = activity;
        return builder;
    }

    public TencentOperator useTencent(String appId) {
        Tencent tencent = Tencent.createInstance(appId, activity);
        return new TencentOperator(tencent);
    }

    public WeiboOperator useWeibo(String appKey) {
        return new WeiboOperator(appKey);
    }

    public WechatOperator useWechat(String appId) {
        return new WechatOperator(appId);
    }


    public class TencentOperator {
        Tencent tencent;

        TencentOperator(Tencent tencent) {
            this.tencent = tencent;
        }

        public Tencent login(IUiListener listener, Callback callback) {
            int login = tencent.login(activity, "all", listener);
            return tencent;
        }

        public void share(Share share, IUiListener listener, Callback callback) {
            if (share.getThumbBitmap() != null && TextUtils.isEmpty(share.getUrl())) {
                shareLocalImage(share, listener, callback);
                return;
            }
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, share.getTitle());
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, share.getSummary());
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, share.getUrl());
            String shareIconUrl = share.getImageUrl();
            if (!TextUtils.isEmpty(shareIconUrl)) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareIconUrl);
            } else {
                params.putInt(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, share.getAppShareIcon());
            }
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, share.getAppName());
            if (callback != null) {
                if (tencent != null) {
                    try {
                        tencent.shareToQQ(activity, params, listener);
                        callback.onSuccess();
                    } catch (Exception e) {
                        callback.onFailed();
                    }
                } else {
                    callback.onFailed();
                }
            }
        }

        private void shareLocalImage(Share share, IUiListener listener, Callback callback) {
            String url = saveShare(share.getThumbBitmap());
            if (TextUtils.isEmpty(url)) return;
            Bundle params = new Bundle();
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, url);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, share.getAppName());
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHATOQQF);
            if (tencent != null) {
                try {
                    tencent.shareToQQ(activity, params, listener);
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onFailed();
                }
            } else {
                callback.onFailed();
            }
        }
    }

    static void close(Closeable... closeables) {
        if (closeables == null || closeables.length == 0)
            return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class WeiboOperator {
        String appKey;

        WeiboOperator(String appKey) {
            this.appKey = appKey;
        }

        public SsoHandler login(WeiboAuthListener listener) {
            AuthInfo authInfo = new AuthInfo(activity, appKey, "http://sns.whalecloud.com/sina2/callback", null);
            SsoHandler handler = new SsoHandler(activity, authInfo);
            handler.authorize(listener);
            return handler;
        }

        public void share(Share share, Callback callback) {
            IWeiboShareAPI weiBoShareSDK = WeiboShareSDK.createWeiboAPI(activity, appKey, false);
            if (share.getThumbBitmap() != null && TextUtils.isEmpty(share.getUrl())) {
                shareLocalImage(weiBoShareSDK, share, callback);
                return;
            }
            if (!(weiBoShareSDK.isWeiboAppInstalled()
                    && weiBoShareSDK.isWeiboAppSupportAPI()
                    && weiBoShareSDK.registerApp())) {
                if (callback != null)
                    callback.onFailed();
                return;
            }

            // 1. 初始化微博的分享消息
            // 分享网页
            WebpageObject webpageObject = new WebpageObject();

            webpageObject.identify = Utility.generateGUID();
            webpageObject.title = share.getTitle();
            webpageObject.description = share.getTitle();
            webpageObject.defaultText = share.getTitle();

            Bitmap bitmap = share.getThumbBitmap();
            if (bitmap == null) {
                bitmap = OpenUtils.getShareBitmap(activity.getApplicationContext(), share.getBitmapResID());
            }

            // 设置 Bitmap 类型的图片到视频对象里         最好设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            webpageObject.setThumbImage(bitmap);
            webpageObject.actionUrl = share.getUrl();
            webpageObject.defaultText = " - 开源中国";

            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

            TextObject textObject = new TextObject();
            textObject.text = share.getTitle();
            textObject.title = share.getTitle();
            weiboMessage.textObject = textObject;
            weiboMessage.mediaObject = webpageObject;
            // 2. 初始化从第三方到微博的消息请求
            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.multiMessage = weiboMessage;

            // 3. 发送请求消息到微博，唤起微博分享界面
            if ((!weiBoShareSDK.sendRequest(activity, request)) && callback != null) {
                callback.onFailed();
                if (!bitmap.isRecycled())
                    bitmap.recycle();
            } else {
                if (callback != null)
                    callback.onSuccess();
                if (!bitmap.isRecycled())
                    bitmap.recycle();
            }
        }

        private void shareLocalImage(IWeiboShareAPI api, Share share, Callback callback) {
            WeiboMultiMessage msg = new WeiboMultiMessage();
            ImageObject img = new ImageObject();
            img.setImageObject(share.getThumbBitmap());
            msg.imageObject = img;
            SendMultiMessageToWeiboRequest multRequest = new SendMultiMessageToWeiboRequest();
            multRequest.multiMessage = msg;
            //以当前时间戳为唯一识别符
            multRequest.transaction = String.valueOf(System.currentTimeMillis());
            if (api.sendRequest(activity, multRequest)) {
                callback.onSuccess();
            } else {
                callback.onFailed();
            }
        }
    }

    public class WechatOperator {
        String appId;

        WechatOperator(String appId) {
            this.appId = appId;
        }

        public void login(Callback callback) {
            IWXAPI iwxapi = init();
            if (iwxapi == null) {
                if (callback != null)
                    callback.onFailed();
                return;
            }
            // 唤起微信登录授权
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_login";
            // 失败回调
            if (!iwxapi.sendReq(req) && callback != null) {
                callback.onFailed();
            } else {
                if (callback != null)
                    callback.onSuccess();
            }
        }

        public void shareSession(Share share, Callback callback) {
            share(share, SendMessageToWX.Req.WXSceneSession, callback);
        }

        public void shareTimeLine(Share share, Callback callback) {
            share(share, SendMessageToWX.Req.WXSceneTimeline, callback);
        }

        private IWXAPI init() {
            IWXAPI iwxapi = WXAPIFactory.createWXAPI(activity, appId, false);
            if (iwxapi.isWXAppInstalled() && iwxapi.isWXAppSupportAPI() && iwxapi.registerApp(appId)) {
                return iwxapi;
            }
            return null;
        }

        private void share(Share share, int scene, Callback callback) {
            if (share.getThumbBitmap() != null && TextUtils.isEmpty(share.getUrl())) {
                share(share.getThumbBitmap(), scene);
                return;
            }
            IWXAPI iwxapi = init();
            if (iwxapi == null) {
                if (callback != null)
                    callback.onFailed();
                return;
            }

            //1.初始化一个WXTextObject对象,填写分享的文本内容
            WXWebpageObject wxWebpageObject = new WXWebpageObject();
            wxWebpageObject.webpageUrl = share.getUrl();
            wxWebpageObject.extInfo = share.getDescription();

            //2.用WXTextObject对象初始化一个WXMediaMessage对象
            WXMediaMessage msg = new WXMediaMessage();
            msg.title = share.getTitle();
            msg.mediaObject = wxWebpageObject;
            msg.description = share.getDescription();

            Bitmap bitmap = share.getThumbBitmap();
            if (bitmap == null) {
                bitmap = OpenUtils.getShareBitmap(activity, share.getBitmapResID());
            }
            if (!bitmap.isRecycled())
                msg.setThumbImage(bitmap);

            //3.构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = OpenUtils.buildTransaction("webPage");
            //transaction字段用于唯一标识一个请求
            req.message = msg;
            req.scene = scene;

            //4.发送这次分享
            boolean sendReq = iwxapi.sendReq(req);
            //发送请求失败,回调
            if (!sendReq && callback != null) {
                callback.onFailed();
                if (!bitmap.isRecycled())
                    bitmap.recycle();
            } else {
                if (callback != null)
                    callback.onSuccess();
                if (!bitmap.isRecycled())
                    bitmap.recycle();
            }
        }

        /**
         * 单纯分享图片
         */
        private void share(Bitmap bitmap, int scene) {
            try {
                String url = saveShare(bitmap);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                Uri uri = FileProvider.getUriForFile(activity,"net.oschina.app.provider",new File(url));
                intent.putExtra(Intent.EXTRA_STREAM, uri);//uri为你要分享的图片的uri
                intent.setType("image/*");
                intent.setClassName("com.tencent.mm", scene == SendMessageToWX.Req.WXSceneTimeline ?
                        "com.tencent.mm.ui.tools.ShareToTimeLineUI"
                        : "com.tencent.mm.ui.tools.ShareImgUI");
                activity.startActivityForResult(intent, 1);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(activity,"请安装微信",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface Callback {
        void onFailed();

        void onSuccess();
    }
}
