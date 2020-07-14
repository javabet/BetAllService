package com.wisp.game.share.netty.server;

import com.wisp.game.share.netty.server.ServerNettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


public class PeerTcpServer implements Runnable {

    private int port;

    private ServerNettyInitializer serverNettyInitializer;

    public PeerTcpServer() {

    }

    public void start( int port,ServerNettyInitializer serverNettyInitializer )
    {
        this.port = port;
        this.serverNettyInitializer = serverNettyInitializer;

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
                    .childHandler(serverNettyInitializer)
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
