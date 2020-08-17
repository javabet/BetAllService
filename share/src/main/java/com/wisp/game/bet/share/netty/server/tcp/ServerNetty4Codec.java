package com.wisp.game.bet.share.netty.server.tcp;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.head.PacketHeadC;
import com.wisp.game.bet.share.netty.head.PacketHeadS;
import com.wisp.game.bet.share.netty.infos.MsgBuf;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.common.EnableProcessinfo;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerNetty4Codec extends ByteToMessageCodec<MsgBuf> {

    private static int HEAD_LENGTH = 8;

    private Logger logger = LoggerFactory.getLogger(getClass());

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
        ByteString byteString;

        if( msgBuf.isNeed_route() )
        {
            byteString = msgBuf.getByteString();
        }
        else
        {
            byteString = msgBuf.getMsg().toByteString();
        }

        byteBuf.writeIntLE(time);
        byteBuf.writeShortLE( msgBuf.getPacket_id() );
        byteBuf.writeShortLE(byteString.size());
        //byteBuf.writeByte(33);
        //byteBuf.writeByte(36);
        //byteBuf.writeByte(37);
        //byteBuf.writeByte(63);
        byteBuf.writeBytes(byteString.toByteArray());

        if(msgBuf.getPacket_id() == 6 && msgBuf.getMsg() != null )
        {
            logger.info(" send trans packet,the packetId: " + ProtocolClassUtils.getProtocolByClass(msgBuf.getMsg().getClass()));
        }


    }

    protected  void decode(ChannelHandlerContext var1, ByteBuf buf, List<Object> list) throws Exception
    {
        if( buf.readableBytes() < HEAD_LENGTH )
        {
            return;
        }

        if( messageRegister == null )
        {
            messageRegister = SpringContextHolder.getBean(RequestMessageRegister.class);
        }

        ByteBuf packHeadBuf = Unpooled.buffer(HEAD_LENGTH);
        buf.readBytes(packHeadBuf);

        int time =  packHeadBuf.readIntLE();
        int packetId = packHeadBuf.readShortLE();
        int packetSize = packHeadBuf.readShortLE();
        //byte checkMark0 = packHeadBuf.readByte();
        //byte checkMark1 = packHeadBuf.readByte();
        //byte checkMark2 = packHeadBuf.readByte();
        //byte checkMark3 = packHeadBuf.readByte();

        if( buf.readableBytes() < packetSize)
        {
            buf.resetReaderIndex();
            return;
        }

        ByteBuf readMsgBuf = Unpooled.buffer(packetSize);
        buf.readBytes(readMsgBuf);


        //byte[] msgBytes = readMsgBuf.array();
        ByteString byteString = ByteString.copyFrom(readMsgBuf.array());


        Message message =  messageRegister.getMessageByProtocolId(packetId,byteString);

        MsgBuf msgBuf = new MsgBuf();
        msgBuf.setPacket_id(packetId);

        if( message == null )
        {
            msgBuf.setByteString(byteString);
        }
        else
        {
            msgBuf.setMsg(message);
        }

        if( packetId != 4 && packetId != 9 && packetId != 5 )
        {
            logger.info(" recevie protocolId:" + packetId);
        }

        list.add(msgBuf);
    }

}
