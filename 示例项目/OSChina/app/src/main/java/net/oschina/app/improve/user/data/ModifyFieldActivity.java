package net.oschina.app.improve.user.data;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.db.DBManager;
import net.oschina.app.improve.widget.SimplexToast;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 修改专属领域界面
 * Created by huanghaibin on 2017/8/22.
 */

public class ModifyFieldActivity extends BackActivity {

    static final int TYPE_FIELD = 5;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerField;
    private FieldAdapter mAdapter;

    public static void show(Activity activity, User info) {
        Intent intent = new Intent(activity, ModifyFieldActivity.class);
        intent.putExtra("user_info", info);
        activity.startActivityForResult(intent, TYPE_FIELD);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_modify_skill;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        mRecyclerField.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new FieldAdapter(this);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                Field field = mAdapter.getItem(position);
                if (field == null) return;
                if (!field.isSelected()) {
                    List<Field> fields = mAdapter.getSelects();
                    if (fields.size() >= 3) {
                        SimplexToast.show(ModifyFieldActivity.this, "最多选择3个");
                        return;
                    }
                }
                field.setSelected(!field.isSelected());
                mAdapter.updateItem(position);
            }
        });
        mRecyclerField.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        User mUser = (User) getIntent().getSerializableExtra("user_info");
        if (mUser == null || mUser.getMore() == null) {
            finish();
            return;
        }
        mAdapter.resetItem(DBManager.getCountryManager().get(Field.class));
        int[] fields = mUser.getMore().getField();
        if (fields == null || fields.length == 0)
            return;
        try {
            for (int id : fields) {
                Field field = new Field();
                field.setId(id);
                Field item = mAdapter.getItem(mAdapter.getItems().indexOf(field));
                assert item != null;
                item.setSelected(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_commit) {
            modifyFields(mAdapter.getFields(mAdapter.getSelects()));
        }
        return false;
    }


    private void modifyFields(String fields) {
        showLoadingDialog("正在修改...");
        OSChinaApi.updateUserInfo(null, null, null, fields, null, null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SimplexToast.show(ModifyFieldActivity.this, "网络错误");
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
                                SimplexToast.show(ModifyFieldActivity.this,bean.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isDestroy()) {
                                return;
                            }
                            SimplexToast.show(ModifyFieldActivity.this, "修改失败");
                        }
                    }
                });
    }


}
