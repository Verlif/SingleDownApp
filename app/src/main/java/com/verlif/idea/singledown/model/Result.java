package com.verlif.idea.singledown.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Result extends JSONBuilder {

    public static final int CODE_SUCCESS = 200;
    public static final int CODE_FAIL = 400;

    protected int code;
    protected String msg;
    protected String data;

    public Result(int code, Object object) {
        this.code = code;
        addObject(object);
    }

    public <T> Result(int code, String key, List<T> list) {
        this.code = code;
        addList(key, list);
    }

    public void addObject(Object object) {
        addObject(object.getClass().getSimpleName(), object);
    }

    public <T> void addList(String key, List<T> list) {
        JSONArray array = new JSONArray();
        array.addAll(list);
        addObject(key, array);
    }

    public void addObject(String key, Object object) {
        JSONObject json = JSON.parseObject(data);
        if (json == null) {
            json = new JSONObject();
        }
        json.put(key, object);
        data = json.toJSONString();
    }

    public <T> T getDataObject(String key) {
        JSONObject object = JSON.parseObject(data);
        return (T) object.get(key);
    }

    public <T> T getDataObject(Class<T> cl) {
        JSONObject object = JSON.parseObject(data);
        return object.getObject(cl.getSimpleName(), cl);
    }

}
