package net.oschina.app.improve.detail.share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 文章详情分享界面
 * Created by huanghaibin on 2017/9/25.
 */

public abstract class ShareFragment extends BaseFragment implements Runnable {
    protected OWebView mWebView;
    protected SubBean mBean;
    private ShareDialog mShareDialog;
    private Bitmap mBitmap;
    private ProgressDialog mDialog;
    protected NestedScrollView mViewScroller;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWebView = (OWebView) mRoot.findViewById(R.id.webView);
        mWebView.setUseShareCss(true);
        mBean = (SubBean) getArguments().getSerializable("bean");
        mViewScroller = (NestedScrollView) mRoot.findViewById(R.id.lay_nsv);
        mDialog = DialogHelper.getProgressDialog(mContext);
        mDialog.setMessage("请稍候...");
    }

    @Override
    protected void initData() {
        super.initData();
        mWebView.loadDetailDataAsync(mBean.getBody(), (Runnable) mContext);
        mShareDialog = new ShareDialog(getActivity(), -1, false);
    }

    public void share() {
        recycle();
        mDialog.show();
        mRoot.postDelayed(this, 2000);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save() {
        recycle();
        mBitmap = create(mViewScroller.getChildAt(0));
        FileOutputStream os = null;
        try {
            String url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/save/";
            File file = new File(url);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = String.format("%s%s.jpg", url, System.currentTimeMillis());
            os = new FileOutputStream(path);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            SimplexToast.show(mContext, "保存成功");
            Uri localUri = Uri.fromFile(new File(path));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            getActivity().sendBroadcast(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
            SimplexToast.show(mContext, "保存失败");
        } finally {
            StreamUtil.close(os);
            recycle();
        }
    }

    @Override
    public void run() {
        mBitmap = create(mViewScroller.getChildAt(0));
        mShareDialog.bitmap(mBitmap);
        mDialog.dismiss();
        mShareDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mShareDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycle();
    }

    private void recycle() {
        if (mBitmap != null && mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }

    private static Bitmap create(View v) {
        try {
            int w = v.getWidth();
            int h = v.getHeight();
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.WHITE);
            v.layout(0, 0, w, h);
            v.draw(c);
            return bmp;
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            return null;
        }
    }
}
