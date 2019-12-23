package net.oschina.app.improve.user.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 专属领域
 * Created by huanghaibin on 2017/8/22.
 */

class FieldAdapter extends BaseRecyclerAdapter<Field> {
    FieldAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new FieldHolder(mInflater.inflate(R.layout.item_list_skill, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Field item, int position) {
        FieldHolder h = (FieldHolder) holder;
        h.mCheck.setChecked(item.isSelected());
        h.mTextSkill.setText(item.getName());
    }

    List<Field> getSelects() {
        List<Field> fields = new ArrayList<>();
        for (Field field : mItems) {
            if (field.isSelected())
                fields.add(field);
        }
        return fields;
    }

    String getFields(List<Field> fields) {
        if (fields.size() == 0)
            return String.valueOf(-1);
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            sb.append(field.getId());
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private static class FieldHolder extends RecyclerView.ViewHolder {
        CheckBox mCheck;
        TextView mTextSkill;

        FieldHolder(View itemView) {
            super(itemView);
            mCheck = (CheckBox) itemView.findViewById(R.id.cb_selected);
            mTextSkill = (TextView) itemView.findViewById(R.id.tv_skill);
        }
    }
}
