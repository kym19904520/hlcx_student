package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:知识点分析
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class Know extends BaseBean {
    private String knowTotal;
    private Double classCorrectRate;//班级正确率
    private Double myCorrectRate;//我的正确率
    private Double allCorrectRate;//全部正确率
    private String knowName;
    private String myKnowCorrect;
    private String knowId;

    public String getKnowTotal() {
        return knowTotal;
    }

    public void setKnowTotal(String knowTotal) {
        this.knowTotal = knowTotal;
    }


    public String getKnowName() {
        return knowName;
    }

    public void setKnowName(String knowName) {
        this.knowName = knowName;
    }

    public Double getMyCorrectRate() {
        return myCorrectRate;
    }

    public void setMyCorrectRate(Double myCorrectRate) {
        this.myCorrectRate = myCorrectRate;
    }


    public String getKnowId() {
        return knowId;
    }

    public void setKnowId(String knowId) {
        this.knowId = knowId;
    }

    public Double getClassCorrectRate() {
        return classCorrectRate;
    }

    public void setClassCorrectRate(Double classCorrectRate) {
        this.classCorrectRate = classCorrectRate;
    }

    public String getMyKnowCorrect() {
        return myKnowCorrect;
    }

    public void setMyKnowCorrect(String myKnowCorrect) {
        this.myKnowCorrect = myKnowCorrect;
    }

    public Double getAllCorrectRate() {
        return allCorrectRate;
    }

    public void setAllCorrectRate(Double allCorrectRate) {
        this.allCorrectRate = allCorrectRate;
    }
}
