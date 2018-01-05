package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:已移除列表
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class HasRemoveErrorsModel extends BaseBean {
    private  int  number;
    private  int  source;
    private  String  sourceName;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
