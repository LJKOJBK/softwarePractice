package net.oschina.app.improve.detail.share.blog;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.detail.share.ShareActivity;
import net.oschina.app.improve.detail.share.ShareFragment;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;

/**
 * 博客长图分享
 * Created by huanghaibin on 2017/9/25.
 */

public class ShareBlogActivity extends ShareActivity {

    @Bind(R.id.iv_avatar)
    PortraitView mImageAvatar;


    @Bind(R.id.tv_name)
    TextView mTextName;

    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;

    @Bind(R.id.tv_title)
    TextView mTextTitle;

    @Bind(R.id.tv_detail_abstract)
    TextView mTextAbstract;


    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, ShareBlogActivity.class);
        intent.putExtra("bean", bean);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_blog_share;
    }

    @Override
    protected void initData() {
        super.initData();
        Author author = mBean.getAuthor();
        if (author != null) {
            mTextName.setText(author.getName());
            mImageAvatar.setup(author);
        }
        mTextPubDate.setText(StringUtils.formatYearMonthDay(mBean.getPubDate()));
        mTextTitle.setText(mBean.getTitle());
        mTextAbstract.setText(mBean.getSummary());
        if (TextUtils.isEmpty(mBean.getSummary())) {
            findViewById(R.id.line).setVisibility(View.GONE);
            findViewById(R.id.line1).setVisibility(View.GONE);
            mTextAbstract.setVisibility(View.GONE);
        }
    }
    @Override
    protected ShareFragment getShareFragment() {
        return ShareBlogFragment.newInstance(mBean);
    }
}
