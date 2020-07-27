package com.wisp.game.bet.world.unit;


import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.share.common.EnableObjectManager;
import com.wisp.game.share.utils.SessionHelper;
import org.springframework.stereotype.Component;
import server_protocols.ServerProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServersManager extends EnableObjectManager<Integer,WorldPeer> {

    public static ServersManager Instance;

    private boolean m_closeing = false;
    private boolean m_shutdowning = false;
    private double m_wait_time = 0;
    private double m_force_time = 0;
    private double m_close_time = 0;
    private ConcurrentHashMap<Integer,Double> m_notice_map = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,WorldPeer> servers_map = new ConcurrentHashMap<>();

    public ServersManager() {
        Instance = this;
    }

    public boolean regedit_server(WorldPeer peer)
    {
        WorldPeer worldPeer = find_objr(peer.get_id());
        if( worldPeer == null )
        {
            return false;
        }

        boolean flag = servers_map.containsKey(peer.get_remote_id());
        if(!flag)
        {
            servers_map.put(peer.get_remote_id(),peer);
            return true;
        }

        return false;
    }

    public WorldPeer find_server(int serverId)
    {
        return servers_map.get(serverId);
    }

    public boolean remove_server( WorldPeer worldPeer )
    {
        servers_map.remove(worldPeer.get_remote_id());
        return remove_obj(worldPeer.get_id());
    }

    public void heartbeat(double elapsed )
    {
        for(WorldPeer worldPeer : obj_map.values())
        {
            worldPeer.heartbeat(elapsed);
        }

        //等待，60秒后关服
        if( m_wait_time > 0 )
        {
            m_wait_time -= elapsed;
            if( m_wait_time < 0 )
            {
                m_shutdowning = true;
                m_force_time = 300;
            }
        }

        if( m_force_time > 0 )
        {
            m_force_time -= elapsed;
            if( m_force_time < 0 )
            {
                serverdown();
            }
        }


        ////关闭world
        if( m_close_time > 0 )
        {
            m_close_time -= elapsed;
            if( m_close_time < 0)
            {
                WorldServer.Instance.close();
            }
        }
    }

    public void prepare_shutdown(int game_id)
    {

    }

    public  boolean is_shutdowning()
    {
        return false;
    }


    public void serverdown()
    {
        if( m_closeing )
        {
            return;
        }

        ServerProtocol.packet_serverdown.Builder builder = ServerProtocol.packet_serverdown.newBuilder();
        broadcast_msg(builder.build());

        BackstageManager.Instance.serverdown();

        m_close_time = 20;
        m_closeing = true;
    }

    public void broadcast_msg(Message msg)
    {
        this.broadcast_msg(msg,-1);
    }

    public void broadcast_msg(Message msg,int except_id)
    {

    }

    public int send_msg_to_client(int sessionid, Message msg)
    {
        return 0;
    }

    public int send_msg_to_client(List<Integer> sessionids,Message msg)
    {
        return 0;
    }

    private void broadcast_msg(int packet_id,Message msg)
    {
        this.broadcast_msg(packet_id,msg,-1);
    }

    private void broadcast_msg(int packet_id,Message msg,int except_id)
    {

    }

    private void broadcast_msg2(int packet_id,Message msg)
    {
        broadcast_msg2(packet_id,msg,-1);
    }

    private void broadcast_msg2(int packet_id,Message msg,int game_sid)
    {
        broadcast_msg2(packet_id,msg,game_sid,false);
    }

    private void broadcast_msg2(int packet_id,Message msg,int game_sid,boolean bWithdraw)
    {

    }


    private int send_msg_to_client(int sessionid,int packet_id,Message msg)
    {
        int gateId = SessionHelper.get_gateid(sessionid);
        WorldPeer worldPeer = find_server(gateId);
        if( worldPeer == null )
        {
            return -1;
        }

        ServerProtocol.packet_transmit_msg.Builder builder = ServerProtocol.packet_transmit_msg.newBuilder();
        builder.setSessionid(sessionid);
        server_protocols.ServerBase.msg_packet.Builder packetBuilder =  builder.getMsgpakBuilder();
        packetBuilder.setMsgid(packet_id);
        builder.setMsgpak(packetBuilder);

        return worldPeer.send_msg(builder.build());
    }

    private int send_msg_to_client(List<Integer> sessionids,int packet_id,Message msg)
    {

        ByteString msgInfos = msg.toByteString();

        Map<Integer,List<Integer>> gateList = new HashMap<Integer, List<Integer>>();
        for(int i = 0; i < sessionids.size();i++)
        {
            int sessionId = sessionids.get(i);
            int gateId = SessionHelper.get_gateid( sessionId );
            if( gateList.containsKey(gateId) )
            {
                List<Integer> list = gateList.get(gateId);
                list.add( sessionId );
            }
            else
            {
                List<Integer> list = new ArrayList<>();
                list.add(sessionId);
                gateList.put(gateId,list);
            }
        }

        for( Integer gateId : gateList.keySet()  )
        {
            WorldPeer worldPeer = find_server(gateId);
            if( worldPeer == null )
            {
                continue;
            }

            ServerProtocol.packet_broadcast_msg.Builder builder = ServerProtocol.packet_broadcast_msg.newBuilder();
            builder.addAllSessionids(gateList.get(gateId));

            server_protocols.ServerBase.msg_packet.Builder packBuilder = builder.getMsgpakBuilder();
            packBuilder.setMsgid(packet_id);
            packBuilder.setMsginfo( msgInfos );

            worldPeer.send_msg( builder.build() );
        }

        return 0;
    }

}
