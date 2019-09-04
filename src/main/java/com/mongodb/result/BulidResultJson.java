package com.mongodb.result;

/**
 * <h3>BulidResultJson  Class</h3>
 *
 * @author : YuXiang
 * @date : 2019-09-03 18:04
 **/
public class BulidResultJson {
    public static Json getResultJson(Boolean success, Object objs, String status, String msg){
        Json json = new Json();
        json.setObjs(objs);
        json.setSuccess(success);
        json.setStatus(status);
        json.setMsg(msg);
        return json;
    }
}
