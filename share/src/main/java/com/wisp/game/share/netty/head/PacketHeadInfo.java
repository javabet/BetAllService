package com.wisp.game.share.netty.head;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketHeadInfo {
    private int tick_time;

    private int packet_id;

    private int packet_size;

    private byte[] head_mark = new byte[]{'!','$','%','?'};


    public PacketHeadInfo() {

    }

    public byte[] to_array()
    {
        ByteBuf byteBuf = Unpooled.buffer(12);
        byteBuf.writeInt(tick_time);
        byteBuf.writeShort(packet_id);
        byteBuf.writeShort( packet_size );
        byteBuf.writeChar(head_mark[0]);
        byteBuf.writeChar(head_mark[1]);
        byteBuf.writeChar(head_mark[2]);
        byteBuf.writeChar(head_mark[3]);

        return byteBuf.array();
    }

    public void from_array(ByteBuf byteBuf)
    {
        tick_time =  byteBuf.readInt();
        packet_id = byteBuf.readShort();
        packet_size = byteBuf.readShort();
        head_mark = new byte[]{byteBuf.readByte(),byteBuf.readByte(),byteBuf.readByte(),byteBuf.readByte()};
    }

    public int getTick_time() {
        return tick_time;
    }

    public void setTick_time(int tick_time) {
        this.tick_time = tick_time;
    }

    public int getPacket_id() {
        return packet_id;
    }

    public void setPacket_id(int packet_id) {
        this.packet_id = packet_id;
    }

    public int getPacket_size() {
        return packet_size;
    }

    public void setPacket_size(int packet_size) {
        this.packet_size = packet_size;
    }

    public byte[] getHead_mark() {
        return head_mark;
    }

    public void setHead_mark(byte[] head_mark) {
        this.head_mark = head_mark;
    }
}
