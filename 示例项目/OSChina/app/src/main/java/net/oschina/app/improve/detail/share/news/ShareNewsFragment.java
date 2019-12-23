package net.oschina.app.improve.detail.share.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.detail.share.ShareFragment;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;

/**
 * 资讯长图分享
 * Created by huanghaibin on 2017/9/25.
 */

public class ShareNewsFragment extends ShareFragment {

    @Bind(R.id.tv_title)
    TextView mTextTitle;

    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;

    @Bind(R.id.tv_author)
    TextView mTextAuthor;

    @Bind(R.id.iv_avatar)
    PortraitView mPortraitView;

    static ShareFragment newInstance(SubBean bean) {
        ShareNewsFragment fragment = new ShareNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_share;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        super.initData();
        mTextTitle.setText(mBean.getTitle());
        mTextPubDate.setText("发布于 " + StringUtils.formatYearMonthDay(mBean.getPubDate()));
        Author author = mBean.getAuthor();
        if (author != null) {
            mTextAuthor.setText(author.getName());
        }
        mPortraitView.setup(mBean.getAuthor());

    }
}
