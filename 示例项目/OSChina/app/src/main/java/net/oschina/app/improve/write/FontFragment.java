package net.oschina.app.improve.write;

import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.widget.rich.RichEditLayout;
import net.oschina.app.improve.widget.rich.RichEditText;
import net.oschina.app.improve.widget.rich.TextSection;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 字体面板布局
 * Created by huanghaibin on 2017/8/14.
 */
@Deprecated
public class FontFragment extends BaseFragment implements RichEditText.OnSectionChangeListener, View.OnClickListener {

    @Bind(R.id.recyclerSize)
    RecyclerView mRecyclerTitle;
    @Bind(R.id.recyclerColor)
    RecyclerView mRecyclerColor;
    private ColorAdapter mColorAdapter;
    private TitleAdapter mTitleAdapter;

    @Bind(R.id.btn_bold)
    ImageButton mBtnBold;
    @Bind(R.id.btn_italic)
    ImageButton mBtnItalic;
    @Bind(R.id.btn_mid_line)
    ImageButton mBtnMidLine;
    @Bind(R.id.btn_align_left)
    ImageButton mBtnAlignLeft;
    @Bind(R.id.btn_align_center)
    ImageButton mBtnAlignCenter;
    @Bind(R.id.btn_align_right)
    ImageButton mBtnAlignRight;

    private OnFontStyleChangeListener mListener;

    static FontFragment newInstance() {
        return new FontFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_font;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mColorAdapter = new ColorAdapter(mContext);
        mRecyclerColor.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerColor.setAdapter(mColorAdapter);
        mRecyclerColor.addItemDecoration(new GridItemDecoration(Util.dipTopx(mContext, 16)));
        mColorAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mColorAdapter.setSelectedPosition(position);
                if (mListener != null)
                    mListener.onColorChange(mColorAdapter.getItem(position));
            }
        });

        mTitleAdapter = new TitleAdapter(mContext);
        mRecyclerTitle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerTitle.setAdapter(mTitleAdapter);
        mRecyclerTitle.addItemDecoration(new GridItemDecoration(Util.dipTopx(mContext, 16)));
        mTitleAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mTitleAdapter.setSelectedPosition(position);
                if (mListener != null)
                    mListener.onTitleChange(mTitleAdapter.getItem(position));
            }
        });
        mListener = (OnFontStyleChangeListener) mContext;
    }

    @OnClick({R.id.btn_bold,R.id.btn_italic,R.id.btn_mid_line,
            R.id.btn_align_left,R.id.btn_align_center,R.id.btn_align_right})
    @Override
    public void onClick(View v) {
        if (mListener == null)
            return;
        switch (v.getId()) {
            case R.id.btn_bold:
                mBtnBold.setSelected(!mBtnBold.isSelected());
                mListener.onBoldChange(mBtnBold.isSelected());
                DrawableCompat.setTint(mBtnBold.getDrawable(), mBtnBold.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);
                break;
            case R.id.btn_italic:
                mBtnItalic.setSelected(!mBtnItalic.isSelected());
                mListener.onItalicChange(mBtnItalic.isSelected());
                DrawableCompat.setTint(mBtnItalic.getDrawable(), mBtnItalic.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);
                break;
            case R.id.btn_mid_line:
                mBtnMidLine.setSelected(!mBtnMidLine.isSelected());
                mListener.onMidLineChange(mBtnMidLine.isSelected());
                DrawableCompat.setTint(mBtnMidLine.getDrawable(), mBtnMidLine.isSelected() ? 0xff24cf5f : 0xFFFFFFFF);
                break;
            case R.id.btn_align_left:
                mBtnAlignLeft.setSelected(!mBtnAlignLeft.isSelected());
                mListener.onAlignChange(TextSection.LEFT);
                DrawableCompat.setTint(mBtnAlignLeft.getDrawable(), 0xff24cf5f);
                DrawableCompat.setTint(mBtnAlignCenter.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mBtnAlignRight.getDrawable(), 0xFFFFFFFF);
                break;
            case R.id.btn_align_center:
                mBtnAlignCenter.setSelected(!mBtnAlignCenter.isSelected());
                mListener.onAlignChange(TextSection.CENTER);
                DrawableCompat.setTint(mBtnAlignLeft.getDrawable(), 0xFFFFFFFF);
                DrawableCompat.setTint(mBtnAlignCenter.getDrawable(), 0xff24cf5f);
                DrawableCompat.setTint(mBtnAlignRight.getDrawable(), 0xFFFFFFFF);
                break;
            case R.id.btn_align_right:
                mBtnAlignRight.setSelected(!mBtnAlignRight.isSelected());
                mListener.onAlignChange(TextSection.RIGHT);
                DrawableCompat.setTint(mBtnAlignLeft.getDrawable(), 0xff111111);
                DrawableCompat.setTint(mBtnAlignCenter.getDrawable(), 0xff111111);
                DrawableCompat.setTint(mBtnAlignRight.getDrawable(), 0xff24cf5f);
                break;

        }
    }

    @Override
    public void onSectionChange(TextSection section) {
        if (mRoot == null)
            return;
        DrawableCompat.setTint(mBtnBold.getDrawable(), section.isBold() ? 0xff24cf5f : 0xff111111);
        DrawableCompat.setTint(mBtnItalic.getDrawable(), section.isItalic() ? 0xff24cf5f : 0xff111111);
        DrawableCompat.setTint(mBtnMidLine.getDrawable(), section.isMidLine() ? 0xff24cf5f : 0xff111111);

        if (section.getAlignment() == TextSection.LEFT) {
            DrawableCompat.setTint(mBtnAlignLeft.getDrawable(), 0xff24cf5f);
            DrawableCompat.setTint(mBtnAlignCenter.getDrawable(), 0xff111111);
            DrawableCompat.setTint(mBtnAlignRight.getDrawable(), 0xff111111);
        } else if (section.getAlignment() == TextSection.CENTER) {
            DrawableCompat.setTint(mBtnAlignLeft.getDrawable(), 0xff111111);
            DrawableCompat.setTint(mBtnAlignCenter.getDrawable(), 0xff24cf5f);
            DrawableCompat.setTint(mBtnAlignRight.getDrawable(), 0xff111111);
        } else if (section.getAlignment() == TextSection.RIGHT) {
            DrawableCompat.setTint(mBtnAlignLeft.getDrawable(), 0xff111111);
            DrawableCompat.setTint(mBtnAlignCenter.getDrawable(), 0xff111111);
            DrawableCompat.setTint(mBtnAlignRight.getDrawable(), 0xff24cf5f);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRoot != null) {
            if (RichEditLayout.KEYBOARD_HEIGHT != 0) {
                mRoot.getLayoutParams().height = RichEditLayout.KEYBOARD_HEIGHT;
            } else {
                mRoot.getLayoutParams().height = Util.dipTopx(mContext, 270);
            }
        }
    }

    interface OnFontStyleChangeListener {
        void onBoldChange(boolean isBold);

        void onItalicChange(boolean isItalic);

        void onMidLineChange(boolean isMidLine);

        void onAlignChange(int align);

        void onTitleChange(TitleAdapter.Title title);

        void onColorChange(String color);
    }
}
