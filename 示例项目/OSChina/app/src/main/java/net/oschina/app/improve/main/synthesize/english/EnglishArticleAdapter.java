package net.oschina.app.improve.main.synthesize.english;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.main.synthesize.DataFormat;
import net.oschina.app.improve.main.synthesize.TypeFormat;
import net.oschina.app.improve.media.Util;
import net.oschina.app.util.TDevice;

/**
 * 英文推荐界面
 * Created by huanghaibin on 2017/10/23.
 */

public class EnglishArticleAdapter extends BaseRecyclerAdapter<Article> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {

    private static final int VIEW_TYPE_NOT_IMG = 1;
    private static final int VIEW_TYPE_ONE_IMG = 2;
    private static final int VIEW_TYPE_THREE_IMG = 3;

    private static int WIDTH = 0;
    private static final String FORMAT = "!/both/330x246/quality/100";
    private RequestManager mLoader;
    private OSCApplication.ReadState mReadState;

    public EnglishArticleAdapter(Context context, int mode) {
        super(context, mode);
        mReadState = OSCApplication.getReadState("sub_list");
        setOnLoadingHeaderCallBack(this);
        mLoader = Glide.with(mContext);
        WIDTH = (Util.getScreenWidth(context) - Util.dipTopx(context, 48)) / 3;
    }

    @Override
    public int getItemViewType(int position) {
        Article article = getItem(position);
        if (article != null) {
            String imgs[] = article.getImgs();
            if (imgs == null || imgs.length == 0)
                return VIEW_TYPE_NOT_IMG;
            if (imgs.length < 3)
                return VIEW_TYPE_ONE_IMG;
            return VIEW_TYPE_THREE_IMG;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderHolder(mHeaderView);
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == VIEW_TYPE_NOT_IMG) {
            return new TextHolder(mInflater.inflate(R.layout.item_list_article_english_not_img, parent, false));
        } else if (type == VIEW_TYPE_ONE_IMG) {
            return new OneImgHolder(mInflater.inflate(R.layout.item_list_article_english_one_img, parent, false));
        } else {
            return new ThreeImgHolder(mInflater.inflate(R.layout.item_list_article_english_three_img, parent, false));
        }
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Article item, int position) {
        int type = getItemViewType(position);
        Resources resources = mContext.getResources();
        String sourceName = item.getSource();
        String desc = TextUtils.isEmpty(item.getDesc()) ? "" : item.getDesc().replaceFirst("\\s*|\t|\n", "");
        switch (type) {
            case VIEW_TYPE_NOT_IMG:
                TextHolder h = (TextHolder) holder;
                setTag(h.mTextTitle, h.mImageTag, item);
                h.mTextDesc.setText(desc);
                h.mTextDesc.setVisibility(TextUtils.isEmpty(desc) ? View.GONE : View.VISIBLE);
                h.mTextTime.setText(DataFormat.parsePubDate(item.getPubDate()));
                h.mTextAuthor.setText(TextUtils.isEmpty(item.getAuthorName()) ? "匿名" : item.getAuthorName());
                h.mTextOrigin.setText(TextUtils.isEmpty(item.getAuthorName()) ? sourceName : item.getAuthorName());
                h.mTextCommentCount.setText(String.valueOf(item.getCommentCount()));
                if (mReadState.already(item.getKey())) {
                    h.mTextTitle.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
                    h.mTextDesc.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
                } else {
                    h.mTextTitle.setTextColor(TDevice.getColor(resources, R.color.text_title_color));
                    h.mTextDesc.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
                }
                if (TextUtils.isEmpty(item.getTitleTranslated())) {
                    h.mTextTitleCN.setVisibility(View.GONE);
                    h.mTextTitleCN.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
                } else {
                    h.mTextTitleCN.setVisibility(View.VISIBLE);
                    h.mTextTitleCN.setText(item.getTitleTranslated());
                    h.mTextTitleCN.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
                }
                break;
            case VIEW_TYPE_ONE_IMG:
                OneImgHolder h1 = (OneImgHolder) holder;
                setTag(h1.mTextTitle, h1.mImageTag, item);
                h1.mFrameImage.getLayoutParams().width = WIDTH;
                h1.mTextTime.setText(DataFormat.parsePubDate(item.getPubDate()));
                h1.mTextAuthor.setText(TextUtils.isEmpty(item.getAuthorName()) ? "匿名" : item.getAuthorName());
                h1.mTextOrigin.setText(TextUtils.isEmpty(item.getAuthorName()) ? sourceName : item.getAuthorName());
                h1.mTextCommentCount.setText(String.valueOf(item.getCommentCount()));
                mLoader.load(item.getImgs()[0] + FORMAT)
                        .fitCenter()
                        .error(R.mipmap.ic_split_graph)
                        .into(h1.mImageView);
                if (mReadState.already(item.getKey())) {
                    h1.mTextTitle.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
                } else {
                    h1.mTextTitle.setTextColor(TDevice.getColor(resources, R.color.text_title_color));
                }
                if (TextUtils.isEmpty(item.getTitleTranslated())) {
                    h1.mTextTitleCN.setVisibility(View.GONE);
                    h1.mTextTitleCN.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
                } else {
                    h1.mTextTitleCN.setVisibility(View.VISIBLE);
                    h1.mTextTitleCN.setText(item.getTitleTranslated());
                    h1.mTextTitleCN.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
                }
                break;
            case VIEW_TYPE_THREE_IMG:
                ThreeImgHolder h2 = (ThreeImgHolder) holder;
                setTag(h2.mTextTitle, h2.mImageTag, item);
                h2.mTextTime.setText(DataFormat.parsePubDate(item.getPubDate()));
                h2.mTextAuthor.setText(TextUtils.isEmpty(item.getAuthorName()) ? "匿名" : item.getAuthorName());
                h2.mTextOrigin.setText(TextUtils.isEmpty(item.getAuthorName()) ? sourceName : item.getAuthorName());
                h2.mTextCommentCount.setText(String.valueOf(item.getCommentCount()));
                mLoader.load(item.getImgs()[0] + FORMAT)
                        .fitCenter()
                        .error(R.mipmap.ic_split_graph)
                        .into(h2.mImageOne);
                mLoader.load(item.getImgs()[1] + FORMAT)
                        .fitCenter()
                        .into(h2.mImageTwo);
                mLoader.load(item.getImgs()[2] + FORMAT)
                        .fitCenter()
                        .error(R.mipmap.ic_split_graph)
                        .into(h2.mImageThree);
                if (mReadState.already(item.getKey())) {
                    h2.mTextTitle.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
                } else {
                    h2.mTextTitle.setTextColor(TDevice.getColor(resources, R.color.text_title_color));
                }
                if (TextUtils.isEmpty(item.getTitleTranslated())) {
                    h2.mTextTitleCN.setVisibility(View.GONE);
                    h2.mTextTitleCN.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
                } else {
                    h2.mTextTitleCN.setVisibility(View.VISIBLE);
                    h2.mTextTitleCN.setText(item.getTitleTranslated());
                    h2.mTextTitleCN.setTextColor(TDevice.getColor(resources, R.color.text_secondary_color));
                }
                break;
        }

    }


    private void setTag(TextView textView, ImageView imageView, Article article) {
        if (article.getType() == Article.TYPE_QUESTION) {
            setEmptyTag(textView, article);
            imageView.setImageResource(R.mipmap.tag_question);
            imageView.setVisibility(View.VISIBLE);
        } else if (TypeFormat.isGit(article)) {
            setEmptyTag(textView, article);
            imageView.setImageResource(R.mipmap.tag_gitee);
            imageView.setVisibility(View.VISIBLE);
        } else if (article.getType() == Article.TYPE_ZB) {
            setEmptyTag(textView, article);
            imageView.setImageResource(R.mipmap.tag_zb);
            imageView.setVisibility(View.VISIBLE);
        } else if (article.getType() == Article.TYPE_SOFTWARE) {
            setEmptyTag(textView, article);
            imageView.setImageResource(R.mipmap.tag_software);
            imageView.setVisibility(View.VISIBLE);
        } else if (article.getType() == Article.TYPE_AD) {
            setEmptyTag(textView, article);
            imageView.setImageResource(R.mipmap.tag_ad);
            imageView.setVisibility(View.VISIBLE);
        } else if (article.getType() == Article.TYPE_TRANSLATE) {
            setEmptyTag(textView, article);
            imageView.setImageResource(R.mipmap.tag_translate);
            imageView.setVisibility(View.VISIBLE);
        } else {
            textView.setText(article.getTitle());
            imageView.setVisibility(View.GONE);
        }
    }

    private void setEmptyTag(TextView textView, Article article) {
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        spannable.append("[icon] ");
        spannable.append(article.getTitle());
        Drawable img = mContext.getResources().getDrawable(R.mipmap.tag_empty);
        if (img != null) {
            img.setBounds(0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight());
        }
        ImageSpan imageSpan = new ImageSpan(img, ImageSpan.ALIGN_BOTTOM);
        spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }


    private static final class TextHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle,
                mTextTitleCN,
                mTextDesc,
                mTextTime,
                mTextOrigin,
                mTextAuthor,
                mTextCommentCount;
        ImageView mImageTag;

        TextHolder(View itemView) {
            super(itemView);
            mImageTag = (ImageView) itemView.findViewById(R.id.iv_tag);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTextTitleCN = (TextView) itemView.findViewById(R.id.tv_title_cn);
            mTextDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            mTextTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTextOrigin = (TextView) itemView.findViewById(R.id.tv_origin);
            mTextAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            mTextCommentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
        }
    }

    private static final class OneImgHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle,
                mTextTitleCN,
                mTextTime,
                mTextOrigin,
                mTextAuthor,
                mTextCommentCount;
        ImageView mImageView, mImageTag;
        FrameLayout mFrameImage;

        OneImgHolder(View itemView) {
            super(itemView);
            mFrameImage = (FrameLayout) itemView.findViewById(R.id.fl_image);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTextTitleCN = (TextView) itemView.findViewById(R.id.tv_title_cn);
            mTextTime = (TextView) itemView.findViewById(R.id.tv_time);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
            mTextOrigin = (TextView) itemView.findViewById(R.id.tv_origin);
            mTextAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            mImageTag = (ImageView) itemView.findViewById(R.id.iv_tag);
            mTextCommentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
        }
    }

    private static final class ThreeImgHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle,
                mTextTitleCN,
                mTextTime,
                mTextOrigin,
                mTextAuthor,
                mTextCommentCount;
        ImageView mImageOne, mImageTwo, mImageThree, mImageTag;

        ThreeImgHolder(View itemView) {
            super(itemView);
            mImageTag = (ImageView) itemView.findViewById(R.id.iv_tag);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTextTitleCN = (TextView) itemView.findViewById(R.id.tv_title_cn);
            mTextTime = (TextView) itemView.findViewById(R.id.tv_time);
            mImageOne = (ImageView) itemView.findViewById(R.id.iv_img_1);
            mImageTwo = (ImageView) itemView.findViewById(R.id.iv_img_2);
            mImageThree = (ImageView) itemView.findViewById(R.id.iv_img_3);
            mTextOrigin = (TextView) itemView.findViewById(R.id.tv_origin);
            mTextAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            mTextCommentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
        }
    }


    private static final class HeaderHolder extends RecyclerView.ViewHolder {
        HeaderHolder(View itemView) {
            super(itemView);
        }
    }


}
