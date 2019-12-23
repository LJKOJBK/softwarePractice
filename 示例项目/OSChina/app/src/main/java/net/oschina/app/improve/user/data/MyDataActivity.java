package net.oschina.app.improve.user.data;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.media.SelectImageActivity;
import net.oschina.app.improve.media.config.SelectOptions;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;

import java.io.File;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 我的资料界面 + 修改功能
 * Created by huanghaibin on 2017/8/22.
 */

public class MyDataActivity extends BackActivity implements View.OnClickListener {

    @Bind(R.id.tv_nickname)
    TextView mTextNickname;
    @Bind(R.id.tv_join_date)
    TextView mTextJoinTime;
    @Bind(R.id.tv_area)
    TextView mTextArea;
    @Bind(R.id.tv_skill)
    TextView mTextSkill;
    @Bind(R.id.tv_field)
    TextView mTextField;
    @Bind(R.id.tv_signature)
    TextView mTextSignature;
    @Bind(R.id.iv_avatar)
    PortraitView mImageAvatar;
    private User mInfo;

    public static void show(Context context, User info) {
        Intent intent = new Intent(context, MyDataActivity.class);
        intent.putExtra("user_info", info);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_data;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @Override
    protected void initData() {
        super.initData();
        mInfo = (User) getIntent().getSerializableExtra("user_info");
        if (mInfo == null || mInfo.getMore() == null) {
            finish();
            return;
        }
        mImageAvatar.setup(mInfo);
        mImageAvatar.setVisibility(View.VISIBLE);
        mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageActivity.show(MyDataActivity.this, new SelectOptions.Builder()
                        .setSelectCount(1)
                        .setHasCam(true)
                        .setCrop(700, 700)
                        .setCallback(new SelectOptions.Callback() {
                            @Override
                            public void doSelected(String[] images) {
                                String path = images[0];
                                uploadNewPhoto(new File(path));
                            }
                        }).build());
            }
        });
        mTextNickname.setText(mInfo.getName());
        mTextJoinTime.setText(getText(StringUtils.formatYearMonthDayNew(mInfo.getMore().getJoinDate())));
        mTextArea.setText(mInfo.getMore().getCity());
        mTextField.setText(mInfo.getMore().getExpertise());
        mTextSkill.setText(mInfo.getMore().getPlatform());
        mTextSignature.setText(mInfo.getDesc());
    }

    @OnClick({R.id.ll_avatar, R.id.ll_nickname, R.id.ll_join_time, R.id.ll_area,
            R.id.ll_skill, R.id.ll_field, R.id.ll_signature})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_avatar:
                SelectImageActivity.show(this, new SelectOptions.Builder()
                        .setSelectCount(1)
                        .setHasCam(true)
                        .setCrop(700, 700)
                        .setCallback(new SelectOptions.Callback() {
                            @Override
                            public void doSelected(String[] images) {
                                String path = images[0];
                                uploadNewPhoto(new File(path));
                            }
                        }).build());
                break;
            case R.id.ll_nickname:
                ModifyDataActivity.show(this, mInfo, ModifyDataActivity.TYPE_NICKNAME);
                break;
            case R.id.ll_area:
                ModifyAreaActivity.show(this, mInfo);
                break;
            case R.id.ll_skill:
                ModifySkillActivity.show(this, mInfo);
                break;
            case R.id.ll_field:
                ModifyFieldActivity.show(this, mInfo);
                break;
            case R.id.ll_signature:
                ModifyDataActivity.show(this, mInfo, ModifyDataActivity.TYPE_SIGNATURE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        mInfo = (User) data.getSerializableExtra("user_info");
        AccountHelper.updateUserCache(mInfo);
        switch (requestCode) {
            case ModifyDataActivity.TYPE_NICKNAME:
                mTextNickname.setText(mInfo.getName());
                break;
            case ModifyDataActivity.TYPE_SIGNATURE:
                mTextSignature.setText(mInfo.getDesc());
                break;
            case ModifyAreaActivity.TYPE_MODIFY_AREA:
                mTextArea.setText(mInfo.getMore().getCity());
                break;
            case ModifySkillActivity.TYPE_SKILL:
                mTextSkill.setText(mInfo.getMore().getPlatform());
                break;
            case ModifyFieldActivity.TYPE_FIELD:
                mTextField.setText(mInfo.getMore().getExpertise());
                break;
        }
    }


    private void uploadNewPhoto(File file) {
        // 获取头像缩略图
        if (file == null || !file.exists() || file.length() == 0) {
            AppContext.showToast(getString(R.string.title_icon_null));
        } else {
            showLoadingDialog("正在上传头像");
            OSChinaApi.updateUserIcon(file, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        Type type = new TypeToken<ResultBean<User>>() {
                        }.getType();

                        ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            User userInfo = (User) resultBean.getResult();
                            mImageAvatar.setup(userInfo);
                            mImageAvatar.setVisibility(View.VISIBLE);
                            //缓存用户信息
                            AccountHelper.updateUserCache(userInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (isDestroy())
                        return;
                    dismissLoadingDialog();
                }
            });
        }

    }

    private String getText(String text) {
        if (text == null || text.equalsIgnoreCase("null"))
            return "<无>";
        else return text;
    }
}
