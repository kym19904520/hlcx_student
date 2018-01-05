package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:学情分析
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class LearningAnalysisModel extends BaseBean {

    private String gradeCorrectRate;
    private String myCorrectRate;
    private String classCorrectRate;
    private int totalNum;//试卷总数
    private List<XqfxJcModel> materialList;

    public String getGradeCorrectRate() {
        return gradeCorrectRate;
    }

    public void setGradeCorrectRate(String gradeCorrectRate) {
        this.gradeCorrectRate = gradeCorrectRate;
    }

    public String getMyCorrectRate() {
        return myCorrectRate;
    }

    public void setMyCorrectRate(String myCorrectRate) {
        this.myCorrectRate = myCorrectRate;
    }

    public String getClassCorrectRate() {
        return classCorrectRate;
    }

    public void setClassCorrectRate(String classCorrectRate) {
        this.classCorrectRate = classCorrectRate;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public List<XqfxJcModel> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<XqfxJcModel> materialList) {
        this.materialList = materialList;
    }
}
