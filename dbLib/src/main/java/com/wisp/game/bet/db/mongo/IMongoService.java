package com.wisp.game.bet.db.mongo;

import org.springframework.data.mongodb.core.MongoTemplate;

public interface IMongoService {
    public void setMongoTemplate(MongoTemplate mongoTemple);
}
