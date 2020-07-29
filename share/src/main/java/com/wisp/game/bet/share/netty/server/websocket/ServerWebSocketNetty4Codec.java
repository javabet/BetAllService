package com.wisp.game.bet.share.netty.server.websocket;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import com.wisp.game.bet.share.netty.infos.MsgBuf;
import com.wisp.game.bet.core.SpringContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class ServerWebSocketNetty4Codec extends ByteToMessageCodec<MsgBuf> {

    private RequestMessageRegister messageRegister;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgBuf msgBuf, ByteBuf byteBuf) throws Exception {
        byte[] bytes;

        if( msgBuf.isNeed_route() )
        {
            bytes = msgBuf.getBytes();
        }
        else
        {
            bytes = msgBuf.getMsg().toByteArray();
        }


        byteBuf.writeIntLE( msgBuf.getPacket_id() );
        byteBuf.writeShortLE(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if( byteBuf.readableBytes() < 6 )
        {
            return;
        }

        if( messageRegister == null )
        {
            messageRegister = SpringContextHolder.getBean(RequestMessageRegister.class);
        }

        ByteBuf packHeadBuf = Unpooled.buffer(6);
        byteBuf.readBytes(packHeadBuf);

        int packetId = packHeadBuf.readIntLE();
        int packetSize = packHeadBuf.readShortLE();


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
            msgBuf.setNeed_route(true);
            msgBuf.setBytes(msgBytes);
            msgBuf.setByteString(ByteString.copyFrom(msgBytes));
        }
        else
        {
            msgBuf.setMsg(message);
        }

        System.out.printf("protocolId:" + packetId + "\n");

        list.add(msgBuf);
    }
}
