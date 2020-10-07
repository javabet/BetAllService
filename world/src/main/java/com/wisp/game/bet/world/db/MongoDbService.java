package com.wisp.game.bet.world.db;

import com.wisp.game.bet.db.mongo.IMongoService;
import com.wisp.game.bet.db.mongo.MongoServiceMeta;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.common.ClassScanner;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.sshare.DbBase;
import com.wisp.game.bet.world.dbConfig.AgentInfoConfig;
import com.wisp.game.core.utils.FilePathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
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
    private DbCloud dbCloud;

    @Autowired
    private DbGame dbGame;

    @Autowired
    private DbInfo dbInfo;

    @Autowired
    private DbLog dbLog;

    @Autowired
    private DbPay dbPay;

    @Autowired
    private  DbPlayer dbPlayer;

    @Autowired
    private SpringContextHolder springContextHolder;

    @Autowired
    public Environment environment;

    @Autowired
    private AgentInfoConfig agentInfoConfig;

    @Autowired
    private TimeHelper timeHelper;

    public  void afterPropertiesSet() throws Exception
    {
        //初始化
        init_db();

        setMongoTemplteHandler( dbAccount,"account" );
        setMongoTemplteHandler( dbConfig,"config" );
        setMongoTemplteHandler( dbCloud,"cloud" );
        setMongoTemplteHandler( dbGame,"game" );
        setMongoTemplteHandler( dbInfo,"info" );
        setMongoTemplteHandler( dbLog,"log" );
        setMongoTemplteHandler( dbPay,"pay" );
        setMongoTemplteHandler( dbPlayer,"player" );
    }

    private boolean init_db()
    {

        if( environment.containsProperty("cfg.playerdb_url") && environment.containsProperty("cfg.playerdb_name") )
        {
            DbPlayer.Instance.init_db(environment.getProperty("cfg.playerdb_url"),environment.getProperty("cfg.playerdb_name"));
        }

        if( environment.containsProperty("cfg.gamedb_url") && environment.containsProperty("cfg.gamedb_name") )
        {
            DbGame.Instance.init_db(environment.getProperty("cfg.gamedb_url"),environment.getProperty("cfg.gamedb_name"));
        }

        if( environment.containsProperty("cfg.paydb_url") && environment.containsProperty("cfg.paydb_name") )
        {
            DbPay.Instance.init_db(environment.getProperty("cfg.paydb_url"),environment.getProperty("cfg.paydb_name"));
        }

        if( environment.containsProperty("cfg.configdb_url") && environment.containsProperty("cfg.configdb_name") )
        {
            DbConfig.Instance.init_db(environment.getProperty("cfg.configdb_url"),environment.getProperty("cfg.configdb_name"));
        }

        if( environment.containsProperty("cfg.clouddb_url") && environment.containsProperty("cfg.clouddb_name") )
        {
            DbCloud.Instance.init_db(environment.getProperty("cfg.clouddb_url"),environment.getProperty("cfg.clouddb_name"));
        }

        if( environment.containsProperty("cfg.accountdb_url") && environment.containsProperty("cfg.accountdb_name") )
        {
            DbAccount.Instance.init_db(environment.getProperty("cfg.accountdb_url"),environment.getProperty("cfg.accountdb_name"));
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
