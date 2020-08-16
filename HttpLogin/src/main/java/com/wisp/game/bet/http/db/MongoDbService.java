package com.wisp.game.bet.http.db;

import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.db.mongo.IMongoService;
import com.wisp.game.bet.db.mongo.MongoServiceMeta;
import com.wisp.game.bet.share.common.ClassScanner;
import com.wisp.game.bet.sshare.DbBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;

@Component
public class MongoDbService implements InitializingBean {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DbAccount dbAccount;

    @Autowired
    private DbConfig dbConfig;

    @Autowired
    private SpringContextHolder springContextHolder;

    @Autowired
    public Environment environment;

    @Autowired
    private DbLog dbLog;


    public  void afterPropertiesSet() throws Exception
    {
        //初始化
        init_db();

        setMongoTemplteHandler( dbAccount,"account" );
        setMongoTemplteHandler( dbConfig,"config" );
        setMongoTemplteHandler( dbLog,"log" );
    }

    private boolean init_db()
    {

        if( environment.containsProperty("cfg.configdb_url") && environment.containsProperty("cfg.configdb_name") )
        {
            DbConfig.Instance.init_db(environment.getProperty("cfg.configdb_url"),environment.getProperty("cfg.configdb_name"));
        }

        if( environment.containsProperty("cfg.accountdb_url") && environment.containsProperty("cfg.accountdb_name") )
        {
            DbAccount.Instance.init_db(environment.getProperty("cfg.accountdb_url"),environment.getProperty("cfg.accountdb_name"));
        }

        if( environment.containsProperty("cfg.configdb_log") && environment.containsProperty("cfg.configlog_name") )
        {
            DbLog.Instance.init_db(environment.getProperty("cfg.configdb_log"),environment.getProperty("cfg.configlog_name"));
        }

        return true;
    }

    private void setMongoTemplteHandler(DbBase dbBase,String mongoName)
    {
        Set<Class<?>> clzSet = ClassScanner.listAllSubclasses("com.wisp.game.bet.db.mongo", IMongoService.class);
        Iterator<Class<?>> clzIt =  clzSet.iterator();

        while (clzIt.hasNext())
        {
            Class<?> clz =  clzIt.next();
            MongoServiceMeta mongoService =  clz.getAnnotation( MongoServiceMeta.class );

            if( mongoService == null )
            {
                logger.warn("the mongoName has not the MongoServiceMeta:" + mongoName + " clz:" + clz.getName());
                continue;
            }

            if( mongoService.value() == "" )
            {
                continue;
            }

            if(  !mongoName.toLowerCase().equals(mongoService.value()) )
            {
                continue;
            }

            IMongoService mongoService1 = (IMongoService) SpringContextHolder.getBean(clz);
            mongoService1.setMongoTemplate(dbBase.getMongoTemplate());
        }
    }
}
