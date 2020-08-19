package com.wisp.game.bet.share.netty.server.tcp;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.head.PacketHeadC;
import com.wisp.game.bet.share.netty.head.PacketHeadS;
import com.wisp.game.bet.share.netty.infos.MsgBuf;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.common.EnableProcessinfo;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import com.wisp.game.bet.share.netty.server.buffer.ChannelBuffer;
import com.wisp.game.bet.share.netty.server.buffer.ChannelBuffers;
import com.wisp.game.bet.share.netty.server.buffer.DynamicChannelBuffer;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ServerNetty4Codec extends ByteToMessageCodec<MsgBuf> {

    private static int HEAD_LENGTH = 8;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private RequestMessageRegister messageRegister;
    private int bufferSize;
    private ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(1024);

    public ServerNetty4Codec() {
        bufferSize = 8 * 1024;          //默认8k

        //logger.error("new init the serverNetty");
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
    }

    protected  void decode(ChannelHandlerContext var1, ByteBuf buf, List<Object> list) throws Exception
    {
        /**
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

         **/

        if( buf.readableBytes() <= 0 )
        {
            return;
        }

        if( messageRegister == null )
        {
            messageRegister = SpringContextHolder.getBean(RequestMessageRegister.class);
        }

        byte[] bytes =  new byte[buf.readableBytes()];
        buf.readBytes(bytes);



        ChannelBuffer message;
        if (buffer.readable()) {
            logger.error("断点续传");
            if (buffer instanceof DynamicChannelBuffer) {
                buffer.writeBytes(bytes);
                message = buffer;
            } else {
                int size = buffer.readableBytes() + buf.readableBytes();
                message = ChannelBuffers.dynamicBuffer(size > bufferSize ? size : bufferSize);
                message.writeBytes(buffer, buffer.readableBytes());
                message.writeBytes(bytes);
            }
        } else {
            message = ChannelBuffers.wrappedBuffer(bytes);
        }

        MsgBuf msgbuf;
        int saveReaderIndex;

        try {
            // decode object.
            do {
                saveReaderIndex = message.readerIndex();
                msgbuf = innerdecode(message);
                if (msgbuf == null) {
                    message.readerIndex(saveReaderIndex);
                    break;
                } else {
                    if (saveReaderIndex == message.readerIndex()) {
                        buffer = ChannelBuffers.EMPTY_BUFFER;
                        throw new IOException("Decode without read data.");
                    }
                   list.add(msgbuf);
                }
            } while (message.readable());
        } finally {
            if (message.readable()) {
                message.discardReadBytes();
                buffer = message;
            } else {
                buffer = ChannelBuffers.EMPTY_BUFFER;
            }
            //NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
        }
    }

    //暂时不使用
    private MsgBuf innerdecode(ByteBuf byteBuf)
    {
        ByteBuf packHeadBuf = Unpooled.buffer(HEAD_LENGTH);
        byteBuf.readBytes(packHeadBuf);

        int time =  packHeadBuf.readIntLE();
        int packetId = packHeadBuf.readShortLE();
        int packetSize = packHeadBuf.readShortLE();
        //byte checkMark0 = packHeadBuf.readByte();
        //byte checkMark1 = packHeadBuf.readByte();
        //byte checkMark2 = packHeadBuf.readByte();
        //byte checkMark3 = packHeadBuf.readByte();

        if( byteBuf.readableBytes() < packetSize)
        {
            return null;
        }

        ByteBuf readMsgBuf = Unpooled.buffer(packetSize);
        byteBuf.readBytes(readMsgBuf);

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

        return msgBuf;
    }

    private MsgBuf innerdecode(ChannelBuffer byteBuf)
    {
        byte[] headBytes = new byte[HEAD_LENGTH];
        byteBuf.readBytes(headBytes);
        ByteBuf packHeadBuf = Unpooled.wrappedBuffer(headBytes);


        int time =  packHeadBuf.readIntLE();
        int packetId = packHeadBuf.readShortLE();
        int packetSize = packHeadBuf.readShortLE();
        //byte checkMark0 = packHeadBuf.readByte();
        //byte checkMark1 = packHeadBuf.readByte();
        //byte checkMark2 = packHeadBuf.readByte();
        //byte checkMark3 = packHeadBuf.readByte();

        if( byteBuf.readableBytes() < packetSize)
        {
            return null;
        }

        byte[] bodyByte = new byte[packetSize];
        byteBuf.readBytes(bodyByte);
        ByteBuf readMsgBuf = Unpooled.wrappedBuffer(bodyByte);

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

        return msgBuf;
    }

}
