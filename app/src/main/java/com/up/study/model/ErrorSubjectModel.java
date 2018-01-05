package com.up.study.model;

import com.up.common.base.BaseBean;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/6/23.
 */

public class ErrorSubjectModel extends BaseBean{
    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
