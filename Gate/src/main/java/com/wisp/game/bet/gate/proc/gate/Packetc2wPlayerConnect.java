package com.wisp.game.bet.gate.proc.gate;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import msg_type_def.MsgTypeDef;

@IRequest
public class Packetc2wPlayerConnect extends DefaultRequestMessage<Client2WorldProtocol.packetc2w_player_connect, GatePeer> {

    public boolean packet_process(GatePeer peer, Client2WorldProtocol.packetc2w_player_connect msg) {

        boolean transmit = true;
        if( peer.world_id == 0 )
        {
            boolean flag =  peer.check_gate(msg.getAccount());
            if( !flag )
            {
                Client2WorldProtocol.packetw2c_player_connect_result.Builder builder = Client2WorldProtocol.packetw2c_player_connect_result.newBuilder();
                builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_serverid_error_VALUE);
                peer.send_msg(builder.build());
                peer.IsValid = false;
                transmit = false;
                peer.check_gate_fail();
            }
        }

        if( transmit )
        {
            ClientManager.Instance.route_handler(peer,msg.getPacketId().getNumber(),msg.toByteString());
        }

        return true;
    }
}
