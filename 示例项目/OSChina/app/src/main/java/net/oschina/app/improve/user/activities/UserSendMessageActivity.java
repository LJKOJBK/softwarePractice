package net.oschina.app.improve.user.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.behavior.KeyboardInputDelegation;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.media.SelectImageActivity;
import net.oschina.app.improve.media.config.SelectOptions;
import net.oschina.app.improve.user.adapter.UserSendMessageAdapter;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.improve.utils.PicturesCompressor;
import net.oschina.app.improve.utils.QuickOptionDialogHelper;
import net.oschina.app.util.HTMLUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 发送消息界面
 * Created by huanghaibin_dev
 * on 2016/8/18.
 */
public class UserSendMessageActivity extends BaseRecyclerViewActivity<Message>
        implements BaseRecyclerAdapter.OnItemLongClickListener {

    @Bind(R.id.root)
    CoordinatorLayout mCoordinatorLayout;
    private KeyboardInputDelegation mDelegation;

    EditText mViewInput;
    private User mReceiver;
    private ProgressDialog mDialog;
    private boolean isFirstLoading = true;
    private Map<String, Message> mSendQuent = new HashMap<>();

    public static void show(Context context, User sender) {
        Intent intent = new Intent(context, UserSendMessageActivity.class);
        intent.putExtra("receiver", sender);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_send_message;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mDialog = new ProgressDialog(this);
        mReceiver = (User) getIntent().getSerializableExtra("receiver");
        setTitle(mReceiver.getName());
        init();
    }

    /**
     * 下拉刷新为加载更多
     */
    @Override
    public void onRefreshing() {
        OSChinaApi.getMessageList(mReceiver.getId(), mBean.getNextPageToken(), mHandler);
    }

    /**
     * 去掉上拉加载
     */
    @Override
    public void onLoadMore() {

    }

    protected void init() {
        mDelegation = KeyboardInputDelegation.delegation(this, mCoordinatorLayout, null);
        mDelegation.showEmoji(getSupportFragmentManager());
        mDelegation.showPic(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectImageActivity.show(UserSendMessageActivity.this, new SelectOptions.Builder()
                        .setHasCam(true)
                        .setSelectCount(1)
                        .setCallback(new SelectOptions.Callback() {
                            @Override
                            public void doSelected(String[] images) {
                                final File file = new File(images[0]);
                                String path = file.getPath();
                                if (mSendQuent.containsKey(getFileName(path))) {
                                    Toast.makeText(UserSendMessageActivity.this, "图片已经在发送队列", Toast.LENGTH_SHORT).show();
                                } else {
                                    compress(path, new Run());
                                }
                            }
                        }).build());
            }
        });

        mDelegation.setSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mDelegation.getInputText().replaceAll("[ \\s\\n]+", " ");
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(UserSendMessageActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDialog.setMessage("正在发送中...");
                mDialog.show();
                OSChinaApi.pubMessage(mReceiver.getId(), content, new CallBack(null));
            }
        });
        mViewInput = mDelegation.getInputView();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onItemClick(int position, long itemId) {
        Message message = mAdapter.getItem(position);
        if(message==null)
            return;
        if (Message.TYPE_IMAGE == message.getType()) {
            if (message.getId() == 0) {
                // TODO: 2017/10/27
            } else if (message.getId() == -1) { //重新发送
                message.setId(0);
                mAdapter.updateItem(position);
                File file = new File(message.getResource());
                OSChinaApi.pubMessage(mReceiver.getId(), file, new CallBack(getFileName(file.getPath())));
            } else {
                ImageGalleryActivity.show(this, message.getResource(), true, true);
            }
        }
    }

    @Override
    protected void setListData(ResultBean<PageBean<Message>> resultBean) {
        super.setListData(resultBean);
        if (isFirstLoading) {
            scrollToBottom();
            isFirstLoading = false;
        }

    }

    private void scrollToBottom() {
        mRecyclerView.scrollToPosition(mAdapter.getItems().size() - 1);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Message>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Message> getRecyclerAdapter() {
        return new UserSendMessageAdapter(this);
    }

    @Override
    public void onLongClick(int position, long itemId) {
        final Message message = mAdapter.getItem(position);
        if (message == null || TextUtils.isEmpty(message.getContent())) return;
        QuickOptionDialogHelper.with(getContext())
                .addCopy(HTMLUtil.delHTMLTag(message.getContent()))
                .show();
    }

    private class CallBack extends TextHttpResponseHandler {
        private String filePath;

        private CallBack(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (filePath != null) {
                Message message = mSendQuent.get(filePath);
                message.setId(-1);
                mAdapter.updateItem(mAdapter.getItems().indexOf(message));
            }
            Toast.makeText(UserSendMessageActivity.this, "发送失败，请检查数据", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            Type type = new TypeToken<ResultBean<Message>>() {
            }.getType();
            try {
                ResultBean<Message> resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    if (filePath != null) {
                        Message message = mSendQuent.get(filePath);
                        if (message != null) {
                            mAdapter.removeItem(message);
                            mSendQuent.remove(filePath);
                            delete(filePath);
                        }
                    }
                    mAdapter.addItem(resultBean.getResult());
                    scrollToBottom();
                    mViewInput.setText("");
                } else {
                    if (filePath != null) {
                        Message message = mSendQuent.get(filePath);
                        message.setId(-1);
                        mAdapter.updateItem(mAdapter.getItems().indexOf(message));
                    }
                    Toast.makeText(UserSendMessageActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mDialog.dismiss();
        }

        @Override
        public void onStart() {
            super.onStart();
            // 发送前添加到最近联系人队列
            ContactsCacheManager.addRecentCache(mReceiver);
        }
    }

    private void compress(final String oriPath, final Run runnable) {
        final String path = getFilesDir() + "/message/" + getFileName(oriPath);
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                if (PicturesCompressor.compressImage(oriPath, path, 512 * 1024)) {
                    runnable.setPath(path);
                    runOnUiThread(runnable);
                }
            }
        });
    }

    private String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void delete(String path) {
        File file = new File(path);
        if (file.exists())
            file.delete();
    }

    private class Run implements Runnable {
        private String path;

        @Override
        public void run() {
            File file = new File(path);
            OSChinaApi.pubMessage(mReceiver.getId(), file, new CallBack(getFileName(path)));
            Message message = new Message();
            message.setType(Message.TYPE_IMAGE);
            message.setSender(AccountHelper.getUser());
            message.setResource(path);
            mSendQuent.put(getFileName(path), message);
            mAdapter.addItem(message);
            scrollToBottom();
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
