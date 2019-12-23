package net.oschina.app.improve.write;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * 字体适配器
 * Created by huanghaibin on 2017/8/14.
 */
@Deprecated
class ColorAdapter extends BaseRecyclerAdapter<String> {

    ColorAdapter(Context context) {
        super(context, NEITHER);
        addItem("111111");
        addItem("4fd6aa");
        addItem("754acd");
        addItem("df45ac");
        addItem("fd313a");
        addItem("ca4a6d");
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ColorHolder(mInflater.inflate(R.layout.item_list_color, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, String item, int position) {
        ColorHolder h = (ColorHolder) holder;
        h.mImageSelect.setVisibility(position == mSelectedPosition ? View.VISIBLE : View.GONE);
        h.mColorView.setBackgroundColor(Color.parseColor("#" + item));
    }

    private static class ColorHolder extends RecyclerView.ViewHolder {
        View mColorView;
        ImageView mImageSelect;

        ColorHolder(View itemView) {
            super(itemView);
            mColorView = itemView.findViewById(R.id.viewColor);
            mImageSelect = (ImageView) itemView.findViewById(R.id.iv_select);
        }
    }
}
