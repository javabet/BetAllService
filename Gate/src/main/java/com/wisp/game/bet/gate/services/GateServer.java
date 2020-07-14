package com.wisp.game.bet.gate.services;

import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.share.netty.client.ClientHandler;
import com.wisp.game.share.netty.client.NettyClient;
import com.wisp.game.sshare.ServerBase;
import io.netty.channel.ChannelHandler;
import org.omg.CosNaming._BindingIteratorImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class GateServer extends ServerBase  {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    public SpringContextHolder springContextHolder;

    @Autowired
    public Environment environment;

    @Autowired
    public BackstageManager backstageManager;

    @Autowired
    public GatePeerManager gatePeerManager;

    @Autowired
    public ClientManager clientManager;

    protected void on_run()
    {
        double elapsed = 0;
        while (is_runing())
        {
            long cur_tm_ms = System.currentTimeMillis();
            gatePeerManager.heartbeat(elapsed);
            clientManager.heartbeat(elapsed);
            backstageManager.heartbeat(elapsed);

            elapsed = System.currentTimeMillis() - cur_tm_ms;

            if( elapsed  < 500 )
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception e)
                {
                    logger.error("thread sleep has error");
                }
            }
            else
            {
                logger.error("server_run longtime:" + elapsed);
            }
        }
    }



    public boolean on_init()
    {
        init_db();
        connect_monitor();

        return true;
    }

    private void init_db()
    {

    }



    public  <T extends NettyClient> T  create_peer(Class<T> cls,int remote_type )
    {
        ServerPeer serverPeer = new ServerPeer();
        serverPeer.init_peer( generate_id() ) ;
        serverPeer.set_remote_type(remote_type);
        return (T)serverPeer;
    }

    private void connect_monitor()
    {
        if( environment.containsProperty("cfg.monitor_ip") && environment.containsProperty("cfg.monitor_port") )
        {
            ServerPeer serverPeer = create_peer(ServerPeer.class,6);
            String host = environment.getProperty("cfg.monitor_ip","localhost");
            int port = environment.getProperty("cfg.monitor_port",Integer.class);
            serverPeer.connect(host,port);
        }
    }

    @Override
    public void on_exit() {

    }
}
