package com.wisp.game.bet.db.mongo.config.service;

import com.wisp.game.bet.db.mongo.IMongoService;
import com.wisp.game.bet.db.mongo.MongoServiceMeta;
import com.wisp.game.bet.db.mongo.config.doc.ServerListDoc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@MongoServiceMeta
public class ServerListImpl implements  IMongoService {

    private MongoTemplate mongoTemplate;

    public List<ServerListDoc> findAll()
    {
        Criteria criteria = Criteria.where("ServerType").gt(2).and("ServerId").is(12001);
        Query query = Query.query(criteria);
        query.limit(100).skip(1).fields();
        //query.with(Sort.by(Sort.Order.desc("")));


        Update update = Update.update("Status",-1);

        ServerListDoc serverList = new ServerListDoc();
        serverList.setServerId(1);
        serverList.setServerType(2);
        serverList.setServerIp("127.0..1");
        mongoTemplate.insert(serverList);

        return mongoTemplate.findAll(ServerListDoc.class);
    }

    @Override
    public void setMongoTemplate(MongoTemplate mongoTemple) {
        this.mongoTemplate = mongoTemple;
    }
}
