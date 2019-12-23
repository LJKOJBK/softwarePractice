package net.oschina.app.improve.main.header;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.Banner;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.media.Util;
import net.oschina.app.util.UIHelper;

/**
 * 新版本新闻界面
 * Created by huanghaibin on 2017/10/25.
 */

public class NewsHeaderView extends HeaderView {
    public NewsHeaderView(Context context, String api, String cacheName) {
        super(context, api, cacheName);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_news_header;
    }

    @Override
    protected BaseRecyclerAdapter<Banner> getAdapter() {
        return new NewsBannerAdapter(getContext());
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Banner banner = mAdapter.getItem(position);
        if (banner != null) {
            int type = banner.getType();
            long id = banner.getId();
            if(type  == News.TYPE_HREF){
                UIHelper.openExternalBrowser(getContext(),banner.getHref());
            }else {
                UIHelper.showDetail(getContext(), type, id, banner.getHref());
            }
        }
    }

    private static final class NewsBannerAdapter extends BaseRecyclerAdapter<Banner> {
        private RequestManager mLoader;

        NewsBannerAdapter(Context context) {
            super(context, NEITHER);
            mLoader = Glide.with(context);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            return new Holder(mInflater.inflate(R.layout.item_list_news_banner, parent, false));
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Banner item, int position) {
            Holder h = (Holder) holder;
            mLoader.load(item.getImg())
                    .fitCenter()
                    .into(h.mImageBanner);
            h.mTextName.setText(item.getName());
        }

        private static final class Holder extends RecyclerView.ViewHolder {
            ImageView mImageBanner;
            TextView mTextName;

            Holder(View itemView) {
                super(itemView);
                itemView.getLayoutParams().width = Util.getScreenWidth(itemView.getContext()) / 5 * 3;
                mImageBanner = (ImageView) itemView.findViewById(R.id.iv_banner);
                mTextName = (TextView) itemView.findViewById(R.id.tv_name);
            }
        }
    }
}
