package com.wisp.game.bet.gate.services;

import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.share.netty.infos.e_peer_state;
import com.wisp.game.share.netty.client.ClientHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server_protocols.ServerBase;

public class GateClientHandler extends ClientHandler {

    private  Logger logger = LoggerFactory.getLogger( getClass() );

    private ServerPeer serverPeer;
    private e_peer_state peerState;
    public GateClientHandler(ServerPeer serverPeer) {

        this.serverPeer = serverPeer;
        peerState = e_peer_state.e_ps_disconnected;
    }

    //将在一个连接建立时被调用。
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        peerState = e_peer_state.e_ps_connected;

        logger.info("server_connected id:" + serverPeer.get_id() + " remoteid:" + serverPeer.get_remote_id());

        if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_monitor.getNumber() )
        {
            serverPeer.regedit_to_monitor();
        }
        else
        {
            serverPeer.regedit_to_server();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        peerState = e_peer_state.e_ps_disconnecting;

        BackstageManager backstageManager = SpringContextHolder.getBean(BackstageManager.class);

        if( backstageManager.remove_server(serverPeer) )
        {
            GateServer gateServer = SpringContextHolder.getBean(GateServer.class);
            gateServer.push_id(serverPeer.get_id());
        }

        if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_logic.getNumber() )
        {
            ClientManager clientManager = SpringContextHolder.getBean(ClientManager.class);
            clientManager.serverdown_client(serverPeer.get_id());
        }
        else if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_world.getNumber() )
        {
            backstageManager.world_serverdown(serverPeer.get_id());
        }

    }


    public e_peer_state get_state()
    {
        return peerState;
    }
}
