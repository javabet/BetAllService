package com.wisp.game.share.netty.server;

import com.google.protobuf.Message;
import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.share.netty.RequestMessageRegister;
import com.wisp.game.share.netty.head.IPacketHead;
import com.wisp.game.share.netty.head.PacketHeadC;
import com.wisp.game.share.netty.head.PacketHeadS;
import com.wisp.game.share.netty.infos.MsgBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.nio.ByteOrder;
import java.util.List;

public class ServiceNetty4Codec extends ByteToMessageCodec<MsgBuf> {


    private PacketHeadC packetHead_r;
    private PacketHeadS packetHead_s;

    private RequestMessageRegister messageRegister;

    public ServiceNetty4Codec() {
        packetHead_r = new PacketHeadC();
        packetHead_s = new PacketHeadS();
    }

    protected  void encode(ChannelHandlerContext ctx, MsgBuf var2, ByteBuf var3) throws Exception
    {

    }

    protected  void decode(ChannelHandlerContext var1, ByteBuf buf, List<Object> list) throws Exception
    {
        if( buf.readableBytes() < 12 )
        {
            return;
        }

        ByteBuf packHeadBuf = Unpooled.buffer(12);
        buf.readBytes(packHeadBuf);

        int time =  packHeadBuf.readIntLE();
        int packetId = packHeadBuf.readShortLE();
        int packetSize = packHeadBuf.readShortLE();
        byte checkMark0 = packHeadBuf.readByte();
        byte checkMark1 = packHeadBuf.readByte();
        byte checkMark2 = packHeadBuf.readByte();
        byte checkMark3 = packHeadBuf.readByte();

        if( buf.readableBytes() < packetSize)
        {
            buf.resetReaderIndex();
            return;
        }

        ByteBuf readMsgBuf = Unpooled.buffer(packetSize);
        buf.readBytes(readMsgBuf);

        if( messageRegister == null )
        {
            messageRegister = SpringContextHolder.getBean(RequestMessageRegister.class);
        }

        byte[] msgBytes = readMsgBuf.array();
        Message message =  messageRegister.getMessageByProtocolId(packetId,msgBytes);

        MsgBuf msgBuf = new MsgBuf();
        msgBuf.setPacket_id(packetId);

        if( message == null )
        {
            msgBuf.setBytes(msgBytes);
        }
        else
        {
            msgBuf.setMsg(message);
        }


        list.add(message);
    }


    private void printEveryOne(ByteBuf byteBuf)
    {
        byteBuf.markReaderIndex();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 12;i++)
        {
            builder.append(" ").append( byteBuf.readByte() );
        }

        System.out.printf("buf:" + builder.toString());
        byteBuf.resetReaderIndex();
    }

}
