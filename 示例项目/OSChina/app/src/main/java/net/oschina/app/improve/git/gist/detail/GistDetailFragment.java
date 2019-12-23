package net.oschina.app.improve.git.gist.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.git.bean.CodeDetail;
import net.oschina.app.improve.git.bean.Gist;
import net.oschina.app.improve.git.gist.comment.GistCommentActivity;
import net.oschina.app.improve.git.utils.MarkdownUtils;
import net.oschina.app.improve.git.utils.SourceEditor;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.media.Util;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 代码片段详情
 * Created by haibin on 2017/5/10.
 */

public class GistDetailFragment extends BaseFragment implements GistDetailContract.View, View.OnClickListener {
    private SourceEditor mEditor;
    @Bind(R.id.tv_summary)
    TextView mTextSummary;
    @Bind(R.id.tv_start_count)
    TextView mTexStartCount;
    @Bind(R.id.tv_fork_count)
    TextView mTextForkCount;

    @Bind(R.id.tv_language)
    TextView mTextLanguage;
    @Bind(R.id.tv_category)
    TextView mTextCategory;
    @Bind(R.id.tv_last_update)
    TextView mTextLastUpdate;
    @Bind(R.id.tv_comment_count)
    TextView mTextCommentCount;

    @Bind(R.id.ll_content)
    LinearLayout mLinearContent;
    @Bind(R.id.line1)
    View mLine1;
    @Bind(R.id.line2)
    View mLine2;
    @Bind(R.id.ll_tool)
    LinearLayout mLinearTool;
    private Gist mGist;
    private ShareDialog mAlertDialog;

    static GistDetailFragment newInstance(Gist gist) {
        GistDetailFragment fragment = new GistDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gist", gist);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gist_detail;
    }


    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mGist = (Gist) bundle.getSerializable("gist");
    }

    @SuppressWarnings("MalformedFormatString")
    @SuppressLint("DefaultLocale")
    private void init(Gist gist) {
        assert gist != null;
        mTextSummary.setText(gist.getSummary());
        mTexStartCount.setText(String.valueOf(gist.getStartCounts()));
        mTextForkCount.setText(String.valueOf(gist.getForkCounts()));
        mTextCategory.setText(gist.getCategory());
        mTextLanguage.setText(gist.getLanguage());
        if (gist.getLastUpdateDate() != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mTextLastUpdate.setText(String.format("最后更新于%s", StringUtils.formatSomeAgo(dateFormat.format(gist.getLastUpdateDate()))));
        }
        //mTextDescription.setVisibility(TextUtils.isEmpty(gist.getDescription()) ? View.GONE : View.VISIBLE);
        mTextLanguage.setVisibility(TextUtils.isEmpty(gist.getLanguage()) ? View.GONE : View.VISIBLE);
        mTextCategory.setVisibility(TextUtils.isEmpty(gist.getCategory()) ? View.GONE : View.VISIBLE);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    @SuppressWarnings("all")
    private void addFiles(Gist.File[] files) {
        mLinearContent.removeAllViews();
        for (Gist.File file : files) {//不确定排序
            if (file.getType() == Gist.File.FILE_CODE) {
                TextView textName = new TextView(mContext);
                textName.setText(file.getName());
                textName.setTextColor(Color.parseColor("#111111"));
                textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8), Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8));
                textName.setLayoutParams(params);
                mLinearContent.addView(textName);

                View view = new View(mContext);
                LinearLayout.LayoutParams paramsLine = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
                view.setBackgroundColor(mContext.getResources().getColor(R.color.list_divider_color));
                view.setLayoutParams(paramsLine);
                mLinearContent.addView(view);

                final GistWebView webView = new GistWebView(mContext);
                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDefaultFontSize(10);
                settings.setAllowContentAccess(true);
                webView.setWebChromeClient(new WebChromeClient() {
                });
                mEditor = new SourceEditor(webView);
                mLinearContent.addView(webView);
                mEditor.setMarkdown(MarkdownUtils.isMarkdown(file.getName()));
                CodeDetail detail = new CodeDetail();
                detail.setContent(file.getContent());
                mEditor.setSource(file.getName(), detail);
            }
        }
        for (final Gist.File file : files) {
            if (file.getType() == Gist.File.FILE_BIN) {
                TextView textName = new TextView(mContext);
                textName.setText(file.getName());
                textName.setTextColor(Color.parseColor("#24cf5f"));
                textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8), Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8));
                textName.setLayoutParams(params);
                mLinearContent.addView(textName);
                textName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(file.getContent());
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                });
            }
        }

        for (final Gist.File file : files) {
            if (file.getType() == Gist.File.FILE_IMAGE) {
                TextView textName = new TextView(mContext);
                textName.setText(file.getName());
                textName.setTextColor(Color.parseColor("#111111"));
                textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8), Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8));
                textName.setLayoutParams(params);
                mLinearContent.addView(textName);

                ImageView imageView = new ImageView(mContext);
                params.setMargins(Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8), Util.dipTopx(mContext, 16), Util.dipTopx(mContext, 8));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(params);
                getImgLoader().load(file.getContent())
                        .fitCenter()
                        .into(imageView);
                mLinearContent.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageGalleryActivity.show(mContext,file.getContent());
                    }
                });
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        init(mGist);
    }

    @OnClick({R.id.ll_comment, R.id.ll_share})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_comment:
                GistCommentActivity.show(mContext, mGist);
                break;
            case R.id.ll_share:
                toShare();
                break;
        }
    }

    @Override
    public void setPresenter(GistDetailContract.Presenter presenter) {

    }

    @Override
    public void showNetworkError(int strId) {

    }

    @Override
    public void showGetDetailSuccess(Gist gist, int strId) {
        mGist = gist;
        init(gist);
        if (gist.getFiles() != null && gist.getFiles().length != 0) {
            addFiles(gist.getFiles());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAlertDialog != null)
            mAlertDialog.dismiss();
    }

    @Override
    public void showGetCommentCountSuccess(int count) {
        mTextCommentCount.setText(String.format("评论（%s）", count));
    }

    @Override
    public void showLandscape() {
        mLinearTool.setVisibility(View.GONE);
        mLine1.setVisibility(View.GONE);
        mLine2.setVisibility(View.GONE);
    }

    @Override
    public void showPortrait() {
        mLinearTool.setVisibility(View.VISIBLE);
        mLine1.setVisibility(View.VISIBLE);
        mLine2.setVisibility(View.VISIBLE);
    }

    private void toShare() {
        String content = mGist.getSummary().trim();
        if (content.length() > 55) {
            content = HTMLUtil.delHTMLTag(content);
            if (content.length() > 55)
                content = StringUtils.getSubString(0, 55, content);
        } else {
            content = HTMLUtil.delHTMLTag(content);
        }
        if (TextUtils.isEmpty(content))
            content = "";

        // 分享
        if (mAlertDialog == null) {
            mAlertDialog = new
                    ShareDialog(getActivity())
                    .title(mGist.getOwner().getName() + "/" + mGist.getSummary())
                    .content(content)
                    .url(mGist.getUrl())
                    .bitmapResID(R.mipmap.ic_git)
                    .with();
        }
        mAlertDialog.show();

    }
}
