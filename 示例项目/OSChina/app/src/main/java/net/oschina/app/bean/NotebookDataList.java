package net.oschina.app.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("oschina")
public class NotebookDataList extends Entity implements
        ListEntity<NotebookData> {

    private static final long serialVersionUID = 1L;
    @XStreamAlias("stickies")
    private List<NotebookData> list = new ArrayList<NotebookData>();

    @Override
    public List<NotebookData> getList() {
        return list;
    }

    public void setList(List<NotebookData> list) {
        this.list = list;
    }

}