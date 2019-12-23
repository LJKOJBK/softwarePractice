package net.oschina.app.improve.user.data;

import net.oschina.app.improve.detail.db.Column;
import net.oschina.app.improve.detail.db.PrimaryKey;
import net.oschina.app.improve.detail.db.Table;

/**
 * 专属领域
 * Created by huanghaibin on 2017/8/22.
 */

@Table(tableName = "field")
public class Field {
    @PrimaryKey(column = "id", autoincrement = false)
    private int id;
    @Column(column = "name")
    private String name;
    private boolean isSelected;


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

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
        if (obj != null && obj instanceof Field) {
            int id = ((Field) obj).id;
            return id == this.id;
        }
        return false;
    }
}