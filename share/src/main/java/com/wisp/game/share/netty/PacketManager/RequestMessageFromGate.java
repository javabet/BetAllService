package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;

public abstract class RequestMessageFromGate <T extends Message,P extends PeerTcp,P2> implements IRequestMessage {

    @Override
    public  boolean packet_process(PeerTcp peer, Message msg) {
        return false;
    }

    @Override
    public boolean packet_process(PeerTcp peer, int sessionId, Message msg) {
        return false;
    }


    @Override
    public boolean is_from_gate() {
        return true;
    }

    @Override
    public boolean use_sessionid() {
        return false;
    }


}
