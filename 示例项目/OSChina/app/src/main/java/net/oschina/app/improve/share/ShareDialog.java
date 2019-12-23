package net.oschina.app.improve.share;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.widget.BottomDialog;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 分享对话框
 * Created by huanghaibin on 2017/6/25.
 */
@SuppressWarnings("unused")
public class ShareDialog extends BottomDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private BaseShare.Builder mBuilder;
    public static final int ONE_DAY = 86400000;

    @SuppressLint("InflateParams")
    public ShareDialog(@NonNull Context context) {
        super(context, true);
        View view = getLayoutInflater().inflate(R.layout.dialog_share, null);
        view.findViewById(R.id.ll_share_wechat).setOnClickListener(this);
        view.findViewById(R.id.ll_share_moments).setOnClickListener(this);
        view.findViewById(R.id.ll_share_weibo).setOnClickListener(this);
        view.findViewById(R.id.ll_share_qq).setOnClickListener(this);
        view.findViewById(R.id.ll_share_browser).setOnClickListener(this);
        view.findViewById(R.id.ll_share_copy).setOnClickListener(this);
        setContentView(view);
        setOnDismissListener(this);
    }


    public void init(Activity activity, String title, String content, String url) {
        if (mBuilder == null) {
            mBuilder = new BaseShare.Builder(activity);
        }
        mBuilder.title(title)
                .content(TextUtils.isEmpty(content) ? title : content.length() > 30 ? content.substring(0, 30) : content)
                .url(url);
    }

    @SuppressWarnings("all")
    public void setShareApp(boolean isShareApp) {
        if (mBuilder != null) {
            mBuilder.isShareApp(true);
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (mBuilder != null) {
            mBuilder.bitmap(bitmap);
        }
        clearShare();
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        if (mBuilder != null) {
            mBuilder.thumbBitmap(thumbBitmap);
        }
        clearShare();
    }

    @Override
    public void onClick(View v) {
        if (mBuilder == null)
            return;
        BaseShare share = null;
        switch (v.getId()) {
            case R.id.ll_share_wechat:
                share = new WeChatShare(mBuilder);
                break;
            case R.id.ll_share_moments:
                share = new MomentsShare(mBuilder);
                break;
            case R.id.ll_share_weibo:
                share = new SinaShare(mBuilder);
                break;
            case R.id.ll_share_qq:
                share = new TencentQQShare(mBuilder);
                break;
            case R.id.ll_share_browser:
                UIHelper.openExternalBrowser(getContext(), mBuilder.url);
                break;
            case R.id.ll_share_copy:
                TDevice.copyTextToBoard(mBuilder.url);
                break;
        }
        if (share != null)
            share.share();
        dismiss();
    }


    /**
     * 保存分享
     */
    @SuppressWarnings("ResultOfMethodCallIgnored,unused")
    private static boolean saveShare(Bitmap bitmap, Context context) {
        FileOutputStream os = null;
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/share/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = path + String.valueOf(System.currentTimeMillis()) + ".jpg";
            os = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            Uri localUri = Uri.fromFile(new File(path));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            context.sendBroadcast(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            StreamUtil.close(os);
        }
        return true;
    }

    private static void clearShare() {
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/share/";
            //FileHelper.deleteFileOrDir(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mBuilder != null && mBuilder.bitmap != null && !mBuilder.bitmap.isRecycled()) {
            mBuilder.bitmap.recycle();
            mBuilder.bitmap(null);
        }
    }
}
