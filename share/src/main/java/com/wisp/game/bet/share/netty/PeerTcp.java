package com.wisp.game.bet.share.netty;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.infos.MsgBuf;
import com.wisp.game.bet.share.netty.infos.e_peer_state;
import com.wisp.game.bet.sshare.IRouterHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class PeerTcp {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected int remote_id;
    protected int remote_type;
    protected boolean m_need_route;
    protected boolean m_bcheck_time;
    protected IRouterHandler m_router_handler;
    protected e_peer_state m_state;
    protected ChannelHandlerContext channelHandlerContext;
    protected int m_peerId = 0;


    protected Queue<MsgBuf> receive_queue = new ConcurrentLinkedQueue<>();
    private Queue<MsgBuf> send_queue = new ConcurrentLinkedQueue<>();

    public PeerTcp() {

    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext)
    {
        this.channelHandlerContext = channelHandlerContext;
    }

    public ChannelHandlerContext getChannelHandlerContext()
    {
        return this.channelHandlerContext;
    }

    public void packet_send_msgs()
    {
        boolean needSend = false;
        while( send_queue.size() > 0 )
        {
            MsgBuf msgBuf =  send_queue.remove();
            this.channelHandlerContext.write(msgBuf);
            needSend = true;
        }

        if( needSend )
        {
            if( this.channelHandlerContext.channel().isActive() && this.channelHandlerContext.channel().isOpen()   )
            {
                this.channelHandlerContext.channel().flush();
            }
        }
    }

    public int packet_service( int process_count )
    {
        int i = 0;
        long cur_tm_ms = 0;
        while( receive_queue.size() > 0 )
        {
            MsgBuf msgBuf =  receive_queue.poll();
            cur_tm_ms = System.currentTimeMillis();

            if( msgBuf.getPacket_id() == 5001 )
            {
                System.out.printf("go this...");
            }

            if( m_need_route && msgBuf.isNeed_route() )
            {
                if( m_router_handler != null  )
                {
                    boolean processFlag = m_router_handler.route_handler(this,msgBuf.getPacket_id(),msgBuf.getByteString());
                    if(!processFlag) {
                        return msgBuf.getPacket_id();
                    }
                }
            }
            else
            {
                RequestMessageRegister.ProtocolStruct protocolStruct = RequestMessageRegister.Instance.getProtocolStruct(msgBuf.getPacket_id());
                if( protocolStruct == null || protocolStruct.getHandlerInstance() == null ||
                        !protocolStruct.getHandlerInstance().packet_process(this,msgBuf.getMsg()) )
                {
                    return msgBuf.getPacket_id();
                }
            }

            long pre_tm_ms = cur_tm_ms;
            cur_tm_ms = System.currentTimeMillis();

            if( cur_tm_ms - pre_tm_ms >= 50 )
            {
                logger.warn("sessionId:" + get_id() + " packet_process id:" + msgBuf.getPacket_id() + " time:" + ( cur_tm_ms - pre_tm_ms ) );
            }

            i++;
            if( process_count > 0 && process_count <= i )
            {
                break;
            }

        }

        return 0;
    }

     public void addProcessMsg(MsgBuf msgBuf)
     {
         receive_queue.add(msgBuf);
     }

     public void addSendMsg(Message msg)
     {
         int protocolId =  ResponseMessageRegitser.Instance.getProtocolIdByMessageClass(msg);

         MsgBuf msgBuf = new MsgBuf();
         msgBuf.setNeed_route(false);
         msgBuf.setPacket_id(protocolId);
         msgBuf.setMsg(msg);

         this.send_queue.add(msgBuf);
     }



    public void dispose()
    {
        if( m_state == e_peer_state.e_ps_disconnected )
        {
            return;
        }

        if( !channelHandlerContext.isRemoved() )
        {
            channelHandlerContext.close();
        }

        m_state = e_peer_state.e_ps_disconnected;
    }

    public void set_check_time()
    {
        m_bcheck_time = true;
    }

    public int send_msg(Message.Builder builder)
    {
        return send_msg(builder.build());
    }

    public int send_msg(Message msg)
    {
        if( channelHandlerContext == null || channelHandlerContext.channel() == null
                || !channelHandlerContext.channel().isActive() )
        {
            return -1;
        }

        int protocolId =  ResponseMessageRegitser.Instance.getProtocolIdByMessageClass(msg);

        MsgBuf msgBuf = new MsgBuf();
        msgBuf.setNeed_route(false);
        msgBuf.setPacket_id(protocolId);
        msgBuf.setMsg(msg);

        channelHandlerContext.writeAndFlush(msgBuf);
        return 1;
    }

    public int send_msg(int packet_id,Message msg)
    {
        if( m_state != e_peer_state.e_ps_connected )
        {
            return -1;
        }

        MsgBuf msgBuf = new MsgBuf();
        msgBuf.setNeed_route(false);
        msgBuf.setPacket_id(packet_id);
        msgBuf.setMsg(msg);

        channelHandlerContext.writeAndFlush(msgBuf);
        return 1;
    }


    public int send_msg(int packet_id,ByteString byteString)
    {
        if(m_state != e_peer_state.e_ps_connected)
        {
            logger.error("the pee is not e_ps_connected:" + m_state);
            return -1;
        }

        MsgBuf msgBuf = new MsgBuf();
        msgBuf.setPacket_id(packet_id);
        msgBuf.setNeed_route(true);
        msgBuf.setByteString(byteString);

        channelHandlerContext.writeAndFlush(msgBuf);

        return 1;
    }

    //TODO wisp
//    public int get_remote_port()
//    {
//        InetSocketAddress inetSocketAddress = (InetSocketAddress)channelHandlerContext.channel().localAddress();
//        return inetSocketAddress.getPort();
//    }

    //TODO wisp
    public String get_remote_ip()
    {
        InetSocketAddress inetSocketAddress = (InetSocketAddress)channelHandlerContext.channel().localAddress();
        return inetSocketAddress.getHostString();
    }

    public void set_remote_type(int _type)
    {
        remote_type = _type;
    }

    public int get_remote_type()
    {
        return remote_type;
    }

    public int get_remote_id()
    {
        return remote_id;
    }

    public void set_remote_id(int _id)
    {
        remote_id = _id;
    }

    public e_peer_state get_state()
    {
        return m_state;
    }

    public void set_state(e_peer_state m_state) {
        this.m_state = m_state;
    }

    public int get_id()
    {
        return m_peerId;
    }

    public abstract int get_type();

    public void set_route_handler(IRouterHandler route_handler )
    {
        m_need_route = true;
        m_router_handler = route_handler;
    }
}
