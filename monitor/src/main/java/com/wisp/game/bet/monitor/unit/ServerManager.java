package com.wisp.game.bet.monitor.unit;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.wisp.game.bet.monitor.db.DbAccount;
import com.wisp.game.bet.monitor.db.DbConfig;
import com.wisp.game.bet.monitor.proc.PacketUpdateSelfInfo;
import com.wisp.game.db.config.info.ServerList;
import com.wisp.game.share.common.EnableObjectManager;
import com.wisp.game.share.common.EnableProcessinfo;
import io.netty.channel.ChannelId;
import org.apache.catalina.util.ServerInfo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class ServerManager extends EnableObjectManager<Integer,MonitorPeer> {

    private static final int UPDATE_TIME = 3000;            //单位ms
    private double m_checktime = 0;
    private ConcurrentHashMap<ChannelId,ServerBase.server_info.Builder> m_infomap;
    public static ServerManager Instance;
    public ServerManager() {
        ServerManager.Instance = this;
        m_infomap = new ConcurrentHashMap<ChannelId, ServerBase.server_info.Builder>();
        m_checktime = EnableProcessinfo.get_tick_count();
    }

    public boolean regedit_server(MonitorPeer peer, ServerBase.server_info.Builder sinfo)
    {
        MonitorPeer monitorPeer =  find_objr(peer.get_id());
        if( monitorPeer == null )
        {
            return false;
        }

        for( ServerBase.server_info.Builder server_info : m_infomap.values() )
        {
            if( server_info.getServerPort() == sinfo.getServerPort() )
            {
                return false;
            }
        }

        if( peer.get_remote_type() == ServerBase.e_server_type.e_st_gate.getNumber() )
        {
            Query query = new Query(Criteria.where("ServerId").is(sinfo.getServerPort()).and("ServerType").is(ServerBase.e_server_type.e_st_gate.getNumber()));
            Update update = new Update();
            update.set("Status",1);
            update.set("ServerIp",sinfo.getServerIp());
            DbConfig.Instance.getMongoTemplate().updateFirst(query,update, ServerList.class);
        }

        if( peer.get_remote_type() == ServerBase.e_server_type.e_st_world.getNumber() )
        {
            Query query = new Query(Criteria.where("ServerId").is(sinfo.getServerPort()).and("ServerType").is(ServerBase.e_server_type.e_st_world.getNumber()));
            Update update = new Update();
            update.set("Status",1);
            update.set("ServerIp",sinfo.getServerIp());
            DbConfig.Instance.getMongoTemplate().updateFirst(query,update, ServerList.class);
        }


        m_infomap.put(peer.getChannelId(),sinfo);

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


    //主要功能，同步monitor上面的信息到其它机器上面
    public void heartbeat( double  elapsed )
    {
        m_checktime += elapsed;
        if( m_checktime > UPDATE_TIME && !m_infomap.isEmpty()  )
        {
            ServerProtocol.packet_updata_servers_info.Builder builder = ServerProtocol.packet_updata_servers_info.newBuilder();

            for(ServerBase.server_info.Builder server_info_builder : m_infomap.values() )
            {
                builder.addSinfos( server_info_builder.clone() );
            }

            m_checktime = 0;
        }

        for(MonitorPeer monitorPeer : obj_map.values())
        {
            monitorPeer.heartbeat(elapsed);
        }
    }

    public void broadcast_msg(Message message)
    {
        this.broadcast_msg(message,null);
    }

    public void broadcast_msg(Message message, ChannelId except_id)
    {
        for( MonitorPeer monitorPeer : obj_map.values() )
        {
            if( monitorPeer.getChannelId() == except_id )
            {
                continue;
            }
            monitorPeer.send_msg(message);
        }
    }


    public void reset_world_id(int world_id)
    {
        Criteria criteria = Criteria.where("WorldId").is(world_id);
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("WorldId",0);
        DbAccount.Instance.getMongoTemplate().updateFirst(query,update,DbAccount.DB_SERVERINFO);

        criteria = Criteria.where("ServerId").is(world_id).and("ServerType").is(ServerBase.e_server_type.e_st_world_VALUE);
        query = new Query(criteria);
        update = new Update();
        update.set("Status",0);
        DbConfig.Instance.getMongoTemplate().updateFirst(query,update, DbConfig.DB_SERVERLIST);
    }

    public void reset_gate_id(int gate_id)
    {
        Query query = new Query(Criteria.where("GateId").is(gate_id));
        Update update = new Update();
        update.set("GateId",0);
        DbAccount.Instance.getMongoTemplate().updateFirst(query,update,DbAccount.DB_SERVERINFO);

        query = new Query(Criteria.where("ServerType").is(ServerBase.e_server_type.e_st_gate_VALUE));
        update = new Update();
        update.set("Status",0);
        DbConfig.Instance.getMongoTemplate().updateFirst(query,update,DbConfig.DB_SERVERLIST);
    }

    public void peer_disconnected(int peerid)
    {
        MonitorPeer monitorPeer = find_objr(peerid);
        if( monitorPeer == null )
        {
            return;
        }
        remove_server(peerid);

        ServerProtocol.packet_other_server_disconnect.Builder builder = ServerProtocol.packet_other_server_disconnect.newBuilder();
        builder.setServerId(monitorPeer.get_remote_id());
        broadcast_msg(builder.build());

        if( monitorPeer.get_remote_type() == ServerBase.e_server_type.e_st_world_VALUE )
        {
            reset_world_id(monitorPeer.get_remote_id());
        }
        else if( monitorPeer.get_remote_type() == ServerBase.e_server_type.e_st_gate_VALUE )
        {
            reset_gate_id(monitorPeer.get_remote_id());
        }
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

}
