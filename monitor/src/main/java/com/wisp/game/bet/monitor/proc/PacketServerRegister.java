package com.wisp.game.bet.monitor.proc;

import com.wisp.game.bet.monitor.unit.MonitorPeer;
import com.wisp.game.bet.monitor.unit.MonitorServer;
import com.wisp.game.bet.monitor.unit.ServerManager;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;


@IRequest
public class PacketServerRegister  extends DefaultRequestMessage<ServerProtocol.packet_server_register, MonitorPeer> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public boolean packet_process(MonitorPeer peer,ServerProtocol.packet_server_register msg) {

        ServerProtocol.packet_server_register_result.Builder builder =  ServerProtocol.packet_server_register_result.newBuilder();

        if( peer == null )
        {
            return false;
        }

        peer.set_remote_id( msg.getServerPort() );
        peer.set_remote_type(msg.getServerType().getNumber());

        ServerBase.server_info.Builder server_info = ServerBase.server_info.newBuilder();
        server_info.setServerPort(msg.getServerPort());
        server_info.setServerType(msg.getServerType());
        server_info.setServerIp(peer.get_remote_ip());
        server_info.setSubType(msg.getSubType());
        if( msg.hasAttributes() )
        {
            server_info.setAttributes(msg.getAttributes());
        }


        if(ServerManager.Instance.regedit_server(peer,server_info))
        {
            ServerProtocol.packet_other_server_connect.Builder broadMsg = ServerProtocol.packet_other_server_connect.newBuilder();
            broadMsg.setSinfo(server_info.clone());
            ServerManager.Instance.broadcast_msg(broadMsg.build(),peer.getChannelId());
        }
        else
        {
            logger.error("server_regedit error id:" + peer.get_id() + " type: " + server_info.getServerType() + " ip:" + server_info.getServerIp() + " port:" + server_info.getServerPort());
            return false;
        }

        builder.setServerTime(TimeHelper.Instance.get_cur_time());
        builder.setGroupId(MonitorServer.Instance.get_groupid());

        peer.send_msg(builder.build());

        logger.info("monitor packet_server_register: " + msg.getServerPort() );

        return true;
    }
}
