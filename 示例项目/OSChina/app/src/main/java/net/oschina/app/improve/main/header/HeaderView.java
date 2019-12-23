package net.oschina.app.improve.main.header;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Banner;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.utils.CacheManager;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 新版本Header
 * Created by huanghaibin on 2017/10/25.
 */

public abstract class HeaderView extends LinearLayout implements BaseRecyclerAdapter.OnItemClickListener {
    private String mAPI;
    private String mCacheName;
    protected BaseRecyclerAdapter<Banner> mAdapter;

    public HeaderView(Context context, String api, String cacheName) {
        super(context, null);
        this.mAPI = api;
        this.mCacheName = cacheName;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(getLayoutId(), this, true);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        List<Banner> banners = CacheManager.readListJson(context, mCacheName, Banner.class);
        if (banners != null) {
            mAdapter.resetItem(banners);
        }
        requestBanner();
    }


    public void requestBanner() {
        OSChinaApi.getBanner(mAPI, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    final ResultBean<PageBean<Banner>> result = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<PageBean<Banner>>>() {
                            }.getType());
                    if (result != null && result.isSuccess()) {
                        CacheManager.saveToJson(getContext(), mCacheName, result.getResult().getItems());
                        setBanners(result.getResult().getItems());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void setBanners(List<Banner> banners) {
        mAdapter.resetItem(banners);
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    }

    protected abstract int getLayoutId();

    protected abstract BaseRecyclerAdapter<Banner> getAdapter();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Util.getScreenWidth(getContext()), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
