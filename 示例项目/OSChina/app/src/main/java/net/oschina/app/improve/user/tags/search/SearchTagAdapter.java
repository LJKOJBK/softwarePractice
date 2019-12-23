package net.oschina.app.improve.user.tags.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tags;

 class SearchTagAdapter extends BaseRecyclerAdapter<Tags> {

    private OnViewClickListener mRelateListener;

    SearchTagAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }


    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TagHolder(mInflater.inflate(R.layout.item_list_search_tags, parent, false));
    }

    @Override
    protected void onBindClickListener(RecyclerView.ViewHolder holder) {
        TagHolder h = (TagHolder) holder;
        h.mTextRelate.setTag(holder);
        h.mTextRelate.setOnClickListener(mRelateListener);
    }

    void setRelateListener(OnViewClickListener mRelateListener) {
         this.mRelateListener = mRelateListener;
     }

     @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Tags item, int position) {
        TagHolder h = (TagHolder) holder;
        h.mTextTag.setText(item.getName());
        if(item.isRelated()){
            h.mTextRelate.setBackgroundResource(R.drawable.selector_related);
            h.mTextRelate.setText("取消关注");
            h.mTextRelate.setTextColor(0xFF6a6a6a);
        }else {
            h.mTextRelate.setBackgroundResource(R.drawable.selector_event_sign_up);
            h.mTextRelate.setText("关注标签");
            h.mTextRelate.setTextColor(0xFFFFFFFF);
        }
    }

    private static final class TagHolder extends RecyclerView.ViewHolder {

        private TextView mTextTag;
        private TextView mTextRelate;
        private TagHolder(View itemView) {
            super(itemView);
            mTextTag = (TextView) itemView.findViewById(R.id.tv_tag);
            mTextRelate = (TextView) itemView.findViewById(R.id.tv_relations);
        }
    }
}
