package net.oschina.app.improve.git.gist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Gist;
import net.oschina.app.improve.widget.PortraitView;

import java.text.DecimalFormat;

/**
 * 代码片段适配器
 * Created by haibin on 2017/5/10.
 */

class GistAdapter extends BaseRecyclerAdapter<Gist> {
    private DecimalFormat decimalFormat = new DecimalFormat(".0");

    GistAdapter(Context context) {
        super(context, ONLY_FOOTER);
        setState(STATE_HIDE, false);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new GistViewHolder(mInflater.inflate(R.layout.item_list_gist, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Gist item, int position) {
        GistViewHolder h = (GistViewHolder) holder;
        h.mImageOwner.setup(0, item.getOwner().getName(), item.getOwner().getNewPortrait());
        h.mImageOwner.setOnClickListener(null);
        h.mTextSummary.setText(item.getSummary());
        h.mTextCategory.setText(item.getCategory());
        h.mTextLanguage.setText(item.getLanguage());
        h.mTextLanguage.setVisibility(TextUtils.isEmpty(item.getLanguage()) ? View.GONE : View.VISIBLE);
        h.mTextCategory.setVisibility(TextUtils.isEmpty(item.getCategory()) ? View.GONE : View.VISIBLE);
        h.mTextFavCount.setText(getCount(item.getStartCounts()));
        h.mTextForkCount.setText(getCount(item.getForkCounts()));
    }

    private static class GistViewHolder extends RecyclerView.ViewHolder {
        PortraitView mImageOwner;
        TextView mTextSummary,
                mTextFavCount, mTextForkCount, mTextLanguage,mTextCategory;

        GistViewHolder(View itemView) {
            super(itemView);
            mImageOwner = (PortraitView) itemView.findViewById(R.id.civ_owner);
            mTextSummary = (TextView) itemView.findViewById(R.id.tv_summary);
            mTextFavCount = (TextView) itemView.findViewById(R.id.tv_fav_count);
            mTextForkCount = (TextView) itemView.findViewById(R.id.tv_fork_count);
            mTextLanguage = (TextView) itemView.findViewById(R.id.tv_language);
            mTextCategory = (TextView) itemView.findViewById(R.id.tv_category);
        }
    }

    private String getCount(int count) {
        return count >= 1000 ? String.format("%sk", decimalFormat.format((float) count / 1000)) : String.valueOf(count);
    }
}
