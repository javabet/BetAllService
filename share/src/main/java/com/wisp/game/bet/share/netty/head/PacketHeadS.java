package com.wisp.game.bet.share.netty.head;

import io.netty.buffer.ByteBuf;

public class PacketHeadS implements IPacketHead {

    private PacketHeadInfo packetHeadInfo;

    public PacketHeadS() {
        packetHeadInfo = new PacketHeadInfo();
    }

    @Override
    public void init(int pid, int psize) {

    }

    @Override
    public void to_array(ByteBuf buf) {
        this.to_array(buf,0);
    }

    @Override
    public void to_array(ByteBuf buf, int offset) {
        buf.writeBytes( packetHeadInfo.to_array() );
    }

    @Override
    public void parse_from(ByteBuf buf) {
        this.parse_from(buf,0);
    }

    @Override
    public void parse_from(ByteBuf buf, int offset) {

    }

    @Override
    public void buffer_decryption(ByteBuf buf, int len) {
        //do nothing
    }

    @Override
    public boolean check_head() {
        return true;
    }

    @Override
    public int get_id() {
        return packetHeadInfo.getPacket_id();
    }

    @Override
    public int get_size() {
        return packetHeadInfo.getPacket_size();
    }

    public PacketHeadInfo getPacketHeadInfo() {
        return packetHeadInfo;
    }

    public void setPacketHeadInfo(PacketHeadInfo packetHeadInfo) {
        this.packetHeadInfo = packetHeadInfo;
    }
}
