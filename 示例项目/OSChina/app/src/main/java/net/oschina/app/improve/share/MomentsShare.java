package net.oschina.app.improve.share;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 * WeChatShare
 * Created by huanghaibin on 2017/6/12.
 */
@SuppressWarnings("unused")
public class MomentsShare extends WeChatShare {
    private static final String APP_ID = "";

    MomentsShare(Builder mBuilder) {
        super(mBuilder);
    }

    @Override
    public boolean share() {
        if (mBuilder.isShareImage && mBuilder.bitmap != null) {
            shareImage();
        } else {
            wechatShare(1);
        }
        return true;
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
            intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            mBuilder.mActivity.startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
