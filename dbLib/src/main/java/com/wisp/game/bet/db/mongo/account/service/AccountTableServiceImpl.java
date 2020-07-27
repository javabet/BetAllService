package com.wisp.game.bet.db.mongo.account.service;

import com.wisp.game.bet.db.mongo.IMongoService;
import com.wisp.game.bet.db.mongo.MongoServiceMeta;
import com.wisp.game.bet.db.mongo.account.info.AccountTableInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;


@MongoServiceMeta("account")
public class AccountTableServiceImpl implements IMongoService {

    private MongoTemplate mongoTemplate;

    public AccountTableInfo findByAccount(String account)
    {
        CriteriaDefinition definition = Criteria.where("Account").is(account);
        Query query = new Query(definition);
       return mongoTemplate.findOne(query,AccountTableInfo.class);
    }

    public AccountTableInfo load_data(String account)
    {
        Criteria criteria = Criteria.where("Account").is(account);
        Query query = new Query(criteria);

        AccountTableInfo accountTableInfo =  mongoTemplate.findOne(query,AccountTableInfo.class);

        return accountTableInfo;
    }

    public AccountTableInfo check_world(String account,int serverId)
    {
        Query query = new Query(Criteria.where("Account").is(account).and("WorldId").is(serverId));
        AccountTableInfo accountTableInfo = mongoTemplate.findOne(query,AccountTableInfo.class);

        return accountTableInfo;
    }

    public boolean check_token(AccountTableInfo accountTableInfo,String account,String sign,String cstoken,int serverId )
    {
        if( accountTableInfo.getAccount() != account )
        {
            return false;
        }

        if( check_world(account,serverId) != null )
        {
            return false;
        }

        return true;
    }


    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

}
