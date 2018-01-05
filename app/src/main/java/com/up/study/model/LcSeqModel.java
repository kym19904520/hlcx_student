package com.up.study.model;

import android.text.TextUtils;

import com.up.common.base.BaseBean;

/**
 * TODO:林城 题
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class LcSeqModel extends BaseBean {
    //private String subId;//试题ID，林城的接口数据
    private String subjectId;//试题ID ,刘全的接口数据
    private String answer;//答案
    private String seq;//题号
    private int mark;//该题总分数
    private String relationId;//试卷id，任务id
    private int status;//正确与否

    private String title;//标题
    private String subjectType;//题型
    private String analysis;//解题思路
    private String content;//内容
    private String knowName;//知识点
    private String options;//选项
    private boolean select;//是否被选中
    private String errAttached;//错误图片
    private String err_type;//错误选项
    private String errName;//错误选项对应的原因
    private String err_describe;//错误描述，错误原因为D才需要
    private String err_status; //错题状态

    public String getErr_status() {
        return err_status;
    }

    public void setErr_status(String err_status) {
        this.err_status = err_status;
    }

    public String getAnswer() {
        if (!TextUtils.isEmpty(answer)) {
            if (getSubjectType().contains("判断")) {
                String as = answer;
                as = as.replace("0", "A");
                as = as.replace("1", "B");
                as = as.replace("错","A");
                as = as.replace("对","B");
                answer = as;
            }
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

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
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

    public String getOptions() {
        if(getSubjectType().contains("判断")){
            setOptions("[{\"text\":\"正确\",\"opt\":\"A\"},{\"text\":\"错误\",\"opt\":\"B\"}]");
        }
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    /*public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }*/

    public String getKnowName() {
        return knowName;
    }

    public void setKnowName(String knowName) {
        this.knowName = knowName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getErrAttached() {
        return errAttached;
    }

    public void setErrAttached(String errAttached) {
        this.errAttached = errAttached;
    }

    public String getErr_type() {
        return err_type;
    }

    public void setErr_type(String err_type) {
        this.err_type = err_type;
    }

    public String getErrName() {
        return errName;
    }

    public void setErrName(String errName) {
        this.errName = errName;
    }

    public String getErr_describe() {
        return err_describe;
    }

    public void setErr_describe(String err_describe) {
        this.err_describe = err_describe;
    }
}
