package net.oschina.app.improve.bean;

/**
 * Created by huanghaibin
 * on 16-5-25.
 */
public class News extends PrimaryBean {

    public static final int TYPE_HREF = 0;
    public static final int TYPE_SOFTWARE = 1;
    public static final int TYPE_QUESTION = 2;
    public static final int TYPE_BLOG = 3;
    public static final int TYPE_TRANSLATE = 4;
    public static final int TYPE_EVENT = 5;
    public static final int TYPE_NEWS = 6;
    public static final int TYPE_FIND_PERSON = 11;

    protected int commentCount;
    protected int type;
    protected boolean recommend;
    protected String title;
    protected String body;
    protected String author;
    protected String href;
    protected String pubDate;
    protected int viewCount;

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
