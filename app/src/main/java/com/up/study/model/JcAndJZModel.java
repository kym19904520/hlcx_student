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

public class JcAndJZModel extends BaseBean {
            /*"id": "35",
            "createDate": null,
            "status": null,
            "name": "2014年青岛版数学三年级上册",
            "parentId": "-1",
            "grade": null,
            "major": null,
            "type": "jc",
            "material_id": "0",
            "count": 0*/
    private String id;
    private String name;
    private String type;//jc:教材，zj：章节
    private String material_id;
    private boolean isSelect;
    private boolean isShow;
    public boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    public void setCheck(boolean check) {
        this.isCheck = check;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public JcAndJZModel(String id,String name,String type,String material_id,boolean isSelect,
                        boolean isShow,boolean isCheck){
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.material_id = material_id;
        this.isSelect = isSelect;
        this.isShow = isShow;
        this.isCheck = isCheck;
    }
}
