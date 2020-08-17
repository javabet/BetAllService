package com.wisp.game.bet.world.dbConfig;

import com.mongodb.internal.client.model.FindOptions;
import com.wisp.game.bet.db.mongo.config.doc.AgentConfigDoc;
import com.wisp.game.bet.db.mongo.games.AgentGameConfigDoc;
import com.wisp.game.bet.world.db.DbGame;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import sun.management.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AgentConfig {

    private Map<Integer, AgentConfigDoc> m_agent_cfg;
    private Map<Integer,Map<Integer,AgentGameConfigDoc>>  m_game_cfg;

    public AgentConfig() {
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
