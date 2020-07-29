package com.wisp.game.bet.share.netty.server.tcp;

import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.head.PacketHeadC;
import com.wisp.game.bet.share.netty.head.PacketHeadS;
import com.wisp.game.bet.share.netty.infos.MsgBuf;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.common.EnableProcessinfo;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class ServerNetty4Codec extends ByteToMessageCodec<MsgBuf> {


    private PacketHeadC packetHead_r;
    private PacketHeadS packetHead_s;

    private RequestMessageRegister messageRegister;

    public ServerNetty4Codec() {
        packetHead_r = new PacketHeadC();
        packetHead_s = new PacketHeadS();
    }

    protected  void encode(ChannelHandlerContext ctx, MsgBuf msgBuf, ByteBuf byteBuf) throws Exception
    {
        int time = EnableProcessinfo.get_tick_count();
        byte[] bytes;

        if( msgBuf.isNeed_route() )
        {
            bytes = msgBuf.getBytes();
        }
        else
        {
            bytes = msgBuf.getMsg().toByteArray();
        }

        byteBuf.writeIntLE(time);
        byteBuf.writeShortLE( msgBuf.getPacket_id() );
        byteBuf.writeShortLE(bytes.length);
        byteBuf.writeByte(33);
        byteBuf.writeByte(36);
        byteBuf.writeByte(37);
        byteBuf.writeByte(63);
        byteBuf.writeBytes(bytes);

    }

    protected  void decode(ChannelHandlerContext var1, ByteBuf buf, List<Object> list) throws Exception
    {
        if( buf.readableBytes() < 12 )
        {
            return;
        }

        if( messageRegister == null )
        {
            messageRegister = SpringContextHolder.getBean(RequestMessageRegister.class);
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

        System.out.printf("protocolId:" + packetId + "\n");

        list.add(msgBuf);
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
