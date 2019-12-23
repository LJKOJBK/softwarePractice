package net.oschina.app.improve.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * base share
 * Created by huanghaibin on 2017/6/12.
 */
@SuppressWarnings("unused")
public abstract class BaseShare {
    static final String APP_PROVIDER = "net.oschina.app.provider";
    Builder mBuilder;

    BaseShare(Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    public abstract boolean share();

    public abstract void shareImage();

    public static final class Builder {
        Activity mActivity;
        String title;
        String content;
        int resId;
        String url;
        String imageUrl;
        Bitmap bitmap;
        Bitmap thumbBitmap;
        boolean isShareApp;
        boolean isShareImage;
        int itemIcon;//显示的分享项图标
        String itemTitle;//显示的分享项名称

        public Builder(Activity mActivity) {
            this.mActivity = mActivity;
            this.isShareImage = true;
        }

        public Builder resId(int resId) {
            this.resId = resId;
            return this;
        }

        public Builder itemIcon(int itemIcon) {
            this.itemIcon = itemIcon;
            return this;
        }

        public Builder itemTitle(String itemTitle) {
            this.itemTitle = itemTitle;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder bitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public Builder thumbBitmap(Bitmap thumbBitmap) {
            this.thumbBitmap = thumbBitmap;
            return this;
        }

        @SuppressWarnings("all")
        public Builder isShareApp(boolean isShareApp) {
            this.isShareApp = isShareApp;
            return this;
        }

        public Builder isShareImage(boolean isShareImage) {
            this.isShareImage = isShareImage;
            return this;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static String saveImage(Bitmap bitmap) {
        FileOutputStream os = null;
        String url = null;
        try {
            File file = new File(url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/share/");
            if (!file.exists()) {
                file.mkdirs();
            }
            url = file.getPath() + String.valueOf(System.currentTimeMillis()) + ".jpg";
            os = new FileOutputStream(url);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(os);
        }
        return url;
    }
}
