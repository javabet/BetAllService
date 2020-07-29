package com.wisp.game.bet.share.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class PeerTcpServer implements Runnable {

    private int port;

    private ChannelHandler childHandler;

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
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(childHandler)
                    .option(ChannelOption.SO_BACKLOG,2048)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    ;

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
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
