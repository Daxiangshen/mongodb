package com.mongodb.entity;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * <h3>UploadFile  Class</h3>
 *
 * @author : YuXiang
 * @date : 2019-09-04 09:26
 **/
@Document
@Data
public class UploadFile {
    @Id
    private String id;
    private String name; // 文件名
    private Date createdTime; // 上传时间
    private Binary content; // 文件内容
    private String contentType; // 文件类型
    private long size; // 文件大小
}
