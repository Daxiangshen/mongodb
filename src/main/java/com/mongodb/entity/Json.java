package com.mongodb.entity;

import lombok.Data;

/**
 * <h3>Json  Class</h3>
 *
 * @author : YuXiang
 * @date : 2019-09-03 18:04
 **/
@Data
public class Json {
    private String msg;            //返回说明
    private Object objs;           //返回对象体
    private Boolean success;      //返回是否成功
    private String status;        //返回状态
    private String code;          //返回状态码
}
