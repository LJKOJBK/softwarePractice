package net.oschina.app.improve.user.tags;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tags;

 class TagAdapter extends BaseRecyclerAdapter<Tags> {

    private OnViewClickListener mDeleteListener;

    TagAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TagHolder(mInflater.inflate(R.layout.item_list_tag, parent, false));
    }

    @Override
    protected void onBindClickListener(RecyclerView.ViewHolder holder) {
        TagHolder h = (TagHolder) holder;
        h.mTextDelete.setTag(holder);
        h.mTextDelete.setOnClickListener(mDeleteListener);
    }

    public void setDeleteListener(OnViewClickListener mDeleteListener) {
        this.mDeleteListener = mDeleteListener;
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Tags item, int position) {
        TagHolder h = (TagHolder) holder;
        h.mTextTag.setText(item.getName());
    }

    private static final class TagHolder extends RecyclerView.ViewHolder {
        private TextView mTextTag;
        private TextView mTextDelete;


        private TagHolder(View itemView) {
            super(itemView);
            mTextTag = (TextView) itemView.findViewById(R.id.tv_tag);
            mTextDelete = (TextView) itemView.findViewById(R.id.tv_delete);
        }
    }
}
