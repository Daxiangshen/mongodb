package com.mongodb.controller;

import com.mongodb.entity.UploadFile;
import com.mongodb.result.BulidResultJson;
import com.mongodb.result.Json;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

/**
 * <h3>MongodbImageController  Class</h3>
 *
 * @author : YuXiang
 * @date : 2019-09-04 09:24
 **/
@Controller
public class MongodbImageController {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 上传图片
     * */
    @PostMapping("/uploadImage")
    @ResponseBody
    public Json uploadImage(@RequestParam(value = "image") MultipartFile file){
        if (file.isEmpty()){
            return BulidResultJson.getResultJson(true,"","200","请选择一个文件");
        }
        String fileName=file.getOriginalFilename();
        try {
            UploadFile uploadFile=new UploadFile();
            uploadFile.setName(fileName);
            uploadFile.setCreatedTime(new Date());
            uploadFile.setContent(new Binary(file.getBytes()));
            uploadFile.setContentType(file.getContentType());
            uploadFile.setSize(file.getSize());

            UploadFile savedFile=mongoTemplate.save(uploadFile);
            String url="http://localhost:8001/downloadImage/"+savedFile.getId();
            return BulidResultJson.getResultJson(true,url,"200","图片上传成功");
        } catch (IOException e) {
            e.printStackTrace();
            return BulidResultJson.getResultJson(true,"","200","图片上传失败请重试");
        }
    }


    /**
     * 显示图片
     * */
    @GetMapping(value = "/showImage/{id}",produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] image(@PathVariable String id){
        byte[] data=null;
        UploadFile file=mongoTemplate.findById(id,UploadFile.class);
        if (null!=file){
            data=file.getContent().getData();
        }
        return data;
    }


    /**
     * 下载图片
     * */
    @RequestMapping(value = "/downloadImage", method = {RequestMethod.GET, RequestMethod.POST})
    public void downloadImage(HttpServletResponse response,@RequestParam(value = "id") String id) throws UnsupportedEncodingException {
        UploadFile file=mongoTemplate.findById(id,UploadFile.class);
        String fileName=file.getName();
        String contentType=file.getContentType();
        String url="http://localhost:8001/downloadImage/"+file.getId();
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            // 通知浏览器进行文件下载
            response.setContentType(contentType);
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
            URL imageUrl=new URL(url);
            URLConnection connection=imageUrl.openConnection();
            outputStream=response.getOutputStream();
            inputStream=connection.getInputStream();
            IOUtils.copy(inputStream,outputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
