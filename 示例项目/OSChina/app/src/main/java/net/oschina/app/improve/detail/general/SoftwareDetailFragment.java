package net.oschina.app.improve.detail.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.AutoScrollView;
import net.oschina.app.util.UIHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class SoftwareDetailFragment extends DetailFragment {

    ImageView mImageRecommend;
    ImageView mImageSoftware;

    TextView mTextName;

    TextView mTextAuthor;

    TextView mTextProtocol;

    TextView mTextLanguage;

    TextView mTextSystem;

    TextView mTextRecordTime;

    LinearLayout mLinearAvatar;

    public static SoftwareDetailFragment newInstance() {
        return new SoftwareDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_software_detail_v2;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mImageRecommend = (ImageView) mHeaderView.findViewById(R.id.iv_label_recommend);
        mImageSoftware = (ImageView) mHeaderView.findViewById(R.id.iv_software_icon);
        mTextName = (TextView) mHeaderView.findViewById(R.id.tv_software_name);
        mTextAuthor = (TextView) mHeaderView.findViewById(R.id.tv_software_author_name);
        mTextProtocol = (TextView) mHeaderView.findViewById(R.id.tv_software_protocol);
        mTextLanguage = (TextView) mHeaderView.findViewById(R.id.tv_software_language);
        mTextSystem = (TextView) mHeaderView.findViewById(R.id.tv_software_system);
        mTextRecordTime = (TextView) mHeaderView.findViewById(R.id.tv_software_record_time);
        mLinearAvatar = (LinearLayout) mHeaderView.findViewById(R.id.ll_avatar);
        mTextAuthor.setOnClickListener(this);
        mHeaderView.findViewById(R.id.tv_home).setOnClickListener(this);
        mHeaderView.findViewById(R.id.tv_document).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_SOFTWARE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_home:
            case R.id.tv_document:
                Map<String, Object> extras1 = mBean.getExtra();
                if (extras1 != null)
                    UIHelper.showUrlRedirect(mContext, getExtraString(extras1.get("softwareHomePage")));
                break;
            case R.id.tv_software_author_name:
                Author author = mBean.getAuthor();
                if (author == null) return;
                OtherUserHomeActivity.show(getActivity(), author);
                break;
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        if (mContext == null)
            return;

        mImageRecommend.setVisibility(bean.isRecommend() ? View.VISIBLE : View.INVISIBLE);
        List<SubBean.Image> images = bean.getImages();
        if (images != null && images.size() != 0)
            getImgLoader().load(images.get(0).getHref()).asBitmap().into(mImageSoftware);

        Author author = bean.getAuthor();
        if (author != null) {
            mTextAuthor.setText(author.getName());
        } else {
            mLinearAvatar.setVisibility(View.GONE);
            mTextAuthor.setText("匿名");
        }
        Map<String, Object> extras = bean.getExtra();
        if (extras != null) {
            mTextName.setText(getExtraString(extras.get("softwareTitle")) + "   " +  getExtraString(extras.get("softwareName")));
            String protocol = getExtraString(extras.get("softwareLicense"));
            if (TextUtils.isEmpty(protocol))
                protocol = "未知";
            mTextProtocol.setText(protocol);
            mTextRecordTime.setText(getExtraString(extras.get("softwareCollectionDate")));
            mTextSystem.setText(getExtraString(extras.get("softwareSupportOS")));
            mTextLanguage.setText(getExtraString(extras.get("softwareLanguage")));
        }
    }

    @Override
    protected int getCommentOrder() {
        return OSChinaApi.COMMENT_HOT_ORDER;
    }


    @Override
    protected View getHeaderView() {
        return new SoftwareDetailHeaderView(mContext);
    }

    private static class SoftwareDetailHeaderView extends AutoScrollView {
        public SoftwareDetailHeaderView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.layout_software_detail_header, this, true);
        }
    }
}
