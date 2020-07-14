package com.wisp.game.bet.gate.services;

import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.share.common.ServerManagerHandler;
import com.wisp.game.share.netty.client.NettyClient;
import io.netty.channel.ChannelHandler;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.ArrayList;
import java.util.List;

public final class ServerPeer extends NettyClient {

    private ServerManagerHandler mgr_handler;
    private GateClientHandler handler;

    public ServerPeer() {
        super();

        List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
        handler = new GateClientHandler(this);
        handlers.add(handler);

        this.setHandlers(handlers);
    }

    public int get_type()
    {
        return ServerBase.e_server_type.e_st_gate_VALUE;
    }


    public void regedit_to_monitor()
    {
        ServerProtocol.packet_server_register.Builder builder = ServerProtocol.packet_server_register.newBuilder();
        builder.setServerType( ServerBase.e_server_type.e_st_gate );
        builder.setServerPort(10099);
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

    @Override
    public int get_state() {
        return handler.get_state().getNumber();
    }
}
