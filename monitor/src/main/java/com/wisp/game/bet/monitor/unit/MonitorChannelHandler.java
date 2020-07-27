package com.wisp.game.bet.monitor.unit;

import com.wisp.game.share.netty.infos.MsgBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class MonitorChannelHandler extends SimpleChannelInboundHandler<MsgBuf> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final AttributeKey<Integer> ATTR_PEERID = AttributeKey.newInstance("PEERID");


    public MonitorChannelHandler() {

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

        int peerid = MonitorServer.Instance.generate_id();
        ctx.attr( ATTR_PEERID ).set(peerid);

        MonitorPeer monitorPeer = new MonitorPeer();
        monitorPeer.init_peer(ctx,false,false);
        monitorPeer.set_id(peerid);
        ServerManager.Instance.add_obj(peerid,monitorPeer);
    }

    //每当接收数据时，都会调用这个方法
    protected void channelRead0(ChannelHandlerContext ctx, MsgBuf msgBuf) throws Exception
    {
         int peerId = ctx.attr(ATTR_PEERID).get();
        MonitorPeer monitorPeer = ServerManager.Instance.find_objr( peerId );
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
        //super.exceptionCaught(ctx,cause);

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
        int peerId = ctx.attr(ATTR_PEERID).get();
        ServerManager.Instance.peer_disconnected(peerId);
        MonitorServer.Instance.push_id(peerId);
    }

}
