package net.oschina.app.improve.user.data;

import android.text.TextUtils;

import net.oschina.app.improve.detail.db.Column;
import net.oschina.app.improve.detail.db.PrimaryKey;
import net.oschina.app.improve.detail.db.Table;

import java.io.Serializable;

/**
 * 城市
 * Created by huanghaibin on 2017/8/21.
 */
@Table(tableName = "city")
public class City implements Serializable {
    @PrimaryKey(column = "id",autoincrement = false)
    private int id;

    @Column(column = "name")
    private String name;

    @Column(column = "province")
    private String province;

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof City) {
            String name = ((City) obj).getName();
            return !TextUtils.isEmpty(name) && name.equals(this.name);
        }
        return false;
    }
}
