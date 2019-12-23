package net.oschina.app.improve.write;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 间隔
 * Created by huanghaibin on 2017/8/14.
 */
@Deprecated
class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

     GridItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.right = space;
    }
}
