package com.wisp.game.bet.sshare;

import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.netty.server.PeerTcpServer;
import com.wisp.game.bet.share.netty.server.tcp.ServerNettyInitializer;
import com.wisp.game.bet.share.netty.server.websocket.ServerWebSocketNettyInitializer;
import io.netty.channel.ChannelHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.env.Environment;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerBase implements InitializingBean,Runnable {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private  AtomicInteger atomicInteger = new AtomicInteger(0);
    private Deque<Integer> id_queue = new ConcurrentLinkedDeque<Integer>();

    public static boolean IO_RUN = true;

    private boolean b_run;
    private boolean b_closing;
    private boolean m_is_web;
    private  int m_serverid;
    private int m_groupid;

    protected  int m_ncount;

    private PeerTcpServer peerTcpServer;

    public ServerBase() {
        b_run = true;
        m_serverid = 0;
        m_is_web = false;
        m_groupid = 0;
        b_closing = false;

        Runtime.getRuntime().addShutdownHook( new Thread(new Runnable() {
            @Override
            public void run() {
                s_exit();
            }
        }) );
    }

    public void afterPropertiesSet() throws Exception
    {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(this);
        //Thread thread = new Thread(this);
        //thread.setDaemon(true);
        //thread.setName("主线程");
        //thread.start();
    }

    public void run()
    {
        Thread.currentThread().setName("主线程");
        try
        {
            Environment environment = SpringContextHolder.getBean(Environment.class);
            ApplicationArguments applicationArguments = SpringContextHolder.getBean(ApplicationArguments.class);
            boolean sInitFlag =  s_init(applicationArguments,environment);
            if( !sInitFlag )
            {
                logger.error("sInitFlag has error");
                System.exit(0);
            }
        }
        catch (Exception ex)
        {
            logger.error("游戏线程异常![{}]", ExceptionUtils.getStackTrace(ex));
        }
    }

    protected boolean s_init(ApplicationArguments args, Environment environment )
    {
        logger.info("server start");

        int groupId = environment.getProperty("cfg.server_id",Integer.class,-1);
        if( groupId != -1 )
        {
            m_groupid = groupId;
        }

        int type = environment.getProperty("cfg.server_type",Integer.class,-1);
        m_is_web = environment.getProperty("cfg.websocket",Integer.class,-1) == 1;

        if( environment.containsProperty("cfg.out_port") ){
            m_serverid = environment.getProperty("cfg.out_port",Integer.class,-1);

            ChannelHandler channelHandler = getChannelHandler();

            if( channelHandler == null )
            {
                logger.error(getClass().getName() + " need override the channelHandler");
                System.exit(0);
            }

            ChannelHandler childChannelHandler =  m_is_web ? new ServerWebSocketNettyInitializer(getChannelHandler()) : new ServerNettyInitializer(getChannelHandler());

            peerTcpServer = new PeerTcpServer();
            peerTcpServer.start(m_serverid,childChannelHandler);
        }

        if(!on_init())
        {
            logger.error("on_init error!");
            return false;
        }

        logger.info("server_base start ");

        return s_run();
    }

    //作为服务器 处理接收客户端连接的数据处理
    protected  abstract ChannelHandler getChannelHandler();

    public abstract boolean on_init();

    protected   abstract void on_run();         //底层

    public abstract  void on_exit();

    private boolean s_run()
    {
        on_run();

        IO_RUN = false;

        s_exit();
        on_exit();

        return true;
    }

    protected void s_exit()
    {
        try
        {
            Thread.sleep(300);
        }
        catch (Exception exception)
        {
            logger.error("thread stop has error");
        }

        b_run = false;

        logger.error("ready to exit");
    }

    public  void close()
    {
        b_closing = true;
    }

    protected boolean is_runing()
    {
        if( b_closing )
        {
            //将日志输出到文件中，并暂停200ms 以文件日志写入

            b_run = b_closing = false;
        }

        return b_run;
    }

    public int generate_id()
    {
        m_ncount ++;

        if( id_queue.size() >= 1000 )        //保证释放的id，不会被马上利用
        {
            return id_queue.pollFirst();
        }

       return atomicInteger.addAndGet(1);
    }

    public void push_id(int peer_id)
    {
        m_ncount --;
        id_queue.add( peer_id );
    }

    protected int get_peer_count()
    {
        return m_ncount;
    }

    public void set_groupid( int groupid )
    {
        m_groupid = groupid;
    }

    public int get_groupid()
    {
        return m_groupid;
    }

    public int get_serverid()
    {
        return m_serverid;
    }

}
