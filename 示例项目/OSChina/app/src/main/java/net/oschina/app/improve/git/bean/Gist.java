package net.oschina.app.improve.git.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */
@SuppressWarnings("unused")
public class Gist implements Serializable {
    private String url;
    private String id;
    private User owner;
    private String language;
    private String summary;
    private String category;
    private String content;
    private File[] files;
    @SerializedName("comments_count")
    private int commentCounts;
    @SerializedName("stars_count")
    private int startCounts;
    @SerializedName("forks_count")
    private int forkCounts;
    @SerializedName("created_at")
    private Date createdDate;
    @SerializedName("updated_at")
    private Date lastUpdateDate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentCounts() {
        return commentCounts;
    }

    public void setCommentCounts(int commentCounts) {
        this.commentCounts = commentCounts;
    }

    public int getStartCounts() {
        return startCounts;
    }

    public void setStartCounts(int startCounts) {
        this.startCounts = startCounts;
    }

    public int getForkCounts() {
        return forkCounts;
    }

    public void setForkCounts(int forkCounts) {
        this.forkCounts = forkCounts;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }


    public static class File implements Serializable {

        public static final int FILE_CODE = 1;//代码文件
        public static final int FILE_IMAGE = 2;//图片
        public static final int FILE_BIN = 3;//二进制文件，只能下载

        private int type;
        private String name;
        private String content;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
