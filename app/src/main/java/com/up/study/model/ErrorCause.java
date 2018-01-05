package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:错误原因
 * 世界上最遥远的距离不是生与死
 * 而是你亲手制造的BUG就在你眼前
 * 你却怎么都找不到它
 * Created by yy_cai
 * On 2017/3/12.
 */

public class ErrorCause extends BaseBean {
    private String errorTotal;
    private String errorName;
    private String code;
    private String errorRate;

    public String getErrorTotal() {
        return errorTotal;
    }

    public void setErrorTotal(String errorTotal) {
        this.errorTotal = errorTotal;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(String errorRate) {
        this.errorRate = errorRate;
    }
}
