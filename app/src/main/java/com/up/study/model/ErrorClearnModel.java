package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:错题扫除
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class ErrorClearnModel extends BaseBean {
    private  int totalCount;
    private List<LcSeqModel> subList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<LcSeqModel> getSubList() {
        return subList;
    }

    public void setSubList(List<LcSeqModel> subList) {
        this.subList = subList;
    }
}
