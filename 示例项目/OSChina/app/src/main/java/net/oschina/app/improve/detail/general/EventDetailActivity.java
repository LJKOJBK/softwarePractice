package net.oschina.app.improve.detail.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.Event;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.comment.CommentsActivity;
import net.oschina.app.improve.detail.apply.ApplyActivity;
import net.oschina.app.improve.detail.sign.SignUpActivity;
import net.oschina.app.improve.detail.v2.DetailActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.user.sign.up.SignUpInfoActivity;
import net.oschina.app.util.UIHelper;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailActivity extends DetailActivity implements View.OnClickListener {
    private MenuItem mMenuFav;

    @Bind(R.id.iv_sign)
    ImageView mImageSign;

    @Bind(R.id.ll_sign)
    LinearLayout mLinearSign;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.iv_event)
    ImageView mImageEvent;

    @Bind(R.id.header_view)
    View mHeaderView;

    @Bind(R.id.ll_operate)
    LinearLayout mLinerOperate;

    @Bind(R.id.tv_apply_status)
    TextView mTextApplyStatus;

    @Bind(R.id.tv_comment)
    TextView mTextComment;

    @Bind(R.id.line)
    View mLine;

    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_EVENT);
        bean.setId(id);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id, boolean isFav) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_EVENT);
        bean.setId(id);
        bean.setFavorite(isFav);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_event_detail_v2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick({R.id.lay_comment, R.id.ll_sign})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_comment:
                CommentsActivity.show(this, mBean.getId(), mBean.getType(), 2, mBean.getTitle());
                break;
            case R.id.ll_sign:
                HashMap<String, Object> extra = mBean.getExtra();
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(this, 0x02);
                    return;
                }
                //非原创会，而且是收费活动，跳转到外部链接
                if (extra != null && getExtraBool(extra.get("isPayEvent")) && getExtraInt(extra.get("eventType")) != 1) {
                    UIHelper.openExternalBrowser(EventDetailActivity.this, getExtraString(extra.get("feeLink")));
                    return;
                }

                if (extra != null) {
                    int eventApplyStatus = getExtraInt(extra.get("eventApplyStatus"));
                    switch (eventApplyStatus) {
                        case EventDetail.APPLY_STATUS_AUDIT://已报名
                        case EventDetail.APPLY_STATUS_CONFIRMED://已报名
                            SignUpInfoActivity.show(this, mBean.getId(), 1);
                            break;
                        case EventDetail.APPLY_STATUS_PRESENTED://已出席
                            ApplyActivity.show(this, mBean.getId());
                            break;
                        default://报名
                            SignUpActivity.show(this, mBean.getId());
                            break;
                    }
                } else {
                    SignUpActivity.show(this, mBean.getId());
                }
                break;
        }
    }

    @Override
    protected DetailFragment getDetailFragment() {
        return EventDetailFragment.newInstance();
    }

    @Override
    public void hideEmptyLayout() {
        super.hideEmptyLayout();
        if (isDestroy())
            return;
        mHeaderView.setVisibility(View.VISIBLE);
        mHeaderView.getLayoutParams().height = 400;
        List<SubBean.Image> images = mBean.getImages();
        if (images == null || images.size() == 0)
            return;
        getImageLoader().load(images.get(0).getHref()).into(mImageEvent);
        mImageEvent.setVisibility(View.VISIBLE);
        if (mMenuFav != null)
            mMenuFav.setIcon(mBean.isFavorite() ? R.mipmap.ic_faved_light_normal : R.mipmap.ic_fav_light_normal);
        mLinerOperate.setVisibility(View.VISIBLE);
        mLine.setVisibility(View.VISIBLE);
        mTextComment.setText(String.format("评论(%s)", mBean.getStatistics().getComment()));

        HashMap<String, Object> extra = mBean.getExtra();
        if (extra != null) {

            //活动状态判断
            int eventStatus = getExtraInt(extra.get("eventStatus"));
            switch (eventStatus) {
                case Event.STATUS_END:
                    mTextApplyStatus.setText(getResources().getString(R.string.event_status_end));
                    mLinearSign.setEnabled(false);
                    mTextApplyStatus.setEnabled(false);
                    mImageSign.setEnabled(false);
                    break;
                case Event.STATUS_ING:
                    mTextApplyStatus.setText(getResources().getString(R.string.event_apply_status_un_sign));
                    break;
                case Event.STATUS_SING_UP:
                    mTextApplyStatus.setText(getResources().getString(R.string.event_status_sing_up));
                    mLinearSign.setEnabled(false);
                    mTextApplyStatus.setEnabled(false);
                    mImageSign.setEnabled(false);
                    break;
            }
            if (eventStatus == Event.STATUS_END || eventStatus == Event.STATUS_SING_UP) {
                return;
            }

            int eventApplyStatus = getExtraInt(extra.get("eventApplyStatus"));

            int applyStr = 0;
            switch (eventApplyStatus) {
                case EventDetail.APPLY_STATUS_UN_SIGN:
                    applyStr = R.string.event_apply_status_un_sign;
                    break;
                case EventDetail.APPLY_STATUS_AUDIT:
                    applyStr = R.string.event_sign_info;
                    break;
                case EventDetail.APPLY_STATUS_CONFIRMED:
                    applyStr = R.string.event_sign_info;
                    break;
                case EventDetail.APPLY_STATUS_PRESENTED:
                    applyStr = R.string.event_apply_status_presented;
                    break;
                case EventDetail.APPLY_STATUS_CANCELED:
                    applyStr = R.string.event_apply_status_un_sign;
                    break;
                case EventDetail.APPLY_STATUS_REFUSED:
                    applyStr = R.string.event_apply_status_un_sign;
                    break;
            }
            mTextApplyStatus.setText(getResources().getString(applyStr));
            // mTextApplyStatus.setText(getString(getApplyStatusStrId(eventApplyStatus)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        mMenuFav = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mImageEvent.getVisibility() == View.GONE)
            return false;
        switch (item.getItemId()) {
            case R.id.menu_share:
                toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
                break;
            case R.id.menu_fav:
                mPresenter.favReverse();
                break;
        }
        return true;
    }

    @Override
    public void showFavReverseSuccess(boolean isFav, int favCount, int strId) {
        if (mMenuFav == null)
            return;
        mMenuFav.setIcon(isFav ? R.mipmap.ic_faved_light_normal : R.mipmap.ic_fav_light_normal);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case 0x01:
                    mBean.getExtra().put("eventApplyStatus", 1);
                    mTextApplyStatus.setText(getResources().getString(getApplyStatusStrId(EventDetail.APPLY_STATUS_AUDIT)));
                    break;
                case 0x02:
                    mBean.getExtra().put("eventApplyStatus", -1);
                    mTextApplyStatus.setText(getResources().getString(getApplyStatusStrId(EventDetail.APPLY_STATUS_UN_SIGN)));
                    break;
            }
        }
    }

    public int getApplyStatusStrId(int status) {
        int strId = R.string.event_apply_status_un_sign;
        switch (status) {
            case EventDetail.APPLY_STATUS_UN_SIGN:
                strId = R.string.event_apply_status_un_sign;
                break;
            case EventDetail.APPLY_STATUS_AUDIT:
                strId = R.string.event_sign_info;
                break;
            case EventDetail.APPLY_STATUS_CONFIRMED:
                strId = R.string.event_sign_info;
                break;
            case EventDetail.APPLY_STATUS_PRESENTED:
                strId = R.string.event_apply_status_presented;
                break;
            case EventDetail.APPLY_STATUS_CANCELED:
                strId = R.string.event_apply_status_un_sign;
                break;
            case EventDetail.APPLY_STATUS_REFUSED:
                strId = R.string.event_apply_status_un_sign;
                break;
        }
        return strId;
    }
}
