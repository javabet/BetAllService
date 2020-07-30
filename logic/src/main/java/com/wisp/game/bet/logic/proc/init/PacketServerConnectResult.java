package com.wisp.game.bet.logic.proc.init;

import com.wisp.game.bet.logic.unit.BackstageManager;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.logic.unit.LogicServer;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerConnectResult extends DefaultRequestMessage<ServerProtocol.packet_server_connect_result, LogicPeer> {
    @Override
    public boolean packet_process(LogicPeer peer, ServerProtocol.packet_server_connect_result msg) {

        peer.set_remote_id( peer.get_remote_port() );
        if( peer.get_remote_type() == ServerBase.e_server_type.e_st_world.getNumber())
        {
            BackstageManager.Instance.set_world_state(true);
            LogicServer.Instance.init_game_engine();
        }

        return true;
    }
}
