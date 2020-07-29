package com.wisp.game.bet.share.netty;

import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.PacketManager.IRequestMessage;

public abstract class AbstractGameMessage<T extends Message,P extends PeerTcp> implements IRequestMessage {

}
