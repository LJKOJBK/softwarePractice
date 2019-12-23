package net.oschina.app.improve.detail.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import net.oschina.app.R;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.detail.v2.ReportDialog;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class QuestionDetailActivity extends DetailActivity {
    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id, boolean isFav) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bean.setFavorite(isFav);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initWidget() {
        mCommentHint = "我要回答";
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_detail, menu);
//        MenuItem menuItem = menu.findItem(R.id.menu_report);
//        DrawableCompat.setTint(menuItem.getIcon(),Color.WHITE);
        return true;
    }


    @Override
    protected DetailFragment getDetailFragment() {
        return QuestionDetailFragment.newInstance();
    }


    protected void toReport(long id, String href) {
        ReportDialog.create(this, id, href, Report.TYPE_QUESTION,"").show();
    }
}
