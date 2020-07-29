package com.wisp.game.bet.share.netty;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.infos.MsgBuf;
import com.wisp.game.bet.share.netty.infos.e_peer_state;
import com.wisp.game.bet.sshare.IRouterHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class PeerTcp {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected boolean m_is_websocket = false;

    private boolean m_send_encrypt = false;
    private boolean m_recv_decrypt = false;
    private boolean m_set_xor_seed = false;

    protected int remoto_id;
    protected int remoto_type;
    protected boolean m_need_route;
    protected boolean m_bcheck_time;
    protected boolean m_bread;
    protected IRouterHandler m_router_handler;

    protected e_peer_state m_state;

    protected ChannelHandlerContext channelHandlerContext;
    private int m_id;
    private ChannelId channelId;

    private Queue<MsgBuf> receive_queue = new ConcurrentLinkedQueue<>();

    private Queue<MsgBuf> send_queue = new ConcurrentLinkedQueue<>();


    public PeerTcp() {

    }

    public void init_peer(ChannelHandlerContext channelHandlerContext, boolean _encrypt, boolean _is_web)
    {
        this.channelHandlerContext = channelHandlerContext;
        m_is_websocket = _is_web;
        this.channelId = channelHandlerContext.channel().id();
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

    public int send_msg(Message msg)
    {
        if( !channelHandlerContext.channel().isActive() )
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

    public int send_msg(int packet_id, ByteString byteString)
    {
        return send_msg(packet_id,byteString.toByteArray());
    }

    public int send_msg(int packet_id,byte[] bytes)
    {
        if(m_state != e_peer_state.e_ps_connected)
        {
            return -1;
        }

        MsgBuf msgBuf = new MsgBuf();
        msgBuf.setPacket_id(packet_id);
        msgBuf.setNeed_route(true);
        msgBuf.setBytes(bytes);

        channelHandlerContext.writeAndFlush(msgBuf);

        return 1;
    }

    public static char[] xor_ary = new char[]{
            0x0b, 0x33, 0x1b, 0x04, 0x2c, 0x3c, 0x3b, 0x0d, 0x24, 0x0c, 0x1c, 0x37, 0x2b, 0x1d, 0x05, 0x23,
            0x2d, 0x19, 0x2c, 0x1a, 0x1d, 0x21, 0x32, 0x1c, 0x41, 0x0b, 0x42, 0x18, 0x3c, 0x3d, 0x38, 0x0c,
            0x31, 0xDC, 0x4A, 0xE5, 0x52, 0xD8, 0xE8, 0xE8, 0x2C, 0xAF, 0xF3, 0x2C, 0xA9, 0x1D, 0x7B, 0x28,
            0x42, 0x09, 0xF4, 0x15, 0x72, 0xA1, 0xB7, 0x2A, 0xB3, 0x0D, 0x78, 0x80, 0xFA, 0x02, 0x55, 0x59,
            0x22, 0x4f, 0x02, 0x3a, 0x31, 0x01, 0x39, 0x0d, 0x2b, 0x3b, 0x1b, 0x11, 0x08, 0x09, 0x28, 0x0a,
            0x4f, 0x26, 0x4e, 0x06, 0x32, 0x22, 0x14, 0x3d, 0x16, 0x0d, 0x3b, 0x36, 0x2a, 0x38, 0x07, 0x24,
            0x38, 0x0a, 0x11, 0x05, 0x2d, 0x1d, 0x09, 0x1c, 0x17, 0x2b, 0x0b, 0x27, 0x3a, 0x68, 0x37, 0x03,
            0x12, 0x15, 0x02, 0x35, 0x21, 0x3c, 0x39, 0x2c, 0x25, 0x0c, 0xeb, 0x08, 0x1a, 0x29, 0x18, 0x23,
            0x4f, 0x26, 0x4e, 0x06, 0x32, 0x22, 0x14, 0x3d, 0x16, 0x0d, 0x3b, 0x36, 0xfa, 0x38, 0xc7, 0x64,
            0x31, 0x0a, 0x11, 0x05, 0x2d, 0x1d, 0x09, 0x1c, 0x17, 0x2b, 0x2f, 0x22, 0x3a, 0x28, 0xa7, 0x03,
            0x85, 0x3c, 0x84, 0x12, 0x64, 0xFD, 0xCA, 0x70, 0x0b, 0x6A, 0x99, 0xD7, 0x88, 0xB8, 0x5E, 0x32,
            0x9F, 0x08, 0xF0, 0x11, 0x94, 0x0C, 0x28, 0x3E, 0x3b, 0x2C, 0xE5, 0x0E, 0xDC, 0x5C, 0x6F, 0x39,
            0x74, 0x2a, 0x34, 0x3b, 0x05, 0xC9, 0x59, 0x55, 0x0d, 0xB3, 0x15, 0xF3, 0x09, 0x6E, 0x84, 0xe4,
            0x85, 0x3a, 0x32, 0x0b, 0x98, 0x71, 0xDE, 0xF9, 0x2b, 0x41, 0x81, 0x93, 0xCD, 0x42, 0xDC, 0x09,
            0x29, 0x1a, 0x5F, 0x1b, 0x02, 0x9E, 0x62, 0xEA, 0x0c, 0x56, 0xC2, 0xAF, 0x7A, 0xAC, 0x6d, 0x39,
            0x8D, 0x2a, 0x8C, 0x3b, 0x44, 0xCC, 0xE3, 0xEE, 0x0d, 0x3E, 0xDE, 0x20, 0x74, 0xCB, 0x60, 0x16
    };

    /**
     * 此函数有问题，需要修改
     * @param str
     * @param len
     */
    public void set_xor_seed(char str,int len)
    {
        if( len > 16 ) len = 16;

        m_set_xor_seed = true;
    }

    //TODO wisp
    public int get_remote_port()
    {
        InetSocketAddress inetSocketAddress = (InetSocketAddress)channelHandlerContext.channel().localAddress();
        return inetSocketAddress.getPort();
    }

    //TODO wisp
    public String get_remote_ip()
    {
        InetSocketAddress inetSocketAddress = (InetSocketAddress)channelHandlerContext.channel().localAddress();
        return inetSocketAddress.getHostString();
    }

    public void set_remote_type(int _type)
    {
        remoto_type = _type;
    }

    public int get_remote_type()
    {
        return remoto_type;
    }

    public int get_remote_id()
    {
        return remoto_id;
    }

    public void set_remote_id(int _id)
    {
        remoto_id = _id;
    }

    public e_peer_state get_state()
    {
        return m_state;
    }

    public void set_state(e_peer_state m_state) {
        this.m_state = m_state;
    }

    public void set_id(int peer_id)
    {
        m_id = peer_id;
    }

    public int get_id()
    {
        return m_id;
    }

    public abstract int get_type();

    public ChannelId getChannelId() {
        return channelId;
    }

    public void setChannelId(ChannelId channelId) {
        this.channelId = channelId;
    }

    public void set_route_handler(IRouterHandler route_handler )
    {
        m_need_route = true;
        m_router_handler = route_handler;
    }
}
