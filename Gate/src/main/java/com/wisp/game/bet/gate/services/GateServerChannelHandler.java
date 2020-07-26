package com.wisp.game.bet.gate.services;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;
import com.wisp.game.share.netty.infos.MsgBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import jodd.cli.Cli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * 作为服务器，接收客户端发送过来的信号
 */

@ChannelHandler.Sharable
public class GateServerChannelHandler extends SimpleChannelInboundHandler<MsgBuf> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final AttributeKey<Integer> ATTR_PEERID = AttributeKey.newInstance("PEERID");

    public GateServerChannelHandler() {

    }

    /**
     * ChannelUnregistered Channel 已经被创建，但还未注册到 EventLoop
     * ChannelRegistered Channel 已经被注册到了 EventLoop
     * ChannelActive Channel 处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了
     * ChannelInactive Channel 没有连接到远程节点
     */

    //将在一个连接建立时被调用。
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        int peerid = GateServer.Instance.generate_id();
        ctx.attr( ATTR_PEERID ).set(peerid);

        GatePeer gatePeer = new GatePeer();
        gatePeer.init_peer(ctx,false,true);
        gatePeer.set_id(peerid);
        gatePeer.set_net_param();
        gatePeer.set_route_handler(ClientManager.Instance);
        ClientManager.Instance.regedit_client(gatePeer);

        System.out.printf("the........ peerId:" + peerid);
    }

    //每当接收数据时，都会调用这个方法
    protected void channelRead0(ChannelHandlerContext ctx, MsgBuf msgBuf) throws Exception
    {
        int peerId = ctx.attr( ATTR_PEERID ).get();
        GatePeer gatePeer = ClientManager.Instance.find_objr(peerId);
        if( gatePeer != null )
        {
            gatePeer.addProcessMsg( msgBuf );
        }
        else
        {
            logger.error("channelRead0 has the data but the peer is not exist");
        }
    }

    //当 ChannelnboundHandler.fireUserEventTriggered()方法被调
    //用时被调用，因为一个 POJO 被传经了 ChannelPipeline
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    //当处理过程中在 ChannelPipeline 中有错误产生时被调用
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx,cause);

        ctx.close();
    }


    //当把 ChannelHandler 添加到 ChannelPipeline 中时被调用
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    //当从 ChannelPipeline 中移除 ChannelHandler 时被调用
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        int peerId = ctx.attr( ATTR_PEERID ).get();

        System.out.printf("channelInactive" + ctx.channel().id() + "\n");

        ctx.close();
        ctx.channel().close();
        ctx.channel().close();
        GatePeer gatePeer = ClientManager.Instance.find_objr(peerId);
        if( gatePeer != null )
        {
            GateServer.Instance.push_id(gatePeer.get_id());
        }
        ClientManager.Instance.peer_disconnected(peerId);
        ClientManager.Instance.remove_client(peerId);

    }

}
