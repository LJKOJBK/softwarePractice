package net.oschina.app.improve.bean;

/**
 * Created by huanghaibin
 * on 16-5-25.
 */
public class Event extends PrimaryBean {
    public static final int STATUS_END = 1;
    public static final int STATUS_ING = 2;
    public static final int STATUS_SING_UP = 3;

    public static final int EVENT_TYPE_OSC = 1;
    public static final int EVENT_TYPE_TEC = 2;
    public static final int EVENT_TYPE_OTHER = 3;
    public static final int EVENT_TYPE_OUTSIDE = 4;

    protected int applyCount;
    protected int status;
    protected int type;
    protected String title;
    protected String body;
    protected String img;
    protected String startDate;
    protected String endDate;
    protected String pubDate;
    protected String href;

    public int getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(int applyCount) {
        this.applyCount = applyCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
