package com.wisp.game.share.netty.client;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<GeneratedMessage> {


    public ClientHandler() {
    }

    //将在一个连接建立时被调用。
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    //每当接收数据时，都会调用这个方法
    protected void channelRead0(ChannelHandlerContext var1, GeneratedMessage var2) throws Exception
    {
        System.out.printf("..channelRead0...");
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       super.exceptionCaught(ctx,cause);
    }


    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);


    }

}
