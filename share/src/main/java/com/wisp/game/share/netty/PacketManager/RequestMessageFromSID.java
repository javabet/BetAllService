package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;

public abstract class RequestMessageFromSID <T extends Message,P extends PeerTcp,P2> implements IRequestMessage  {

    @Override
    public abstract   boolean packet_process(PeerTcp peer, Message msg);

    @Override
    public abstract boolean packet_process(PeerTcp peer, int sessionId, Message msg);

    @Override
    public boolean packet_process(PeerTcp peer,Object player,Message msg)
    {
        return false;
    }

    @Override
    public boolean is_from_gate() {
        return true;
    }

    @Override
    public boolean use_sessionid() {
        return true;
    }

}
