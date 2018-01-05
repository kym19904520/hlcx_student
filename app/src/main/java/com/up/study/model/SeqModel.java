package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:题
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class SeqModel extends BaseBean {
    private int subjectId;//试题ID
    private int subId;//试题ID 林城的
    private String answer;//答案
    private String seq;//题号
    private double mark;//该题总分数
    private String relationId;//试卷id，任务id
    private int status;//正确与否 0:正确，其他情况都视为错误,（默认正确）

    private String title;//标题
    private String subjectType;//题型
    private String analysis;//解题思路
    private String content;//内容
    private String knowName;//知识点
    private String options;//选项
    private boolean select;//是否被选中

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getAnswer() {
         if (getSubjectType().contains("判断")){
            String as = answer;
            as = as.replace("0","A");
            as = as.replace("1","B");
             answer = as;
        }
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;

    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKnowName() {
        return knowName;
    }

    public void setKnowName(String knowName) {
        this.knowName = knowName;
    }

    public String getOptions() {
        if(getSubjectType().contains("判断")){
           setOptions("[{\"text\":\"正确\",\"opt\":\"A\"},{\"text\":\"错误\",\"opt\":\"B\"}]");
        }
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
