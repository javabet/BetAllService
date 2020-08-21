package com.wisp.game.bet.games.baccarat.logic;

import com.wisp.game.bet.db.mongo.games.doc.GameRoomMgrDoc;
import com.wisp.game.bet.games.share.RoomMgr;
import com.wisp.game.bet.games.share.config.RMConfig;
import com.wisp.game.bet.games.share.config.RMConfigData;
import com.wisp.game.bet.games.share.config.RMStockConfig;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.core.random.RandomHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LogicLobby {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private boolean m_init;					//是否已初始化
    private int m_max_player;				//最大人数限制

    private Map<Integer,LogicRoom>  m_rooms;
    private Map<Integer,LogicPlayer> m_all_players;

    @Autowired
    private RMStockConfig rmStockConfig;
    @Autowired
    private RMConfig rmConfig;

    private int m_check_cache;
    public LogicLobby() {
        m_rooms = new ConcurrentHashMap<>();
        m_all_players = new ConcurrentHashMap<>();

        logger.info("the lobby construct");
    }

    public void set_room(int agentid, int roomid)
    {
        int type = RoomMgr.Instance.set_room(agentid, roomid);
        //刷新库存配置
        RoomMgr.Instance.set_stock_cfg(RMStockConfig.Instance);
        if (type == 1)
        {
            //新增房间
            GameRoomMgrDoc obj =  RoomMgr.Instance.get_room(roomid);
            if (obj != null)
            {
                if (obj.isOpen())
                {
                    LogicRoom logicRoom = m_rooms.get(obj.getRoomId());
                    if (logicRoom == null)
                    {
                        RMConfigData cfgData = RMConfig.Instance.GetData(obj.getRoomId());
                        LogicRoom room =  new LogicRoom(cfgData, this);
                        m_rooms.put(obj.getRoomId(), room);
                    }
                }
            }
        }
        else if (type == 4)
        {
            //新增代理初始化的房间
            Map<Integer,GameRoomMgrDoc> rooms = RoomMgr.Instance.get_rooms();
            for(GameRoomMgrDoc obj : rooms.values())
            {
                if (obj.isOpen() && obj.getAgentId() == agentid)
                {
                    LogicRoom logicRoom = m_rooms.get(obj.getRoomId());
                    if (logicRoom == null)
                    {
                        RMConfigData cfgData = RMConfig.Instance.GetData(obj.getRoomId());
                        LogicRoom room = new LogicRoom(cfgData,this);
                        m_rooms.put(obj.getRoomId(), room);
                    }
                }
            }
        }
        GameManager.Instance.reflush_game_room();
    }

    public void open_room(int agentid, boolean open)
    {
        if (RoomMgr.Instance.open_room(agentid, open))
        {
            Map<Integer,GameRoomMgrDoc> mgrRooms = RoomMgr.Instance.get_rooms();
            //刷新库存配置
            RoomMgr.Instance.set_stock_cfg(RMStockConfig.Instance);
            for (GameRoomMgrDoc gameRoomMgrDoc : mgrRooms.values())
            {
                if (!gameRoomMgrDoc.isOpen())
                    continue;

                LogicRoom logicRoom = m_rooms.get(gameRoomMgrDoc.getRoomId());
                if (logicRoom == null)
                {
                    RMConfigData cfgData = RMConfig.Instance.GetData(gameRoomMgrDoc.getRoomId());
                    LogicRoom room = new LogicRoom(cfgData,this);
                    m_rooms.put(gameRoomMgrDoc.getRoomId(), room);
                }
            }
            GameManager.Instance.reflush_game_room();
        }
    }

    public void init_game(int maxCount)
    {
        m_max_player = maxCount;

        //初始化房间管理
        RoomMgr.Instance.init(rmConfig,null);

        //创建混服房间
        if(GameManager.Instance.open_room())
        {
            RoomMgr.Instance.register_room(0);
            Map<Integer, GameRoomMgrDoc> mgrRooms =  RoomMgr.Instance.get_rooms();

            RoomMgr.Instance.set_stock_cfg(rmStockConfig);
            for( GameRoomMgrDoc gameRoomMgrDoc : mgrRooms.values())
            {
                if( !gameRoomMgrDoc.isOpen() )
                {
                    continue;
                }
                RMConfigData rmConfigData =  RMConfig.Instance.GetData(gameRoomMgrDoc.getRoomId());
                if( rmConfigData == null )
                {
                    logger.warn(" the rmConfig is not exist: " + gameRoomMgrDoc.getRoomId());
                    continue;
                }
                LogicRoom logicRoom = new LogicRoom( rmConfigData,this );
                m_rooms.put(gameRoomMgrDoc.getRoomId(), logicRoom );
            }
        }

        m_init = true;
    }

    public  void release_game()
    {
        if(!m_init)return;

        for ( LogicPlayer logicPlayer : m_all_players.values() )
        {
            logicPlayer.release();
        }
        m_all_players.clear();

        m_rooms.clear();
    }


    public void heartbeat( double elapsed )
    {
        if(!m_init)return;

        for (LogicRoom logicRoom : m_rooms.values() )
        {
            logicRoom.heartbeat(elapsed);
        }
        m_check_cache += elapsed;
        if(m_check_cache > 1000 * 3600)     //TODO wisp,一分钟后即可关闭，测试阶段，修改为一个小时
        {
            //save_cache();
            m_check_cache = 0;
            Iterator<LogicRoom> iterator =  m_rooms.values().iterator();
            while (iterator.hasNext())
            {
                 LogicRoom logicRoom  = iterator.next();
                if( logicRoom.get_room_cfg().getmIsOpen() == false || logicRoom.get_player_count() == 0 )
                {
                    iterator.remove();
                }
            }
        }
    }

    public boolean player_enter_game(GamePlayer gamePlayer, int roomId)
    {
        if(!m_init)return false;

        if(m_all_players.size()>= m_max_player)
            return false;

        if(m_all_players.containsKey(gamePlayer.get_playerid()))
            return false;

        LogicPlayer lp = new LogicPlayer();
        lp.setGamePlayer(gamePlayer);
        gamePlayer.setPhandler(lp);
        lp.enter_game(this);
        //lp.getIGamePlayer()->gameLog(true, lp->get_gold());
        m_all_players.put( gamePlayer.get_playerid(),lp );//std::make_pair(igplayer->get_playerid(), lp));

        //自动进入房间
        if (roomId != -1)
        {
            int ret = enter_room(gamePlayer.get_playerid(), roomId);
            if (ret != 1)
            {
                player_leave_game(gamePlayer.get_playerid());
                return false;
            }
        }


        return true;
    }

    public void player_leave_game(int playerid)
    {
        if(!m_init)return;

        LogicPlayer it = m_all_players.get(playerid);
        if(it == null)
            return;

        //it.getIGamePlayer()->gameLog(false, it->second->get_gold());
        it.leave_room();
        m_all_players.remove(playerid);
    }


    public int player_join_friend_game(GamePlayer igplayer, int friendid)
    {
        if(player_enter_game(igplayer,0))
        {
            LogicPlayer it = m_all_players.get(friendid);
            if(it == null)
                return 2;

            LogicRoom room = it.get_room();
            if(room != null)
                return enter_room(igplayer.get_playerid(),room.get_room_id());

            return 1;
        }

        return 2;
    }

    public int enter_room(int pid, int rid)
    {
        LogicPlayer it = m_all_players.get(pid);
        if(it == null)
            return 2;										//返回失败

        LogicRoom room = m_rooms.get(rid);
        if(room == null)
            return 2;										//返回失败

        return room.enter_room(it);
    }

    public void leave_room(int pid)
    {
        LogicPlayer it = m_all_players.get(pid);
        if(it == null)
            return;

        it.leave_room();
    }

    public Map<Integer, LogicRoom> get_rooms()
    {
        return m_rooms;
    }


    public LogicPlayer get_player(int pid)
    {
        LogicPlayer it = m_all_players.get(pid);
        if(it != null)
        {
            return it;
        }
        return null;
    }

    //返回一个机器人 返回的机器人未进入房间？
    public void response_robot(int playerid, int tag)
    {
        LogicPlayer it = m_all_players.get(playerid);
        if(it != null)
        {
            tag = tag % 100;
            enter_room(playerid, tag);
        }
        else
        {
            //std::cout << "logic_lobby::response_robot()  player is null" << std::endl;
        }

    }

    public void deduct_stock(int room_id, int gold)
    {
        LogicRoom logicRoom = m_rooms.get(room_id);
        if(logicRoom != null)
        {
            logicRoom.deduct_stock(gold);
        }
    }
}
