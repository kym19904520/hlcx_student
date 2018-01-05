package com.up.study.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.up.common.base.BaseBean;
import com.up.common.utils.SPUtil;
import com.up.study.TApplication;

/**
 * TODO:登录
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class Login extends BaseBean {
    private  ClasssModel classs;
    private  StudentModel student;
    private  UserModel user;
    private String IMGROOT;

    public ClasssModel getClasss() {
        return classs;
    }

    public void setClasss(ClasssModel classs) {
        this.classs = classs;
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getIMGROOT() {
        return IMGROOT;
    }

    public void setIMGROOT(String IMGROOT) {
        this.IMGROOT = IMGROOT;
    }

    public Login get() {
        String loginInfo = (String) SPUtil.get(TApplication.getInstance(), "user", "");
        if (TextUtils.isEmpty(loginInfo)) {
            return null;
        }
        return new Gson().fromJson(loginInfo, Login.class);
    }
}
