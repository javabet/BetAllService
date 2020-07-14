package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;

public abstract class DefaultRequestMessage<T extends Message,P extends PeerTcp,P2> implements IRequestMessage {


    @Override
    public abstract   boolean packet_process(PeerTcp peer, Message msg);

    @Override
    public boolean packet_process(PeerTcp peer, int sessionId, Message msg) {
        return false;
    }

    @Override
    public boolean packet_process(PeerTcp peer, Object player, Message msg) {
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
