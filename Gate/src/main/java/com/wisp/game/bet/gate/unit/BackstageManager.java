package com.wisp.game.bet.gate.unit;

import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.common.EnableObjectManager;
import com.wisp.game.bet.share.netty.infos.e_peer_state;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import server_protocols.ServerBase;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存其它服务器的信息
 */
@Component
public class BackstageManager extends EnableObjectManager<Integer,ServerPeer> implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static BackstageManager Instance;
    private ConcurrentHashMap<Integer,ServerPeer> servers_map = new ConcurrentHashMap<Integer,ServerPeer>();
    //key为端口号
    private ConcurrentHashMap<Integer, ServerBase.server_info> SInfoMap = new ConcurrentHashMap<Integer, ServerBase.server_info>();

    public BackstageManager() {
        Instance = this;
    }




    public void afterPropertiesSet() throws Exception
    {

    }

    public boolean regedit_server(ServerPeer serverPeer)
    {
        ServerPeer serverPeer1 =  find_objr(serverPeer.get_id());
        if( serverPeer1 == null )
        {
            logger.warn("can't regedit_server,the peerid:" + serverPeer.get_id() );
            return false;
        }

        servers_map.put(serverPeer.get_remote_id(),serverPeer);

        return true;
    }

    public boolean remove_server(ServerPeer serverPeer)
    {
        ServerPeer serverPeer1 =  servers_map.get(serverPeer.get_remote_id());
        if( serverPeer1 != null )
        {
            servers_map.remove(serverPeer.get_remote_id());
        }

        return remove_obj(serverPeer.get_id());
    }

    public ServerPeer get_server_byid(int serverId)
    {
        return servers_map.get(serverId);
    }

    public ServerPeer get_server_bytype(int serverType)
    {
        Iterator<ServerPeer> it = servers_map.values().iterator();

        while (it.hasNext())
        {
            ServerPeer serverPeer = it.next();
            if( serverPeer.get_remote_type() == serverType )
            {
                return serverPeer;
            }
        }

        return null;
    }


    public int alloc_world_server()
    {
        int serverId = 0;
        int min_count = -1;
        for(ServerBase.server_info server_info : SInfoMap.values() )
        {
            ServerPeer serverPeer = get_server_byid( server_info.getServerPort() );
            if( serverPeer == null )
            {
                continue;
            }

            if( serverPeer.get_remote_type() != ServerBase.e_server_type.e_st_world_VALUE )
            {
                continue;
            }

            if( serverPeer.get_remote_type() != e_peer_state.e_ps_connected.getNumber())
            {
                continue;
            }

            int player_count = server_info.getAttributes().getClientCount();
            if( min_count == -1 || player_count < min_count )
            {
                min_count = player_count;
                serverId = server_info.getServerPort();
            }
        }

        return serverId;
    }

    public void heartbeat( double elapsed )
    {
        for(ServerPeer peer : obj_map.values())
        {
            peer.heartbeat(elapsed);
        }
    }



    public int alloc_gstate_server()
    {
        return 0;
    }
    public int alloc_activity_server()
    {
        return 0;
    }

    public void check_servers()
    {
        Iterator<ServerBase.server_info> it = SInfoMap.values().iterator();
        while (it.hasNext())
        {
            ServerBase.server_info server_info = it.next();
            boolean needCoonect = false;
            switch ( server_info.getServerType()  )
            {
                case e_st_world:
                case e_st_logic:
                case e_st_gstate:
                case e_st_activity:
                    ServerPeer serverPeer = get_server_byid(server_info.getServerPort());
                    if( serverPeer == null || serverPeer.get_state() == e_peer_state.e_ps_disconnected )
                    {
                        needCoonect = true;
                    }

                    break;
                default:break;
            }

            if( needCoonect )
            {
                GateServer gateServer = SpringContextHolder.getBean(GateServer.class);
                ServerPeer serverPeer = gateServer.create_peer(server_info.getServerType().getNumber());
                serverPeer.set_remote_id(server_info.getServerPort());
                serverPeer.connect(server_info.getServerIp(),server_info.getServerPort());
                regedit_server(serverPeer);

                logger.info("need connect the server port: " + server_info.getServerPort());
            }
        }
    }

    public void world_serverdown( int server_id )
    {
        ClientManager.Instance.world_serverdown(server_id);

        boolean bdown = true;
        for(ServerPeer serverPeer : obj_map.values())
        {
            if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_world_VALUE &&
                serverPeer.get_state() != e_peer_state.e_ps_disconnected)
            {
                bdown = false;
                break;
            }
        }


        if( bdown )
        {
            ClientManager.Instance.serverdown_client(0);
        }
    }

    public ConcurrentHashMap<Integer,ServerBase.server_info> getSInfoMap()
    {
        return SInfoMap;
    }

}
