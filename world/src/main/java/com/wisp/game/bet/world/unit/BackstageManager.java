package com.wisp.game.bet.world.unit;

import com.wisp.game.share.common.EnableObjectManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class BackstageManager extends EnableObjectManager<Integer,ServerPeer> implements InitializingBean {
    public static BackstageManager Instance;

    private ConcurrentHashMap<Integer, ServerBase.server_info> SInfoMap = new ConcurrentHashMap<>();

    public BackstageManager() {
        Instance = this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public void heartbeat(double elapsed)
    {
        for( ServerPeer serverPeer : obj_map.values() )
        {
            serverPeer.heartbeeat(elapsed);
        }
    }

    public void serverdown()
    {
        for( ServerPeer serverPeer : obj_map.values() )
        {
            if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_monitor_VALUE )
            {
                ServerProtocol.packet_serverdown.Builder builder = ServerProtocol.packet_serverdown.newBuilder();
                serverPeer.send_msg(builder.build());
            }
        }
    }


    public boolean remove_server( ServerPeer serverPeer )
    {
        return remove_obj(serverPeer.get_id());
    }



}
