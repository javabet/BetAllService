package com.wisp.game.bet.db.mongo;

import org.springframework.data.mongodb.core.MongoTemplate;

@MongoServiceMeta
public abstract class DefaultMongoService implements IMongoService {

    protected MongoTemplate mongoTemplate;

    @Override
    public void setMongoTemplate(MongoTemplate mongoTemple) {
        this.mongoTemplate = mongoTemple;
    }
}
