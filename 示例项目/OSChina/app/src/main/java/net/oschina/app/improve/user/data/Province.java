package net.oschina.app.improve.user.data;

import android.text.TextUtils;

import net.oschina.app.improve.detail.db.Column;
import net.oschina.app.improve.detail.db.PrimaryKey;
import net.oschina.app.improve.detail.db.Table;

import java.io.Serializable;

/**
 * 省份
 * Created by huanghaibin on 2017/8/21.
 */

@Table(tableName = "province")
public class Province implements Serializable {
    @PrimaryKey(column = "id", autoincrement = false)
    private int id;
    @Column(column = "name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Province) {
            String name = ((Province) obj).getName();
            return !TextUtils.isEmpty(name) && name.equals(this.name);
        }
        return false;
    }
}
