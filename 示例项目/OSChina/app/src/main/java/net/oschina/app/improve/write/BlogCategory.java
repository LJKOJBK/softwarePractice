package net.oschina.app.improve.write;

import net.oschina.app.improve.detail.db.Column;
import net.oschina.app.improve.detail.db.PrimaryKey;
import net.oschina.app.improve.detail.db.Table;

import java.io.Serializable;

/**
 * 博客分类
 * Created by huanghaibin on 2017/8/31.
 */
@SuppressWarnings("all")
@Table(tableName = "BlogCategory")
public class BlogCategory implements Serializable {

    @PrimaryKey(column = "id", autoincrement = false)
    private long id;

    @Column(column = "name")
    private String name;

    @Column(column = "create_time")
    private String create_time;

    @Column(column = "options")
    private int options;

    @Column(column = "sort_order")
    private int sort_order;

    @Column(column = "space")
    private long space;

    @Column(column = "type")
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public int getSort_order() {
        return sort_order;
    }

    public void setSort_order(int sort_order) {
        this.sort_order = sort_order;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
