package net.oschina.app.improve.widget.rich;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 墨记富文本编辑器
 * 需要实现的功能如下：
 * 1：样式列表必须要和文本同步，这是为了复制样式
 * 2: 当两个EditText合并时，双方继承样式
 * <p>
 * 做法，删除文本的时候，需要合并段落，例如第二行从起点删除，合并到第一个段落，样式去掉，使用第一个段落的样式
 * 选择多个文本删除的时候，直接重新排版
 * <p>
 * Created by huanghaibin on 2017/8/3.
 */
@SuppressLint("ViewConstructor")
@SuppressWarnings("unused")
public class RichEditText extends AppCompatEditText implements TextWatcher, View.OnKeyListener, View.OnClickListener {
    private RichLinearLayout mParent;
    private boolean isMultiSelection;//多选模式
    private boolean isEnter, isDelete,isMultiDelete;
    static final int INDEX_START = 0;
    static final int INDEX_MID = 1;
    static final int INDEX_END = 2;
    OnSectionChangeListener mListener;
    private boolean isDelaying;
    /**
     * 段落列表
     */
    List<TextSection> mSections;

    public RichEditText(Context context, OnSectionChangeListener listener) {
        super(context);
        mListener = listener;
        mSections = new ArrayList<>();
        addTextChangedListener(this);
        setBackgroundColor(Color.TRANSPARENT);
        setOnKeyListener(this);
        mSections.add(getDefaultSection(context));
        //clearKeyboard();
    }

    private TextSection getDefaultSection(Context context) {
        TextSection defaultSection = new TextSection();
        defaultSection.setBold(false);
        defaultSection.setTextSize(18);
        defaultSection.setItalic(false);
        defaultSection.setText("");
        defaultSection.setAlignment(TextSection.LEFT);
        defaultSection.setColorHex("111111");
        return defaultSection;
    }

    /**
     * 清除软键盘，不弹出
     */
    private void clearKeyboard() {
        try {
            Class<EditText> cls = EditText.class;
            Method setSoftInputShownOnFocus;
            setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setSoftInputShownOnFocus.setAccessible(true);
            setSoftInputShownOnFocus.invoke(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 光标改变
     *
     * @param selStart 开始位置
     * @param selEnd   selEnd
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        isMultiSelection = (selEnd - selStart >= 1);
        if (isMultiSelection)
            return;
        int index = getSelectionIndexFromStart(selStart);
        if (mListener != null && index < mSections.size()) {
            mListener.onSectionChange(mSections.get(index));
        }
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 文本变化之前发生
     *
     * @param s     当前的文本
     * @param start 输入光标的起点
     * @param count 基本不用管
     * @param after 此次输入的多少字符串
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Log.e("beforeTextChanged", "" + s + "  --  " + start + "  --  " + count + "  --  " + after + "  --  " + getSelectionStart());
        if (isMultiSelection && isDelete && !isMultiDelete) {//调整段落
            adjustSection(s.toString(), start, count);
        }
    }

    /**
     * 文本变化后
     *
     * @param s 变化后的文本
     */
    @Override
    public void afterTextChanged(Editable s) {
        //Log.e("afterTextChanged", "  --  " + s.toString());
        isDelete = false;
        isPaste = false;
        isMultiDelete = false;
    }

    /**
     * 文本正在输入
     *
     * @param s      当前的字符
     * @param start  输入的光标位置
     * @param before 不用理
     * @param count  输入的量
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mSections == null || isPaste)
            return;
        int index = getSelectionIndex();//获取当前段落位置
        //Log.e("onTextChanged", "" + s + "  --  " + start + "  --  " + before + "  --  " + count + "  --   " + mSections.size() + "  --  " + index);
        if (index >= 0 && index < mSections.size()) {
            TextSection section = mSections.get(index);
            setSectionStyle(section);
        }
        if (!isEnter)
            return;
        int preIndex = index - 1;
        if (preIndex >= 0 && preIndex < mSections.size()) {
            TextSection section = mSections.get(preIndex);
            setColorSpan(Color.parseColor(("#" + section.getColorHex())), preIndex);
            setBold(section.isBold(), preIndex);
            setItalic(section.isItalic(), preIndex);
            setMidLine(section.isMidLine(), preIndex);
            setAlignStyle(section.getAlignment(), preIndex);
        }
        isEnter = false;
    }

    private boolean isPaste;

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {//拦截复制事件，清除复制文本的style
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item != null) {
                    String paste = item.coerceToText(getContext()).toString().trim();
                    isPaste = true;
                    int selectionStart = getSelectionStart();
                    int selectionEnd = getSelectionEnd();
                    int indexStart = getSelectionIndexFromStart(selectionStart);//光标起始段落
                    if (indexStart >= 0 && indexStart < mSections.size()) {
                        TextSection section = mSections.get(indexStart);
                        if (paste.contains("\n")) {//来自粘贴有换行的情况　
                            String[] selections = paste.split("\n", -1);
                            if (isMultiSelection || selectionStart < selectionEnd) {//如果是多选，注意头和尾之间的style段落都会被清除
                                int indexEnd = getSelectionIndexFromStart(selectionEnd);//光标结束位置
                                if (indexStart < indexEnd) {
                                    List<TextSection> sections = new ArrayList<>();
                                    for (int i = indexStart + 1; i <= indexEnd; i++) {
                                        sections.add(mSections.get(i));
                                    }
                                    mSections.removeAll(sections);
                                }
                                getText().replace(selectionStart, selectionEnd, paste);
                                setSectionStyle(section, indexStart);
                                int length = selections.length;
                                for (int i = 1; i < length; i++) {
                                    TextSection clone = section.cloneTextSelection();
                                    setSectionStyle(clone, indexStart + i);
                                    mSections.add(indexStart, clone);
                                }
                            } else {//如果是单选，则直接插入
                                getText().replace(selectionStart, selectionEnd, paste);
                                setSectionStyle(section, indexStart);
                                for (int i = 1; i < selections.length; i++) {
                                    TextSection clone = section.cloneTextSelection();
                                    mSections.add(indexStart, clone);
                                    setSectionStyle(clone, indexStart + i);
                                }
                            }
                        } else {//没有换行则不管
                            getText().replace(selectionStart, selectionEnd, paste);
                            setSectionStyle(section);
                        }
                    }
                }
                return true;
            }
        }
        if (id == android.R.id.cut) {//剪切
            isPaste = true;
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            final int indexStart = getSelectionIndexFromStart(selectionStart);//光标起始段落
            if (indexStart >= 0 && indexStart < mSections.size()) {
                final TextSection section = mSections.get(indexStart);
                if (isMultiSelection || selectionStart < selectionEnd) {//如果是多选，注意头和尾之间的style段落都会被清除
                    int indexEnd = getSelectionIndexFromStart(selectionEnd);//光标结束位置
                    if (indexStart < indexEnd) {
                        List<TextSection> sections = new ArrayList<>();
                        for (int i = indexStart + 1; i <= indexEnd; i++) {
                            sections.add(mSections.get(i));
                        }
                        mSections.removeAll(sections);
                    }
                    getText().delete(selectionStart, selectionEnd);
                    setSectionStyle(section, indexStart);
                    return true;
                }
            }
        }
        return super.onTextContextMenuItem(id);
    }

    /**
     * 情况就是开启多选模式
     * 调整段落
     * 合并style
     * 逻辑：
     */
    private void adjustSection(String s, int start, int count) {
        int startSection = getSectionIndex(start);
        int endSection = getSectionIndex(start + count);
        if (startSection == endSection)
            return;
        List<TextSection> removeSections = new ArrayList<>();
        for (int i = startSection + 1; i <= endSection; i++) {
            removeSections.add(mSections.get(i));
        }
        mSections.removeAll(removeSections);
        //最后一个要合并
        TextSection section = mSections.get(startSection);
        setSectionStyle(section);
    }

    private void setSectionStyle(TextSection section) {
        setColorSpan(Color.parseColor(("#" + section.getColorHex())));
        setBold(section.isBold());
        setItalic(section.isItalic());
        setMidLine(section.isMidLine());
        setAlignStyle(section.getAlignment());
        setTextSizeSpan(section.getTextSize());

    }

    void setSectionStyle(TextSection section, int index) {
        setColorSpan(Color.parseColor(("#" + section.getColorHex())), index);
        setBold(section.isBold(), index);
        setItalic(section.isItalic(), index);
        setMidLine(section.isMidLine(), index);
        setAlignStyle(section.getAlignment(), index);
        setTextSizeSpan(section.getTextSize(), index);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mParent.mFocusView = this;
        if (mParent.mFocusPanel != null) {
            mParent.mFocusPanel.showMode(false);
        }
        //mParent.mParent.mParent.setAdjustNothing();
        if (mParent.mParent.mParent.mContentPanel.getVisibility() == VISIBLE) {
            mParent.mParent.mParent.setAdjustNothing();
        }
        mParent.mParent.mParent.isKeyboardOpen = true;
        if (isDelaying) {
            return super.onTouchEvent(event);
        }
        isDelaying = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isDelaying = false;
                mParent.mParent.mParent.mContentPanel.setVisibility(GONE);
                mParent.mParent.mParent.setAdjustResize();
            }
        }, 500);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                isDelete = true;
                if (getSelectionStart() == 0 && !isMultiSelection) {
                    mParent.delete(this);
                    return true;
                }
                if (!isMultiSelection && isDeleteSection() && !TextUtils.isEmpty(getTextString())) {
                    int index = getSelectionIndex();
                    if (index >= 0 && index < mSections.size()) {
                        mSections.remove(getSelectionIndex());
                    }
                }else {
                    isMultiDelete = true;
                    int selectionStart = getSelectionStart();
                    int selectionEnd = getSelectionEnd();
                    final int indexStart = getSelectionIndexFromStart(selectionStart);//光标起始段落
                    if (indexStart >= 0 && indexStart < mSections.size()) {
                        final TextSection section = mSections.get(indexStart);
                        if (isMultiSelection || selectionStart < selectionEnd) {//如果是多选，注意头和尾之间的style段落都会被清除
                            int indexEnd = getSelectionIndexFromStart(selectionEnd);//光标结束位置
                            if (indexStart < indexEnd) {
                                List<TextSection> sections = new ArrayList<>();
                                for (int i = indexStart + 1; i <= indexEnd; i++) {
                                    sections.add(mSections.get(i));
                                }
                                mSections.removeAll(sections);
                            }
                            getText().delete(selectionStart, selectionEnd);
                            setSectionStyle(section, indexStart);
                            return true;
                        }
                    }
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                int index = getSelectionIndex();
                isEnter = true;
                if (index >= 0 && index < mSections.size()) {
                    TextSection section = mSections.get(index);
                    mSections.add(index, section.cloneTextSelection());
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mParent == null) {
            mParent = (RichLinearLayout) getParent();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                break;
            case KeyEvent.KEYCODE_ENTER:
                return false;
        }
        return false;
    }

    private String getTextString() {
        return getText().toString();
    }

    /**
     * 获取所处的段落
     *
     * @param index index 所处下标位置
     * @return 段落位置
     */
    private int getSectionIndex(int index) {
        String text = getTextString();
        if (TextUtils.isEmpty(text))
            return 0;
        String[] selections = text.split("\n", -1);
        int length = selections[0].length();
        if (text.length() <= index) {
            return selections.length - 1;
        }
        if (selections.length == 1 || length >= index) {
            return 0;
        }
        return findIndex(text.substring(0, index));
    }

    /**
     * @return 返回光标所在段落的位置
     */
    int getSelectionIndex() {
        String text = getTextString();
        if (TextUtils.isEmpty(text))
            return 0;
        int start = getSelectionStart();//光标位置
        String[] selections = text.split("\n", -1);
        int length = selections[0].length();
        //如果文本长度==光标位置  则是最后一个段落
        if (text.length() <= start) {
            return selections.length - 1;
        }
        //如果没有会车符号，或者光标位置在第一个个段落
        if (selections.length == 1 || length >= start) {
            return 0;
        } else {
            return findIndex(text.substring(0, start));
        }
    }

    int getSelectionIndexFromStart(int start) {
        String text = getTextString();
        if (TextUtils.isEmpty(text))
            return 0;
        //Log.e("count", "  --   " + findIndex(text.substring(0, start)));
        String[] selections = text.split("\n", -1);
        int length = selections[0].length();
        //如果文本长度==光标位置  则是最后一个段落
        if (text.length() <= start) {
            return selections.length - 1;
        }
        //如果没有会车符号，或者光标位置在第一个个段落
        if (selections.length == 1 || length >= start) {
            return 0;
        } else {
            return findIndex(text.substring(0, start));
        }
    }

    private static Pattern mPattern = Pattern.compile("\n");

    /**
     * 段落下标
     *
     * @param srcText 文本
     * @return 段落下标位置
     */
    private static int findIndex(String srcText) {
        int count = 0;
        Matcher m = mPattern.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * 获取段落文本
     *
     * @return 获取段落文本
     */
    private String getStringFromIndex(int index) {
        String text = getTextString();
        if (TextUtils.isEmpty(text) || !text.contains("\n"))
            return text;
        String[] selections = text.split("\n");
        if (index >= selections.length) {
            return text;
        }
        return selections[index];
    }

    private boolean isDeleteSection() {
        return getSelectionStart() == getSectionStart();
    }

    /**
     * 获取光标所在行数，对应段落
     *
     * @return 光标所在行数 0，1，2，3
     */
    private int getCurrentCursorLine() {
        int selectionStart = getSelectionStart();
        Layout layout = getLayout();
        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart);
        }
        return -1;
    }

    /**
     * 获取段落的文本起始位置
     *
     * @return 起始位置
     */
    private int getSectionStart() {
        int index = getSelectionIndex();
        if (index == 0) return 0;
        String text = getTextString();
        if (TextUtils.isEmpty(text))
            return 0;
        int start = 0;
        String[] selections = text.split("\n", -1);
        int length = selections[0].length();
        for (int i = 0; i < index; i++) {
            start += selections[i].length() + 1;
        }
        return start;
    }

    /**
     * 获取段落的文本起始位置
     *
     * @return 起始位置
     */
    int getSectionStart(int index) {
        if (index == 0) return 0;
        String text = getTextString();
        if (TextUtils.isEmpty(text))
            return 0;
        int start = 0;
        String[] selections = text.split("\n", -1);
        if (index >= selections.length)
            index = selections.length;
        for (int i = 0; i < index; i++) {
            start += selections[i].length() + 1;
        }
        return start;
    }

    /**
     * 获取段落的文本结束位置
     *
     * @return 结束位置
     */
    private int getSectionEnd() {
        String text = getTextString();
        int index = getSelectionIndex();
        int end = 0;
        String[] selections = text.split("\n", -1);
        if (index == 0) return selections[0].length();
        for (int i = 0; i <= index; i++) {
            end += selections[i].length() + 1;
            if (i == index) {
                end -= 1;
            }
        }
        return end;
    }

    /**
     * 获取段落的文本结束位置
     *
     * @return 结束位置
     */
    private int getSectionEnd(int index) {
        String text = getTextString();
        int end = 0;
        String[] selections = text.split("\n", -1);
        if (index == 0) return selections[0].length();
        if (index >= selections.length) {
            return text.length();
        }
        for (int i = 0; i <= index; i++) {
            end += selections[i].length() + 1;
            if (i == index) {
                end -= 1;
            }
        }
        return end;
    }

    void setBold(boolean isBold) {
        int index = getSelectionIndex();
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setBold(isBold);
        }
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        if (isBold) {
            edit.setSpan(new StyleSpan(Typeface.BOLD),
                    star,
                    end,
                    Typeface.BOLD);
        } else {
            StyleSpan[] styleSpans = edit.getSpans(star,
                    end, StyleSpan.class);
            for (CharacterStyle span : styleSpans) {
                if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.BOLD)
                    edit.removeSpan(span);
            }
        }
    }


    void setItalic(boolean isItalic) {
        int index = getSelectionIndex();
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setItalic(isItalic);
        }
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        if (isItalic) {
            edit.setSpan(new StyleSpan(Typeface.ITALIC),
                    star,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            StyleSpan[] styleSpans = edit.getSpans(star,
                    end, StyleSpan.class);
            for (CharacterStyle span : styleSpans) {
                if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.ITALIC)
                    edit.removeSpan(span);
            }
        }
    }

    void setItalic(boolean isItalic, int index) {
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setItalic(isItalic);
        }
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        if (isItalic) {
            edit.setSpan(new StyleSpan(Typeface.ITALIC),
                    star,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            StyleSpan[] styleSpans = edit.getSpans(star,
                    end, StyleSpan.class);
            for (CharacterStyle span : styleSpans) {
                if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.ITALIC)
                    edit.removeSpan(span);
            }
        }
    }

    void setBold(boolean isBold, int index) {
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setBold(isBold);
        }
        Editable edit = getEditableText();
        int star = getSectionStart(index);
        int end = getSectionEnd(index);
        if (star >= end)
            return;
        if (isBold) {
            edit.setSpan(new StyleSpan(Typeface.BOLD),
                    star,
                    end,
                    Typeface.BOLD);
        } else {
            StyleSpan[] styleSpans = edit.getSpans(star,
                    end, StyleSpan.class);
            for (CharacterStyle span : styleSpans) {
                if (span instanceof StyleSpan && ((StyleSpan) span).getStyle() == Typeface.BOLD)
                    edit.removeSpan(span);
            }
        }
    }

    /**
     * 中横线，即删除线
     *
     * @param isMidLine isMidLine
     */
    void setMidLine(boolean isMidLine) {
        int index = getSelectionIndex();
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setMidLine(isMidLine);
        }
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        if (isMidLine) {
            edit.setSpan(new StrikethroughSpan(),
                    star,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            StrikethroughSpan[] styleSpans = edit.getSpans(star,
                    end, StrikethroughSpan.class);
            for (StrikethroughSpan span : styleSpans) {
                edit.removeSpan(span);
            }
        }
    }

    /**
     * 中横线，即删除线
     *
     * @param isMidLine isMidLine
     */
    void setMidLine(boolean isMidLine, int index) {
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setMidLine(isMidLine);
        }
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        if (isMidLine) {
            edit.setSpan(new StrikethroughSpan(),
                    star,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            StrikethroughSpan[] styleSpans = edit.getSpans(star,
                    end, StrikethroughSpan.class);
            for (StrikethroughSpan span : styleSpans) {
                edit.removeSpan(span);
            }
        }
    }


    void setAlignStyle(int align) {
        int index = getSelectionIndex();
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setAlignment(align);
        }
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        AlignmentSpan[] spans = edit.getSpans(star, end, AlignmentSpan.class);
        for (AlignmentSpan span : spans) {
            edit.removeSpan(span);
        }
        edit.setSpan(getAlignmentSpan(align), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    void setAlignStyle(int align, int index) {
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setAlignment(align);
        }
        Editable edit = getEditableText();
        int star = getSectionStart(index);
        int end = getSectionEnd(index);
        if (star >= end)
            return;
        edit.setSpan(getAlignmentSpan(align), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    void setColorSpan(String color) {
        int index = getSelectionIndex();
        if (index >= 0 && index < mSections.size()) {
            mSections.get(index).setColorHex(color);
        }
        setColorSpan(Color.parseColor("#" + color));
    }

    void setColorSpan(int color) {
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        ForegroundColorSpan[] styleSpans = edit.getSpans(star, end, ForegroundColorSpan.class);
        for (ForegroundColorSpan span : styleSpans) {
            edit.removeSpan(span);
        }
        edit.setSpan(new ForegroundColorSpan(color), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    void setColorSpan(int color, int index) {
        Editable edit = getEditableText();
        int star = getSectionStart(index);
        int end = getSectionEnd(index);
        if (star >= end)
            return;
        ForegroundColorSpan[] styleSpans = edit.getSpans(star, end, ForegroundColorSpan.class);
        for (ForegroundColorSpan span : styleSpans) {
            edit.removeSpan(span);
        }
        edit.setSpan(new ForegroundColorSpan(color), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setTextSizeSpan(int index, boolean isIncrease) {
        Editable edit = getEditableText();
        int textSize = 16;
        if (index >= 0 && index < mSections.size()) {
            TextSection section = mSections.get(index);
            int s = section.getTextSize();
            s = s + (isIncrease ? 1 : -1);
            section.setTextSize(s);
            textSize = s;
        }
        setTextSizeSpan(index, textSize);
    }


    void setTextSizeSpanIncrease(boolean isIncrease) {
        int index = getSelectionIndex();
        Editable edit = getEditableText();
        int textSize = 16;
        if (index >= 0 && index < mSections.size()) {
            TextSection section = mSections.get(index);
            int s = section.getTextSize();
            if ((s == 70 && isIncrease) || (s == 10 && !isIncrease))// 10- 70 sp
                return;
            s = s + (isIncrease ? 1 : -1);
            section.setTextSize(s);
            if (mListener != null) {
                mListener.onSectionChange(section);
            }
            textSize = s;
        }
        setTextSizeSpan(textSize);
    }


    private void setTextSizeSpan(int textSize, int index) {
        Editable edit = getEditableText();
        int star = getSectionStart(index);
        int end = getSectionEnd(index);
        if (star >= end)
            return;
        AbsoluteSizeSpan[] styleSpans = edit.getSpans(star, end, AbsoluteSizeSpan.class);
        for (AbsoluteSizeSpan span : styleSpans) {
            edit.removeSpan(span);
        }
        edit.setSpan(new AbsoluteSizeSpan(UI.dipToPx(getContext(), textSize)), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setTextSizeSpan(int textSize) {
        Editable edit = getEditableText();
        int star = getSectionStart();
        int end = getSectionEnd();
        if (star >= end)
            return;
        AbsoluteSizeSpan[] styleSpans = edit.getSpans(star, end, AbsoluteSizeSpan.class);
        for (AbsoluteSizeSpan span : styleSpans) {
            edit.removeSpan(span);
        }
        edit.setSpan(new AbsoluteSizeSpan(UI.dipToPx(getContext(), textSize)), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 设置字体大小
     * @param textSize textSize
     */
    void setTextSize(int textSize) {
        Editable edit = getEditableText();
        int index = getSelectionIndex();
        int star = getSectionStart();
        int end = getSectionEnd();
        if (star >= end)
            return;
        if (index >= 0 && index < mSections.size()) {
            TextSection section = mSections.get(index);
            section.setTextSize(textSize);
            if (mListener != null) {
                mListener.onSectionChange(section);
            }
        }
        AbsoluteSizeSpan[] styleSpans = edit.getSpans(star, end, AbsoluteSizeSpan.class);
        for (AbsoluteSizeSpan span : styleSpans) {
            edit.removeSpan(span);
        }
        edit.setSpan(new AbsoluteSizeSpan(UI.dipToPx(getContext(), textSize)), star, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        update(index);
    }


    /**
     * 字体大小改变，会导致后续的段落无法及时刷新，调用这个方法即可
     *
     * @param position 段落位置
     */
    private void update(int position) {
        if (mSections == null || mSections.size() <= 1 || position >= mSections.size() - 1)
            return;
        for (int i = position; i < mSections.size(); i++) {
            TextSection section = mSections.get(i);
            setSectionStyle(section, i);
        }
    }

    /**
     * 合并RichEditText
     *
     * @param removeEdit    被移除的
     * @param mergeEditText 合并后的
     */
    static void mergeRichEditText(RichEditText removeEdit, RichEditText mergeEditText, String content) {
        if (TextUtils.isEmpty(removeEdit.getTextString())) {
            mergeEditText.mSections = removeEdit.mSections;
        } else {
            List<TextSection> sections = removeEdit.mSections;
            if (TextUtils.isEmpty(content)) {
                mergeEditText.mSections = sections;
            } else {
                mergeEditText.mSections.remove(0);
                List<TextSection> mergeSections = new ArrayList<>();
                for (int i = 0; i < sections.size(); i++) {
                    mergeSections.add(sections.get(i).cloneTextSelection());
                }
                mergeSections.addAll(mergeEditText.mSections);
                mergeEditText.mSections = mergeSections;
            }
        }
        for (int i = 0; i < mergeEditText.mSections.size(); i++) {
            TextSection section = mergeEditText.mSections.get(i);
            mergeEditText.setSectionStyle(section, i);
        }
    }

    /**
     * 合并RichEditText
     *
     * @param removeEdit    被移除的 需要移除第一个段落，合并到最后一个段落,如果是空的就没必要合并
     * @param mergeEditText 合并后的
     * @param mergeIndex    合并的位置，末尾处
     */
    static void mergeRichEditText(RichEditText removeEdit, RichEditText mergeEditText, int mergeIndex) {
        List<TextSection> sections = removeEdit.mSections;
        sections.remove(0);//移除第一个段落
        for (TextSection section : sections) {
            mergeEditText.mSections.add(section.cloneTextSelection());
        }
        for (int i = 0; i < mergeEditText.mSections.size(); i++) {
            TextSection section = mergeEditText.mSections.get(i);
            mergeEditText.setSectionStyle(section, i);
        }
    }

    /**
     * 继承风格，适用与RichEditText 起点、中间、结尾 插入图片的情况
     * 起点：oldEditText newEditText继承oldEditText全部段落，oldEditText被移除
     * 中间：oldEditText 保留下标之前、包括下标所在段落　， newEditText 继承oldEditText下标之后、包括下标所在段落
     * 结尾：oldEditText 保留所有段落 ， newEditText继承oldEditText最后一个段落
     *
     * @param oldEditText 被继承的 RichEditText
     * @param newEditText 继承的 RichEditText
     * @param indexType   下标所在位置类型
     *                    static final int INDEX_START = 0; 起点
     *                    static final int INDEX_MID = 1; 居中
     *                    static final int INDEX_END = 2; 末尾
     */
    static void inheritStyle(RichEditText oldEditText, RichEditText newEditText, int index, int indexType) {
        switch (indexType) {
            case INDEX_START:
                newEditText.mSections = oldEditText.mSections;
                break;
            case INDEX_MID:
                newEditText.mSections.clear();
                List<TextSection> oldSections = oldEditText.mSections;
                if (oldSections.size() == 1) {
                    newEditText.mSections.add(oldSections.get(0).cloneTextSelection());
                } else {
                    List<TextSection> removes = new ArrayList<>();
                    for (int i = index; i < oldSections.size(); i++) {
                        TextSection section = oldSections.get(i);
                        newEditText.mSections.add(section.cloneTextSelection());
                        if (i != index) {
                            removes.add(section);
                        }
                    }
                    oldSections.removeAll(removes);
                    for (int i = 0; i < oldSections.size(); i++) {
                        TextSection section = oldSections.get(i);
                        //重新设置被分割的RichEditText样式，因为它重新设置了Text
                        oldEditText.setSectionStyle(section, i);
                    }
                }
                break;
            case INDEX_END:
                newEditText.mSections.clear();
                newEditText.mSections.add(oldEditText.mSections.get(oldEditText.mSections.size() - 1).cloneTextSelection());
                break;
        }
        for (int i = 0; i < newEditText.mSections.size(); i++) {
            TextSection section = newEditText.mSections.get(i);
            newEditText.setSectionStyle(section, i);
        }
    }


    /**
     * 切换段落
     */
    public interface OnSectionChangeListener {
        void onSectionChange(TextSection section);
    }

    private static AlignmentSpan getAlignmentSpan(int align) {
        return new AlignmentSpan.Standard(getAlignment(align));
    }

    private static AlignmentSpan getAlignmentSpan(TextSection section) {
        return new AlignmentSpan.Standard(getAlignment(section.getAlignment()));
    }

    private static Layout.Alignment getAlignment(int alignment) {
        if (alignment == TextSection.CENTER) {
            return Layout.Alignment.ALIGN_CENTER;
        } else if (alignment == TextSection.RIGHT)
            return Layout.Alignment.ALIGN_OPPOSITE;
        return Layout.Alignment.ALIGN_NORMAL;
    }

    /**
     * 生成段落文本输出
     * 段落如果是空白的，也就是只有\n 连空格都没有, 则在上一个段落 + \n
     *
     * @param preViewIsImage 图片的下一个段落要在起始 + \n 回车
     * @param isLastIndex    是否是最后一个
     * @return 段落列表
     */
    @SuppressWarnings("all")
    List<TextSection> getTextSections() {
        String text = getTextString();
        String[] selections = text.split("\n", -1);
        int length = selections.length;
        if (length == 1 && TextUtils.isEmpty(text.trim()))
            return null;

        List<TextSection> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            TextSection section = mSections.get(i).cloneTextSelection();
            section.setText(selections[i]);
            list.add(section);
        }
        return list;
    }
}