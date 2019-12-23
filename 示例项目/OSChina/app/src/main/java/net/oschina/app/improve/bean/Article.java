package net.oschina.app.improve.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 头条
 * Created by huanghaibin on 2017/10/23.
 */
@SuppressWarnings("unused")
public class Article implements Serializable {
    public static final int TYPE_AD = 9999;//广告类型
    public static final int TYPE_ENGLISH = 8000;//英文类型
    public static final int TYPE_HREF = 0;
    public static final int TYPE_SOFTWARE = 1;
    public static final int TYPE_QUESTION = 2;
    public static final int TYPE_BLOG = 3;
    public static final int TYPE_TRANSLATE = 4;
    public static final int TYPE_EVENT = 5;
    public static final int TYPE_NEWS = 6;
    public static final int TYPE_ZB = 7;
    public static final int TYPE_FIND_PERSON = 11;

    private int type;
    private String authorName;
    private String authorId;
    private String key;
    private String title;
    private String desc;
    private String content;//详情
    private String url;
    private String pubDate;
    private String source;
    private String softwareLogo;
    private String[] imgs;
    private Tag[] iTags;
    private int commentCount;
    private boolean favorite;
    private int wordCount;

    @SerializedName("sub_type")
    private int subType;

    private long readTime;

    private String titleTranslated;//中文翻译标题

    @SerializedName("osc_id")
    private long oscId;

    @SerializedName("view_count")
    private int viewCount;

    public Tag[] getiTags() {
        return iTags;
    }

    public void setiTags(Tag[] iTags) {
        this.iTags = iTags;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }


    public long getOscId() {
        return oscId;
    }

    public void setOscId(long oscId) {
        this.oscId = oscId;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getSoftwareLogo() {
        return softwareLogo;
    }

    public void setSoftwareLogo(String softwareLogo) {
        this.softwareLogo = softwareLogo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public String getTitleTranslated() {
        return titleTranslated;
    }

    public void setTitleTranslated(String titleTranslated) {
        this.titleTranslated = titleTranslated;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }
}
