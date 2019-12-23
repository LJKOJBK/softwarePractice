package net.oschina.app.improve.user.data;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.widget.BottomLineEditText;
import net.oschina.app.improve.widget.SimplexToast;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 修改界面
 * Created by huanghaibin on 2017/8/17.
 */

public class ModifyDataActivity extends BackActivity {

    static final int TYPE_NICKNAME = 1;
    static final int TYPE_SIGNATURE = 2;
    private int mType;


    @Bind(R.id.et_data)
    BottomLineEditText mEditData;

    public static void show(Activity activity, User info, int type) {
        Intent intent = new Intent(activity, ModifyDataActivity.class);
        intent.putExtra("user_info", info);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, type);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_modify_data;
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
        User mUser = (User) getIntent().getSerializableExtra("user_info");
        mType = getIntent().getIntExtra("type", 0); if (mUser == null || mType == 0) {
            finish();
            return;
        }

        if (mType == TYPE_NICKNAME) {
            mEditData.setMaxCount(16);
            mEditData.setSingleLine();
            mEditData.setText(mUser.getName());
        } else if (mType == TYPE_SIGNATURE) {
            mEditData.setEllipsize(TextUtils.TruncateAt.END);
            mEditData.setMaxCount(100);
            mEditData.setText(mUser.getDesc());
        }
        mEditData.setSelection(mEditData.getText().toString().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_commit) {
            String text = mEditData.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                return false;
            }
            if (mType == TYPE_NICKNAME) {
                modifyNickname(text);
            } else {
                modifySignature(text);
            }
        }
        return false;
    }

    private void modifyNickname(String text) {
        showLoadingDialog("正在修改昵称...");
        OSChinaApi.updateUserInfo(text, null, null, null, null, null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SimplexToast.show(ModifyDataActivity.this, "网络错误");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        try {
                            Type type = new TypeToken<ResultBean<User>>() {
                            }.getType();
                            ResultBean<User> bean = new Gson().fromJson(responseString, type);
                            if (bean.isSuccess()) {
                                Intent intent = new Intent();
                                intent.putExtra("user_info", bean.getResult());
                                setResult(RESULT_OK, intent);
                                finish();
                            }else {
                                SimplexToast.show(ModifyDataActivity.this,bean.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isDestroy()) {
                                return;
                            }
                            SimplexToast.show(ModifyDataActivity.this, "修改失败");
                        }
                    }
                });
    }

    private void modifySignature(String text) {
        showLoadingDialog("正在修改签名...");
        OSChinaApi.updateUserInfo(null, text, null, null, null, null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SimplexToast.show(ModifyDataActivity.this, "网络错误");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        try {
                            Type type = new TypeToken<ResultBean<User>>() {
                            }.getType();
                            ResultBean<User> bean = new Gson().fromJson(responseString, type);
                            if (bean.isSuccess()) {
                                Intent intent = new Intent();
                                intent.putExtra("user_info", bean.getResult());
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isDestroy()) {
                                return;
                            }
                            SimplexToast.show(ModifyDataActivity.this, "修改失败");
                        }
                    }
                });
    }
}
