package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:知识点-知识点分析
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class XqfxZsdModel extends BaseBean {
    private int knowledgeId;
    private String knowledgeName;
    private double knowledgePoint;

    public int getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(int knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName;
    }

    public double getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(double knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }
}
