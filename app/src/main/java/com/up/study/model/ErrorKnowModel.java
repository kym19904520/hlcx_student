package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:错题知识点分析
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class ErrorKnowModel extends BaseBean {
    private int id;
    private String name ;
    private int type;
    private int parentId;
    private int totalCount;
    private int meterialId;//type = 1的时候meterialId=0为大章节，不=0为小章节


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getMeterialId() {
        return meterialId;
    }

    public void setMeterialId(int meterialId) {
        this.meterialId = meterialId;
    }
}
