package com.wisp.game.bet.gate.services;


import com.wisp.game.share.netty.infos.MsgBuf;
import com.wisp.game.share.netty.infos.e_peer_state;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server_protocols.ServerBase;


/**
 * 作为客户端，发送信息给别的服务器
 */
public class GateClientChannelHandler extends SimpleChannelInboundHandler<MsgBuf> {

    private Logger logger = LoggerFactory.getLogger( getClass() );

    private ServerPeer serverPeer;
    public GateClientChannelHandler(ServerPeer serverPeer) {
        this.serverPeer = serverPeer;
    }

    //将在一个连接建立时被调用。
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        System.out.printf("GateClientChannelHandler channelActive channelId:" + ctx.channel().id());

        serverPeer.set_state(e_peer_state.e_ps_connected);
        serverPeer.init_peer(ctx,false,false);
        serverPeer.set_id(GateServer.Instance.generate_id());
        if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_monitor_VALUE )
        {
            serverPeer.regedit_to_monitor();
        }
        else
        {
            serverPeer.regedit_to_server();
        }

       BackstageManager.Instance.add_obj(serverPeer.get_id(),serverPeer);
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MsgBuf message) throws Exception {
        System.out.printf("GateClientChannelHandler:" + message.getClass().getName());
        serverPeer.addProcessMsg(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        serverPeer.set_state(e_peer_state.e_ps_disconnecting);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        serverPeer.set_state(e_peer_state.e_ps_disconnected);
        GateServer.Instance.push_id(serverPeer.get_id());

        //如果注册失败，则有可能，没有加入到BackstageManager中
        if( BackstageManager.Instance.hasKey(serverPeer.get_id())  )
        {
            BackstageManager.Instance.remove_server(serverPeer);

            if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_logic.getNumber() )
            {
                ClientManager.Instance.serverdown_client(serverPeer.get_remote_id());
            }
            else if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_world_VALUE )
            {
                BackstageManager.Instance.world_serverdown(serverPeer.get_remote_id());
            }
            else if( serverPeer.get_remote_type() == ServerBase.e_server_type.e_st_monitor_VALUE )
            {
                //do nohing
            }
        }

    }


}
