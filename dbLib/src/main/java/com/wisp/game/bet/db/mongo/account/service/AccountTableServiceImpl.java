package com.wisp.game.bet.db.mongo.account.service;

import com.wisp.game.bet.db.mongo.IMongoService;
import com.wisp.game.bet.db.mongo.MongoServiceMeta;
import com.wisp.game.bet.db.mongo.account.doc.AccountTableDoc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;


@MongoServiceMeta("account")
public class AccountTableServiceImpl implements IMongoService {

    private MongoTemplate mongoTemplate;

    public AccountTableDoc findByAccount(String account)
    {
        CriteriaDefinition definition = Criteria.where("Account").is(account);
        Query query = new Query(definition);
       return mongoTemplate.findOne(query, AccountTableDoc.class);
    }

    public AccountTableDoc load_data(String account)
    {
        Criteria criteria = Criteria.where("Account").is(account);
        Query query = new Query(criteria);

        AccountTableDoc accountTableInfo =  mongoTemplate.findOne(query, AccountTableDoc.class);

        return accountTableInfo;
    }

    public AccountTableDoc check_world(String account, int serverId)
    {
        Query query = new Query(Criteria.where("Account").is(account).and("WorldId").is(serverId));
        AccountTableDoc accountTableInfo = mongoTemplate.findOne(query, AccountTableDoc.class);

        return accountTableInfo;
    }

    public boolean check_token(AccountTableDoc accountTableInfo, String account, String sign, String cstoken, int serverId )
    {
        if( !account.equals( accountTableInfo.getAccount() )   )
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
