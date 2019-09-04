package com.mongodb.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.result.Json;
import com.mongodb.result.BulidResultJson;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * <h3>MongodbGFSController  Class</h3>
 *
 * @author : YuXiang
 * @date : 2019-09-03 17:59
 **/
@Controller
public class MongodbGFSController {
    private static final Logger LOGGER= LoggerFactory.getLogger(MongodbGFSController.class);

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 上传文件
     *
     * */
    @PostMapping("/upload")
    @ResponseBody
    public Json uploadFile(@RequestParam("file") MultipartFile file) throws IOException, ServletException {
        // 获得提交的文件名
        String fileName = file.getOriginalFilename();
        // 获取文件输入流
        InputStream ins = file.getInputStream();
        // 获取文件类型
        String contentType = file.getContentType();
        // 将文件存储到mongodb中
        ObjectId objectId = gridFsTemplate.store(ins, fileName, contentType);
        LOGGER.info("保存成功，objectId:" + objectId);
        return BulidResultJson.getResultJson(true, objectId, "200", "成功");
    }

    /**
     * 下载文件
     * */
    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.POST})
    public void downloadFile(@RequestParam(name = "file_id") String fileId,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        LOGGER.info("准备下载文件....");
        Query query = Query.query(Criteria.where("_id").is(fileId));
        // 查询单个文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile == null) {
            return;
        }

        String fileName = gridFSFile.getFilename().replace(",", "");
        String contentType = gridFSFile.getMetadata().get("_contentType").toString();

        // 通知浏览器进行文件下载
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource resource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = resource.getInputStream();
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    /**
     * 删除文件
     * */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public Json deleteFile(@RequestParam(name = "file_id") String fileId){
        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile == null) {
            return BulidResultJson.getResultJson(true, fileId, "200", "未查到此文件!");
        }
        gridFsTemplate.delete(query);
        return BulidResultJson.getResultJson(true, fileId, "200", "删除成功!");
    }
}
