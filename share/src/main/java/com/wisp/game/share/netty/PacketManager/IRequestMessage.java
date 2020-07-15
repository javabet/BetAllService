package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;
import com.wisp.game.share.netty.client.NettyClient;

public interface IRequestMessage<M extends Message ,P extends PeerTcp ,P2> {

    boolean packet_process( P peer,M msg );

    boolean packet_process(P peer, int sessionId,M msg);

    boolean packet_process(P peer,P2 player,M msg);

    boolean is_from_gate();

    boolean use_sessionid();


}
