package com.wisp.game.share.netty.infos;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;

public class MsgBuf {
   private int packet_id;
   private boolean need_route;
   private ByteBuf msgBuff;
   private ByteString byteString;
   private byte[] bytes;
   private Message msg;

    public MsgBuf() {
        packet_id = 0;
        need_route = false;
    }

    public int getPacket_id() {
        return packet_id;
    }

    public void setPacket_id(int packet_id) {
        this.packet_id = packet_id;
    }

    public boolean isNeed_route() {
        return need_route;
    }

    public void setNeed_route(boolean need_route) {
        this.need_route = need_route;
    }

    public ByteBuf getMsgBuff() {
        return msgBuff;
    }

    public void setMsgBuff(ByteBuf msgBuff) {
        this.msgBuff = msgBuff;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }


    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


    public ByteString getByteString() {
        return byteString;
    }

    public void setByteString(ByteString byteString) {
        this.byteString = byteString;
    }
}
