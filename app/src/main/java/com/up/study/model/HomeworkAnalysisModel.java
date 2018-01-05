package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:作业结果
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class HomeworkAnalysisModel extends BaseBean {

    private String timeConsuming;
    private String title;
    private String myAllCorrectRate;
    private String myCorrectSum;
    private String deadline;
    private Double correctRate;
    private List<Know> know;

    public String getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(String timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMyAllCorrectRate() {
        return myAllCorrectRate;
    }

    public void setMyAllCorrectRate(String myAllCorrectRate) {
        this.myAllCorrectRate = myAllCorrectRate;
    }

    public String getMyCorrectSum() {
        return myCorrectSum;
    }

    public void setMyCorrectSum(String myCorrectSum) {
        this.myCorrectSum = myCorrectSum;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public List<Know> getKnow() {
        return know;
    }

    public void setKnow(List<Know> know) {
        this.know = know;
    }

    public Double getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(Double correctRate) {
        this.correctRate = correctRate;
    }
}
