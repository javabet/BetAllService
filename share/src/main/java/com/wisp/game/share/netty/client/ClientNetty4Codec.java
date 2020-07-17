package com.wisp.game.share.netty.client;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.wisp.game.core.SpringContextHolder;
import com.wisp.game.share.common.EnableProcessinfo;
import com.wisp.game.share.netty.RequestMessageRegister;
import com.wisp.game.share.netty.infos.MsgBuf;
import com.wisp.game.share.netty.infos.ProtocolInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class ClientNetty4Codec extends MessageToMessageCodec<ByteBuf, MsgBuf> {

    private Logger logger = LoggerFactory.getLogger(ClientNetty4Codec.class);

    private RequestMessageRegister messageRegister;

    protected  void encode(ChannelHandlerContext var1, MsgBuf msgBuf, List<Object> list) throws Exception {

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

        ByteBuf byteBuf = Unpooled.buffer(12 + bytes.length);

        byteBuf.writeIntLE(time);
        byteBuf.writeShortLE( msgBuf.getPacket_id() );
        byteBuf.writeShortLE(bytes.length);
        byteBuf.writeByte(33);
        byteBuf.writeByte(36);
        byteBuf.writeByte(37);
        byteBuf.writeByte(63);
        byteBuf.writeBytes(bytes);

        list.add(byteBuf);
    }

    protected  void decode(ChannelHandlerContext var1, ByteBuf byteBuf, List<Object> list) throws Exception
    {

        if( byteBuf.readableBytes() < 12 )
        {
            return;
        }

        if( messageRegister == null )
        {
            messageRegister = SpringContextHolder.getBean(RequestMessageRegister.class);
        }

        ByteBuf packHeadBuf = Unpooled.buffer(12);
        byteBuf.readBytes(packHeadBuf);

        int time =  packHeadBuf.readIntLE();
        int packetId = packHeadBuf.readShortLE();
        int packetSize = packHeadBuf.readShortLE();
        byte checkMark0 = packHeadBuf.readByte();
        byte checkMark1 = packHeadBuf.readByte();
        byte checkMark2 = packHeadBuf.readByte();
        byte checkMark3 = packHeadBuf.readByte();

        if( byteBuf.readableBytes() < packetSize)
        {
            byteBuf.resetReaderIndex();
            return;
        }

        ByteBuf readMsgBuf = Unpooled.buffer(packetSize);
        byteBuf.readBytes(readMsgBuf);


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

}
