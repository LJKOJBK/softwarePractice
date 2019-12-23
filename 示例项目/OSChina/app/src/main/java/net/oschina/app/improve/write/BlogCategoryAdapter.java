package net.oschina.app.improve.write;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * 博客分类
 * Created by huanghaibin on 2017/8/31.
 */

class BlogCategoryAdapter extends BaseRecyclerAdapter<BlogCategory> {

    BlogCategoryAdapter(Context context) {
        super(context, NEITHER);
        mSelectedPosition = 0;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new BlogCategoryHolder(mInflater.inflate(R.layout.item_list_category, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, BlogCategory item, int position) {
        BlogCategoryHolder h = (BlogCategoryHolder) holder;
        h.mTextCategory.setText(item.getName());
        h.mTextCategory.setTextColor(mSelectedPosition == position ? 0xFF24cf5f : 0xFF9A9A9A);
    }

    private static class BlogCategoryHolder extends RecyclerView.ViewHolder {
        TextView mTextCategory;

        BlogCategoryHolder(View itemView) {
            super(itemView);
            mTextCategory = (TextView) itemView.findViewById(R.id.tv_category);
        }
    }
}
