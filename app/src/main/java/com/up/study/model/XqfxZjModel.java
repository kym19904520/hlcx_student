package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:章节-知识点分析
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class XqfxZjModel extends BaseBean {
    private int structureId;//章节id
    private String structureName;//章节名称
    private double knowledgePoint;//章节分数
    private List<XqfxZsdModel> knowledge;
    public int getStructureId() {
        return structureId;
    }

    public void setStructureId(int structureId) {
        this.structureId = structureId;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public double getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(double knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }

    public List<XqfxZsdModel> getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(List<XqfxZsdModel> knowledge) {
        this.knowledge = knowledge;
    }
}
