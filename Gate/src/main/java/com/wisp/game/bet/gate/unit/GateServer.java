package com.wisp.game.bet.gate.unit;

import com.wisp.game.bet.gate.db.DbAccount;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import com.wisp.game.bet.sshare.ServerBase;
import io.netty.channel.ChannelHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * 使用
 */
@Component
public final class GateServer extends ServerBase {
    public static GateServer Instance;

    @Autowired
    public SpringContextHolder springContextHolder;

    @Autowired
    public Environment environment;

    @Autowired
    public RequestMessageRegister requestMessageRegister;

    public GateServer() {
        Instance = this;
    }

    protected ChannelHandler getChannelHandler() {
        return new GateServerChannelHandler();
    }

    public boolean on_init() {
        init_db();
        connect_monitor();
        return true;
    }

    protected void on_run() {
        double elapsed = 0;
        while (is_runing())
        {
            long cur_tm_ms = System.currentTimeMillis();

            ClientManager.Instance.heartbeat(elapsed);
            BackstageManager.Instance.heartbeat(elapsed);

            elapsed = System.currentTimeMillis() - cur_tm_ms;

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

    public void on_exit() {
        ClientManager.Instance.clear();
        BackstageManager.Instance.clear();;
    }

    public ServerPeer create_peer(int remote_type)
    {
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

    private boolean init_db()
    {
        if( environment.containsProperty("cfg.accountdb_url") && environment.containsProperty("cfg.accountdb_name") )
        {
            DbAccount.Instance.init_db(environment.getProperty("cfg.accountdb_url"),environment.getProperty("cfg.accountdb_name"));
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
}
