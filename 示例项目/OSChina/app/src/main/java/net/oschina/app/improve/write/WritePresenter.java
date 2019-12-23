package net.oschina.app.improve.write;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.widget.rich.TextSection;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 发布博客
 * Created by huanghaibin on 2017/8/3.
 */

class WritePresenter implements WriteContract.Presenter {
    private final WriteContract.View mView;
    private boolean isLoading;

    WritePresenter(WriteContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void pubBlog(Blog blog, List<TextSection> sections) {
        if (checkIsEmpty(sections)) {
            mView.showPubBlogFailure(R.string.blog_content_empty_error);
            return;
        }
        if (isLoading)
            return;
        if (blog == null) {
            mView.showPubBlogFailure(R.string.blog_empty_error);
            return;
        }
        if (TextUtils.isEmpty(blog.getTitle())) {
            mView.showPubBlogFailure(R.string.blog_title_empty_error);
            return;
        }
//        if (TextUtils.isEmpty(blog.getSummary())) {
//            mView.showPubBlogFailure(R.string.blog_summary_empty_error);
//            return;
//        }
        blog.setContent(getContent(sections));
        if (TextUtils.isEmpty(blog.getContent())) {
            mView.showPubBlogFailure(R.string.blog_content_empty_error);
            return;
        }

        isLoading = true;
        OSChinaApi.pubBlog(blog, new TextHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
                isLoading = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showPubBlogFailure(R.string.pub_blog_failure);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<SubBean>>() {
                    }.getType();
                    ResultBean<SubBean> bean = new Gson().fromJson(responseString, type);
                    if (bean.isSuccess()) {
                        mView.showPubBlogSuccess(R.string.pub_blog_success, bean.getResult());
                    } else {
                        mView.showPubBlogFailure(R.string.pub_blog_failure);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showPubBlogFailure(R.string.pub_blog_failure);
                }
            }
        });
    }

    private String getContent(List<TextSection> sections) {
        if (sections == null || sections.size() == 0)
            return null;
        StringBuilder sb = new StringBuilder();
        for (TextSection section : sections) {
            if (TextUtils.isEmpty(section.getText())) {
                sb.append("<p>&nbsp;</p>");
                continue;
            }
            if (section.isHeader())
                sb.append(getHeaderHtml(section));
            else
                sb.append(getContentHtml(section));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String getHeaderHtml(TextSection section) {
        StringBuilder sb = new StringBuilder();
        insertH(sb, section.getTextSize(), true);
        if (section.isBold()) {
            sb.append("<strong>");
        }
        if (section.isItalic()) {
            sb.append("<em>");
        }
        if (section.isMidLine()) {
            sb.append("<s>");
        }
        sb.append(formatHtml(section.getText()));
        if (section.isMidLine()) {
            sb.append("</s>");
        }
        if (section.isItalic()) {
            sb.append("</em>");
        }
        if (section.isBold()) {
            sb.append("</strong>");
        }
        insertH(sb, section.getTextSize(), false);
        return sb.toString();
    }


    private void insertH(StringBuilder sb, int size, boolean isLeft) {
        switch (size) {
            case 28:
                sb.append(isLeft ? "<h1>" : "</h1>");
                break;
            case 24:
                sb.append(isLeft ? "<h2>" : "</h2>");
                break;
            case 20:
                sb.append(isLeft ? "<h3>" : "</h3>");
                break;
            case 16:
                sb.append(isLeft ? "<h4>" : "</h4>");
                break;
        }
    }

    private String getContentHtml(TextSection section) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<p style=\"text-align: %s\">", getAlign(section.getAlignment())));
        if (section.isBold()) {

            sb.append("<strong>");
        }
        if (section.isItalic()) {

            sb.append("<em>");
        }
        if (section.isMidLine()) {
            sb.append("<s>");
        }
        sb.append(formatHtml(section.getText()));

        if (section.isMidLine()) {
            sb.append("</s>");
        }
        if (section.isItalic()) {

            sb.append("</em>");
        }
        if (section.isBold()) {

            sb.append("</strong>");
        }

        sb.append("</p>");
        return sb.toString();
    }

    private String getAlign(int align) {
        if (align == TextSection.LEFT)
            return "left";
        else if (align == TextSection.CENTER)
            return "center";
        else return
                    "right";
    }

    private String formatHtml(String content) {
        return content.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    /**
     * 检查是否有输入
     *
     * @param sections 全部段落
     * @return 输入是否为空
     */
    private static boolean checkIsEmpty(List<TextSection> sections) {
        if (sections == null || sections.size() == 0)
            return true;
        for (TextSection section : sections) {
            if (!TextUtils.isEmpty(section.getText().trim()))
                return false;

        }
        return true;
    }
}
