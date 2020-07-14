package com.wisp.game.share.netty.client;

import com.google.protobuf.GeneratedMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *用于与其它服务器进行联系时发送消息时的处理
 */
public abstract class NettyClient implements Runnable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ChannelHandler clientHandler;

    private String host;
    private int port;
    private Channel channel;

    private int remoto_type;
    private int remote_id;
    private int m_id;
    private boolean m_is_websocket;

    public NettyClient() {

    }

    protected  void setHandlers(List<ChannelHandler> handlers)
    {
        clientHandler = new ClientNettyInitializer(handlers);
    }

    public void connect(String host, int port)
    {
        this.host = host;
        this.port = port;

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void init_peer(int _id)
    {
        this.init_peer(_id,false);
    }

    public void init_peer(int _id,boolean _is_web)
    {
        m_id = _id;
        m_is_websocket = _is_web;
    }

    public void run()
    {
        Bootstrap client = new Bootstrap();

        //第1步 定义线程组，处理读写和链接事件，没有了accept事件
        EventLoopGroup group = new NioEventLoopGroup();
        client.group(group );

        //第2步 绑定客户端通道
        client.channel(NioSocketChannel.class);

        //第3步 给NIoSocketChannel初始化handler， 处理读写事件
        client.handler(clientHandler);
        try
        {
            ChannelFuture future = client.connect(host, port).sync();       //连接到远程节点，阻塞等待直到连接完成
            channel = future.channel();
            channel.closeFuture().sync();           //阻塞，直到Channel 关闭
        }
        catch (Exception error)
        {
            logger.error(error.getMessage());
        }
        finally
        {
            try
            {
                group.shutdownGracefully().sync();
            }
            catch (Exception err)
            {
                logger.error("close the netty client error");
            }

        }
    }

    public void send_msg(GeneratedMessage message)
    {
        sendMsg(message);
    }

    private void sendMsg(GeneratedMessage message)
    {
        if( channel == null )
        {
            logger.error("the channelId is not null");
            return;
        }

        if( channel.isOpen() )
        {
            channel.writeAndFlush(message);
        }
    }

    public int get_remote_port()
    {
        return 0;
    }

    public String get_remote_ip()
    {
        return "";
    }

    public void set_remote_type(int _type)
    {
        remoto_type = _type;
    }

    public int get_remote_type()
    {
        return remoto_type;
    }

    public int get_remote_id() {
        return remote_id;
    }

    public void set_remote_id(int remote_id) {
        this.remote_id = remote_id;
    }

    //查看当前socket的状态

    /**
     * 	e_ps_disconnected = 0,
     * 	e_ps_disconnecting,
     * 	e_ps_connecting,
     * 	e_ps_connected,
     * 	e_ps_accepting,
     * @return
     */
    public abstract int get_state();

    public int get_id(){return m_id;};
}
