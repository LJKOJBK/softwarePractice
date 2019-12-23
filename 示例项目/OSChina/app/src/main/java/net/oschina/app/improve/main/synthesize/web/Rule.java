package net.oschina.app.improve.main.synthesize.web;

import java.io.Serializable;

/**
 * 广告过滤规则
 * Created by huanghaibin on 2018/1/18.
 */
@SuppressWarnings("all")
public class Rule implements Serializable{
    private String[] removeRules;
    private String[] expandRules;

    public String[] getRemoveRules() {
        return removeRules;
    }

    public void setRemoveRules(String[] removeRules) {
        this.removeRules = removeRules;
    }

    public String[] getExpandRules() {
        return expandRules;
    }

    public void setExpandRules(String[] expandRules) {
        this.expandRules = expandRules;
    }
}
