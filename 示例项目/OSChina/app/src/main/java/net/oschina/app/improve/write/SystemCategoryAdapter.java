package net.oschina.app.improve.write;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统分类
 * Created by huanghaibin on 2017/8/31.
 */

class SystemCategoryAdapter extends BaseRecyclerAdapter<SystemCategoryAdapter.SystemCategory> {
    SystemCategoryAdapter(Context context) {
        super(context, NEITHER);
        addAll(getCategories());
        mSelectedPosition = 0;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SystemCategoryHolder(mInflater.inflate(R.layout.item_list_category, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, SystemCategory item, int position) {
        SystemCategoryHolder h = (SystemCategoryHolder) holder;
        h.mTextCategory.setText(item.getName());
        h.mTextCategory.setTextColor(mSelectedPosition == position ? 0xFF24cf5f : 0xFF9A9A9A);
    }

    static class SystemCategory {
        private int id;
        private String name;

        SystemCategory(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class SystemCategoryHolder extends RecyclerView.ViewHolder {
        TextView mTextCategory;

        SystemCategoryHolder(View itemView) {
            super(itemView);
            mTextCategory = (TextView) itemView.findViewById(R.id.tv_category);
        }
    }

    private static List<SystemCategory> getCategories() {
        List<SystemCategory> categories = new ArrayList<>();
        categories.add(new SystemCategory(428602, "移动开发"));
        categories.add(new SystemCategory(428612, "前端开发"));
        categories.add(new SystemCategory(5611447, "人工智能"));
        categories.add(new SystemCategory(428640, "服务端开发/管理"));
        categories.add(new SystemCategory(429511, "游戏开发"));
        categories.add(new SystemCategory(428609, "编程语言"));
        categories.add(new SystemCategory(428610, "数据库"));
        categories.add(new SystemCategory(428611, "企业开发"));
        categories.add(new SystemCategory(428647, "图像/多媒体"));
        categories.add(new SystemCategory(428613, "系统运维"));
        categories.add(new SystemCategory(428638, "软件工程"));
        categories.add(new SystemCategory(5593654, "大数据"));
        categories.add(new SystemCategory(428639, "云计算"));
        categories.add(new SystemCategory(430884, "开源硬件"));
        categories.add(new SystemCategory(430381, "其他类型"));
        return categories;
    }
}
