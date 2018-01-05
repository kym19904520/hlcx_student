package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:试卷
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class TestModel extends BaseBean {
    private String id;
    private String title;
    private String uid;
    private String name;
    private String deadline;
    private String attached;
    private String subjectSum;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getAttached() {
        return attached;
    }

    public void setAttached(String attached) {
        this.attached = attached;
    }

    public String getSubjectSum() {
        return subjectSum;
    }

    public void setSubjectSum(String subjectSum) {
        this.subjectSum = subjectSum;
    }
}
