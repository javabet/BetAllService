package com.wisp.game.bet.http.manager;

import com.wisp.game.bet.db.mongo.account.doc.ServerInfoDoc;
import com.wisp.game.bet.db.mongo.config.doc.ServerListDoc;
import com.wisp.game.bet.http.db.DbAccount;
import com.wisp.game.bet.http.db.DbConfig;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServerListCache {
    private Map<Integer,ServerInfo> serverCache;

    public ServerListCache() {
        serverCache = new HashMap<Integer, ServerInfo>();
    }

    @Scheduled(fixedRate = 60000)
    private void init()
    {
        if(DbConfig.Instance.getMongoTemplate() == null || DbAccount.Instance.getMongoTemplate() == null)
        {
            return;
        }

        List<ServerListDoc> serverListDocs =  DbConfig.Instance.getMongoTemplate().findAll(ServerListDoc.class);

        for(ServerListDoc serverListDoc : serverListDocs)
        {
            //只缓存gate
            if ( serverListDoc.getServerType() != 1)
            {
                continue;
            }

            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setGateType(serverListDoc.getServerType() + "");
            serverInfo.setStatus(serverListDoc.getStatus());
            serverInfo.setGateId(serverListDoc.getServerId());
            if( serverListDoc.getHost() != null && serverListDoc.getHost() != "" )
            {
                serverInfo.setGateIp(serverListDoc.getHost());
            }
            else
            {
                serverInfo.setGateIp(serverListDoc.getServerIp() + ":" + serverInfo.getGateId());
            }

            if( serverListDoc.getHostWeb() != null && serverListDoc.getHostWeb() != "" )
            {
                serverInfo.setGateWeb( serverListDoc.getHostWeb() );
            }
            else
            {
                serverInfo.setGateWeb( serverListDoc.getServerIp() + ":"  + serverInfo.getGateId());
            }
            serverInfo.setPlayerCount(0);

            serverCache.put(serverInfo.getGateId(),serverInfo);
        }

        //刷新在线人数
        for( ServerInfo serverInfo : serverCache.values() )
        {
            Criteria criteria = Criteria.where("GateId").is(serverInfo.getGateId()).and("IsConnect").is(true);
            long playerCount = DbAccount.Instance.getMongoTemplate().count(Query.query(criteria), ServerInfoDoc.class);
            serverInfo.setPlayerCount((int)playerCount);
        }
    }

    public ServerInfo AllocServer(String account,String platform)
    {
        int gateId = -1;
        Criteria criteria = Criteria.where("Account").is(account);
        ServerInfoDoc serverInfoDoc =  DbAccount.Instance.getMongoTemplate().findOne(Query.query(criteria),ServerInfoDoc.class);
        if( serverInfoDoc != null && serverInfoDoc.getGateId() != 0 )
        {
            gateId = serverInfoDoc.getGateId();
            if( serverCache.containsKey(gateId) )
            {
                if( serverCache.get(gateId).getStatus() == 1 && serverCache.get(gateId).getGateType() == platform )
                {
                    return serverCache.get(gateId);
                }
            }
        }


        int minCount = 999999;
        gateId = -1;

        for( ServerInfo serverInfo : serverCache.values() )
        {
            if( serverInfo.getStatus() != 1 || serverInfo.getGateType() != platform )
            {
                continue;
            }

            if( serverInfo.getPlayerCount() < minCount )
            {
                minCount = serverInfo.playerCount;
                gateId = serverInfo.getGateId();
            }
        }


        if( gateId != -1 )
        {
            Update update = new Update();
            update.set("Account",account);
            update.set("GateId",gateId);
            update.set("IsConnect",1);
            update.set("LastTime",new Date());

            criteria = Criteria.where("Account").is(account);
            DbAccount.Instance.getMongoTemplate().upsert(Query.query(criteria),update,ServerInfoDoc.class);

            serverCache.get(gateId).playerCount ++;
            return serverCache.get(gateId);
        }

        return null;
    }

    public  class ServerInfo
   {
       private String gateType;
       private  int gateId;
       private  int status;
       private String gateIp;
       private String gateWeb;
       private String encryptServerIp;
       private int playerCount;

       public String getGateType() {
           return gateType;
       }

       public void setGateType(String gateType) {
           this.gateType = gateType;
       }

       public int getGateId() {
           return gateId;
       }

       public void setGateId(int gateId) {
           this.gateId = gateId;
       }

       public int getStatus() {
           return status;
       }

       public void setStatus(int status) {
           this.status = status;
       }

       public String getGateIp() {
           return gateIp;
       }

       public void setGateIp(String gateIp) {
           this.gateIp = gateIp;
       }

       public String getGateWeb() {
           return gateWeb;
       }

       public void setGateWeb(String gateWeb) {
           this.gateWeb = gateWeb;
       }

       public String getEncryptServerIp() {
           return encryptServerIp;
       }

       public void setEncryptServerIp(String encryptServerIp) {
           this.encryptServerIp = encryptServerIp;
       }

       public int getPlayerCount() {
           return playerCount;
       }

       public void setPlayerCount(int playerCount) {
           this.playerCount = playerCount;
       }
   }
}
