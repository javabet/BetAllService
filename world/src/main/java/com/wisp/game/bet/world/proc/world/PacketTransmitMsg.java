package com.wisp.game.bet.world.proc.world;

import client2world_protocols.Client2WorldMsgType;
import client2world_protocols.Client2WorldProtocol;
import com.google.protobuf.Message;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.netty.PacketManager.IRequestMessage;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import logic2world_protocols.Logic2WorldMsgType;
import logic2world_protocols.Logic2WorldProtocol;
import server_protocols.ServerProtocol;

@IRequest(6)
public class PacketTransmitMsg extends DefaultRequestMessage<ServerProtocol.packet_transmit_msg, WorldPeer> {
    @Override
    public boolean packet_process(WorldPeer peer, ServerProtocol.packet_transmit_msg msg) {

        RequestMessageRegister.ProtocolStruct protocolStruct =   RequestMessageRegister.Instance.getProtocolStruct(msg.getMsgpak().getMsgid());
        boolean bret = false;

        String packetType = "";
        if( msg.getMsgpak().getMsgid() < 7500 && msg.getMsgpak().getMsgid() > 5000 )
        {
            packetType = " packetType:" +  Client2WorldMsgType.e_server_msg_type.valueOf(msg.getMsgpak().getMsgid());
        }
        else if( msg.getMsgpak().getMsgid() > 20000 && msg.getMsgpak().getMsgid() < 30000 )
        {
            packetType = " packetType:" +  Logic2WorldMsgType.e_server_msg_type.valueOf(msg.getMsgpak().getMsgid());
        }

        if( protocolStruct != null )
        {
            Message innerMsg = RequestMessageRegister.Instance.getMessageByProtocolId(msg.getMsgpak().getMsgid(),msg.getMsgpak().getMsginfo());

            logger.info(" world protocolId:" + msg.getMsgpak().getMsgid() + packetType);

            //try
            {
                if( innerMsg != null )
                {
                    IRequestMessage requestMessage =  protocolStruct.getHandlerInstance();
                    if( requestMessage.use_sessionid() )
                    {
                        bret = requestMessage.packet_process(peer,msg.getSessionid(),innerMsg);
                    }
                    else
                    {
                        GamePlayer player = GamePlayerMgr.Instance.find_player( msg.getSessionid() );
                        if( player != null )
                        {
                            bret = requestMessage.packet_process(peer,player,innerMsg);
                        }
                    }
                }
            }
            /**
            catch (Exception exception)
            {
                bret = false;
                logger.error(exception.getMessage());
                StackTraceElement[] stackTraceElements = exception.getStackTrace();
                int size = stackTraceElements.length;
                for(int i = 0; i < size;i++)
                {
                    logger.error(stackTraceElements[i].toString());
                }
            }
             **/
        }

        if( !bret )
        {

            logger.error("packet_transmit_msg error id:" + msg.getMsgpak().getMsgid() + " serverId:" + peer.get_remote_id() + packetType + " sessionId:" + msg.getSessionid());
            //TODO wisp 在测试期间，此功能暂时去掉，以免影响测试
            //ServerProtocol.packet_player_disconnect.Builder builder =  ServerProtocol.packet_player_disconnect.newBuilder();
            //builder.setSessionid(msg.getSessionid());
            //peer.send_msg(builder.build());
        }

        return true;
    }
}
