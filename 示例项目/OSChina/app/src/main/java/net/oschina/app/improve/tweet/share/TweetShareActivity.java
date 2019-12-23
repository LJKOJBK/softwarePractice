package net.oschina.app.improve.tweet.share;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.utils.DialogHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 动弹分享
 * Created by huanghaibin on 2017/10/16.
 */

public class TweetShareActivity extends BackActivity implements
        EasyPermissions.PermissionCallbacks,
        View.OnClickListener {


    private int mType;
    private static final int TYPE_SHARE = 1;
    private static final int TYPE_SAVE = 2;
    private TweetShareFragment mFragment;

    @Bind(R.id.recyclerView)
    RecyclerView mRecycleView;
    private ShareCommentAdapter mAdapter;
    private ArrayList<TweetComment> mComments;

    public static void show(Context context, Tweet tweet) {
        if (tweet == null)
            return;
        Intent intent = new Intent(context, TweetShareActivity.class);
        intent.putExtra("tweet", tweet);
        context.startActivity(intent);
    }

    public static void show(Context context, Tweet tweet, List<TweetComment> comments) {
        if (tweet == null)
            return;
        Intent intent = new Intent(context, TweetShareActivity.class);
        intent.putExtra("tweet", tweet);
        if (comments != null && comments instanceof ArrayList) {
            intent.putExtra("comments", (ArrayList) comments);
        }
        context.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_tweet_share;
    }

    @SuppressWarnings("ALL")
    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        Tweet tweet = (Tweet) getIntent().getSerializableExtra("tweet");
        mComments = (ArrayList<TweetComment>) getIntent().getSerializableExtra("comments");

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShareCommentAdapter(this, BaseRecyclerAdapter.ONLY_HEADER, tweet);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.resetItem(mComments);

        mFragment = TweetShareFragment.newInstance(tweet, mComments);
        addFragment(R.id.fl_content, mFragment);
        requestData(tweet.getId());
    }

    @OnClick({R.id.ll_share, R.id.ll_save})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_share:
                mType = TYPE_SHARE;
                saveToFileByPermission();
                break;
            case R.id.ll_save:
                mType = TYPE_SAVE;
                saveToFileByPermission();
                break;
        }
    }


    private void requestData(long id) {
        OSChinaApi.getTweetCommentList(id, "", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (isDestroy())
                    return;
                mAdapter.setState(BaseRecyclerAdapter.STATE_INVALID_NETWORK, true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (isDestroy())
                    return;
                try {
                    Type type = new TypeToken<ResultBean<PageBean<TweetComment>>>() {
                    }.getType();
                    ResultBean<PageBean<TweetComment>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess() && resultBean.getResult().getItems() != null) {
                        if (mComments == null || mComments.size() == 0) {
                            mAdapter.resetItem(resultBean.getResult().getItems());
                        }
                        mFragment.initList(resultBean.getResult().getItems());
                    } else {
                        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mAdapter.setState(BaseRecyclerAdapter.STATE_INVALID_NETWORK, true);
                }
            }
        });
    }

    private static final int PERMISSION_ID = 0x0001;

    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    public void saveToFileByPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            if (mType == TYPE_SHARE) {
                mFragment.share();
            } else {
                mFragment.save();
            }
        } else {
            EasyPermissions.requestPermissions(this, "请授予文件读写权限", PERMISSION_ID, permissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "", "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                //finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
