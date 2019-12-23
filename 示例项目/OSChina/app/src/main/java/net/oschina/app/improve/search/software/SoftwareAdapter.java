package net.oschina.app.improve.search.software;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.utils.parser.SearchParser;

/**
 * 只搜索软件
 * Created by huanghaibin on 2018/1/5.
 */

class SoftwareAdapter extends BaseRecyclerAdapter<Article> {
    String mKeyword;
    private RequestManager mLoader;

    SoftwareAdapter(Context context) {
        super(context, ONLY_FOOTER);
        mLoader = Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SoftwareHolder(mInflater.inflate(R.layout.item_list_software, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Article item, int position) {
        SoftwareHolder h = (SoftwareHolder) holder;
        mLoader.load(item.getSoftwareLogo())
                .fitCenter()
                .into(h.mImageLogo);
        h.mTextTitle.setText(SearchParser.getInstance().parse(item.getTitle(), mKeyword));
        h.mTextDesc.setText(SearchParser.getInstance().parse(item.getDesc(), mKeyword));
    }

    private static class SoftwareHolder extends RecyclerView.ViewHolder {
        private ImageView mImageLogo;
        private TextView mTextTitle, mTextDesc;

        private SoftwareHolder(View itemView) {
            super(itemView);
            mImageLogo = (ImageView) itemView.findViewById(R.id.iv_logo);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTextDesc = (TextView) itemView.findViewById(R.id.tv_desc);
        }
    }
}
