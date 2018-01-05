package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:学生试卷
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class ReqImgUrl extends BaseBean {
    private  String readhost;
    private  String accessid;
    private  String policy;
    private  String signature;
    private  String dir;
    private  String host;
    private  String expire;

    public String getReadhost() {
        return readhost;
    }

    public void setReadhost(String readhost) {
        this.readhost = readhost;
    }

    public String getAccessid() {
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }
}
