package net.oschina.app.improve.main.synthesize.read;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.main.synthesize.DataFormat;

/**
 * 文章阅读记录
 * Created by huanghaibin on 2017/12/4.
 */

public class ReadArticleAdapter extends BaseRecyclerAdapter<Article> {
    ReadArticleAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ArticleViewHolder(mInflater.inflate(R.layout.item_list_read_article, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Article item, int position) {
        ArticleViewHolder h = (ArticleViewHolder) holder;
        String type;
        switch (item.getType()) {
            case Article.TYPE_SOFTWARE:
                type = "软件";
                break;
            case Article.TYPE_QUESTION:
                type = "问答";
                break;
            case Article.TYPE_BLOG:
                type = "博客";
                break;
            case Article.TYPE_TRANSLATE:
                type = "翻译";
                break;
            case Article.TYPE_EVENT:
                type = "活动";
                break;
            case Article.TYPE_NEWS:
                type = "资讯";
                break;
            case Article.TYPE_ZB:
                type = "众包";
                break;
            default:
                type = "链接";
                break;
        }
        h.mTypeView.setText(type);
        h.mTitleView.setText(item.getTitle());
        h.mFavDateText.setText(DataFormat.parsePubDate(item.getPubDate()));
        h.mAuthorText.setText(item.getSource());
        h.mCommentCountText.setText(String.valueOf(item.getCommentCount()));
    }


    private class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTypeView, mTitleView, mCommentCountText, mAuthorText, mFavDateText;

        private ArticleViewHolder(View itemView) {
            super(itemView);
            mTypeView = (TextView) itemView.findViewById(R.id.tv_type);
            mTitleView = (TextView) itemView.findViewById(R.id.tv_title);
            mCommentCountText = (TextView) itemView.findViewById(R.id.tv_comment_count);
            mAuthorText = (TextView) itemView.findViewById(R.id.tv_user);
            mFavDateText = (TextView) itemView.findViewById(R.id.tv_fav_date);
        }
    }
}
