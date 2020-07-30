package com.wisp.game.bet.logic.sshare;

import com.google.protobuf.Message;

public class MsgPacketOne {
    private int packet_id;
    private Message msg_packet;

    public int getPacket_id() {
        return packet_id;
    }

    public void setPacket_id(int packet_id) {
        this.packet_id = packet_id;
    }

    public Message getMsg_packet() {
        return msg_packet;
    }

    public void setMsg_packet(Message msg_packet) {
        this.msg_packet = msg_packet;
    }
}
