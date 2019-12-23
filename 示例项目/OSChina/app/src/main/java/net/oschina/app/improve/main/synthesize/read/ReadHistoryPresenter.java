package net.oschina.app.improve.main.synthesize.read;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.common.utils.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 阅读记录
 * Created by huanghaibin on 2017/12/4.
 */

class ReadHistoryPresenter implements ReadHistoryContract.Presenter {
    private final ReadHistoryContract.View mView;

    ReadHistoryPresenter(ReadHistoryContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.readHistory(null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    mView.showNotMore();
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.onComplete();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<Article>>>() {
                    }.getType();
                    ResultBean<List<Article>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        List<Article> list = removeNull(bean.getResult());
                        for (Article article : list) {
                            article.setImgs(removeImgs(article.getImgs()));
                        }
                        mView.onRefreshSuccess(list);
                        if (list.size() == 0) {
                            mView.showNotMore();
                        }
                    } else {
                        mView.showNotMore();
                    }
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNotMore();
                    mView.onComplete();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {

    }

    private static String[] removeImgs(String[] imgs) {
        if (imgs == null || imgs.length == 0)
            return null;
        List<String> list = new ArrayList<>();
        for (String img : imgs) {
            if (!TextUtils.isEmpty(img)) {
                if (img.startsWith("http")) {
                    list.add(img);
                }
            }
        }
        return CollectionUtil.toArray(list, String.class);
    }

    private static List<Article> removeNull(List<Article> list) {
        List<Article> articles = new ArrayList<>();
        for (Article article : list) {
            if (article != null) {
                articles.add(article);
            }
        }
        return articles;
    }
}
