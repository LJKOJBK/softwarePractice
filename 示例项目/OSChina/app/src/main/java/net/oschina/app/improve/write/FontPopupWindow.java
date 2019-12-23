package net.oschina.app.improve.write;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import net.oschina.app.R;
import net.oschina.app.improve.widget.rich.TextSection;

/**
 * 弹出H对话框
 * Created by huanghaibin on 2017/8/31.
 */

class FontPopupWindow extends PopupWindow implements View.OnClickListener{

    private ImageView mImageBold, mImageItalic, mImageLine;
    private OnFontChangeListener mListener;

    @SuppressLint("InflateParams")
    FontPopupWindow(Context context,OnFontChangeListener listener) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_window_font, null),
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_anim_style_alpha);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //setFocusable(true);

        this.mListener = listener;
        View content = getContentView();
        mImageBold = (ImageView) content.findViewById(R.id.iv_font_bold);
        mImageItalic = (ImageView) content.findViewById(R.id.iv_font_italic);
        mImageLine = (ImageView) content.findViewById(R.id.iv_font_line);
        mImageBold.setOnClickListener(this);
        mImageItalic.setOnClickListener(this);
        mImageLine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_font_bold:
                mImageBold.setSelected(!mImageBold.isSelected());
                mListener.onBoldChange(mImageBold.isSelected());
                DrawableCompat.setTint(mImageBold.getDrawable(), mImageBold.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);
                break;
            case R.id.iv_font_italic:
                mImageItalic.setSelected(!mImageItalic.isSelected());
                mListener.onItalicChange(mImageItalic.isSelected());
                DrawableCompat.setTint(mImageItalic.getDrawable(), mImageItalic.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);
                break;
            case R.id.iv_font_line:
                mImageLine.setSelected(!mImageLine.isSelected());
                mListener.onMidLineChange(mImageLine.isSelected());
                DrawableCompat.setTint(mImageLine.getDrawable(), mImageLine.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);
                break;
        }
    }

    void setStyle(TextSection section) {
        DrawableCompat.setTint(mImageBold.getDrawable(), section.isBold() ? 0xff24cf5f : 0xFFFFFFFF);
        DrawableCompat.setTint(mImageItalic.getDrawable(), section.isItalic() ? 0xff24cf5f : 0xFFFFFFFF);
        DrawableCompat.setTint(mImageLine.getDrawable(), section.isMidLine() ? 0xff24cf5f : 0xFFFFFFFF);
    }

    void show(View v) {
        showAsDropDown(v, 0, -2 * v.getMeasuredHeight() + 10);
    }

    interface OnFontChangeListener {
        void onBoldChange(boolean isBold);

        void onItalicChange(boolean isItalic);

        void onMidLineChange(boolean isMidLine);
    }
}
