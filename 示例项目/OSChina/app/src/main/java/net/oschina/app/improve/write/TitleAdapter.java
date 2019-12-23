package net.oschina.app.improve.write;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

import java.io.Serializable;

/**
 * 字体适配器
 * Created by huanghaibin on 2017/8/14.
 */
@Deprecated
class TitleAdapter extends BaseRecyclerAdapter<TitleAdapter.Title> {
    private int mSelectedPosition = -0;

    TitleAdapter(Context context) {
        super(context, NEITHER);
        addItem(new Title(16, "普通"));
        addItem(new Title(36, "标题1"));
        addItem(new Title(28, "标题2"));
        addItem(new Title(20, "标题3"));
        addItem(new Title(14, "标题4"));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TitleHolder(mInflater.inflate(R.layout.item_list_title, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, TitleAdapter.Title item, int position) {
        TitleHolder h = (TitleHolder) holder;
        h.mTextTitle.setText(item.getText());
        h.mTextTitle.setTextSize(item.getSize());
        h.mTextTitle.setTextColor(position == mSelectedPosition ? 0xff24cf5f : 0xff111111);
    }

    private static class TitleHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle;

        TitleHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    static class Title implements Serializable {
        private int size;
        private String text;
        private int type;

        public Title(int size, String text) {
            this.size = size;
            this.text = text;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
