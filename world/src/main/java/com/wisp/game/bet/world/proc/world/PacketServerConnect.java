package com.wisp.game.bet.world.proc.world;

import com.wisp.game.bet.world.unit.ServersManager;
import com.wisp.game.bet.world.unit.WorldPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;


@IRequest(10)
public class PacketServerConnect extends DefaultRequestMessage<ServerProtocol.packet_server_connect, WorldPeer> {
    @Override
    public boolean packet_process(WorldPeer peer, ServerProtocol.packet_server_connect msg) {

        peer.set_remote_id(msg.getServerId());
        peer.set_remote_type(msg.getServerType().getNumber());

        if(ServersManager.Instance.regedit_server(peer))
        {
            ServerProtocol.packet_server_connect_result.Builder builder =  ServerProtocol.packet_server_connect_result.newBuilder();
            builder.setServerType(ServerBase.e_server_type.e_st_world);
            peer.send_msg(builder.build());

            logger.info("packet_server_connect ok id:" + msg.getServerId() + " type:" + msg.getServerType() + " ip:" + peer.get_remote_ip() + " port:" + peer.get_remote_id());
        }
        else
        {
            logger.info("packet_server_connect fail id:" + msg.getServerId()  + " type:" + msg.getServerType() + " ip:" + peer.get_remote_ip() + " port:" + peer.get_remote_id());
        }

        return true;
    }
}
