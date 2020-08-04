package com.wisp.game.bet.world.dbConfig;

import com.sun.javadoc.Doc;
import com.wisp.game.bet.db.mongo.config.doc.AgentInfoDoc;
import com.wisp.game.bet.db.mongo.config.doc.info.GameInfo;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.world.db.DbConfig;
import com.wisp.game.bet.world.dbConfig.info.AgentGameInfo;
import com.wisp.game.bet.world.dbConfig.info.AgentInfo;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class AgentInfoConfig {
    public static AgentInfoConfig Instance;

    private Map<String,AgentInfo> agentInfos;
    public AgentInfoConfig() {
        Instance = this;
        this.agentInfos = new ConcurrentHashMap<>();
    }


    public AgentInfo getGameInfo(String channelId)
    {
        if(!agentInfos.containsKey(channelId))
        {
            if(!loadCustomizeData(channelId))
            {
                return null;
            }
        }


        return agentInfos.get(channelId);
    }

    public  boolean hasGame( String channelId,int gameId )
    {
        if(!agentInfos.containsKey(channelId))
        {
            if(!loadCustomizeData(channelId))
            {
                return false;
            }
        }

        if(!agentInfos.containsKey(channelId))
        {
            return false;
        }

        return agentInfos.get(channelId).getGameMap().containsKey(gameId);
    }

    public boolean loadCustomizeData(String channelId)
    {
        if(agentInfos.containsKey(channelId)  )
        {
            agentInfos.remove(channelId);
        }

        Query query = new Query(Criteria.where("ChannelId").is(channelId));
        AgentInfoDoc doc =  DbConfig.Instance.getMongoTemplate().findOne(query, AgentInfoDoc.class,DbConfig.DB_AgentInfo);
        if( doc == null )
        {
            return false;
        }

        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setUpdateTime(TimeHelper.Instance.get_cur_time());
        agentInfo.setAgentId( channelId );

        agentInfo.getGameMap().clear();

        //int agentId =  doc.getInteger("AgentId");

        List<GameInfo> gameInfos = doc.getGameInfos();
        if( gameInfos != null )
        {
            for( int i = 0; i < gameInfos.size();i++ )
            {
                GameInfo childDoc = gameInfos.get(i);
                AgentGameInfo agentGameInfo = new AgentGameInfo();
                agentGameInfo.setGameId( childDoc.getGameId() );
                agentGameInfo.setSort(childDoc.getSort());
                agentGameInfo.setHot(childDoc.getIsHot() == 1);
                agentInfo.getGameMap().put( agentGameInfo.getGameId(),agentGameInfo );
            }
        }

        agentInfos.put(channelId,agentInfo);

        return true;
    }

}
