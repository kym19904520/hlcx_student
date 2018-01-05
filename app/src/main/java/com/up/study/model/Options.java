package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:选项
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class Options extends BaseBean {
    private  String text;
    private  String opt;
    private int status;//0:无状态，1：正确，2：错误 ,3:选中,4,正确答案但未选中

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOpt2(SeqModel seqModel){
        if (seqModel.getSubjectType().contains("判断")){
            if("A".equals(opt)){
                return "0";
            }
            else if("B".equals(opt)){
                return "1";
            }
        }
        return opt;
    }
}
