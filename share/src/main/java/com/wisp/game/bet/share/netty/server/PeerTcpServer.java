package com.wisp.game.bet.share.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PeerTcpServer implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private int port;

    private ChannelHandler childHandler;

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    private io.netty.channel.Channel channel;

    public PeerTcpServer() {

    }

    public void start( int port,ChannelHandler serverNettyInitializer )
    {
        this.port = port;
        this.childHandler = serverNettyInitializer;

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.setName("PeerTcpServerMain");
        t.start();
    }

    public  void run()
    {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try
        {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,2048)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(childHandler)
                    ;

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.syncUninterruptibly();
            channel = channelFuture.channel();
            channel.closeFuture().sync();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            channel.close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
