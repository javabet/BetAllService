package com.wisp.game.bet.sshare;

import com.google.protobuf.ByteString;
import com.wisp.game.bet.share.netty.PeerTcp;

public interface IRouterHandler<T extends PeerTcp> {
    public boolean route_handler(T peer, int msgid, ByteString byteString);
}
