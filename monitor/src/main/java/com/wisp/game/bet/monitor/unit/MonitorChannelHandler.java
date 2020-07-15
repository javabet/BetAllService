package com.wisp.game.bet.monitor.unit;

import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.infos.MsgBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorChannelHandler extends SimpleChannelInboundHandler<MsgBuf> {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private MonitorServer monitorServer;
    public MonitorChannelHandler(MonitorServer monitorServer) {
        this.monitorServer = monitorServer;
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
        MonitorPeer monitorPeer = new MonitorPeer();
        monitorPeer.init_peer(ctx,ctx.channel().id(),false,false);
        ServerManager.Instance.add_obj(ctx.channel().id(),monitorPeer);
    }

    //每当接收数据时，都会调用这个方法
    protected void channelRead0(ChannelHandlerContext ctx, MsgBuf msgBuf) throws Exception
    {
        MonitorPeer monitorPeer = ServerManager.Instance.find_objr(ctx.channel().id());
        if( monitorPeer != null )
        {
            monitorPeer.addProcessMsg(msgBuf);
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

        ctx.close();
        ctx.channel().close();
        ServerManager.Instance.peer_disconnected(ctx.channel().id());
    }

}
