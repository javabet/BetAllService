package com.wisp.game.bet.gate.unit;

import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.netty.PeerTcp;
import com.wisp.game.bet.share.netty.client.ClientTcpPeer;
import com.wisp.game.bet.share.netty.infos.e_peer_state;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

/*
通过此，创建渠道与别的服务器连接，作为客户端
 */
public final class ServerPeer extends PeerTcp {
    private static final int CHECK_TIME = 10000;
    private int m_checktime = 0;
    private boolean m_regedit;
    private ClientTcpPeer clientTcpPeer;

    public ServerPeer(int peerId,int remoteType) {
        super();

        m_peerId = peerId;
        remote_type = remoteType;

        clientTcpPeer = new ClientTcpPeer(new GateClientChannelHandler(this));
        this.m_state = e_peer_state.e_ps_disconnected;
    }

    public int get_type()
    {
        return ServerBase.e_server_type.e_st_gate_VALUE;
    }

    public void heartbeat(double elapsed)
    {
        int packet_id = packet_service(-1);
        if( packet_id != 0 )
        {
            logger.error("monitor_peer packet_service error id:" + channelHandlerContext.channel().id() + " packetId:" + packet_id +
                    " remote_id:" + get_remote_id() + " remote_type:" + get_remote_type());
        }

        check_state(elapsed);
    }

    private void check_state(double elapsed)
    {
        m_checktime += elapsed;
        if( m_checktime <= CHECK_TIME )
        {
            return;
        }

        e_peer_state state = get_state();
        if(  state != e_peer_state.e_ps_connected && state != e_peer_state.e_ps_connecting )
        {
            logger.info("server_reconnect id:" + get_id() + "  remote_id:" + get_remote_id() + "  remote_type:" + get_remote_type() );
            clientTcpPeer.reconnect();
        }
        else if(!m_regedit && get_remote_type() == ServerBase.e_server_type.e_st_logic.getNumber() )
        {
            //如果注册失败，则关闭socket,并且重新连接
            logger.info("logic_server regedit fail remote_id:" + get_remote_id() + "  remote_type:" + get_remote_type());
            clientTcpPeer.discannect();
        }
        else if( get_remote_type() == ServerBase.e_server_type.e_st_monitor_VALUE )
        {
            //向monitor 发送心跳
            ServerProtocol.packet_heartbeat.Builder builder = ServerProtocol.packet_heartbeat.newBuilder();
            send_msg(builder.build());
        }

        m_checktime = 0;
    }



    public void connect(String host, int port)
    {
        set_state(e_peer_state.e_ps_connecting);
        clientTcpPeer.connect(host,port);
        //set_state(e_peer_state.e_ps_accepting);
    }

    public void regedit_to_monitor()
    {
        ServerProtocol.packet_server_register.Builder builder = ServerProtocol.packet_server_register.newBuilder();
        builder.setServerType( ServerBase.e_server_type.valueOf(get_type()) );
        builder.setServerPort( GateServer.Instance.get_serverid() );

        if( GateServer.Instance.environment.containsProperty("gate_type") )
        {
            String gate_type = GateServer.Instance.environment.getProperty("gate_type");
            builder.setSubType(  gate_type );
        }

        send_msg(builder.build());
    }

    public void regedit_to_server()
    {
        GateServer gateServer = SpringContextHolder.getBean(GateServer.class);
        ServerProtocol.packet_server_connect.Builder builder = ServerProtocol.packet_server_connect.newBuilder();
        builder.setServerId( gateServer.get_serverid() );
        builder.setServerType( ServerBase.e_server_type.valueOf(get_type()) );
        send_msg( builder.build() );
    }

    public void regedit_result(boolean result)
    {
        m_regedit = result;
    }

}
