package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:习题难度
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class DifficultyModel extends BaseBean {
    private  int difficulty;
    private  int auxiliaryNum;
    private  float diffCorrectRate;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getAuxiliaryNum() {
        return auxiliaryNum;
    }

    public void setAuxiliaryNum(int auxiliaryNum) {
        this.auxiliaryNum = auxiliaryNum;
    }

    public float getDiffCorrectRate() {
        return diffCorrectRate;
    }

    public void setDiffCorrectRate(float diffCorrectRate) {
        this.diffCorrectRate = diffCorrectRate;
    }
}
