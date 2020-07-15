package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;

public abstract class RequestMessageFromGate <M extends Message,P extends PeerTcp,P2> implements IRequestMessage<M,P,P2> {

    @Override
    public  boolean packet_process(P peer, M msg) {
        return false;
    }

    @Override
    public boolean packet_process(P peer, int sessionId, M msg) {
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
