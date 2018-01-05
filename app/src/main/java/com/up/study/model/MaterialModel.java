package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:教材
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class MaterialModel extends BaseBean {
    private  String auxiliaryNum;
    private  int materialId;
    private  String materialName;
    private  String materialCorrectRate;
    private  double knowledgePoint;

    public String getAuxiliaryNum() {
        return auxiliaryNum;
    }

    public void setAuxiliaryNum(String auxiliaryNum) {
        this.auxiliaryNum = auxiliaryNum;
    }

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

    public String getMaterialCorrectRate() {
        return materialCorrectRate;
    }

    public void setMaterialCorrectRate(String materialCorrectRate) {
        this.materialCorrectRate = materialCorrectRate;
    }

    public double getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(double knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }
}
