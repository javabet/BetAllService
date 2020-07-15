package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;

public abstract class DefaultRequestMessage<M extends Message ,P extends PeerTcp > implements IRequestMessage<M,P,Object> {

    public DefaultRequestMessage() {
    }


    @Override
    public abstract boolean packet_process(P peer, M msg);
    @Override
    public boolean packet_process(P peer, int sessionId, M msg) {
        return false;
    }

    @Override
    public boolean packet_process(P peer, Object player, M msg) {
        return false;
    }

    @Override
    public boolean is_from_gate() {
        return false;
    }

    @Override
    public boolean use_sessionid() {
        return false;
    }
}
