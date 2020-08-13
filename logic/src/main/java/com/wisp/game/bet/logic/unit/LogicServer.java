package com.wisp.game.bet.logic.unit;

import com.wisp.game.bet.GameConfig.LogicConfig;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.logic.db.MongoDbService;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import com.wisp.game.bet.sshare.ServerBase;
import io.netty.channel.ChannelHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LogicServer extends ServerBase {
    public static LogicServer Instance;

    @Autowired
    public SpringContextHolder springContextHolder;

    @Autowired
    public Environment environment;

    @Autowired
    public RequestMessageRegister requestMessageRegister;

    @Autowired
    public MongoDbService dbControllerService;

    @Autowired
    public LogicConfig logicConfig;

    //这里不能给其赋值，需要加
    private IGameEngine gameEngine;

    public LogicServer() {
        Instance = this;
    }

    @Override
    protected ChannelHandler getChannelHandler() {
        return null;
    }

    public ServerPeer create_peer(int remote_type) {
        ServerPeer serverPeer = new ServerPeer();
        serverPeer.set_remote_type( remote_type );
        serverPeer.set_id(generate_id());
        boolean flag = BackstageManager.Instance.add_obj(serverPeer.get_id(),serverPeer);
        if( !flag )
        {
            logger.error("add obj has error:" + serverPeer.get_id());
        }

        return serverPeer;
    }

    @Override
    public boolean on_init() {

        this.connect_monitor();
        return true;
    }

    @Override
    protected void on_run() {

        double elapsed = 0;

        while ( is_runing() )
        {

            long cur_tm_ms = System.currentTimeMillis();

            ServersManager.Instance.heartbeat(elapsed);
            BackstageManager.Instance.heartbeat(elapsed);
            GamePlayerMgr.Instance.heartbeat(elapsed);
            GameManager.Instance.heartbeat(elapsed);

            elapsed = System.currentTimeMillis() - cur_tm_ms;

            if(  gameEngine != null && GamePlayerMgr.Instance.is_closing() )
            {
                gameEngine.heartbeat(elapsed);
            }

            if( elapsed < 500 )
            {
                try
                {
                    Thread.sleep(50);
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

    public void init_game_engine()
    {
        GameManager.Instance.open();
        if( gameEngine == null )
        {
            gameEngine = GameManager.Instance.get_game_engine();
            if( gameEngine != null )
            {
                if(gameEngine.init_engine())
                {
                    gameEngine.exit_engine();
                    gameEngine = null;
                }
                else if( ServersManager.Instance.get_bot() != null )
                {
                    gameEngine.notify_bot_state(true);
                }
            }
        }
    }

    @Override
    public void on_exit()
    {
        if( gameEngine != null )
        {
            gameEngine.exit_engine();
            gameEngine = null;
        }

        ServersManager.Instance.clear();
        BackstageManager.Instance.clear();
    }
}
