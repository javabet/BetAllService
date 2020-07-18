package com.wisp.game.bet.gate.config;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;


@Configuration
public class MongoConfig {


   // @Value("${mongodb.account.uri}")
    private String accountDbUrl;

    //@Value("${mongodb.config.uri}")
    private String configDbUrl;

    //@Bean(name={"mongodb_account"})
    public MongoTemplate accountMongoTemplate( )
    {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(MongoClients.create(accountDbUrl), "AccountDB");
        MongoConverter mongoConverter = getDefaultMongoConverter(factory);
        return new MongoTemplate(factory,mongoConverter);
    }

    //@Bean(name={"mongodb_config"})
    public MongoTemplate configMongoTemplate( )
    {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(MongoClients.create(configDbUrl), "ConfigDB");
        MongoConverter mongoConverter = getDefaultMongoConverter(factory);
        return new MongoTemplate(factory,mongoConverter);
    }


    private  MongoConverter getDefaultMongoConverter(MongoDatabaseFactory factory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MongoCustomConversions conversions = new MongoCustomConversions(Collections.emptyList());
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(conversions);
        converter.setCodecRegistryProvider(factory);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();
        return converter;
    }

    public String getAccountDbUrl() {
        return accountDbUrl;
    }

    public void setAccountDbUrl(String accountDbUrl) {
        this.accountDbUrl = accountDbUrl;
    }

    public String getConfigDbUrl() {
        return configDbUrl;
    }

    public void setConfigDbUrl(String configDbUrl) {
        this.configDbUrl = configDbUrl;
    }
}
