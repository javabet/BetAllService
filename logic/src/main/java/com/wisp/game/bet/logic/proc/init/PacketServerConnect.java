package com.wisp.game.bet.logic.proc.init;

import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.logic.unit.ServersManager;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerConnect extends DefaultRequestMessage<ServerProtocol.packet_server_connect, LogicPeer> {
    @Override
    public boolean packet_process(LogicPeer peer, ServerProtocol.packet_server_connect msg) {

        logger.info(" packet_server_connect:" + msg.getServerType() + " id:" + msg.getServerId() );

        if(ServersManager.Instance.regedit_server(peer))
        {
            ServerProtocol.packet_server_connect_result.Builder builder = ServerProtocol.packet_server_connect_result.newBuilder();
            builder.setServerType(ServerBase.e_server_type.e_st_logic);
            peer.send_msg(builder.build());
        }
        else
        {
            return false;
        }

        return true;
    }
}
