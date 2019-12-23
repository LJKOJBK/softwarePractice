package net.oschina.app.improve.search.v2;

import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 新版搜索界面
 * Created by huanghaibin on 2018/1/4.
 */

class SearchPresenter implements SearchContract.Presenter {
    static final int TYPE_DEFAULT = -1;
    static final int ORDER_DEFAULT = 1;
    static final int ORDER_HOT = 2;
    static final int ORDER_TIME = 3;
    private final SearchContract.View mView;
    private String mToken;
    int mOrder;
    String mKeyword;

    SearchPresenter(SearchContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mOrder = ORDER_DEFAULT;
    }

    @Override
    public void search(int type, String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            mView.showSearchFailure(R.string.search_keyword_empty_error);
            mView.showViewStatus(View.VISIBLE);
            mView.onComplete();
            return;
        }
        mView.showViewStatus(View.GONE);
        mView.showAddHistory(keyword);
        mKeyword = keyword;
        OSChinaApi.search(type, mOrder, keyword, null,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showSearchFailure(R.string.network_timeout_hint);
                        mView.onComplete();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean<SearchBean> bean = new Gson().fromJson(responseString, getType());
                            if (bean != null) {
                                if (bean.isSuccess() && bean.getResult() != null) {
                                    SearchBean result = bean.getResult();
                                    mView.showSearchSuccess(result);
                                    mToken = result.getNextPageToken();
                                    if ((result.getArticles() == null ||
                                            result.getArticles().size() == 0) &&
                                            (result.getSoftwares() == null || result.getSoftwares().size() == 0)) {
                                        mView.showNotMore();
                                    }
                                } else {
                                    mView.showSearchFailure(bean.getMessage());
                                }
                            } else {
                                mView.showSearchFailure(R.string.search_error);
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
    public void searchMore(int type, String keyword) {
        if (TextUtils.isEmpty(mKeyword)) {
            mView.showSearchFailure(R.string.search_keyword_empty_error);
            return;
        }
        OSChinaApi.search(type, mOrder, mKeyword, mToken,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showSearchFailure(R.string.network_timeout_hint);
                        mView.onComplete();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean<SearchBean> bean = new Gson().fromJson(responseString, getType());
                            if (bean != null) {
                                if (bean.isSuccess() && bean.getResult() != null) {
                                    SearchBean result = bean.getResult();
                                    mToken = result.getNextPageToken();
                                    mView.showLoadMoreSuccess(result.getArticles());
                                    if (result.getArticles() == null ||
                                            result.getArticles().size() == 0) {
                                        mView.showNotMore();
                                    }
                                } else {
                                    mView.showSearchFailure(bean.getMessage());
                                }
                            } else {
                                mView.showSearchFailure(R.string.search_error);
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
