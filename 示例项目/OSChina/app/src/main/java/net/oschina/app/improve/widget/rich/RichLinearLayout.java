package net.oschina.app.improve.widget.rich;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.oschina.app.R;

import java.util.List;

/**
 * 真正的编辑器内容布局
 * Created by huanghaibin on 2017/8/3.
 */
@SuppressWarnings("unused")
public class RichLinearLayout extends LinearLayout {
    RichScrollView mParent;
    RichEditText mFocusView;
    ImagePanel mFocusPanel;
    RichEditText.OnSectionChangeListener mListener;
    AppCompatEditText mEditTitle, mEditSummary;


    public RichLinearLayout(Context context) {
        this(context, null);
    }

    public RichLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        init(context);
    }

    @SuppressWarnings("all")
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.rich_linear_layout, this, true);
        mEditTitle = (AppCompatEditText) findViewById(R.id.et_title);

        mEditTitle.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mParent.mParent.mContentPanel.getVisibility() == VISIBLE) {
                    mParent.mParent.setAdjustNothing();
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mParent.mParent.mContentPanel.setVisibility(GONE);
                        mParent.mParent.setAdjustResize();
                    }
                }, 500);
                return false;
            }
        });
        mEditTitle.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mParent == null || mParent.mParent == null)
                    return;
                mParent.mParent.mRichBar.setBarEnable(!hasFocus);
            }
        });

//        mEditSummary.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (mParent.mParent.mContentPanel.getVisibility() == VISIBLE) {
//                    mParent.mParent.setAdjustNothing();
//                }
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mParent.mParent.mContentPanel.setVisibility(GONE);
//                        mParent.mParent.setAdjustResize();
//                    }
//                }, 500);
//                return false;
//            }
//        });

        RichEditText editText = new RichEditText(context, mListener);
        mFocusView = editText;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(params);
        setPadding(UI.dipToPx(context, 16), 0, UI.dipToPx(context, 16), 0);
        addView(editText);

    }


    String getTitle() {
        return mEditTitle.getText().toString().trim().replace("\n","");
    }

    String getSummary() {
        return mEditSummary.getText().toString().trim();
    }

    /**
     * 删除文本判断第一个字符的操作
     *
     * @param editText 当前富文本editText
     */
    @SuppressLint("SetTextI18n")
    void delete(RichEditText editText) {
        int count = getChildCount();
        if (count <= 2)
            return;
        for (int i = 1; i < count; i++) {
            View view = getChildAt(i);
            if (view == editText) {
                String content = editText.getText().toString();
                if (i == 0) {//如果是第一个 RichEditText 而且没有输入，则删除
                    if (!TextUtils.isEmpty(content))
                        return;
                    removeView(editText);
                    View nextView = getChildAt(0);
                    if (nextView instanceof ImagePanel) {
                        mFocusPanel = (ImagePanel) nextView;
                        mFocusPanel.setFocusMode();
                    } else {
                        mFocusView = (RichEditText) nextView;
                        mFocusView.requestFocus();
                    }
                    return;
                }
                View preView = getChildAt(i - 1);
                if (preView instanceof ImagePanel) {
                    ImagePanel panel = (ImagePanel) preView;
                    if (panel.isDeleteMode) {//有焦点，直接remove ImagePanel ,
                        int index = i - 2;
                        if (index >= 0 && index < getChildCount() && getChildAt(index) instanceof RichEditText) {//合并 RichEditText
                            RichEditText preEdit = (RichEditText) getChildAt(index);//被移除的 RichEditText
                            String text = preEdit.getText().toString() + content;
                            editText.setText(text);
                            mFocusView = editText;
                            mFocusView.setSelection(mFocusView.getText().toString().length());
                            RichEditText.mergeRichEditText(preEdit, mFocusView, content);
                            removeView(preEdit);
                            removeView(panel);
                        } else {
                            removeView(panel);
                        }
                    } else {
                        if (TextUtils.isEmpty(content) && i != count - 1) {
                            removeView(editText);
                            panel.showDeleteMode(i);
                            mFocusPanel = panel;
                            mFocusPanel.setFocusMode();
                        } else {
                            //没有焦点请求显示焦点编辑模式
                            panel.showDeleteMode(i);
                            mFocusPanel = panel;
                        }
                    }
                } else {
                    RichEditText preEdit = (RichEditText) preView;
                    preEdit.setText(preEdit.getText().toString() + "\n" + content);
                    preEdit.setSelection(preEdit.getText().toString().length());
                    mFocusView = preEdit;
                    removeView(editText);
                }
            }
        }
    }

    /**
     * 插入图片 width=match_parent, height=wrap_content
     *
     * @param image 图片路径
     */
    void insertImagePanel(String image) {
        ImagePanel panel = new ImagePanel(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, UI.dipToPx(getContext(), 8), 0, UI.dipToPx(getContext(), 8));
        panel.setLayoutParams(params);
        panel.setImagePath(image);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof RichEditText) {
                if (view.isFocused() || view == mFocusView) {
                    RichEditText curEditText = (RichEditText) view;//当前正在操作的 RichEditText
                    int sectionIndex = curEditText.getSelectionIndex();
                    int indexType = 0;
                    String content = curEditText.getText().toString();
                    if (TextUtils.isEmpty(content)) {//如果插入行没有文本，直接移除
                        removeView(view);
                        addView(panel, i);
                    } else {
                        int start = curEditText.getSelectionStart();//光标起点
                        if (start == 0) {//在RichEditText起点插入，忽略
                            addView(panel, i);
                            return;
                        } else if (start != content.length()) {//在EditText中间插入，分割，注意保留style
                            indexType = RichEditText.INDEX_MID;//合并的时机
                            curEditText.setText(content.substring(0, start));
                            addView(panel, i + 1);
                            final RichEditText editText = new RichEditText(getContext(), mListener);
                            editText.setText(content.substring(start));
                            mFocusView = editText;
                            RichEditText.inheritStyle(curEditText, editText, sectionIndex, indexType);//合并的时机
                            addView(editText, i + 2);
                            if (mParent == null) {
                                mParent = (RichScrollView) getParent();
                            }
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mParent == null)
                                        return;
                                    mParent.smoothScrollTo(0, editText.getTop());
                                    editText.requestFocus();
                                    //openKeyboard(editText);
                                }
                            }, 500);
                            return;
                        } else {// 在RichEditText末尾插入，忽略
                            indexType = RichEditText.INDEX_END;
                            addView(panel, i + 1);
                        }
                    }
                    if (i == count - 1) {//加在最后一个，自动插入输入控件
                        final RichEditText editText = new RichEditText(getContext(), mListener);
                        mFocusView = editText;
                        addView(editText);
                        RichEditText.inheritStyle(curEditText, editText, sectionIndex, indexType);
                        if (mParent == null) {
                            mParent = (RichScrollView) getParent();
                        }
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mParent == null)
                                    return;
                                mParent.smoothScrollTo(0, editText.getTop());
                                editText.requestFocus();
                                //openKeyboard(editText);
                            }
                        }, 500);
                    }
                }
            }
        }
    }

    /**
     * * 调整布局
     * 触发情况
     * EditText + ImagePanel + EditText ImagePanel被删除的情况，EdiText要进行合并
     * ImagePanel 下已经有EditText 点击回车的情况
     *
     * @param imagePanel 图片布局
     * @param isDelete   是否是删除操作,否则是回车
     */
    void adjustLayout(final ImagePanel imagePanel, boolean isDelete) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view == imagePanel) {
                if (isDelete) {
                    adjustLayoutDeleteImage(imagePanel, count, i);
                } else {
                    adjustLayoutEnter(imagePanel, count, i);
                }
            }
        }
    }

    /**
     * 调整图片回车后的布局，
     * ImagePanel + RichEditText 的情况 RichEditText获得焦点移动光标
     * ImagePanel + ImagePanel 的情况插入 RichEditText获得焦点
     *
     * @param imagePanel 被删除的图片
     * @param count      子View数量
     * @param index      被删除图片的位置
     */
    private void adjustLayoutEnter(final ImagePanel imagePanel, int count, int index) {
        int nextIndex = index + 1;
        if (nextIndex >= 0 && nextIndex < count) {
            final View nextView = getChildAt(nextIndex);
            if (nextView instanceof ImagePanel) {//如果下个View是 ImagePanel ，则插入RichEditText，往上遍历RichEditText
                final RichEditText editText = new RichEditText(getContext(), mListener);
                mFocusView = editText;
                for (int i = index - 1; i >= 0; i--) {
                    View view = getChildAt(i);
                    if (view instanceof RichEditText) {
                        editText.mSections.clear();
                        RichEditText preEditText = (RichEditText) view;
                        editText.mSections.add(preEditText.mSections.get(preEditText.mSections.size() - 1).cloneTextSelection());
                        break;
                    }
                }
                addView(editText, index + 1);
                if (mParent == null) {
                    mParent = (RichScrollView) getParent();
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mParent == null)
                            return;
                        mParent.smoothScrollTo(0, editText.getTop());
                        editText.requestFocus();
                        openKeyboard(editText);
                    }
                }, 200);
            } else {
                RichEditText editText = (RichEditText) nextView;
                editText.setSelection(editText.getText().toString().length());
                mFocusView = editText;
                mParent.smoothScrollTo(0, editText.getTop());
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFocusView.requestFocus();
                    }
                }, 100);
            }
        }
    }

    /**
     * 调整删除图片后的布局，主要是 RichEditText + ImagePanel + RichEditText 的情况合并两个RichEditText
     *
     * @param imagePanel 被删除的图片
     * @param count      子View数量
     * @param index      被删除图片的位置
     */
    @SuppressLint("SetTextI18n")
    private void adjustLayoutDeleteImage(final ImagePanel imagePanel, int count, int index) {
        if (index == 0) {
            removeView(imagePanel);
            setRichEditTextFocus();
            return;
        }
        int nextIndex = index + 1;
        int preIndex = index - 1;
        View preView;
        if (preIndex >= 0 && preIndex < count) {
            preView = getChildAt(preIndex);
            if (preView instanceof ImagePanel) {//如果前一个是 ImagePanel ，直接移除当前ImagePanel
                setRichEditTextFocus();
                removeView(imagePanel);
                return;
            }
        } else {
            setRichEditTextFocus();
            removeView(imagePanel);
            return;
        }
        if (nextIndex >= 0 && nextIndex < count) {
            final View nextView = getChildAt(nextIndex);
            if (nextView instanceof ImagePanel) {//下一个是ImagePanel
                ImagePanel nextPanel = (ImagePanel) nextView;
                removeView(imagePanel);
                mFocusPanel = nextPanel;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFocusPanel.setFocusMode();
                    }
                }, 300);
                return;
            }//
            RichEditText preEditText = (RichEditText) preView;//前一个是 RichEditText
            RichEditText nextEditText = (RichEditText) nextView;// 下一个也是 RichEditText
            String content = nextEditText.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                int preSectionIndex = preEditText.mSections.size() - 1;
                preEditText.setText(preEditText.getText().toString() + content);
                RichEditText.mergeRichEditText(nextEditText, preEditText, preSectionIndex);//合并 段落
            }
            preEditText.setSelection(preEditText.getText().toString().length());//合并两个 RichEditText
            removeView(nextEditText);
            mFocusView = preEditText;
            removeView(imagePanel);
            setRichEditTextFocus();
        }
    }

    /**
     * 让当前RichEditText获取焦点
     */
    private void setRichEditTextFocus() {
        mFocusView.setFocusable(true);
        mFocusView.setFocusableInTouchMode(true);
        mFocusView.requestFocus();
    }

    void setBold(boolean isBold) {
        mFocusView.setBold(isBold);
    }


    void setItalic(boolean isItalic) {
        mFocusView.setItalic(isItalic);
    }

    void setMidLine(boolean isMidLine) {
        mFocusView.setMidLine(isMidLine);
    }

    void setAlignStyle(int align) {
        mFocusView.setAlignStyle(align);
    }

    void setColorSpan(String color) {
        mFocusView.setColorSpan(color);
    }

    void setTextSizeSpan(boolean isIncrease) {
        mFocusView.setTextSizeSpanIncrease(isIncrease);
    }

    void setTextSize(int textSize) {
        mFocusView.setTextSize(textSize);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mParent == null) {
            mParent = (RichScrollView) getParent();
        }
    }

    private void openKeyboard(EditText view) {
        view.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 初始化界面
     * 遍历段落，连续的TextSection只能有一个richEditText
     * 判断下一个段落是不是图片，是图片就插入图片，同时新建一个RichEditText
     * <p>
     * 注意初始化日记的时候导入段落
     * 最终使用的时候要在每一个文本段落起始位置和末尾都去掉一个 \n
     * 然后前面有多少个 \n 就加多少个段落，继承当前段落
     * 后面有多少个 \n 就追加多少个段落,继承当前段落
     */
    @SuppressLint("SetTextI18n")
    void init(List<Section> sections) {
        if (sections == null || sections.size() == 0)
            return;
        mFocusView = null;
        mFocusPanel = null;
        removeAllViews();
        RichEditText richEditText = null;
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            boolean nextIsImage = false;
            int nextIndex = i + 1;
            Section nextSection = null;
            if (nextIndex < sections.size()) {
                nextSection = sections.get(nextIndex);
                nextIsImage = nextSection instanceof ImageSection;
            }
            if (section instanceof ImageSection) {
                ImageSection imageSection = (ImageSection) section;
                ImagePanel panel = new ImagePanel(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, UI.dipToPx(getContext(), 8), 0, UI.dipToPx(getContext(), 8));
                panel.setLayoutParams(params);
                panel.setImagePath(imageSection.getFilePath());
                addView(panel);
                richEditText = null;
            } else {
                TextSection textSection = (TextSection) section;
                if (richEditText == null) {//初始化的时候必须要移除所有默认段落样式
                    richEditText = new RichEditText(getContext(), mListener);
                    richEditText.mSections.clear();
                }
                String text = textSection.getText();
                //最终使用的时候要在除了第一个是RichEditText每一个文本段落起始位置和末尾都去掉一个 \n
//                if (text.startsWith("\n") && i != 0) {
//                    text = text.substring(1, text.length());
//                }
//                if (text.endsWith("\n")) {
//                    text = text.substring(0, text.length() - 1);
//                }
                String copyText = text;
                //一个段落前面有多少个\n，则前面就有多少行，即多少个空的段落
                while (copyText.startsWith("\n")) {
                    copyText = copyText.substring(1, copyText.length());
                    TextSection preSection = textSection.cloneTextSelection();
                    preSection.setText("");
                    richEditText.mSections.add(preSection);
                }
                //中间其实就是直接把文本设置进去即可
                String line = (TextUtils.isEmpty(richEditText.getText().toString()) ||
                        i == 0) ? "" : "\n";
                richEditText.setText(richEditText.getText().toString() + line + text);
                TextSection midSection = textSection.cloneTextSelection();
                midSection.setText("");
                richEditText.mSections.add(midSection);
                //一个段落结尾有多少个\n，则后面就有多少行，即多少个空的段落
                while (copyText.endsWith("\n")) {
                    copyText = copyText.substring(0, copyText.length() - 1);
                    TextSection nextTextSection = textSection.cloneTextSelection();
                    nextTextSection.setText("");
                    richEditText.mSections.add(nextTextSection.cloneTextSelection());
                }
                if (richEditText.getParent() == null) {
                    addView(richEditText);
                }
                if (nextIsImage || nextSection == null) {//如果下一个段落是图片,则格式化样式
                    for (int j = 0; j < richEditText.mSections.size(); j++) {
                        TextSection style = richEditText.mSections.get(j);
                        richEditText.setSectionStyle(style, j);
                    }
                }
            }
        }
        int count = getChildCount();
        View view = getChildAt(count - 1);
        if (view instanceof ImagePanel) {
            final RichEditText editText = new RichEditText(getContext(), mListener);
            addView(editText);
            mFocusView = editText;
            post(new Runnable() {
                @Override
                public void run() {
                    editText.requestFocus();
                }
            });
        } else {
            final RichEditText editText = (RichEditText) view;
            editText.setSelection(editText.getText().length());
            mFocusView = editText;
            post(new Runnable() {
                @Override
                public void run() {
                    editText.requestFocus();
                }
            });
        }
    }

    /**
     * 返回日记数量
     *
     * @return 返回日记数量
     */
    int getImageCount() {
        int count = getChildCount();
        int imageCount = 0;
        for (int i = 0; i < count; i++) {
            if (getChildAt(i) instanceof ImagePanel)
                ++imageCount;
        }
        return imageCount;
    }


    /**
     * fan
     */
    List<TextSection> createSectionList() {
        return mFocusView.getTextSections();
    }

}
