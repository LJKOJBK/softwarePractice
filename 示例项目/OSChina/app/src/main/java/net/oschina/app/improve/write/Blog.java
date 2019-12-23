package net.oschina.app.improve.write;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 博客对象
 * Created by huanghaibin on 2017/9/1.
 */

public class Blog implements Serializable {
    private String title;
    @SerializedName("abstract")
    private String summary;
    private String tags;
    private int system;
    private int catalog;
    private int canVisible;
    private int canComment;
    private int isStick;
    private int type;
    private String content;

    public Blog() {
        this.type = 1;
        this.isStick = 1;
        this.canComment = 1;
        this.canVisible = 1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getSystem() {
        return system;
    }

    public void setSystem(int system) {
        this.system = system;
    }

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    public int getCanVisible() {
        return canVisible;
    }

    public void setCanVisible(int canVisible) {
        this.canVisible = canVisible;
    }

    public int getCanComment() {
        return canComment;
    }

    public void setCanComment(int canComment) {
        this.canComment = canComment;
    }

    public int getIsStick() {
        return isStick;
    }

    public void setIsStick(int isStick) {
        this.isStick = isStick;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
