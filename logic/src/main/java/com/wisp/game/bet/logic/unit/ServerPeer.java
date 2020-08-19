package com.wisp.game.bet.logic.unit;

import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.share.netty.PeerTcp;
import com.wisp.game.bet.share.netty.server.PeerTcpClient;
import com.wisp.game.bet.share.netty.infos.e_peer_state;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

/**
 * 与务服务器之间通讯，主动发送信息给服务器
 */
public class ServerPeer extends PeerTcp {
    private final static int  CHECK_TIME = 10 * 1000;
    private double m_checktime = 0;
    private PeerTcpClient peerTcpClient;

    public ServerPeer(int peerid,int remoteType) {
        m_peerId = peerid;
        this.remote_type = remoteType;

        peerTcpClient = new PeerTcpClient(new LogicClientChannelHandler(this));
    }

    @Override
    public int get_type() {
        return ServerBase.e_server_type.e_st_logic_VALUE;
    }

    public void heartbeat(double elapsed)
    {
        int packetId = packet_service(-1);
        if( packetId != 0 )
        {
            logger.error("serverperr packet_service error id:" + get_id() + " packetId:" + packetId + " remote_id:"+ get_remote_id() + " remote_type:" + get_remote_type());
        }

        check_state(elapsed);
    }

    private void check_state(double elapsed)
    {
        m_checktime += elapsed;
        if( m_checktime < CHECK_TIME  )
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
            attrBuilder.setGameId(GameManager.Instance.get_gameid());
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
        peerTcpClient.reconnect();
    }

    public void regedit_to_monitor()
    {
        ServerProtocol.packet_server_register.Builder builder = ServerProtocol.packet_server_register.newBuilder();
        builder.setServerType( ServerBase.e_server_type.valueOf(get_type()) );
        builder.setServerPort(LogicServer.Instance.get_serverid());
        send_msg(builder);
    }

    public void regedit_to_world()
    {
        ServerProtocol.packet_server_connect.Builder builder = ServerProtocol.packet_server_connect.newBuilder();
        builder.setServerId(LogicServer.Instance.get_serverid());
        builder.setServerType( ServerBase.e_server_type.valueOf(get_type()) );
        send_msg(builder);
    }

}
