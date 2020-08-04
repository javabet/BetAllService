package com.wisp.game.bet.sshare;

import com.mongodb.client.MongoClients;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.sshare.convert.DateToTimeIntConvert;
import com.wisp.game.bet.sshare.convert.DateToTimeLongConvert;
import com.wisp.game.bet.sshare.convert.TimeIntToDateConvert;
import com.wisp.game.bet.sshare.convert.TimeLongToDateConvert;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

public class DbBase {


    private String dbName;
    private MongoTemplate mongoTemplate;
    private MongoConverter mongoConverter;
    private MongoTransactionManager mongoTransactionManager;

    public DbBase() {
    }

    public void init_db(String dburi ,String _db_name)
    {
        dbName = _db_name;

        mongoTemplate =  mongoTemplate("mongodb://" + dburi,dbName);

        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) SpringContextHolder.getApplicationContext();
    }

    private MongoTemplate mongoTemplate( String dbUrl,String dbName)
    {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(MongoClients.create(dbUrl), dbName);
        mongoConverter = getDefaultMongoConverter(factory);
        mongoTransactionManager = new MongoTransactionManager(factory);
        return new MongoTemplate(factory,mongoConverter);
    }

    private  MongoConverter getDefaultMongoConverter(MongoDatabaseFactory factory) {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        //converterList.add(new DateToTimeIntConvert());
        //converterList.add(new TimeIntToDateConvert());
        //converterList.add(new DateToTimeLongConvert());
        //converterList.add(new TimeLongToDateConvert());

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MongoCustomConversions conversions = new MongoCustomConversions(converterList);
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

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
    public MongoConverter getMongoConverter(){return mongoConverter;};
    public MongoTransactionManager getMongoTransactionManager(){return mongoTransactionManager;};
}
