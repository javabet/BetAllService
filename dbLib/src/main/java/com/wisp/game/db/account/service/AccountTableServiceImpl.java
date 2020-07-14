package com.wisp.game.db.account.service;

import com.wisp.game.db.account.info.AccountTableInfo;
import com.wisp.game.db.account.interfaceService.AcountService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class AccountTableServiceImpl implements AcountService {


    private MongoTemplate mongoTemplate;

    public AccountTableInfo findByAccount(String account)
    {
        CriteriaDefinition definition = Criteria.where("Account").is(account);
        Query query = new Query(definition);
       return mongoTemplate.findOne(query,AccountTableInfo.class);
    }
}
