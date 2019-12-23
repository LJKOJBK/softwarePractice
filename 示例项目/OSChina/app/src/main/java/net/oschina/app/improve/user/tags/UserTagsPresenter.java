package net.oschina.app.improve.user.tags;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.Tags;
import net.oschina.app.improve.bean.base.ResultBean;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 用户标签界面
 * Created by haibin on 2018/05/22.
 */
class UserTagsPresenter implements UserTagsContract.Presenter {
    private final UserTagsContract.View mView;

    UserTagsPresenter(UserTagsContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getUserTags(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<List<Tags>> bean = new Gson().fromJson(responseString,
                            new TypeToken<ResultBean<List<Tags>>>() {
                            }.getType());
                    if (bean != null && bean.isSuccess()) {
                        mView.onRefreshSuccess(bean.getResult());
                    } else {
                        mView.showNetworkError(R.string.state_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mView.onComplete();
            }
        });
    }

    @Override
    public void onLoadMore() {

    }


    @Override
    public void delete(final Tags tags, final int position) {
        OSChinaApi.putUserTags(null, String.valueOf(tags.getId()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showDeleteFailure(R.string.delete_failed);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            ResultBean<String> bean = new Gson().fromJson(responseString,
                                    new TypeToken<ResultBean<String>>() {
                                    }.getType());
                            if (bean != null) {
                                if (bean.getCode() == 1) {
                                    mView.showDeleteSuccess(tags, position);
                                } else {
                                    mView.showDeleteFailure(bean.getMessage());
                                }
                            } else {
                                mView.showDeleteFailure(R.string.delete_failed);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
