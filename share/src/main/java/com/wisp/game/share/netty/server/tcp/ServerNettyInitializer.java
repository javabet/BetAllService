package com.wisp.game.share.netty.server.tcp;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;

@ChannelHandler.Sharable
public class ServerNettyInitializer extends ChannelInitializer<SocketChannel> {

    private List<ChannelHandler> listHandlers;
    private ChannelHandler endHandler;

    public ServerNettyInitializer(ChannelHandler endHandler ) {

        this.endHandler = endHandler;
    }

    /**
     * 增加额外的中间处理过程
     * @param listHandlers
     */
    public void setMiddleHandlers(List<ChannelHandler> listHandlers)
    {
        this.listHandlers = listHandlers;
    }

    protected  void initChannel(SocketChannel ch) throws Exception
    {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(180,0,0));
        pipeline.addLast(new ServerNetty4Codec());

        if( listHandlers != null )
        {
            for( ChannelHandler channelHandler : listHandlers )
            {
                pipeline.addLast( channelHandler );
            }
        }

        if( endHandler != null )
        {
            pipeline.addLast(endHandler);
        }
    }

}
