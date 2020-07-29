package com.wisp.game.bet.share.netty.server.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class ServerWebSocketCodec extends MessageToMessageCodec<WebSocketFrame, ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(byteBuf);
        binaryWebSocketFrame.retain();
        list.add(binaryWebSocketFrame);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame, List<Object> list) throws Exception {
        BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame)webSocketFrame;
        ByteBuf byteBuf = binaryWebSocketFrame.content();

        if( byteBuf.readableBytes() < 6 )
        {
            return;
        }

        byteBuf.markReaderIndex();
        ByteBuf packHeadBuf = Unpooled.buffer(6);
        byteBuf.readBytes(packHeadBuf);

        int packetId = packHeadBuf.readIntLE();
        int packetSize = packHeadBuf.readShortLE();


        if( byteBuf.readableBytes() < packetSize)
        {
            byteBuf.resetReaderIndex();
            return;
        }
        byteBuf.resetReaderIndex();

        ByteBuf packageeByteBuf =  Unpooled.buffer(6 + packetSize);
        byteBuf.readBytes(packageeByteBuf);

        list.add(packageeByteBuf);
    }
}
