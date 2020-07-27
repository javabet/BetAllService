package com.wisp.game.bet.world.db.mongo.config.service;

import com.wisp.game.bet.world.db.mongo.IMongoService;
import com.wisp.game.bet.world.db.mongo.MongoService;
import com.wisp.game.bet.world.db.mongo.config.info.ServerList;
import com.wisp.game.bet.world.db.mongo.config.interfaceService.ServerListService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@MongoService
public class ServerListImpl implements ServerListService, IMongoService {

    //@Resource(name = "mongodb_config")
    private MongoTemplate mongoTemplate;

    public List<ServerList> findAll()
    {
        Criteria criteria = Criteria.where("ServerType").gt(2).and("ServerId").is(12001);
        Query query = Query.query(criteria);
        query.limit(100).skip(1).fields();
        //query.with(Sort.by(Sort.Order.desc("")));


        Update update = Update.update("Status",-1);

        ServerList serverList = new ServerList();
        serverList.setServerId(1);
        serverList.setServerType(2);
        serverList.setServerIp("127.0..1");
        mongoTemplate.insert(serverList);

        return mongoTemplate.findAll(ServerList.class);
    }

    @Override
    public void setMongoTemplate(MongoTemplate mongoTemple) {
        this.mongoTemplate = mongoTemple;
    }
}