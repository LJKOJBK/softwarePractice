package net.oschina.app.improve.account.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.TDevice;

import java.io.Serializable;
import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * CSDN第三方登陆界面，非Oauth
 * Created by huanghaibin on 2017/8/8.
 */

public class CsdnLoginActivity extends BaseBackActivity implements View.OnClickListener {

    @Bind(R.id.et_nickname)
    AppCompatEditText mEditNickname;
    @Bind(R.id.et_pwd)
    AppCompatEditText mEditPwd;

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, CsdnLoginActivity.class);
        activity.startActivityForResult(intent, 5);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_csdn_login;
    }

    @Override
    public void onClick(View v) {
        String name = mEditNickname.getText().toString().trim().replace(" ", "");
        String pwd = mEditPwd.getText().toString().trim().replace(" ", "");
        if (TextUtils.isEmpty(name)) {
            SimplexToast.show(this, "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            SimplexToast.show(this, "密码不能为空");
            return;
        }
        showLoadingDialog("正在授权登陆");
        OSChinaApi.csdnLogin(name, pwd, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (isDestroy())
                    return;
                dismissLoadingDialog();
                SimplexToast.show(CsdnLoginActivity.this, "网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    TDevice.closeKeyboard(mEditPwd);
                    Type type = new TypeToken<Oauth>() {
                    }.getRawType();
                    Oauth oauth = new Gson().fromJson(responseString, type);
                    if (oauth == null || oauth.getAccess_token() == null) {
                        SimplexToast.show(CsdnLoginActivity.this, "授权失败");
                        return;
                    }
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("openid", oauth.getUsername());
                    jsonObject.addProperty("expires_in", oauth.getExpires_in());
                    jsonObject.addProperty("access_token", oauth.getAccess_token());
                    Intent intent = new Intent();
                    intent.putExtra("json", jsonObject.toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }
        });
    }

    @SuppressWarnings("all")
    public static class Oauth implements Serializable {
        private int expires_in;
        private String access_token;
        private String username;

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

}
