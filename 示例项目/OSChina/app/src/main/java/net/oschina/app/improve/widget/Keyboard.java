package net.oschina.app.improve.widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 键盘辅助类
 * Created by huanghaibin on 2017/7/11.
 */
@SuppressWarnings("unused")
public final class Keyboard {

    public static int KEYBOARD_HEIGHT = 0;

    public static void openKeyboard(View view) {
        view.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void copy(Context context, String text) {
        if (text == null)
            return;
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", text);
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(clipData);
    }

}

