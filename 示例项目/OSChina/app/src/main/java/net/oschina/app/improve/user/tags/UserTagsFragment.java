package net.oschina.app.improve.user.tags;

import android.view.View;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tags;
import net.oschina.app.improve.widget.SimplexToast;

/**
 * 用户标签界面
 * Created by haibin on 2018/05/22.
 */
public class UserTagsFragment extends BaseRecyclerFragment<UserTagsContract.Presenter, Tags> implements UserTagsContract.View {


    public static UserTagsFragment newInstance() {
        return new UserTagsFragment();
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        ((TagAdapter) mAdapter).setDeleteListener(new BaseRecyclerAdapter.OnViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (view.getParent() != null && view.getParent() instanceof SwipeMenuLayout) {
                    ((SwipeMenuLayout) view.getParent()).smoothClose();
                }
                Tags item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                mPresenter.delete(item, position);
            }
        });
        new UserTagsPresenter(this);
    }

    @Override
    protected void onItemClick(Tags tags, int position) {

    }


    @Override
    public void onLoadMore() {

    }

    @Override
    public void showDeleteSuccess(Tags tags, int position) {
        if (mContext == null)
            return;
        Tags item = mAdapter.getItem(position);
        if (item != null && tags.equals(item)) {
            mAdapter.removeItem(position);
        }
    }

    @Override
    public void showDeleteFailure(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, strId);
    }

    @Override
    public void showDeleteFailure(String strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, strId);
    }

    @Override
    protected BaseRecyclerAdapter<Tags> getAdapter() {
        return new TagAdapter(mContext);
    }
}
