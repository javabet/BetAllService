package com.wisp.game.bet.logic.unit;

import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.share.common.EnableObjectManager;
import com.wisp.game.bet.share.netty.infos.e_peer_state;
import org.springframework.stereotype.Component;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class BackstageManager extends EnableObjectManager<Integer,ServerPeer> {

    public static BackstageManager Instance;

    public ConcurrentHashMap<Integer, ServerBase.server_info> SInfoMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,ServerPeer> servers_map = new ConcurrentHashMap<>();

    private boolean is_vaild = false;

    public BackstageManager() {
        Instance = this;
    }

    public ConcurrentHashMap<Integer,ServerPeer> get_world_map()
    {
        return servers_map;
    }

    public void heartbeat(double elapsed)
    {
        for( ServerPeer serverPeer : obj_map.values() )
        {
            serverPeer.heartbeat(elapsed);
        }
    }


    public void connect_world()
    {
        for( ServerBase.server_info server_info : SInfoMap.values() )
        {
            boolean needcionnect = false;
            if( server_info.getServerType().getNumber() == ServerBase.e_server_type.e_st_world_VALUE)
            {
                ServerPeer serverPeer = get_world_byid(server_info.getServerPort());
                if( serverPeer == null || serverPeer.get_state() == e_peer_state.e_ps_disconnected )
                {
                    needcionnect = true;
                }
            }

            if( needcionnect )
            {
                ServerPeer serverPeer = LogicServer.Instance.create_peer(server_info.getServerType().getNumber());
                serverPeer.set_remote_id( server_info.getServerPort() );
                serverPeer.connect(server_info.getServerIp(),server_info.getServerPort());
                regedit_server(serverPeer);
            }

        }
    }

    public boolean regedit_server( ServerPeer serverPeer )
    {
        ServerPeer serverPeer1 =  find_objr(serverPeer.get_id());
        if(serverPeer == null)
        {
            return false;
        }

        servers_map.put(serverPeer.get_remote_id(),serverPeer);

        return true;
    }

    public ServerPeer get_world_byid( int serverid )
    {
        return servers_map.get(serverid);
    }

    public void world_serverdown()
    {
        boolean bdown = true;


        for( ServerPeer serverPeer : obj_map.values() )
        {
            if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_world_VALUE &&
            serverPeer.get_state() != e_peer_state.e_ps_disconnected)
            {
                bdown = true;
                break;
            }
        }

        if( bdown )
        {
            set_world_state(false);
            GameManager.Instance.serverdown();
        }

    }

    public  void set_world_state()
    {
        this.set_world_state(true);
    }

    public  void set_world_state(boolean valid)
    {
        is_vaild = valid;
    }

    public boolean remove_server(ServerPeer serverPeer)
    {
        servers_map.remove(serverPeer.get_remote_id());
        remove_obj(serverPeer.get_id());
        return true;
    }

}
