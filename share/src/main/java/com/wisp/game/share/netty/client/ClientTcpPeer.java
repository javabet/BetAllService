package com.wisp.game.share.netty.client;

import com.google.protobuf.GeneratedMessage;
import com.wisp.game.share.netty.PeerTcp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *用于与其它服务器进行联系时发送消息时的处理
 */
public class ClientTcpPeer  {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String host;
    private int port;

    private ChannelHandler channelHandler;
    private Bootstrap clientBootstrap;
    private Channel channel;

    public ClientTcpPeer(ChannelHandler channelHandler) {
        clientBootstrap = new Bootstrap();

        //第1步 定义线程组，处理读写和链接事件，没有了accept事件
        EventLoopGroup group = new NioEventLoopGroup();
        clientBootstrap.group(group );

        //第2步 绑定客户端通道
        clientBootstrap.channel(NioSocketChannel.class);

        //第3步 给NIoSocketChannel初始化handler， 处理读写事件
        ChannelHandler clientHandler = new ClientNettyInitializer( channelHandler );
        clientBootstrap.handler(clientHandler);

    }

    public void connect(String host, int port)
    {
        this.host = host;
        this.port = port;

        if( channel != null )
        {
            channel.close();
            channel.closeFuture();
        }

        try
        {
            ChannelFuture future = clientBootstrap.connect(host, port).sync();       //连接到远程节点，阻塞等待直到连接完成
            channel = future.channel();
            channel.closeFuture().sync();           //阻塞，直到Channel 关闭
        }
        catch (Exception error)
        {
            logger.error(error.getMessage());
        }
    }

    public void reconnect(String host, int port)
    {
        connect(host,port);
    }

    public void reconnect()
    {
        connect(this.host,this.port);
    }

    public void discannect()
    {
        if( channel != null )
        {
            channel.disconnect();
        }
    }

    public void run()
    {

    }

}
