package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:教材-知识点分析
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class XqfxJcModel extends BaseBean {
    private  int materialId;
    private  String materialName;
    private  double knowledgePoint;
    private List<XqfxZjModel> structureList;

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public double getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(double knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }

    public List<XqfxZjModel> getStructureList() {
        return structureList;
    }

    public void setStructureList(List<XqfxZjModel> structureList) {
        this.structureList = structureList;
    }
}
