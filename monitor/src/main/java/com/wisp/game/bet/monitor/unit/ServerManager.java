package com.wisp.game.bet.monitor.unit;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.wisp.game.share.common.EnableObjectManager;
import com.wisp.game.share.common.EnableProcessinfo;
import org.apache.catalina.util.ServerInfo;
import org.springframework.stereotype.Component;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerManager extends EnableObjectManager<MonitorPeer> {

    private double m_checktime = 0;
    private ConcurrentHashMap<Integer,ServerBase.server_info.Builder> m_infomap;
    public static ServerManager Instance;
    public ServerManager() {
        m_infomap = new ConcurrentHashMap<Integer, ServerBase.server_info.Builder>();
        m_checktime = EnableProcessinfo.get_tick_count();
        ServerManager.Instance = this;
    }

    public boolean regedit_server(MonitorPeer peer, ServerBase.server_info.Builder sinfo)
    {
        MonitorPeer monitorPeer =  find_objr(peer.get_id());
        if( monitorPeer != null )
        {
            return false;
        }

        for( ServerBase.server_info.Builder server_info : m_infomap.values() )
        {
            if( server_info.getServerPort() == sinfo.getServerPort() )
            {
                return false;
            }

            if( peer.get_remote_type() == ServerBase.e_server_type.e_st_gate.getNumber() )
            {

            }

            if( peer.get_remote_type() == ServerBase.e_server_type.e_st_world.getNumber() )
            {

            }


            m_infomap.put(peer.get_id(),sinfo);
        }

        return true;
    }

    public boolean remove_server(int peer_id)
    {
        boolean removeFlag = remove_obj(peer_id);
        ServerBase.server_info.Builder server_info =  m_infomap.remove(peer_id);
        return removeFlag;
    }

    public ServerBase.server_info.Builder get_server_info(int peer_id)
    {
        return m_infomap.get(peer_id);
    }

    public MonitorPeer get_server_bytype(int servertype)
    {
        for( MonitorPeer monitorPeer : obj_map.values() )
        {
            if( monitorPeer.get_remote_type() == servertype )
            {
                return monitorPeer;
            }
        }

        return null;
    }

    public void heartbeat( double  elapsed )
    {

    }

    public void broadcast_msg(Message message)
    {
        this.broadcast_msg(message,-1);
    }

    public void broadcast_msg(Message message, int except_id)
    {

    }

    public void broadcast_msg(int packet_id,Message message,int except_id )
    {

    }

    public void reset_world_id()
    {

    }

    public void reset_gate_id()
    {

    }



}
