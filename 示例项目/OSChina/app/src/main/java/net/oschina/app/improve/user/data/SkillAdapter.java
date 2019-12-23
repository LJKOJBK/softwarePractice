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
 * 开发平台、技能
 * Created by huanghaibin on 2017/8/22.
 */

class SkillAdapter extends BaseRecyclerAdapter<Skill> {
    SkillAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SkillHolder(mInflater.inflate(R.layout.item_list_skill, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Skill item, int position) {
        SkillHolder h = (SkillHolder) holder;
        h.mCheck.setChecked(item.isSelected());
        h.mTextSkill.setText(item.getName());
    }

    private static class SkillHolder extends RecyclerView.ViewHolder {
        CheckBox mCheck;
        TextView mTextSkill;

        SkillHolder(View itemView) {
            super(itemView);
            mCheck = (CheckBox) itemView.findViewById(R.id.cb_selected);
            mTextSkill = (TextView) itemView.findViewById(R.id.tv_skill);
        }
    }

    List<Skill> getSelects() {
        List<Skill> skills = new ArrayList<>();
        for (Skill skill : mItems) {
            if (skill.isSelected())
                skills.add(skill);
        }
        return skills;
    }

    String getSkills(List<Skill> skills) {
        if (skills.size() == 0)
            return String.valueOf(-1);
        StringBuilder sb = new StringBuilder();
        for (Skill skill : skills) {
            sb.append(skill.getId());
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
