package net.oschina.app.improve.user.data;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 修改所在地区
 * Created by huanghaibin on 2017/8/21.
 */

public class ModifyAreaActivity extends BackActivity {

    @Bind(R.id.rv_province)
    RecyclerView mRecyclerProvince;
    @Bind(R.id.rv_city)
    RecyclerView mRecyclerCity;
    private CityAdapter mAdapterCity;
    private ProvinceAdapter mAdapterProvince;
    static final int TYPE_MODIFY_AREA = 3;

    public static void show(Activity activity, User info) {
        Intent intent = new Intent(activity, ModifyAreaActivity.class);
        intent.putExtra("user_info", info);
        activity.startActivityForResult(intent, TYPE_MODIFY_AREA);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_modify_area;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initData() {
        super.initData();

        User mUser = (User) getIntent().getSerializableExtra("user_info");
        if (mUser == null || mUser.getMore() == null) {
            finish();
            return;
        }
        mRecyclerProvince.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerCity.setLayoutManager(new LinearLayoutManager(this));
        mAdapterCity = new CityAdapter(this);
        mAdapterProvince = new ProvinceAdapter(this);
        mRecyclerProvince.setAdapter(mAdapterProvince);
        mRecyclerCity.setAdapter(mAdapterCity);
        mAdapterProvince.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mAdapterProvince.setSelectedPosition(position);
                mAdapterCity.resetItem(DBManager.getCountryManager()
                        .get(City.class, String.format("where province='%s'", mAdapterProvince.getItem(position).getName())));
                mAdapterCity.setSelectedPosition(0);
            }
        });
        mAdapterCity.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                mAdapterCity.setSelectedPosition(position);
            }
        });
        mAdapterProvince.addAll(DBManager.getCountryManager().get(Province.class));
        String province = mUser.getMore().getProvince();
        if (TextUtils.isEmpty(province)) {
            mAdapterCity.resetItem(DBManager.getCountryManager()
                    .get(City.class, String.format("where province='%s'", mAdapterProvince.getItem(0).getName())));
        } else {
            Province p = new Province();
            p.setName(province);
            int index = mAdapterProvince.getItems().indexOf(p);
            if (index >= 0 && index < mAdapterProvince.getCount()) {
                mAdapterProvince.setSelectedPosition(index);
                mRecyclerProvince.scrollToPosition(index);
            }
            mAdapterCity.resetItem(DBManager.getCountryManager()
                    .get(City.class, String.format("where province='%s'", province)));
            String city = mUser.getMore().getCity();
            if (TextUtils.isEmpty(city)) {
                mAdapterCity.setSelectedPosition(0);
            } else {
                City c = new City();
                c.setName(city);
                int i = mAdapterCity.getItems().indexOf(c);
                if (i >= 0 && i < mAdapterCity.getCount()) {
                    mAdapterCity.setSelectedPosition(i);
                    mRecyclerCity.scrollToPosition(i);
                }
            }
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
            Province province = mAdapterProvince.getSelectedProvince();
            City city = mAdapterCity.getSelectedCity();
            if (province == null || city == null) {
                SimplexToast.show(this, "请选择修改地址");
                return false;
            }
            modifyArea(province.getName(), city.getName());
        }
        return false;
    }

    private void modifyArea(String province, String city) {
        showLoadingDialog("正在修改地址...");
        OSChinaApi.updateUserInfo(null, null, null, null, province, city,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if (isDestroy()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SimplexToast.show(ModifyAreaActivity.this, "网络错误");
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
                                SimplexToast.show(ModifyAreaActivity.this,bean.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isDestroy()) {
                                return;
                            }
                            SimplexToast.show(ModifyAreaActivity.this, "修改失败");
                        }
                    }
                });
    }
}
