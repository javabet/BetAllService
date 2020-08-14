package com.wisp.game.bet.gate.proc.gate;

import client2gate_protocols.Client2GateProtocol;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;

@IRequest
public class Packetc2gNetParam extends DefaultRequestMessage<Client2GateProtocol.packetc2g_net_param, GatePeer> {
    public boolean packet_process(GatePeer peer, Client2GateProtocol.packetc2g_net_param msg) {

        logger.info(" set net_param:" + msg.getParam1() + " param2:" + msg.getParam2());

        Client2GateProtocol.packetg2c_net_param.Builder builder = Client2GateProtocol.packetg2c_net_param.newBuilder();
        peer.send_msg(builder);


        return true;
    }
}
