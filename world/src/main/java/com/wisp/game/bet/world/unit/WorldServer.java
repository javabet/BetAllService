package com.wisp.game.bet.world.unit;

import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.db.*;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import com.wisp.game.bet.sshare.ServerBase;
import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.gameMgr.GameRoomMgr;
import io.netty.channel.ChannelHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class WorldServer extends ServerBase {
    public static WorldServer Instance;
    private boolean m_is_main = false;

    @Autowired
    public SpringContextHolder springContextHolder;

    @Autowired
    public Environment environment;

    @Autowired
    public RequestMessageRegister requestMessageRegister;

    @Autowired
    public MongoDbService dbControllerService;

    @Autowired
    public TimeHelper timeHelper;

    public WorldServer() {
        Instance = this;
    }

    @Override
    protected ChannelHandler getChannelHandler() {
        return new WorldServerChannelHandler();
    }

    @Override
    public boolean on_init() {
        connect_monitor();
        return true;
    }

    @Override
    protected void on_run() {
        double elapsed = 0;
        while (is_runing())
        {
            long cur_tm_ms = System.currentTimeMillis();

            GameEngineMgr.Instance.heartbeat(elapsed);
            GameRoomMgr.Instance.heartbeat(elapsed);
            ServersManager.Instance.heartbeat(elapsed);
            BackstageManager.Instance.heartbeat(elapsed);
            GamePlayerMgr.Instance.heartbeat(elapsed);

            elapsed = System.currentTimeMillis() - cur_tm_ms;

            if( elapsed < 500 )
            {
                try
                {
                    Thread.sleep(10);
                    elapsed = System.currentTimeMillis() - cur_tm_ms;
                }
                catch (Exception ex)
                {
                    logger.error("GateServer has error,the run has error");
                }

            }
            else
            {
                logger.warn("server_run longtime:" + elapsed);
            }
        }
    }

    @Override
    public void on_exit() {

        //GameEngineMgr.Instance.cl

        ServersManager.Instance.clear();
    }

    public ServerPeer create_peer(int remote_type)
    {
        ServerPeer serverPeer = new ServerPeer(generate_id(),remote_type);
        return serverPeer;
    }

    private boolean init_db()
    {
        if( environment.containsProperty("cfg.is_main") )
        {
            m_is_main = environment.getProperty("cfg.is_main",Integer.class) == 1;
        }

        return true;
    }

    private void connect_monitor()
    {
        if( environment.containsProperty("cfg.monitor_ip") && environment.containsProperty("cfg.monitor_port") )
        {
            String tarip = environment.getProperty("cfg.monitor_ip");
            int port = environment.getProperty("cfg.monitor_port",Integer.class);

            ServerPeer serverPeer = create_peer( server_protocols.ServerBase.e_server_type.e_st_monitor_VALUE);
            serverPeer.connect(tarip,port);
        }
    }

    public boolean is_main()
    {
        return m_is_main;
    }
}
