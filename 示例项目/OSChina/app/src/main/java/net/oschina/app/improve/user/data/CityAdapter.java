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

class CityAdapter extends BaseRecyclerAdapter<City> {


    CityAdapter(Context context) {
        super(context, NEITHER);
        mSelectedPosition = 0;
    }

    City getSelectedCity() {
        if (mSelectedPosition < 0 || mSelectedPosition >= mItems.size())
            return null;
        return mItems.get(mSelectedPosition);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new AreaHolder(mInflater.inflate(R.layout.item_list_city, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder
                                                   holder, City item, int position) {
        AreaHolder h = (AreaHolder) holder;
        h.mTextArea.setTextColor(position == mSelectedPosition ? 0xFF24cf5f : 0xff333333);
        h.mTextArea.setText(item.getName());
    }

    private static class AreaHolder extends RecyclerView.ViewHolder {
        TextView mTextArea;

        AreaHolder(View itemView) {
            super(itemView);
            mTextArea = (TextView) itemView.findViewById(R.id.tv_area);
        }
    }
}
