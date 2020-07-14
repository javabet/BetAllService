package com.wisp.game.share.netty.client;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;

public class ClientNettyInitializer extends ChannelInitializer<SocketChannel> {

    private List<ChannelHandler> handlers;

    public ClientNettyInitializer(List<ChannelHandler> handlers) {
        this.handlers = handlers;
    }

    protected  void initChannel(SocketChannel ch) throws Exception
    {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(180,0,0));
        pipeline.addLast(new ClientNetty4Codec());

        for( ChannelHandler channelHandler : this.handlers )
        {
            pipeline.addLast(channelHandler);
        }
    }

}
