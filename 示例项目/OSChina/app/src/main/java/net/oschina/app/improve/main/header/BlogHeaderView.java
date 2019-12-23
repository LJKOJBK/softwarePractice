package net.oschina.app.improve.main.header;

import android.annotation.SuppressLint;
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
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.detail.SubActivity;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.media.Util;

/**
 * 每日一博Header
 * Created by huanghaibin on 2017/10/26.
 */

@SuppressLint("ViewConstructor")
public class BlogHeaderView extends HeaderView {
    public BlogHeaderView(Context context, String api, String cacheName) {
        super(context, api, cacheName);
        findViewById(R.id.tv_all).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SubTab tab = new SubTab();
                tab.setName("每日一博");
                tab.setFixed(false);
                tab.setHref("https://www.oschina.net/action/apiv2/sub_list?token=1abf09a23a87442184c2f9bf9dc29e35");
                tab.setNeedLogin(false);
                tab.setSubtype(1);
                tab.setOrder(4);
                tab.setToken("1abf09a23a87442184c2f9bf9dc29e35");
                tab.setType(3);

                SubActivity.show(getContext(), tab);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_blog_header;
    }

    @Override
    protected BaseRecyclerAdapter<Banner> getAdapter() {
        return new BlogBannerAdapter(getContext());
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Banner banner = mAdapter.getItem(position);
        if (banner == null)
            return;
        if (banner.getType() == Banner.BANNER_TYPE_BLOG) {
            BlogDetailActivity.show(getContext(), banner.getId());
        }
    }

    private static final class BlogBannerAdapter extends BaseRecyclerAdapter<Banner> {
        private RequestManager mLoader;

        BlogBannerAdapter(Context context) {
            super(context, NEITHER);
            mLoader = Glide.with(context);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            return new Holder(mInflater.inflate(R.layout.item_list_blog_banner, parent, false));
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Banner item, int position) {
            Holder h = (Holder) holder;
            mLoader.load(item.getImg())
                    .fitCenter()
                    .into(h.mImageBanner);
            h.mTextName.setText(item.getName());
            int p = Util.dipTopx(mContext, 16);
            ((RecyclerView.LayoutParams) h.itemView.getLayoutParams()).setMargins(position == 0 ? p : 0, 0, p, 0);
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
