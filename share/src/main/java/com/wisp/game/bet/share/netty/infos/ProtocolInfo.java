package com.wisp.game.bet.share.netty.infos;

public class ProtocolInfo {
    private int protocolId;
    private byte[] bytes;

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
