package net.oschina.app.improve.search.software;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.search.v2.SearchBean;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 搜索软件
 * Created by huanghaibin on 2018/1/5.
 */

class SearchSoftwarePresenter implements SearchSoftwareContract.Presenter {
    private static final int TYPE_SOFTWARE = 1;
    private final SearchSoftwareContract.View mView;
    private final SearchSoftwareContract.ActionView mActionView;
    String mKeyword;
    private String mToken;

    SearchSoftwarePresenter(SearchSoftwareContract.View mView, SearchSoftwareContract.ActionView actionView) {
        this.mView = mView;
        this.mActionView = actionView;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        if (TextUtils.isEmpty(mKeyword)) {
            mActionView.showSearchFailure(R.string.search_keyword_empty_error);
            mView.onComplete();
            return;
        }
        OSChinaApi.searchSoftware(TYPE_SOFTWARE, mKeyword, null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mActionView.showSearchFailure(R.string.network_timeout_hint);
                        mView.showNetworkError(R.string.network_timeout_hint);
                        mView.onComplete();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean<SearchBean> bean = new Gson().fromJson(responseString, getType());
                            if (bean != null) {
                                if (bean.isSuccess() && bean.getResult() != null) {
                                    SearchBean result = bean.getResult();
                                    mView.onRefreshSuccess(result.getSoftwares());
                                    mToken = result.getNextPageToken();
                                    if (result.getSoftwares() == null ||
                                            result.getSoftwares().size() < 20) {
                                        mView.showNotMore();
                                    }
                                } else {
                                    mView.showNotMore();
                                }
                            } else {
                                mView.showNetworkError(R.string.network_timeout_hint);
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.onComplete();
                        }
                    }
                });
    }

    @Override
    public void onLoadMore() {
        if (TextUtils.isEmpty(mKeyword)) {
            mActionView.showSearchFailure(R.string.search_keyword_empty_error);
            mView.onComplete();
            return;
        }
        OSChinaApi.searchSoftware(TYPE_SOFTWARE, mKeyword, mToken,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mActionView.showSearchFailure(R.string.network_timeout_hint);
                        mView.showNetworkError(R.string.network_timeout_hint);
                        mView.onComplete();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean<SearchBean> bean = new Gson().fromJson(responseString, getType());
                            if (bean != null) {
                                if (bean.isSuccess() && bean.getResult() != null) {
                                    SearchBean result = bean.getResult();
                                    mView.onLoadMoreSuccess(result.getSoftwares());
                                    mToken = result.getNextPageToken();
                                    if (result.getSoftwares() == null ||
                                            result.getSoftwares().size() < 20) {
                                        mView.showNotMore();
                                    }
                                } else {
                                    mView.showNotMore();
                                }
                            } else {
                                mView.showNetworkError(R.string.network_timeout_hint);
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.onComplete();
                        }
                    }
                });
    }

    private static Type getType() {
        return new TypeToken<ResultBean<SearchBean>>() {
        }.getType();
    }
}
