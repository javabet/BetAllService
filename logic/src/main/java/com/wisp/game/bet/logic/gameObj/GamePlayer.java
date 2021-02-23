package com.wisp.game.bet.logic.gameObj;

import com.google.protobuf.Message;
import com.wisp.game.bet.logic.config.MainRobotBaseConfig;
import com.wisp.game.bet.logic.config.MainRobotTypeConfig;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.sshare.IGamePHandler;
import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.bet.logic.unit.BackstageManager;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.logic.unit.ServersManager;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import com.wisp.game.bet.share.utils.SessionHelper;
import com.wisp.game.core.random.RandomHandler;
import logic2world_protocols.Logic2WorldProtocol;
import msg_type_def.MsgTypeDef;
import org.springframework.core.io.Resource;

import java.util.List;

public class GamePlayer {

    private IGamePHandler phandler;

    private int m_sessionId;
    private double m_check_kick = 0;
    private double m_check_life = 0;
    private e_player_state m_state;

    public int PhotoFrame;
    public int VIPLevel;
    public int world_id = -1;
    public boolean IsRobot = false;
    public int PlayerID = 0;
    public int Ticket;
    public LogicPeer GatePeer;
    public LogicPeer m_botPeer;
    public int Gold;
    public String NickName;
    public String IconCustom;
    public int Sex;
    public String channelId;
    public int CreateTime;
    public int Recharged;
    public int Privilege;
    public int RoomCard;

    //机器人性格 1急躁型 2持续型 3抄底型
    public int m_robot_type;
    public boolean m_banker_robot;
    public int tag;
    private  int m_life;


    public GamePlayer() {
        Resource resource;
    }

    public void heartbeat( double elapsed )
    {
        if( m_state == e_player_state.e_ps_disconnect )
        {
            m_check_kick += elapsed;
            if( m_check_kick >= 350 * 1000 )            //大于300s 以上，踢出游戏
            {
                IGameEngine gameEngine = GameManager.Instance.get_game_engine();
                if( gameEngine != null )
                {
                    boolean can_del = true;
                    if(!IsRobot && gameEngine.player_leave_game(PlayerID))
                    {
                        can_del = false;
                    }

                    if( can_del )
                    {
                        GamePlayerMgr.Instance.del_player(PlayerID);
                        m_check_kick = 0;
                    }

                }
            }
        }


        if(IsRobot)
        {
            m_check_life += elapsed;
            //TODO wisp
        }
    }

    public boolean leave_game( boolean shutdown )
    {
        IGameEngine gameEngine = GameManager.Instance.get_game_engine();
        if( gameEngine != null )
        {
            if(!shutdown && IsRobot && !gameEngine.player_leave_game(PlayerID))
            {
                return false;
            }
        }

        if(IsRobot)
        {
            if( m_sessionId != 0 && m_botPeer != null )
            {
                Logic2WorldProtocol.packetl2w_player_logout_result.Builder logoutResultBuilder = Logic2WorldProtocol.packetl2w_player_logout_result.newBuilder();
                logoutResultBuilder.setPlayerid(PlayerID);
                logoutResultBuilder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);
                m_botPeer.send_msg( logoutResultBuilder );
            }
        }
        else
        {
            ServerPeer serverPeer =  BackstageManager.Instance.get_world_byid(world_id);
            if( serverPeer != null )
            {
                //离开时，同步消息给所在的世界服
                Logic2WorldProtocol.packetl2w_player_activity_change.Builder builder = Logic2WorldProtocol.packetl2w_player_activity_change.newBuilder();
                serverPeer.send_msg(builder);
                //TODO wisp

                Logic2WorldProtocol.packetl2w_player_logout_result.Builder logoutResultBuilder = Logic2WorldProtocol.packetl2w_player_logout_result.newBuilder();
                logoutResultBuilder.setPlayerid(PlayerID);
                logoutResultBuilder.setShutdown(shutdown);
                logoutResultBuilder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);
                serverPeer.send_msg(logoutResultBuilder);
            }
        }

        return  true;
    }

    public int get_sessionid()
    {
        return m_sessionId;
    }

    public void set_sessionid(int sessionId)
    {
        this.m_sessionId = sessionId;
    }

    public e_player_state get_state()
    {
        return this.m_state;
    }

    public void set_state(e_player_state eps)
    {
        this.m_state = eps;

        //TODO wisp
    }

    public void reset_gate()
    {
        if( m_sessionId <= 0 || is_robot() )
        {
            return;
        }

        GatePeer = ServersManager.Instance.find_server(SessionHelper.get_gateid(m_sessionId));
    }



    public void set_roomid(int roomId)
    {

    }



    public void reset_robot_life()
    {

    }

    public int get_playerid()
    {
        return PlayerID;
    }

    public  int get_worldid()
    {
        return world_id;
    }

    public String get_channelid()
    {
        return channelId;
    }

    public String get_nickname()
    {
        return NickName;
    }

    public String get_icon_custom()
    {
        return IconCustom;
    }

    public boolean change_gold(int cgold)
    {
        if( -cgold  > Gold)
        {
            return false;
        }

        Gold += cgold;

        if( !is_robot() )
        {
            ServerPeer serverPeer = BackstageManager.Instance.get_world_byid(world_id);
            if( serverPeer == null )
            {
                return false;
            }

            Logic2WorldProtocol.packetw2l_change_player_property.Builder builder = Logic2WorldProtocol.packetw2l_change_player_property.newBuilder();
            builder.setPlayerid(get_playerid());
            builder.getChangeInfoBuilder().setGold(cgold);

            serverPeer.send_msg(builder);
        }

        return true;
    }

    public int send_msg_to_client( Message.Builder builder)
    {
        return send_msg_to_client(builder.build());
    }

    public int send_msg_to_client(Message msg)
    {
        int packet_id = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return send_msg_to_client( packet_id,msg);
    }

    public int send_msg_to_client(int packet_id, Message msg)
    {
        if( m_sessionId == 0 )
        {
            return  -1;
        }

        if( is_robot() )
        {
            LogicPeer bot =  ServersManager.Instance.get_bot();
            if( bot != null )
            {
                //TODO wisp ,这里好像有问题，这里应该是需要修改的session
                return bot.send_msg_to_client(PlayerID,packet_id,msg);
                //return bot.send_msg_to_client(m_sessionId,packet_id,msg);
            }
            return -1;
        }
        else
        {
            if( GatePeer == null )
            {
                return -1;
            }

            return GatePeer.send_msg_to_client(m_sessionId,packet_id,msg);
        }
    }

    public void game_broadcast( int name_type)
    {
        //do nothing,可以不需要的功能is_robot
    }

    public boolean need_release()
    {
        MainRobotBaseConfig.MainRobotBaseConfigData bot_cfg = MainRobotBaseConfig.GetInstnace().GetData(GameManager.Instance.get_gameid());

        if (bot_cfg != null && Gold >= bot_cfg.getmLeaveGold() && !is_banker_robot())
            return true;

        return (m_life > 0 && m_check_life > m_life);
    }

    public int robot_exit_gold()
    {
        MainRobotTypeConfig.MainRobotTypeConfigData data = MainRobotTypeConfig.GetInstnace().GetData(m_robot_type);
        if(data != null )
        {
            return data.getmLeaveGold();
        }

        return 5000;
    }


    public boolean is_robot()
    {
        return IsRobot;
    }

    public boolean is_banker_robot()
    {
        return m_banker_robot;
    }

    public void gameBroadcast( String msg )
    {
        ServerPeer serverPeer =  BackstageManager.Instance.get_world_byid(world_id);
        if( serverPeer == null )
        {
            return;
        }

        Logic2WorldProtocol.packetl2w_game_broadcast.Builder builder = Logic2WorldProtocol.packetl2w_game_broadcast.newBuilder();
        builder.setGameMsg(msg);
        serverPeer.send_msg(builder);
    }


    public double get_bet_interval(boolean first)
    {
        MainRobotTypeConfig.MainRobotTypeConfigData data = MainRobotTypeConfig.GetInstnace().GetData(m_robot_type);
        if(data != null)
        {
            if(first)
                return RandomHandler.Instance.getRandomValue(data.getmBeginTime().get(0), data.getmBeginTime().get(0) + 1);
		else
            return RandomHandler.Instance.getRandomValue(data.getmInterval().get(0),data.getmInterval().get(1) + 1 );
        }

        return 0.0;
    }

    public int get_bet_count(List<Integer> bet_list, int logic_gold)
    {
        int index = 0;
        int oldGold = logic_gold;
        MainRobotTypeConfig.MainRobotTypeConfigData data = MainRobotTypeConfig.GetInstnace().GetData(m_robot_type);
        if(data != null)
        {
            double r = RandomHandler.Instance.getRandomValue(data.getmBetRate().get(0), data.getmBetRate().get(1));
            logic_gold *= r;
            //根据下注筹码列表取整
            for(int i = 0; i < bet_list.size(); i++)
            {
                if(logic_gold >= bet_list.get(i))
                    index = i;
            }

            logic_gold -= (logic_gold % bet_list.get(index));
            if(logic_gold <= 0 && oldGold >= bet_list.get(0))
                logic_gold  = bet_list.get(0);

            return logic_gold;
        }
        return 0;
    }

    public boolean change_ticket(int cticket)
    {
        return true;
    }

    public void reset()
    {

    }

    public int get_recharged()
    {
        return Recharged;
    }

    public int get_profit()
    {
        return 0;
    }


    public IGamePHandler getPhandler() {
        return phandler;
    }

    public void setPhandler(IGamePHandler phandler) {
        this.phandler = phandler;
    }
}
