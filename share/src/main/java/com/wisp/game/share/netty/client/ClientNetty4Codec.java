package com.wisp.game.share.netty.client;

import com.google.protobuf.GeneratedMessage;
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

public class ClientNetty4Codec extends MessageToMessageCodec<ByteBuf,GeneratedMessage> {

    private Logger logger = LoggerFactory.getLogger(ClientNetty4Codec.class);


    protected  void encode(ChannelHandlerContext var1, GeneratedMessage message, List<Object> var3) throws Exception {

        int protocolId = -1;
        try
        {
            Class<?> cls = message.getClass();
            Method method = cls.getMethod("getPacketId");
            Object obj = method.invoke(message);
            Method getNumberMethod = obj.getClass().getMethod("getNumber");
            protocolId = (int) getNumberMethod.invoke(obj);
        }
        catch (Exception exception)
        {
            logger.error("ClientNetty4Codec has error,the message can't find the protocolId" + message.toString());
            return;
        }

        byte[] bytes = message.toByteArray();

        ByteBuf byteBuf =  Unpooled.buffer(bytes.length + 12);
        byteBuf.writeInt(0);
        byteBuf.writeShort(protocolId);
        byteBuf.writeShort(bytes.length);
        byteBuf.writeInt(100);
        //byteBuf.writeChar('1');//!
        //byteBuf.writeChar('$');
        //byteBuf.writeChar('%');
        //byteBuf.writeChar('?');
        byteBuf.writeBytes(bytes);

        byte[] newBytes = new byte[bytes.length + 6];
        byteBuf.readBytes(newBytes);

        var3.add(newBytes);


        //格式 protoclId + len + buff ,(4,2,buff)
        System.out.printf(".......encode");
    }

    protected  void decode(ChannelHandlerContext var1, ByteBuf message, List<Object> var3) throws Exception
    {

        if( message.readableBytes() >= 6 )
        {
            message.markReaderIndex();

            int protocolId = message.readInt();
            int len = message.readShort();

            if( message.readableBytes() >= len )
            {
                byte[] bytes = new byte[len];
                message.readBytes(bytes,0,len);

                ProtocolInfo protocolInfo = new ProtocolInfo();
                protocolInfo.setProtocolId(protocolId);
                protocolInfo.setBytes(bytes);
                var3.add(protocolInfo);
            }
            else
            {
                //包的长度不够，需要等待后续的数据过来
                message.resetReaderIndex();
            }


        }

        System.out.printf("cls:" + message.getClass().toString());
        System.out.printf( "message:"+ message.toString());
        /**
        int protocolId = 100;

        byte[] bytes = message.toByteArray();

        ByteBuf byteBuf =  Unpooled.buffer(bytes.length + 6);
        byteBuf.writeInt(protocolId);
        byteBuf.writeShort(bytes.length);
        byteBuf.writeBytes(bytes);

        byte[] newBytes = new byte[bytes.length + 6];
        byteBuf.readBytes(newBytes);

        var3.add(byteBuf);
         **/

        System.out.printf("go this...decode");
    }

}
