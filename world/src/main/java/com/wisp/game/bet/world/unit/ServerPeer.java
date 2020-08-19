package com.wisp.game.bet.world.unit;

import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.share.netty.PeerTcp;
import com.wisp.game.bet.share.netty.server.PeerTcpClient;
import com.wisp.game.bet.share.netty.infos.e_peer_state;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;


/*
 其作为客户端，连接monitor,等
 */
public class ServerPeer extends PeerTcp {

    private static final int CHECK_TIME = 10 * 1000;

    private PeerTcpClient peerTcpClient;

    private double m_checktime;

    public ServerPeer(int peerId,int remoteType) {
        m_peerId = peerId;
        remote_type = remoteType;
        peerTcpClient = new PeerTcpClient(new WorldClientChannelHandler(this));
    }

    public void heartbeeat( double elapsed )
    {
        int packet_id = packet_service(-1);
        if( packet_id != 0 )
        {
            logger.error("monitor_peer packet_service error id:" + get_id() + " packetid:" + packet_id + " remote_id:" + get_remote_id() + " remote_type:" + get_remote_type() );
        }

        check_state( elapsed );
    }

    public void check_state(double elapsed)
    {
        m_checktime += elapsed;

        if( m_checktime <  CHECK_TIME)
        {
            return;
        }

        e_peer_state eps = get_state();
        if( eps != e_peer_state.e_ps_connected && eps != e_peer_state.e_ps_connecting )
        {
            logger.warn("server_reconnect id:" + get_id() + " remote_id:" + get_remote_id() + " ");
            reconnect();
        }
        else if( get_remote_type() == ServerBase.e_server_type.e_st_monitor.getNumber() )
        {
            ServerProtocol.packet_heartbeat.Builder builder =  ServerProtocol.packet_heartbeat.newBuilder();
            send_msg(builder.build());

            ServerProtocol.packet_updata_self_info.Builder builder1 = ServerProtocol.packet_updata_self_info.newBuilder();
            server_protocols.ServerBase.server_attributes.Builder attrBuilder =  builder1.getAttributesBuilder();
            attrBuilder.setClientCount(GamePlayerMgr.Instance.get_player_count());
            send_msg(builder1.build());
        }

        m_checktime = 0;
    }

    public void connect(String host, int port)
    {
        set_state(e_peer_state.e_ps_connecting);
        peerTcpClient.connect(host,port);
    }

    public void reconnect()
    {
        set_state(e_peer_state.e_ps_connecting);
        peerTcpClient.reconnect();;
    }

    public void regedit_to_monitor()
    {
        ServerProtocol.packet_server_register.Builder builder = ServerProtocol.packet_server_register.newBuilder();
        builder.setServerType(ServerBase.e_server_type.valueOf(get_type()));
        builder.setServerPort(WorldServer.Instance.get_serverid());
        send_msg(builder.build());
    }


    @Override
    public int get_type() {
        return ServerBase.e_server_type.e_st_world_VALUE;
    }
}
