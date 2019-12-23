package net.oschina.app.improve.user.data;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;

import butterknife.Bind;

/**
 * 用户资料：自己 + 其他人
 * Created by huanghaibin on 2017/8/14.
 */

public class UserDataActivity extends BackActivity implements View.OnClickListener {
    @Bind(R.id.iv_avatar)
    PortraitView mUserFace;

    @Bind(R.id.identityView)
    IdentityView identityView;

    @Bind(R.id.tv_name)
    TextView mName;

    @Bind(R.id.tv_join_time)
    TextView mJoinTime;

    @Bind(R.id.tv_location)
    TextView mFrom;

    @Bind(R.id.tv_development_platform)
    TextView mPlatFrom;

    @Bind(R.id.tv_academic_focus)
    TextView mFocus;

    @Bind(R.id.tv_desc)
    TextView mDesc;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    private User userInfo;

    public static void show(Context context, User info) {
        Intent intent = new Intent(context, UserDataActivity.class);
        intent.putExtra("user_info", info);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_data;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
//        setStatusBarDarkMode();
//        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        userInfo = (User) getIntent().getSerializableExtra("user_info");
        if (userInfo == null)
            return;

        if (userInfo.getId() != AccountHelper.getUserId()) {
            String title = TextUtils.isEmpty(userInfo.getName()) ? "" : userInfo.getName();
            setTitle(title);
        }

        fillUI();
    }

    @SuppressWarnings("deprecation")
    private void fillUI() {
        identityView.setup(userInfo);
        mUserFace.setup(userInfo);
        mUserFace.setOnClickListener(null);
        mName.setText(getText(userInfo.getName()));
        mJoinTime.setText(getText(StringUtils.formatYearMonthDayNew(userInfo.getMore().getJoinDate())));
        mFrom.setText(getText(userInfo.getMore().getCity()));
        mPlatFrom.setText(getText(userInfo.getMore().getPlatform()));
        mFocus.setText(getText(userInfo.getMore().getExpertise()));
        mDesc.setText(getText(userInfo.getDesc()));
    }


    @Override
    public void onClick(View v) {

    }

    private String getText(String text) {
        if (text == null || text.equalsIgnoreCase("null"))
            return "<无>";
        else return text;
    }
}
