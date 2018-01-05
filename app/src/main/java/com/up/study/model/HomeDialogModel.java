package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:学生试卷
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class HomeDialogModel extends BaseBean {
    private  String relationType;
    private  int relationTypeId;//1:试卷录入,2:线上作业,3:线下作业
    private  int total;


    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public int getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(int relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
