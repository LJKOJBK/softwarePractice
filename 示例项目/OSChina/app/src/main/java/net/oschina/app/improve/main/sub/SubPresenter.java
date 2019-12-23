package net.oschina.app.improve.main.sub;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.improve.utils.CacheManager;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static net.oschina.app.improve.main.sub.SubFragment.SAVE_ID;

/**
 * 订阅接口
 * Created by huanghaibin on 2017/12/18.
 */

class SubPresenter implements SubContract.Presenter {
    private final SubContract.View mView;
    private String mNextToken;
    private final SubTab mTab;
    private final String CACHE_NAME;

    SubPresenter(SubContract.View mView, SubTab tab) {
        this.mView = mView;
        this.mTab = tab;
        CACHE_NAME = tab.getToken();
        this.mView.setPresenter(this);

    }

    @Override
    public void loadCache() {
        List<SubBean> items = CacheManager.readListJson(OSCApplication.getInstance(), CACHE_NAME, SubBean.class);
        if (items != null) {
            mView.onRefreshSuccess(items);
            mView.updateKey();
            mView.onComplete();
        }
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getSubscription(mTab.getHref(), "", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<SubBean>> resultBean = new Gson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        PageBean<SubBean> pageBean = resultBean.getResult();
                        mView.onUpdateTime(resultBean.getTime());
                        mNextToken = pageBean.getNextPageToken();
                        List<SubBean> list = pageBean.getItems();

                        CacheManager.saveToJson(OSCApplication.getInstance(), CACHE_NAME, list);
                        mView.onRefreshSuccess(list);
                        mView.updateKey();
                        if (list.size() == 0) {
                            mView.showNotMore();
                        }

                        if(mTab.getType() == 6){
                            Collections.sort(list);
                            SubBean bean = list.get(0);
                            OSCSharedPreference.getInstance().putTheNewsId(bean.getNewsId());
                            if(SAVE_ID){
                                OSCSharedPreference.getInstance().putLastNewsId(bean.getNewsId());
                                ApiHttpClient.setHeaderNewsId();
                            }
                        }
                    } else {
                        mView.showNotMore();
                    }
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.network_timeout_hint);
                    mView.onComplete();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getSubscription(mTab.getHref(), mNextToken, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<SubBean>> resultBean = new Gson().fromJson(responseString, getType());
                    if (resultBean != null && resultBean.isSuccess()) {
                        mView.onUpdateTime(resultBean.getTime());
                        PageBean<SubBean> pageBean = resultBean.getResult();
                        mNextToken = pageBean.getNextPageToken();
                        List<SubBean> list = pageBean.getItems();
                        CacheManager.saveToJson(OSCApplication.getInstance(), CACHE_NAME, list);
                        mView.onLoadMoreSuccess(list);
                        mView.updateKey();
                        if (list.size() == 0) {
                            mView.showNotMore();
                        }
                    } else {
                        mView.showNotMore();
                    }
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.network_timeout_hint);
                    mView.onComplete();
                }
            }
        });
    }

    private Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }
}
