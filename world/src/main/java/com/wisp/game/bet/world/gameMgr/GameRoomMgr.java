package com.wisp.game.bet.world.gameMgr;

import com.mongodb.client.result.UpdateResult;
import com.wisp.game.bet.db.mongo.games.doc.GameRoomMgrDoc;
import com.wisp.game.bet.db.mongo.games.doc.GameRoomSetDoc;
import com.wisp.game.bet.world.config.MainGameVerConfig;
import com.wisp.game.bet.world.db.DbGame;
import com.wisp.game.bet.world.dbConfig.AgentConfig;
import com.wisp.game.bet.world.gameMgr.info.AgentRooms;
import com.wisp.game.bet.world.unit.ServersManager;
import com.wisp.game.bet.world.unit.WorldPeer;
import logic2world_protocols.Logic2WorldProtocol;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameRoomMgr {
    public static GameRoomMgr Instance;
    private Map<Integer,Map<Integer, AgentRooms>> agent_rooms;
    private  double m_elapsed = 0;

    public GameRoomMgr() {
        Instance = this;
        this.agent_rooms = new HashMap<>();
    }

    public void heartbeat(double elapsed)
    {
        m_elapsed -= elapsed;
        if( m_elapsed >= 0 )
        {
            return;
        }

        m_elapsed = 60 * 1000;

        load_room_data();
        check_open_room();
    }

    public void init_room(int agentId)
    {
        //如果是混服，则不需要
        if( AgentConfig.Instance.is_share_agent(agentId) )
        {
            return;
        }
        Map<Integer, GameEngineMgr.GameInfo> games =  GameEngineMgr.Instance.getGames();

        for( GameEngineMgr.GameInfo gameInfo : games.values() )
        {
            if( gameInfo.getServersMap().size() == 0 )
            {
                continue;
            }

            if( gameInfo.getGameId() != 5 )
            {
                continue;
            }

            if(  agent_rooms.containsKey(agentId) )
            {
                continue;
            }

            Criteria criteria = Criteria.where("AgentId").is(agentId).and("GameId").is(gameInfo.getGameId());
            GameRoomMgrDoc gameRoomMgrDoc =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),GameRoomMgrDoc.class);
            if( gameRoomMgrDoc != null )
            {
                //房间已存在，激活房间
                Logic2WorldProtocol.packetw2l_room_open.Builder builder = Logic2WorldProtocol.packetw2l_room_open.newBuilder();
                builder.setOpen(true);
                builder.setAgentId(agentId);
                for(int serverId : gameInfo.getServersMap().keySet() )
                {
                    WorldPeer worldPeer = ServersManager.Instance.find_server(serverId);
                    if( worldPeer != null)
                    {
                        worldPeer.send_msg(builder);
                    }
                }
            }
            else
            {
                //代理未创建房间,找一个人少的服务器去创建房间
                criteria = Criteria.where("AgentId").is(agentId).and("GameId").is(gameInfo.getGameId()).and("Type").is(4);

                GameRoomSetDoc gameRoomSetDoc = DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria), GameRoomSetDoc.class);
                if( gameRoomSetDoc == null )
                {
                    int serverid = GameEngineMgr.Instance.get_server_id(gameInfo.getGameId());
                    if( serverid == 0 )
                    {
                        continue;
                    }

                    criteria = Criteria.where("AgentId").is(agentId).and("GameId").is(gameInfo).and("Type").is(4).and("ServerId").exists(false);

                    Update update = new Update();
                    update.set("ServerId",serverid);
                    UpdateResult updateResult =  DbGame.Instance.getMongoTemplate().updateFirst(Query.query(criteria),update,GameRoomSetDoc.class);

                    if( updateResult.getMatchedCount() == 0 )
                    {
                        Logic2WorldProtocol.packetw2l_set_room.Builder builder = Logic2WorldProtocol.packetw2l_set_room.newBuilder();
                        builder.setAgentId(agentId);
                        builder.setRoomId(0);
                        WorldPeer worldPeer = ServersManager.Instance.find_server(serverid);
                        if( worldPeer != null )
                        {
                            worldPeer.send_msg(builder);
                        }
                    }

                }
            }
        }
    }

    public void set_game_room( int agentId,int gameId,int roomId )
    {
        if( AgentConfig.Instance.is_share_agent(agentId))
        {
            return;
        }

        int serverId = 0;

        Criteria criteria = Criteria.where("AgentId").is(agentId).and("GameId").is(gameId).and("RoomId").is(roomId);
        GameRoomSetDoc gameRoomSetDoc =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),GameRoomSetDoc.class);
        if( gameRoomSetDoc == null )
        {
            return;
        }

        if( gameRoomSetDoc.getType() == 1 ) //添加房间
        {
            init_room(agentId);

            criteria = Criteria.where("AgentId").is(agentId).and("GameId").is(gameId).and("Type").is(4);
            GameRoomSetDoc gameRoomSetDoc2 = DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),GameRoomSetDoc.class);
            if(gameRoomSetDoc2 != null)
            {
                serverId = gameRoomSetDoc2.getServerId();
            }

            Map<Integer,AgentRooms> agentRoomsMap = agent_rooms.get(gameId);
            if( !agentRoomsMap.containsKey(agentId) )
            {
                criteria = Criteria.where("AgentId").is(agentId).and("GameId").is(gameId);
                GameRoomMgrDoc gameRoomMgrDoc =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),GameRoomMgrDoc.class);
                if( gameRoomMgrDoc != null )
                {
                    serverId = gameRoomMgrDoc.getServerId();
                }
                if(serverId == 0)
                {
                    GameEngineMgr.GameInfoStruct gameInfoStruct = GameEngineMgr.Instance.get_game_info_struct(gameId);
                    if( gameInfoStruct != null )
                    {
                        serverId = gameInfoStruct.serverId;
                    }
                }
            }
            else
            {
                AgentRooms agentRooms = agentRoomsMap.get(agentId);
                int roomCount = Integer.MAX_VALUE;
                Map<Integer,Integer> serverRoomCount = new HashMap<>();
                for( GameRoomMgrDoc gameRoomMgrDoc : agentRooms.getRooms().values() )
                {
                    if( !serverRoomCount.containsKey(gameRoomMgrDoc.getServerId()) )
                    {
                        serverRoomCount.put(gameRoomMgrDoc.getServerId(),0);
                    }
                    int oldValue = serverRoomCount.get(gameRoomMgrDoc.getServerId());
                    serverRoomCount.put(gameRoomMgrDoc.getServerId(),oldValue + 1);
                }

                for(int serverIdKey : serverRoomCount.keySet())
                {
                    int cnt =  serverRoomCount.get(serverIdKey);
                    if( cnt < roomCount )
                    {
                        roomCount = cnt;
                        serverId = serverIdKey;
                    }
                }
            }
        }
        else if(gameRoomSetDoc.getType() == 2) //关闭房间
        {

            if( agent_rooms.containsKey(gameId) && agent_rooms.get(gameId).containsKey(agentId) )
            {
                GameRoomMgrDoc gameRoomMgrDoc = agent_rooms.get(gameId).get(agentId).get_room(roomId);
                serverId = gameRoomMgrDoc.getServerId();
            }

            if(serverId == 0)
            {
                //房间未上线，直接设置为离线
                criteria = Criteria.where("AgentId").is(agentId).and("GameId").is(gameId).is("RoomId").is(roomId);
                Update update = new Update();
                update.set("IsOpen",false);
                DbGame.Instance.getMongoTemplate().updateFirst(Query.query(criteria),update,GameRoomMgrDoc.class);
                DbGame.Instance.getMongoTemplate().findAndRemove(Query.query(criteria),GameRoomSetDoc.class);
            }
        }

        if( serverId != 0 )
        {
            Logic2WorldProtocol.packetw2l_set_room.Builder builder =  Logic2WorldProtocol.packetw2l_set_room.newBuilder();
            builder.setAgentId(agentId);
            builder.setRoomId(roomId);
            WorldPeer worldPeer = ServersManager.Instance.find_server(serverId);
            if( worldPeer != null )
            {
                worldPeer.send_msg(builder);
            }
        }
    }


    private void load_room_data()
    {
        synchronized (agent_rooms)
        {
            agent_rooms.clear();

            Criteria criteria = Criteria.where("Status").is(1);
            List<GameRoomMgrDoc> gameRoomMgrDocList =  DbGame.Instance.getMongoTemplate().find(Query.query(criteria),GameRoomMgrDoc.class);

            for( GameRoomMgrDoc gameRoomMgrDoc : gameRoomMgrDocList )
            {
                if( !agent_rooms.containsKey(gameRoomMgrDoc.getGameId()) )
                {
                    agent_rooms.put(gameRoomMgrDoc.getGameId(),new HashMap<>());
                }
               Map<Integer, AgentRooms> agentRoomsMap =   agent_rooms.get(gameRoomMgrDoc.getGameId());

               if(  !agentRoomsMap.containsKey(gameRoomMgrDoc.getAgentId()) )
               {
                   AgentRooms agentRooms = new AgentRooms();
                   agentRooms.init(gameRoomMgrDoc.getGameId(),gameRoomMgrDoc.getAgentId());
                   agentRoomsMap.put(gameRoomMgrDoc.getAgentId(),agentRooms);
               }

               agentRoomsMap.get(gameRoomMgrDoc.getAgentId()).add_room(gameRoomMgrDoc);
            }
        }
    }



    public void check_open_room()
    {
        //只有在主服务器才使用,即有多个world服务器时才需要的额外处理
        Criteria criteria = Criteria.where("Type").is(5);
        List<GameRoomSetDoc> gameRoomSetDocList =  DbGame.Instance.getMongoTemplate().find(Query.query(criteria),GameRoomSetDoc.class);

        Map<Integer,List<Integer>> serverList = new HashMap<>();

        if( gameRoomSetDocList != null && gameRoomSetDocList.size() > 0 )
        {
            for( GameRoomSetDoc gameRoomSetDoc : gameRoomSetDocList)
            {
                //打开房间
                criteria = Criteria.where("AgentId").is(gameRoomSetDoc.getAgentId()).and("GameId").is(gameRoomSetDoc.getGameId())
                        .and("RoomId").is(gameRoomSetDoc.getRoomId()).and("Status").is(0).and("IsOpen").is(false);

                Update update = new Update();
                update.set("IsOpen",true);
                FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options();
                findAndModifyOptions.returnNew(true);
                GameRoomMgrDoc gameRoomMgrDoc =  DbGame.Instance.getMongoTemplate().findAndModify(Query.query(criteria),update,findAndModifyOptions,GameRoomMgrDoc.class);
                if( gameRoomMgrDoc != null && gameRoomMgrDoc.getServerId() > 0 )
                {
                   if( !serverList.containsKey(gameRoomMgrDoc.getServerId()) )
                   {
                       serverList.put(gameRoomMgrDoc.getServerId(),new ArrayList<>());
                   }
                   if( !serverList.get(gameRoomMgrDoc.getServerId()).contains(gameRoomSetDoc.getAgentId()) )
                   {
                       serverList.get(gameRoomMgrDoc.getServerId()).add(gameRoomSetDoc.getAgentId());
                   }

                   criteria = Criteria.where("AgentId").is(gameRoomSetDoc.getAgentId()).and("GameId").is(gameRoomSetDoc.getGameId())
                           .and("RooomId").is(gameRoomSetDoc.getRoomId()).and("RoomId").is(gameRoomSetDoc.getRoomId()).and("Type").is(5);

                   DbGame.Instance.getMongoTemplate().remove(Query.query(criteria),GameRoomSetDoc.class);
                }
            }
        }


        for(int serverId:serverList.keySet())
        {
            List<Integer> agentList = serverList.get(serverId);

            for(int agentId : agentList)
            {
                Logic2WorldProtocol.packetw2l_room_open.Builder builder = Logic2WorldProtocol.packetw2l_room_open.newBuilder();
                builder.setAgentId(agentId);
                builder.setOpen(true);
                WorldPeer worldPeer = ServersManager.Instance.find_server(serverId);
                if( worldPeer != null )
                {
                    worldPeer.send_msg(builder);
                }
            }
        }

    }


    public List<GameRoomMgrDoc> get_room_list(int gameId,int agentId)
    {
        List<GameRoomMgrDoc> list = new ArrayList<>();

        MainGameVerConfig.MainGameVerConfigData cfg =  MainGameVerConfig.GetInstnace().GetData(gameId);
        if( cfg == null || cfg.getmGameType() != 1 )
        {
            return list;
        }

        Map<Integer,AgentRooms> agentRoomsMap = agent_rooms.get(gameId);
        if( agentRoomsMap == null )
        {
            return list;
        }

        for(AgentRooms agentRooms : agentRoomsMap.values())
        {
            if( agentRooms.getAgentId() == 0 || agentRooms.getAgentId() == agentId )
            {
                list.addAll(agentRooms.getRooms().values());
            }

        }

        return list;
    }


}
