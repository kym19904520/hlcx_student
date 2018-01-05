package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:答题卡
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class AnswerSheetModel extends BaseBean {
    private String id;
    private String title;
    private String cuid;//试卷创建者，对应老师ID
    private double markSum;
    private String subjectType;
    private String subjectTypeId;
    private int completedTotal;//已完成数
    private int subjectSum;
    private List<SeqModel> seqList;

    public int getCompletedTotal() {
        return completedTotal;
    }

    public void setCompletedTotal(int completedTotal) {
        this.completedTotal = completedTotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public double getMarkSum() {
        return markSum;
    }

    public void setMarkSum(double markSum) {
        this.markSum = markSum;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getSubjectTypeId() {
        return subjectTypeId;
    }

    public void setSubjectTypeId(String subjectTypeId) {
        this.subjectTypeId = subjectTypeId;
    }

    public int getSubjectSum() {
        return subjectSum;
    }

    public void setSubjectSum(int subjectSum) {
        this.subjectSum = subjectSum;
    }

    public List<SeqModel> getSeqList() {
        return seqList;
    }

    public void setSeqList(List<SeqModel> seqList) {
        this.seqList = seqList;
    }
}
