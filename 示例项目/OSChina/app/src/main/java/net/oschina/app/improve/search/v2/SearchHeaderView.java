package net.oschina.app.improve.search.v2;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.EventDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.main.synthesize.detail.ArticleDetailActivity;
import net.oschina.app.improve.main.synthesize.web.WebActivity;
import net.oschina.app.improve.media.Util;
import net.oschina.app.improve.utils.parser.SearchParser;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

/**
 * 搜索头部
 * Created by huanghaibin on 2018/1/5.
 */

public class SearchHeaderView extends LinearLayout implements BaseRecyclerAdapter.OnItemClickListener{

    String mKeyword;
    private Adapter mAdapter;
    private RadioGroup mRadioGroup;
    private TextView mTextSoftwareCount;
    private LinearLayout mLinearSoftware;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearArticles;

    public SearchHeaderView(Context context) {
        this(context, null);
    }

    public SearchHeaderView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_search_header, this, true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerSoftware);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new Adapter(context);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mLinearSoftware = (LinearLayout) findViewById(R.id.ll_software);
        mLinearArticles = (LinearLayout) findViewById(R.id.ll_article);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mTextSoftwareCount = (TextView) findViewById(R.id.tv_software_count);
        setVisibility(GONE);
    }

    void setSearchSoftwareListener(OnClickListener listener) {
        mTextSoftwareCount.setOnClickListener(listener);
    }

    void setData(SearchBean bean) {
        if (bean.getArticleCount() == 0) {
            mLinearArticles.setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            mLinearArticles.setVisibility(VISIBLE);
        }
        if (bean.getSoftwares() == null)
            return;
        if (bean.getSoftwares().size() == 0) {
            mLinearSoftware.setVisibility(GONE);
            mRecyclerView.setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            mLinearSoftware.setVisibility(VISIBLE);
            mRecyclerView.setVisibility(VISIBLE);
            mLinearArticles.setVisibility(VISIBLE);
        }
        mAdapter.mKeyword = mKeyword;
        mAdapter.resetItem(bean.getSoftwares());
        mTextSoftwareCount.setText(String.format("查看其余%s款软件",bean.getSoftwareCount()));
    }

    void setOrderChangeListener(RadioGroup.OnCheckedChangeListener listener) {
        mRadioGroup.setOnCheckedChangeListener(listener);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Article top = mAdapter.getItem(position);
        if(top == null){
            return;
        }
        if (!TDevice.hasWebView(getContext()))
            return;
        if (top.getType() == 0) {
            if (TypeFormat.isGit(top)) {
                WebActivity.show(getContext(), TypeFormat.formatUrl(top));
            } else {
                ArticleDetailActivity.show(getContext(), top);
            }
        } else {
            int type = top.getType();
            long id = top.getOscId();
            switch (type) {
                case News.TYPE_SOFTWARE:
                    SoftwareDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_QUESTION:
                    QuestionDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_BLOG:
                    BlogDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_TRANSLATE:
                    NewsDetailActivity.show(getContext(), id, News.TYPE_TRANSLATE);
                    break;
                case News.TYPE_EVENT:
                    EventDetailActivity.show(getContext(), id);
                    break;
                case News.TYPE_NEWS:
                    NewsDetailActivity.show(getContext(), id);
                    break;
                default:
                    UIHelper.showUrlRedirect(getContext(), top.getUrl());
                    break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Util.getScreenWidth(getContext()), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private static class Adapter extends BaseRecyclerAdapter<Article> {
        private RequestManager mLoader;
        private String mKeyword;

        public Adapter(Context context) {
            super(context, NEITHER);
            mLoader = Glide.with(context);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            return new SoftwareHolder(mInflater.inflate(R.layout.item_list_software, parent, false));
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Article item, int position) {
            SoftwareHolder h = (SoftwareHolder) holder;
            mLoader.load(item.getSoftwareLogo())
                    .fitCenter()
                    .into(h.mImageLogo);
            h.mTextTitle.setText(SearchParser.getInstance().parse(item.getTitle(),mKeyword));
            h.mTextDesc.setText(SearchParser.getInstance().parse(item.getDesc(),mKeyword));
        }

        private static class SoftwareHolder extends RecyclerView.ViewHolder {
            private ImageView mImageLogo;
            private TextView mTextTitle, mTextDesc;

            private SoftwareHolder(View itemView) {
                super(itemView);
                mImageLogo = (ImageView) itemView.findViewById(R.id.iv_logo);
                mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
                mTextDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            }
        }
    }
}
