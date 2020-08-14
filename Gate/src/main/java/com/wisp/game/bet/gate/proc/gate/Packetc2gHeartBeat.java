package com.wisp.game.bet.gate.proc.gate;

import client2gate_protocols.Client2GateProtocol;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class Packetc2gHeartBeat extends DefaultRequestMessage<Client2GateProtocol.packetc2g_heartbeat, GatePeer> {
    public boolean packet_process(GatePeer peer, Client2GateProtocol.packetc2g_heartbeat msg) {
        peer.reset_checktime();
        Client2GateProtocol.packetg2c_heartbeat.Builder builder = Client2GateProtocol.packetg2c_heartbeat.newBuilder();
        peer.send_msg(builder);
        return true;
    }
}
