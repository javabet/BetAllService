package com.wisp.game.bet.monitor.proc;

import com.wisp.game.bet.monitor.unit.MonitorPeer;
import com.wisp.game.bet.monitor.unit.ServerManager;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.share.netty.PacketManager.IRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

@IRequest
public class PacketUpdateSelfInfo extends DefaultRequestMessage<ServerProtocol.packet_updata_self_info, MonitorPeer> {

    public boolean packet_process( MonitorPeer peer,ServerProtocol.packet_updata_self_info message) {

        ServerBase.server_info.Builder sinfo =  ServerManager.Instance.get_server_info(peer.get_id());
        if( sinfo != null )
        {
            sinfo.setAttributes( message.getAttributes() );
        }

        return true;
    }
}