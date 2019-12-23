package net.oschina.app.improve.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.detail.share.ShareActivity;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.tweet.activities.TweetPublishActivity;
import net.oschina.app.improve.tweet.share.TweetShareActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.BottomDialog;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.TDevice;
import net.oschina.common.utils.StreamUtil;
import net.oschina.open.bean.Share;
import net.oschina.open.constants.OpenConstant;
import net.oschina.open.factory.OpenBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by haibin
 * on 2016/12/28.
 */
@SuppressWarnings("all")
public class ShareDialog extends BottomDialog implements OpenBuilder.Callback,
        DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {
    private Activity mActivity;
    private ProgressDialog mDialog;
    private About.Share mAboutShare;
    private Share mShare;
    private boolean isOnlyBitmap;
    private boolean isShareDetail;
    private boolean isShareTweet;
    private SubBean mBean;
    private Tweet mTweet;

    private ShareItemClickListener mItemClickListener;

    public ShareDialog(@NonNull Activity activity) {
        super(activity, true);
        this.mActivity = activity;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View contentView = inflater.inflate(R.layout.dialog_share_main, null, false);
        RecyclerView shareRecycle = (RecyclerView) contentView.findViewById(R.id.share_recycler);
        final ShareActionAdapter adapter = new ShareActionAdapter(activity);
        adapter.addAll(getAdapterData());
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                ShareDialog.this.onItemClick(position, adapter.getItem(position));
            }
        });
        shareRecycle.setAdapter(adapter);
        shareRecycle.setItemAnimator(new DefaultItemAnimator());
        shareRecycle.setLayoutManager(new GridLayoutManager(getContext(), 4));
        setContentView(contentView);
        setOnCancelListener(this);
        setOnDismissListener(this);
        mShare = new Share();
        mShare.setAppName("开源中国");
    }

    public ShareDialog(@NonNull Activity activity, boolean isShareTweet) {
        super(activity, true);
        this.mActivity = activity;
        this.isShareTweet = isShareTweet;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View contentView = inflater.inflate(R.layout.dialog_share_main, null, false);
        RecyclerView shareRecycle = (RecyclerView) contentView.findViewById(R.id.share_recycler);
        final ShareActionAdapter adapter = new ShareActionAdapter(activity);
        adapter.addAll(getAdapterData());
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                ShareDialog.this.onItemClick(position, adapter.getItem(position));
            }
        });
        shareRecycle.setAdapter(adapter);
        shareRecycle.setItemAnimator(new DefaultItemAnimator());
        shareRecycle.setLayoutManager(new GridLayoutManager(getContext(), 4));
        setContentView(contentView);
        setOnCancelListener(this);
        setOnDismissListener(this);
        mShare = new Share();
        mShare.setAppName("开源中国");
    }

    public ShareDialog(@NonNull Activity activity, long id, boolean isShareDetail) {
        this(activity);
        this.isShareDetail = isShareDetail;
        this.mActivity = activity;
        mAboutShare = new About.Share();
        mAboutShare.id = id;
        if (id < 0)
            isOnlyBitmap = true;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View contentView = inflater.inflate(R.layout.dialog_share_main, null, false);
        RecyclerView shareRecycle = (RecyclerView) contentView.findViewById(R.id.share_recycler);
        final ShareActionAdapter adapter = new ShareActionAdapter(activity);
        adapter.addAll(getAdapterData());
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                ShareDialog.this.onItemClick(position, adapter.getItem(position));
            }
        });
        shareRecycle.setAdapter(adapter);
        shareRecycle.setItemAnimator(new DefaultItemAnimator());
        shareRecycle.setLayoutManager(new GridLayoutManager(getContext(), 4));
        setContentView(contentView);
        setOnCancelListener(this);
        setOnDismissListener(this);
        mShare = new Share();
        mShare.setAppName("开源中国");
    }

    public void setBean(SubBean mBean) {
        this.mBean = mBean;
    }

    public void setTweet(Tweet mTweet) {
        this.mTweet = mTweet;
    }

    public void setItemClickListener(ShareItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        hideWaitDialog();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        hideWaitDialog();
    }

    private List<ShareItem> getAdapterData() {
        List<ShareItem> shareActions = new ArrayList<>();

        //0.新浪微博
        shareActions.add(new ShareItem(R.mipmap.ic_login_3party_weibo, R.string.platform_sina));

        //1.朋友圈
        shareActions.add(new ShareItem(R.mipmap.ic_action_moments, R.string.platform_wechat_circle));

        //2.微信
        shareActions.add(new ShareItem(R.mipmap.ic_login_3party_wechat, R.string.platform_wechat));

        //3.QQ
        shareActions.add(new ShareItem(R.mipmap.ic_login_3party_qq, R.string.platform_qq));

        if (isOnlyBitmap) {
            shareActions.add(new ShareItem(R.mipmap.ic_action_tweet, R.string.platform_tweet));
            shareActions.add(new ShareItem(R.mipmap.ic_action_preview, R.string.platform_preview));
            return shareActions;
        }
        //4.动弹
        if (About.check(mAboutShare)) {
            shareActions.add(new ShareItem(R.mipmap.ic_action_tweet, R.string.platform_tweet));
        }

        //5.browser
        shareActions.add(new ShareItem(R.mipmap.ic_action_browser, R.string.platform_browser));

        if (isShareDetail || isShareTweet) {
            //
            shareActions.add(new ShareItem(R.mipmap.ic_action_screenshot, R.string.platform_picture));
        } else {
            //6.复制链接
            shareActions.add(new ShareItem(R.mipmap.ic_action_url, R.string.platform_copy_link));
        }

        //7.更多
        shareActions.add(new ShareItem(R.mipmap.ic_action_more, R.string.platform_more_option));

        return shareActions;
    }

    public ShareDialog with() {
        mShare.setAppShareIcon(R.mipmap.ic_share_app_logo);
        if (mShare.getBitmapResID() == 0)
            mShare.setBitmapResID(R.mipmap.ic_share_app_logo);
        return this;
    }

    public ShareDialog title(String title) {
        mShare.setTitle(title);
        if (mAboutShare == null)
            mAboutShare = new About.Share();
        mAboutShare.title = title;
        return this;
    }

    public ShareDialog summary(String summary) {
        mShare.setSummary(summary);
        mAboutShare.content = summary;
        return this;
    }

    public ShareDialog content(String content) {
        mShare.setContent(content);
        summary(content);
        description(content);
        mAboutShare.content = content;
        return this;
    }

    public ShareDialog description(String description) {
        mShare.setDescription(description);
        return this;
    }

    public ShareDialog url(String url) {
        mShare.setUrl(url);
        return this;
    }

    public ShareDialog bitmapResID(int bitmapResID) {
        mShare.setBitmapResID(bitmapResID);
        return this;
    }

    public ShareDialog bitmap(Bitmap bitmap) {
        mShare.setThumbBitmap(bitmap);
        return this;
    }

    public ShareDialog imageUrl(final String imageUrl) {
        mShare.setImageUrl(imageUrl);

        if (!TextUtils.isEmpty(imageUrl)) {
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap thumbBitmap = Glide.with(getContext())
                                .load(imageUrl)
                                .asBitmap().into(100, 100).get();
                        //为微博和微信加入分享的详情icon

                        mShare.setThumbBitmap(thumbBitmap);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return this;
    }


    public ShareDialog id(long id) {
        mAboutShare.id = id;
        return this;
    }

    public ShareDialog type(int type) {
        mAboutShare.type = type;
        return this;
    }

    public Share getShare() {
        return mShare;
    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onSuccess() {

    }

    public void onItemClick(int position, ShareItem item) {
        final Share share = getShare();
        switch (item.iconId) {
            //新浪微博
            case R.mipmap.ic_login_3party_weibo:
                showWaitDialog(R.string.login_weibo_hint);
                OpenBuilder.with(mActivity)
                        .useWeibo(OpenConstant.WB_APP_KEY)
                        .share(share, this);
                break;
            //朋友圈
            case R.mipmap.ic_action_moments:
                showWaitDialog(R.string.login_wechat_hint);
                OpenBuilder.with(mActivity)
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareTimeLine(share, this);
                break;
            //微信会话
            case R.mipmap.ic_login_3party_wechat:
                showWaitDialog(R.string.login_wechat_hint);
                OpenBuilder.with(mActivity)
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareSession(share, this);
                break;
            //QQ
            case R.mipmap.ic_login_3party_qq:
                showWaitDialog(R.string.login_tencent_hint);
                OpenBuilder.with(mActivity)
                        .useTencent(OpenConstant.QQ_APP_ID)
                        .share(share, new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                hideWaitDialog();
                            }

                            @Override
                            public void onError(UiError uiError) {
                                hideWaitDialog();
                                AppContext.showToast(R.string.share_hint, Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onCancel() {
                                hideWaitDialog();
                            }
                        }, this);
                break;
            //转发到动弹
            case R.mipmap.ic_action_tweet:
                if (About.check(mAboutShare))
                    TweetPublishActivity.show(getContext(), null, null, mAboutShare);
                if (isOnlyBitmap && mShare.getThumbBitmap() != null) {

                    showWaitDialog(R.string.loading_image);
                    AppOperator.runOnThread(new Runnable() {
                        @Override
                        public void run() {
                            final String url = OpenBuilder.saveShare(share.getThumbBitmap());
                            AppOperator.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (TextUtils.isEmpty(url)) return;
                                        TweetPublishActivity.show(getContext(), false, url);
                                        cancelLoading();
                                        hideProgressDialog();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
                break;
            //在浏览器中打开
            case R.mipmap.ic_action_browser:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                // intent.setAction(Intent.CATEGORY_BROWSABLE);
                Uri content_url = Uri.parse(share.getUrl());
                intent.setData(content_url);
                mActivity.startActivity(intent);
                cancelLoading();
                break;
            //复制链接
            case R.mipmap.ic_action_url:
                TDevice.copyTextToBoard(share.getUrl());
                cancelLoading();
                break;
            case R.mipmap.ic_action_screenshot:
                if (isShareTweet) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onShareTweet();
                    } else {
                        TweetShareActivity.show(getContext(), mTweet);
                    }
                    cancelLoading();
                } else {
                    ShareActivity.show(getContext(), mBean);
                    cancelLoading();
                }
                break;
            //保存到本地
            case R.mipmap.ic_action_preview:
                showWaitDialog(R.string.loading_image);
                AppOperator.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        final String url = OpenBuilder.saveShare(share.getThumbBitmap());
                        AppOperator.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!TextUtils.isEmpty(url)) {
                                        ImageGalleryActivity.show(mActivity, url);
                                    }
                                    cancelLoading();
                                    hideProgressDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                break;
            //更多(调用系统分享)
            default:
                showSystemShareOption(share.getTitle(), share.getUrl());
                cancelLoading();
                break;
        }
    }

    public static void saveShare(Activity activity, Bitmap bitmap) {
        FileOutputStream os = null;
        String url = null;
        try {
            File file = new File(url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/");
            if (!file.exists())
                file.mkdirs();
            url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "开源中国/" +
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
            StreamUtil.close(os);
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.fromFile(new File(url));
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                SimplexToast.show(activity, "保存成功");
            }
        }
    }

    private void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.cancel();
                // dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private ProgressDialog showWaitDialog(@StringRes int messageId) {
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(mActivity);
        }
        mDialog.setMessage(mActivity.getResources().getString(messageId));
        mDialog.show();
        return mDialog;
    }

    public void cancelLoading() {
        if (this != null && this.isShowing()) {
            this.cancel();
            this.dismiss();
            //mAlertDialog.dismiss();
        }
    }

    private void shareSystemImage(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = FileProvider.getUriForFile(mActivity, "net.oschina.app.provider", new File(url));
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        mActivity.startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }

    private void showSystemShareOption(final String title, final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        getContext().startActivity(Intent.createChooser(intent, "选择分享"));
    }

    private class ShareActionAdapter extends BaseRecyclerAdapter<ShareItem> {

        ShareActionAdapter(Context context) {
            super(context, NEITHER);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            return new ShareViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.dialog_share_item,
                    parent, false));
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, ShareItem item, int position) {
            ShareViewHolder h = (ShareViewHolder) holder;
            h.mIvIcon.setImageResource(item.iconId);
            h.mTvName.setText(item.nameId);
        }
    }


    static class ShareViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_icon)
        ImageView mIvIcon;
        @Bind(R.id.share_name)
        TextView mTvName;

        public ShareViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class ShareItem {
        int iconId;
        int nameId;

        ShareItem(int iconId, int nameId) {
            this.iconId = iconId;
            this.nameId = nameId;
        }
    }

    public void hideProgressDialog() {
        if (mDialog == null)
            return;
        mDialog.dismiss();
    }

    public interface ShareItemClickListener {
        void onShareTweet();
    }
}
