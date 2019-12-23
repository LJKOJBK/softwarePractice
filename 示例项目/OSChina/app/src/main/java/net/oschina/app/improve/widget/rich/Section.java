package net.oschina.app.improve.widget.rich;

import java.io.Serializable;

/**
 * 段落
 * Created by huanghaibin on 2017/8/3.
 */

public class Section implements Serializable {
    private static final long serialVersionUID = -7779844465111313231L;
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    /**
     * P类型，0：文本 1：图片
     */

    protected int type;

    protected int index;
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}