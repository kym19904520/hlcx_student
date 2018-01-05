package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:试卷分析
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class AnalysisModel extends BaseBean {

    private String point;
    private int rankNum;
    private List<Know> know;
    private List<ErrorCause> errorCause;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public int getRankNum() {
        return rankNum;
    }

    public void setRankNum(int rankNum) {
        this.rankNum = rankNum;
    }

    public List<Know> getKnow() {
        return know;
    }

    public void setKnow(List<Know> know) {
        this.know = know;
    }

    public List<ErrorCause> getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(List<ErrorCause> errorCause) {
        this.errorCause = errorCause;
    }
}
