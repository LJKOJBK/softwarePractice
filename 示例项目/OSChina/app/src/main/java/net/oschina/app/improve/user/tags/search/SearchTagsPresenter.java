package net.oschina.app.improve.user.tags.search;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.Tags;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 用户搜索标签界面
 * Created by haibin on 2018/05/28.
 */
class SearchTagsPresenter implements SearchTagsContract.Presenter {
    private final SearchTagsContract.View mView;
    private String mToken;
    private String mKeyword;

    SearchTagsPresenter(SearchTagsContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void search(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            mView.showSearchFailure(R.string.search_keyword_empty_error);
            mView.onComplete();
            return;
        }
        mKeyword = keyword;
        OSChinaApi.searchUserTags(keyword, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showSearchFailure(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<Tags>> bean = new Gson().fromJson(responseString, getType());
                    if (bean != null) {
                        if (bean.isSuccess() && bean.getResult() != null) {
                            PageBean<Tags> result = bean.getResult();
                            mToken = result.getNextPageToken();
                            mView.showSearchSuccess(result.getItems());
                            if ((result.getItems() == null ||
                                    result.getItems().size() == 0)) {
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
    public void searchMore(String keyword) {
        if (TextUtils.isEmpty(mKeyword)) {
            mView.showSearchFailure(R.string.search_keyword_empty_error);
            return;
        }
        OSChinaApi.searchUserTags(keyword, mToken, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showSearchFailure(R.string.network_timeout_hint);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<Tags>> bean = new Gson().fromJson(responseString, getType());
                    if (bean != null) {
                        if (bean.isSuccess() && bean.getResult() != null) {
                            PageBean<Tags> result = bean.getResult();
                            mToken = result.getNextPageToken();
                            mView.showLoadMoreSuccess(result.getItems());
                            if ((result.getItems() == null ||
                                    result.getItems().size() == 0)) {
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
    public void putTags(final Tags tags, final int position) {
        String ids = null;
        String deleteIds = null;
        if (tags.isRelated()) {
            deleteIds = String.valueOf(tags.getId());
        } else {
            ids = String.valueOf(tags.getId());
        }
        OSChinaApi.putUserTags(ids, deleteIds,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showPutFailure(R.string.delete_failed);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean<String> bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean<String>>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.getCode() == 1) {
                                    tags.setRelated(!tags.isRelated());
                                    mView.showPutSuccess(tags, position);
                                } else {
                                    mView.showPutFailure(bean.getMessage());
                                }
                            } else {
                                mView.showPutFailure(R.string.delete_failed);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private static Type getType() {
        return new TypeToken<ResultBean<PageBean<Tags>>>() {
        }.getType();
    }
}
