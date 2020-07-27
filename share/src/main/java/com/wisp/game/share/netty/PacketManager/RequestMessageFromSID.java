package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;

public abstract class RequestMessageFromSID <M extends Message,P extends PeerTcp> implements IRequestMessage<M,P,Object>  {

    @Override
    public  boolean packet_process(P peer, M msg){return false;};

    @Override
    public abstract boolean packet_process(P peer, int sessionId, M msg);

    @Override
    public boolean packet_process(P peer,Object player,M msg)
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
