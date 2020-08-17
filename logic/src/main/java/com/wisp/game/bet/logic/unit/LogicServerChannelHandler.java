package com.wisp.game.bet.logic.unit;

import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.infos.MsgBuf;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server_protocols.ServerBase;


/**
 * 作为服务器，接收客户端发送过来的信号
 */
@ChannelHandler.Sharable
public class LogicServerChannelHandler extends SimpleChannelInboundHandler<MsgBuf> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final AttributeKey<Integer> ATTR_PEERID = AttributeKey.newInstance("PEERID");

    //将在一个连接建立时被调用。
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        int peerid = LogicServer.Instance.generate_id();
        ctx.attr( ATTR_PEERID ).set(peerid);

        LogicPeer logicPeer = new LogicPeer(ctx,peerid);
        ServersManager.Instance.add_obj(peerid,logicPeer);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MsgBuf msgBuf) throws Exception {
        int peerId = channelHandlerContext.attr( ATTR_PEERID ).get();
        LogicPeer logicPeer = ServersManager.Instance.find_objr(peerId);

        if( logicPeer != null )
        {
            logicPeer.addProcessMsg( msgBuf );
        }
        else
        {
            logger.error("channelRead0 has the data but the peer is not exist,the peerId:" + peerId);
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

        int peerId = ctx.attr( ATTR_PEERID ).get();

        System.out.printf("channelInactive" + ctx.channel().id() + "\n");

        ctx.close();
        ctx.channel().close();
        ctx.channel().close();

        LogicPeer logicPeer = ServersManager.Instance.find_objr(peerId);
        if( logicPeer != null )
        {
            ServersManager.Instance.remove_server(logicPeer);
            LogicServer.Instance.push_id(logicPeer.get_id());
        }

    }


}
