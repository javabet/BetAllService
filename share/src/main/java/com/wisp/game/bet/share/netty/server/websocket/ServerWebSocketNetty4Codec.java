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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerWebSocketNetty4Codec extends ByteToMessageCodec<MsgBuf> {

    private RequestMessageRegister messageRegister;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgBuf msgBuf, ByteBuf byteBuf) throws Exception {
        ByteString byteString;

        if( msgBuf.isNeed_route() )
        {
            byteString = msgBuf.getByteString();
        }
        else
        {
            byteString = msgBuf.getMsg().toByteString();
        }


        byteBuf.writeIntLE( msgBuf.getPacket_id() );
        byteBuf.writeShortLE(byteString.size());
        byteBuf.writeBytes(byteString.toByteArray());
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


        //byte[] msgBytes = readMsgBuf.array();
        ByteString byteString = ByteString.copyFrom(readMsgBuf.array());
        Message message =  messageRegister.getMessageByProtocolId(packetId,byteString);

        MsgBuf msgBuf = new MsgBuf();
        msgBuf.setPacket_id(packetId);

        if( message == null )
        {
            msgBuf.setNeed_route(true);
            msgBuf.setByteString(byteString);
        }
        else
        {
            msgBuf.setMsg(message);
        }

        if( packetId != 301 )
        {
            logger.info("recevie protocolId:" + packetId);
        }


        list.add(msgBuf);
    }
}
