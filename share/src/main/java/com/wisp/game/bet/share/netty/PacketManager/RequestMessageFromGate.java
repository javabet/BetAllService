package com.wisp.game.bet.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.PeerTcp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RequestMessageFromGate <M extends Message,P extends PeerTcp,P2> implements IRequestMessage<M,P,P2> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

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
