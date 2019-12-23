package net.oschina.app.improve.detail.general;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.detail.pay.PayDialog;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.AutoScrollView;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.StringUtils;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class BlogDetailFragment extends DetailFragment {

    private PortraitView mImageAvatar;

    private IdentityView mIdentityView;

    private TextView mTextName;

    private TextView mTextPubDate;

    private TextView mTextTitle;

    private TextView mTextAbstract;

    private Button mBtnRelation;


    private PayDialog mDialog;


    public static BlogDetailFragment newInstance() {
        return new BlogDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blog_detail_v2;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTextTitle = (TextView)mHeaderView.findViewById(R.id.tv_title);
        mTextPubDate = (TextView)mHeaderView.findViewById(R.id.tv_pub_date);
        mTextName = (TextView)mHeaderView.findViewById(R.id.tv_name);
        mTextAbstract = (TextView)mHeaderView.findViewById(R.id.tv_detail_abstract);
        mImageAvatar = (PortraitView)mHeaderView.findViewById(R.id.iv_avatar);
        mIdentityView = (IdentityView)mHeaderView.findViewById(R.id.identityView);
        mBtnRelation = (Button)mHeaderView.findViewById(R.id.btn_relation);
        mHeaderView.findViewById(R.id.btn_pay).setOnClickListener(this);
        mBtnRelation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBean.getAuthor() != null) {
                    mPresenter.addUserRelation(mBean.getAuthor().getId());
                }
            }
        });
        mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBean != null && mBean.getAuthor() != null) {
                    OtherUserHomeActivity.show(mContext, mBean.getAuthor());
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_BLOG;
    }

    @Override
    public void onClick(View v) {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(this, 1);
            return;
        }
        if (mDialog == null) {
            mDialog = new PayDialog(mContext, mBean);
            mDialog.setOnPayListener(new PayDialog.OnPayListener() {
                @Override
                public void onPay(float money, int type) {
                    mPresenter.payDonate(mBean.getAuthor().getId(), mBean.getId(), money, type);
                }
            });
        }
        mDialog.show();
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        if (mContext == null) return;
        Author author = bean.getAuthor();
        mIdentityView.setup(author);
        if (author != null) {
            mTextName.setText(author.getName());
            mImageAvatar.setup(author);
        }
        mTextPubDate.setText(StringUtils.formatYearMonthDay(bean.getPubDate()));
        mTextTitle.setText(bean.getTitle());
        mTextAbstract.setText(bean.getSummary());
        if (TextUtils.isEmpty(bean.getSummary())) {
            mRoot.findViewById(R.id.line3).setVisibility(View.GONE);
            mRoot.findViewById(R.id.line4).setVisibility(View.GONE);
            mTextAbstract.setVisibility(View.GONE);
        }
        mBtnRelation.setText(bean.getAuthor().getRelation() < UserRelation.RELATION_ONLY_HER
                ? "已关注" : "关注");

        SpannableStringBuilder spannable = new SpannableStringBuilder(bean.getTitle());
        Resources resources = mContext.getResources();
        int top = Util.dipTopx(mContext, 2);
        boolean isToday = StringUtils.isToday(mBean.getPubDate());
        if (isToday) {
            spannable.append(" [icon] ");
            Drawable originate = resources.getDrawable(R.mipmap.ic_label_today);
            if (originate != null) {
                originate.setBounds(0, -top, originate.getIntrinsicWidth() + top, originate.getIntrinsicHeight());
            }
            ImageSpan imageSpan = new ImageSpan(originate, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(imageSpan, spannable.length() - 7, spannable.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        if (bean.isOriginal()) {
            spannable.append(" [icon] ");
            Drawable originate = resources.getDrawable(R.mipmap.ic_label_originate);
            if (originate != null) {
                originate.setBounds(0, -top, originate.getIntrinsicWidth() + top, originate.getIntrinsicHeight());
            }
            ImageSpan imageSpan = new ImageSpan(originate, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(imageSpan, spannable.length() - 7, spannable.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            spannable.append(" [icon] ");
            Drawable originate = resources.getDrawable(R.mipmap.ic_label_reprint);
            if (originate != null) {
                originate.setBounds(0, -top, originate.getIntrinsicWidth() + top, originate.getIntrinsicHeight());
            }
            ImageSpan imageSpan = new ImageSpan(originate, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(imageSpan, spannable.length() - 7, spannable.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        if (bean.isRecommend()) {
            spannable.append(" [icon] ");
            Drawable recommend = resources.getDrawable(R.mipmap.ic_label_recommend);
            if (recommend != null) {
                recommend.setBounds(0, -top, recommend.getIntrinsicWidth() + top , recommend.getIntrinsicHeight());
            }
            ImageSpan imageSpan = new ImageSpan(recommend, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(imageSpan, spannable.length() - 7, spannable.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        mTextTitle.setText(spannable);
    }

    @Override
    public void showAddRelationSuccess(boolean isRelation, int strId) {
        mBtnRelation.setText(isRelation ? "已关注" : "关注");
        SimplexToast.show(mContext, strId);
    }

    @Override
    protected int getCommentOrder() {
        return OSChinaApi.COMMENT_NEW_ORDER;
    }

    @Override
    protected View getHeaderView() {
        return new BlogDetailHeaderView(mContext);
    }

    private static class BlogDetailHeaderView extends AutoScrollView {
        public BlogDetailHeaderView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.layout_blod_detail_header, this, true);
        }
    }
}
