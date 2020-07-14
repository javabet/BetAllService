package com.wisp.game.bet.monitor.unit;

import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.share.common.EnableProcessinfo;
import com.wisp.game.share.netty.infos.e_peer_state;
import com.wisp.game.share.netty.PeerTcp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server_protocols.ServerBase;

public class MonitorPeer extends PeerTcp {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final static int  TIME_OUT_COUNT = 30000;

    private double m_check_time;
    private ServerManager serverManager;
    public MonitorPeer() {
        reset_time();
    }

    public int get_type()
    {
        return ServerBase.e_server_type.e_st_monitor.getNumber();
    }

    public void heartbeat(double elapsed)
    {
        if( check_timeout() )
        {
           if( serverManager == null )
           {
               serverManager = SpringContextHolder.getBean(ServerManager.class);
           }
           ServerBase.server_info.Builder  server_info = serverManager.get_server_info(get_id());
           if( server_info != null )
           {
                logger.error("monitor_peer timeout id:" + get_id() + " server_type:" +  server_info.getServerType() + " server_ip:" + server_info.getServerIp() + " server_port:" + server_info.getServerPort() );
           }
           else
           {
               logger.error("monitor_peer timeout id:" + get_id() + " can't find server_info!");
           }

            reset_time();
        }
    }

    public void reset_time()
    {
        m_check_time = EnableProcessinfo.get_tick_count();
    }

    private boolean check_timeout()
    {
        if( get_state() != e_peer_state.e_ps_connected)
        {
            return false;
        }

        return ( EnableProcessinfo.get_tick_count() - m_check_time ) > TIME_OUT_COUNT;
    }

}
