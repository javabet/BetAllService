package com.wisp.game.bet.logic.gameMgr;

import com.wisp.game.bet.logic.unit.BackstageManager;
import com.wisp.game.bet.logic.unit.ServerPeer;
import logic2world_protocols.Logic2WorldProtocol;
import org.springframework.stereotype.Component;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {

    public static GameManager Instance;

    private boolean m_is_shutdowning = false;
    private double m_force_time = 0;
    private double mCheckTime = 0;
    private  int m_gameid = 0;
    private  int m_gamever = 0;
    private RobotManager m_robotmgr;


    public GameManager() {
        Instance = this;
    }


    public boolean open()
    {
        return true;
    }

    public void heartbeat( double elapsed )
    {

    }

    public void serverdown()
    {

    }

    public void on_init_engine(int gameId,String game_ver)
    {
        m_gameid = 0;
        m_gamever = 100;

        m_robotmgr.set_gameid(gameId);
        game_regedit();
    }

    public int get_gameid()
    {
        return m_gameid;
    }

    public int get_gamever()
    {
        return  m_gamever;
    }



    public void game_regedit()
    {
        if( m_gameid <= 0 )
        {
            return;
        }

        ConcurrentHashMap<Integer, ServerPeer> world_map = BackstageManager.Instance.get_world_map();

        for( ServerPeer serverPeer : world_map.values() )
        {
            Logic2WorldProtocol.packetl2w_game_ready.Builder builder = Logic2WorldProtocol.packetl2w_game_ready.newBuilder();
            builder.setGameId(m_gameid);
            builder.setGameVer(m_gamever);
            serverPeer.send_msg(builder.build());
        }

    }

    public void notify_bot_state( boolean connected )
    {

    }

    public void prepare_shutdown()
    {

    }

    public boolean is_shutdowning()
    {
        return false;
    }
}
