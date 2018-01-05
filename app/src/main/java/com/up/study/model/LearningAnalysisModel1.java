package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:学情分析 备份
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class LearningAnalysisModel1 extends BaseBean {

    private String studentId;
    private String gradeCorrectRate;
    private String myCorrectRate;
    private String classCorrectRate;
    private List<DifficultyModel> difficulty;
    private List<MaterialModel> materialList;
    private WrongMsgModel wrongMsg;
    private int totalNum;//试卷总数

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

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

    public List<DifficultyModel> getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(List<DifficultyModel> difficulty) {
        this.difficulty = difficulty;
    }

    public List<MaterialModel> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<MaterialModel> materialList) {
        this.materialList = materialList;
    }

    public WrongMsgModel getWrongMsg() {
        return wrongMsg;
    }

    public void setWrongMsg(WrongMsgModel wrongMsg) {
        this.wrongMsg = wrongMsg;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
}
