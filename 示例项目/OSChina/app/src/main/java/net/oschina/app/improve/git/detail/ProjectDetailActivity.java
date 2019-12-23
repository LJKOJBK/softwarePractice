package net.oschina.app.improve.git.detail;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/9.
 */

public class ProjectDetailActivity extends BackActivity implements ProjectDetailContract.EmptyView {

    @Bind(R.id.emptyLayout)
    EmptyLayout mEmptyLayout;

    public static void show(final Context context, final Project project) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("project", project);
        context.startActivity(intent);
    }

    public static void show(final Context context, final String pathWithNamespace, final String name, final String url) {
        API.getProjectDetail(pathWithNamespace + "%2F" + name, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (TDevice.hasInternet()) {
                    UIHelper.openExternalBrowser(context, url);
                } else {
                    show(context, pathWithNamespace, name);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Project>>() {
                    }.getType();
                    ResultBean<Project> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        show(context, pathWithNamespace, name);
                    } else {
                        UIHelper.openExternalBrowser(context, url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UIHelper.openExternalBrowser(context, url);
                }
            }
        });
    }

    public static void show(Context context, String pathWithNamespace, String name) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        Project project = new Project();
        project.setName(name);
        project.setPathWithNamespace(pathWithNamespace);
        intent.putExtra("project", project);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_project_detail;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initWidget() {
        super.initWidget();
        setStatusBarDarkMode();
        setDarkToolBar();
        final Project project = (Project) getIntent()
                .getExtras()
                .getSerializable("project");
        ProjectDetailFragment fragment = ProjectDetailFragment.newInstance(project);
        final ProjectDetailContract.Presenter presenter = new ProjectDetailPresenter(fragment, this, project);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    assert project != null;
                    if (project.getId() == 0) {
                        presenter.getProjectDetail(project.getName(), project.getPathWithNamespace());
                    } else {
                        presenter.getProjectDetail(project.getId());
                    }
                }
            }
        });

        addFragment(R.id.fl_content, fragment);
    }

    @Override
    public void showGetDetailSuccess(int strId) {
        mEmptyLayout.setErrorType(strId);
    }

    @Override
    public void showGetDetailFailure(int strId) {
        mEmptyLayout.setErrorType(strId);
    }
}
