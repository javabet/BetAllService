package com.wisp.game.bet.world.proc;

import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.unit.WorldPeer;

public abstract class DefaultWorldRequestMessage<M extends Message> extends RequestMessageFromGate<M, WorldPeer, GamePlayer> {
}
