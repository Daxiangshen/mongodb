package com.mongodb.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * <h3>MongodbConf  Class</h3>
 * 解决新版本不支持获取GGridFSDBFile
 * @author : YuXiang
 * @date : 2019-09-03 17:58
 **/
@Configuration
public class MongodbConf {
    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private GridFSBucket gridFSBucket;


    @Bean
    public GridFSBucket getGridFSBucket() {
        MongoDatabase db = mongoDbFactory.getDb();
        return GridFSBuckets.create(db);
    }
}
