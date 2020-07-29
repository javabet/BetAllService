package com.wisp.game.bet.share.netty.PacketManager;

import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.PeerTcp;

public interface IRequestMessage<M extends Message ,P extends PeerTcp,P2> {

    boolean packet_process( P peer,M msg );

    boolean packet_process(P peer, int sessionId,M msg);

    boolean packet_process(P peer,P2 player,M msg);

    boolean is_from_gate();

    boolean use_sessionid();


}
