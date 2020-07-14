package com.wisp.game.share.netty.server;

import com.wisp.game.share.netty.server.ServiceNetty4Codec;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.List;

public class ServerNettyInitializer extends ChannelInitializer<SocketChannel> {

    private List<ChannelHandler> listHandlers;
    private boolean isWebSocket = false;
    private ChannelHandler endHandler;

    public ServerNettyInitializer(boolean isWebSocket,ChannelHandler endHandler ) {
        this.isWebSocket = isWebSocket;
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

        if( isWebSocket )
        {
            //http解码器
            pipeline.addLast(new HttpServerCodec());
            //支持写大数据流
            pipeline.addLast(new ChunkedWriteHandler());
            //http聚合器
            pipeline.addLast(new HttpObjectAggregator(1024*62));
            //websocket支持,设置路由
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        }


        pipeline.addLast(new ServiceNetty4Codec());

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
