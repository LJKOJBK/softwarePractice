package net.oschina.app.improve.bean;

import java.io.Serializable;

/**
 * 标签化
 * Created by huanghaibin on 2017/11/23.
 */

public class Tag implements Serializable {
    private long oscId;
    private String name;
    private String tagId;

    public long getOscId() {
        return oscId;
    }

    public void setOscId(long oscId) {
        this.oscId = oscId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
