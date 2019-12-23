package net.oschina.app.improve.user.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * 地区适配器
 * Created by huanghaibin on 2017/8/21.
 */

class ProvinceAdapter extends BaseRecyclerAdapter<Province> {

    ProvinceAdapter(Context context) {
        super(context, NEITHER);
        mSelectedPosition = 0;
    }


    Province getSelectedProvince() {
        if (mSelectedPosition < 0 || mSelectedPosition >= mItems.size())
            return null;
        return mItems.get(mSelectedPosition);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new AreaHolder(mInflater.inflate(R.layout.item_list_province, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder
                                                   holder, Province item, int position) {
        AreaHolder h = (AreaHolder) holder;
        h.itemView.setBackgroundColor(position == mSelectedPosition ? 0xFFFFFFFF : 0xFFF6F6F6);
        h.mTextArea.setText(item.getName());
        h.mViewSelected.setVisibility(position == mSelectedPosition ? View.VISIBLE : View.INVISIBLE);
        h.mViewLine.setVisibility(position == mSelectedPosition ? View.INVISIBLE : View.VISIBLE);
    }

    private static class AreaHolder extends RecyclerView.ViewHolder {
        TextView mTextArea;
        View mViewSelected;
        View mViewLine;

        AreaHolder(View itemView) {
            super(itemView);
            mTextArea = (TextView) itemView.findViewById(R.id.tv_area);
            mViewSelected = itemView.findViewById(R.id.viewSelected);
            mViewLine = itemView.findViewById(R.id.line);
        }
    }
}
