package com.wisp.game.bet.gate.message.request;


import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.IRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

@IRequest
public class ServerRegisterRequest implements IRequestMessage<ServerProtocol.packet_server_connect_result>  {

    public Class getProtocolClass()
    {
        server_protocols.ServerMsgType.e_server_msg_type protocolId = ServerProtocol.packet_server_register.newBuilder().getPacketId();

        ServerProtocol.packet_server_register.Builder builder = ServerProtocol.packet_server_register.newBuilder();
        builder.setServerType(ServerBase.e_server_type.e_st_undefine);

       int num =  ServerProtocol.packet_server_connect.newBuilder().getPacketId().getNumber();

        System.out.printf(protocolId.getNumber() + "" + num);

        return ServerProtocol.packet_server_register.class;
    }

    public void doNothing()
    {

    }
}
