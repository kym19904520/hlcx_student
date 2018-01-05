package com.up.study.model;

import com.up.common.base.BaseBean;

import java.util.List;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/6/23.
 */

public class ErrorSubjectContentModel extends BaseBean {
    private String createDate;
    private int total;
    private List<ListContent> child;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<ListContent> getChild() {
        return child;
    }

    public void setChild(List<ListContent> child) {
        this.child = child;
    }

    public class ListContent {
        private int number;
        private String sourceName;
        private String source;
        private String createDate;


        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }


}
