package com.up.common.base;

import java.io.Serializable;
import java.util.Map;

/**
 * TODO:
 * Created by 王剑洪
 * On 2017/2/24.
 */

public class SerializableMap implements Serializable {
    private Map<String, Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
