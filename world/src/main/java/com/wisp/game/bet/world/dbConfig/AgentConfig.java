package com.wisp.game.bet.world.dbConfig;

import com.wisp.game.bet.db.mongo.config.doc.AgentConfigDoc;
import com.wisp.game.bet.db.mongo.games.doc.AgentGameConfigDoc;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.world.db.DbGame;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AgentConfig {
    public static AgentConfig Instance;
    private Map<Integer, AgentConfigDoc> m_agent_cfg;
    private Map<Integer,Map<Integer,AgentGameConfigDoc>>  m_game_cfg;
    private double m_lastTime = 0;

    public AgentConfig() {
        Instance = this;
        m_game_cfg = new HashMap<>();
        m_agent_cfg = new HashMap<>();
    }

    public boolean load_agent()
    {
        Criteria criteria = Criteria.where("_id").exists(true);

        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC,"Index"));


        List<AgentConfigDoc> agentConfigDocList = DbGame.Instance.getMongoTemplate().findAll(AgentConfigDoc.class);
         synchronized (m_agent_cfg)
         {
             m_agent_cfg.clear();;
             for( AgentConfigDoc agentConfigDoc : agentConfigDocList )
             {
                 m_agent_cfg.put(agentConfigDoc.getAgentId(),agentConfigDoc);
             }
         }

        List<AgentGameConfigDoc> agentGameConfigDocList = DbGame.Instance.getMongoTemplate().findAll(AgentGameConfigDoc.class);
         synchronized (m_game_cfg)
         {
             m_game_cfg.clear();
             for(AgentGameConfigDoc agentGameConfigDoc : agentGameConfigDocList)
             {
                 if( !m_game_cfg.containsKey( agentGameConfigDoc.getGameId() ) )
                 {
                     m_game_cfg.put(agentGameConfigDoc.getAgentId(),new HashMap<Integer, AgentGameConfigDoc>());
                 }
                 m_game_cfg.get(agentGameConfigDoc.getAgentId()).put(agentGameConfigDoc.getGameId(),agentGameConfigDoc);
             }
         }


        return true;
    }

    public boolean is_share_agent(int agentId)
    {
        int cur_time_helper = TimeHelper.Instance.get_cur_time();
        if( cur_time_helper - m_lastTime > 30 )
        {
            m_lastTime = cur_time_helper;
            load_agent();
        }

        if( !m_agent_cfg.containsKey(agentId) )
        {
            return  false;
        }

        return m_agent_cfg.get(agentId).isShareServer();
    }

    public List<Integer> game_custom_list(int agentId,int gameId)
    {
        synchronized (m_game_cfg)
        {
            if( !m_game_cfg.containsKey(agentId))
            {
                return null;
            }

            return m_game_cfg.get(agentId).get(gameId).getCustomList();
        }
    }
}
