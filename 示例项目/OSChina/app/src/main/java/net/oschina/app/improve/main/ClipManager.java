package net.oschina.app.improve.main;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.main.update.OSCSharedPreference;

import java.util.regex.Pattern;

/**
 * 剪切版监听
 * Created by huanghaibin on 2017/12/1.
 */

public final class ClipManager {

    public static boolean IS_SYSTEM_URL = false;
    private static OnClipChangeListener mListener;
    private static ClipboardManager mManager;
    private static ClipboardManager.OnPrimaryClipChangedListener mChangeListener;
    private static String mUrl;

    public static void register(Context context, OnClipChangeListener listener) {
        mManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mListener = listener;
        if (mChangeListener == null) {
            mChangeListener = new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    if (mManager == null)
                        return;
                    try {
                        if (mManager.hasPrimaryClip() && mManager.getPrimaryClip().getItemCount() > 0) {
                            CharSequence addedText = mManager.getPrimaryClip().getItemAt(0).getText();
                            if (addedText != null && mListener != null && checkUrl(addedText.toString())) {
                                if (BaseActivity.IS_ACTIVE) {
                                    if (OSCSharedPreference.getInstance().isRelateClip() && !IS_SYSTEM_URL) {
                                        OSCSharedPreference.getInstance().putLastShareUrl(mUrl);
                                        mListener.onClipChange(addedText.toString());
                                        mUrl = null;
                                    }
                                } else {
                                    mUrl = addedText.toString();
                                }
                            } else {
                                mUrl = null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUrl = null;
                    }

                }
            };
        }
        if (mManager == null)
            return;
        mManager.addPrimaryClipChangedListener(mChangeListener);
        String url = getClipText();
        if (checkUrl(url) && !url.equals(OSCSharedPreference.getInstance().getLastShareUrl())) {
            OSCSharedPreference.getInstance().putLastShareUrl(url);
            mListener.onClipChange(getClipText());
        }
    }

    @SuppressWarnings("unused")
    static void unregister() {
        mListener = null;
        if (mManager == null || mChangeListener == null)
            return;
        mManager.removePrimaryClipChangedListener(mChangeListener);
        mManager = null;
        mChangeListener = null;
    }

    public static void onResume() {
        if (mManager == null || TextUtils.isEmpty(mUrl) || mListener == null)
            return;
        if (OSCSharedPreference.getInstance().isRelateClip()) {
            OSCSharedPreference.getInstance().putLastShareUrl(mUrl);
            mListener.onClipChange(mUrl);
        }
        mUrl = null;
    }

    public interface OnClipChangeListener {
        void onClipChange(String url);
    }

    private static boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        return pattern.matcher(url).find();
    }

    private static String getClipText() {
        try {
            if (mManager != null && mManager.hasPrimaryClip() && mManager.getPrimaryClip().getItemCount() > 0) {
                CharSequence addedText = mManager.getPrimaryClip().getItemAt(0).getText();
                if (addedText != null) {
                    return addedText.toString();
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getClipUrl() {
        String text = getClipText();
        if (checkUrl(text)) {
            return text;
        }
        return "";
    }
}
