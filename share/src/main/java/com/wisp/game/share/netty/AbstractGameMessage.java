package com.wisp.game.share.netty;

import com.google.protobuf.Message;
import com.wisp.game.share.netty.PacketManager.IRequestMessage;

public abstract class AbstractGameMessage<T extends Message,P extends PeerTcp> implements IRequestMessage {

}
