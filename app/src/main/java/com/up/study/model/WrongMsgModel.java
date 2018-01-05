package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:学生试卷
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class WrongMsgModel extends BaseBean {
    private List<WrongMsgSubModel> subList;
    private int totalNum;

    public List<WrongMsgSubModel> getSubList() {
        return subList;
    }

    public void setSubList(List<WrongMsgSubModel> subList) {
        this.subList = subList;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
}
