package com.wisp.game.share.netty.PacketManager;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.wisp.game.share.netty.PeerTcp;
import com.wisp.game.share.netty.client.NettyClient;

public interface IRequestMessage<T extends Message,P extends PeerTcp,P2> {

    boolean packet_process( P peer,T msg );

    boolean packet_process(P peer, int sessionId,T msg);

    boolean packet_process(P peer,P2 player,T msg);

    boolean is_from_gate();

    boolean use_sessionid();


}
