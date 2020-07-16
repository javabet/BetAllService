package com.wisp.game.bet.monitor.unit;


import com.wisp.game.bet.monitor.db.DbAccount;
import com.wisp.game.bet.monitor.db.DbConfig;
import com.wisp.game.bet.monitor.db.GameDb;
import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.db.config.info.ServerList;
import com.wisp.game.sshare.ServerBase;
import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MonitorServer extends ServerBase  {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    public SpringContextHolder springContextHolder;

    @Autowired
    public ServerManager serverManager;

    @Autowired
    public Environment environment;

    @Autowired
    public DbAccount dbAccount;

    @Autowired
    public DbConfig dbConfig;

    public static MonitorServer Instance;

    public MonitorServer() {
        MonitorServer.Instance = this;
    }

    public boolean on_init() {
        init_db();
        return true;
    }

    protected void on_run() {
        double elapsed = 0;
        while (is_runing())
        {
            long cur_tm_ms = System.currentTimeMillis();
            serverManager.heartbeat(elapsed);
            elapsed = System.currentTimeMillis() - cur_tm_ms;

            if( elapsed < 100 )
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception exception)
                {
                    logger.warn("server_run longtime:" + elapsed);
                }
            }
            else
            {
                logger.warn("server_run longtime:" + elapsed);
            }
        }
    }

    public void on_exit() {

    }

    protected void init_db()
    {

        if( environment.containsProperty("cfg.accountdb_url") && environment.containsProperty("cfg.accountdb_name") )
        {
            dbAccount.init_db(environment.getProperty("cfg.accountdb_url"),environment.getProperty("cfg.accountdb_name"));
        }

        if( environment.containsProperty("cfg.configdb_url") && environment.containsProperty("cfg.configdb_name") )
        {
            dbConfig.init_db(environment.getProperty("cfg.configdb_url"),environment.getProperty("cfg.configdb_name"));
        }

        //开服时,重置数据
        /**
         * 	db_account::instance().clearTable(db_account::DB_SERVERINFO);
         * 	db_config::instance().updatemany(db_config::DB_SERVERLIST,
         * 		document{} << finalize,
         * 		document{} << "$set" << open_document  << "Status" << 0 << close_document << finalize);
         */

        dbAccount.getMongoTemplate().dropCollection(GameDb.DB_SERVERINFO);
        Query query = new Query();
        Update update = new Update();
        update.set("Status",0);
        dbConfig.getMongoTemplate().updateMulti(query,update, ServerList.class);
    }


    @Override
    protected ChannelHandler getChannelHandler() {
        return new MonitorChannelHandler();
    }
}
