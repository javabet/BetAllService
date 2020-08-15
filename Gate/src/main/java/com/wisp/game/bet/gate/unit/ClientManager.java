package com.wisp.game.bet.gate.unit;

import client2gate_protocols.Client2GateProtocol;
import client2logic_protocols.Client2LogicMsgType;
import client2world_protocols.Client2WorldMsgType;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.common.EnableObjectManager;
import com.wisp.game.bet.share.utils.SessionHelper;
import com.wisp.game.bet.sshare.IRouterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存与客户端的信息
 */
@Component
public class ClientManager extends EnableObjectManager<Integer,GatePeer> implements IRouterHandler<GatePeer> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static ClientManager Instance;
    private boolean m_is_shutdowning;
    private double m_updatetime = 0;
    private int m_oldcount = 0;
    private ConcurrentHashMap<Integer,Integer> m_msg_route = new ConcurrentHashMap<Integer, Integer>();

    public ClientManager() {
        Instance = this;
        m_is_shutdowning = false;
    }

    public boolean regedit_client(GatePeer gatePeer)
    {
        return add_obj(gatePeer.get_id(),gatePeer);
    }

    public boolean remove_client(int peerid )
    {
        return remove_obj(peerid);
    }

    public void heartbeat( double elapsed )
    {
        for( GatePeer gatePeer : obj_map.values() )
        {
            gatePeer.heartbeat(elapsed);
        }

        updateinfo(elapsed);
    }

    public void prepare_shutdown(int gameId)
    {
        if( gameId == 0 )
        {
            m_is_shutdowning = true;
        }
    }

    public boolean is_shutdowning()
    {
        return m_is_shutdowning;
    }

    public void broadcast_msg(Message msg, List<Integer> peerid_list)
    {
        for( int i = 0; i < peerid_list.size();i++ )
        {
            GatePeer gatePeer = find_objr(peerid_list.get(i));
            if(  gatePeer != null)
            {
                gatePeer.send_msg(msg);
            }
        }
    }

    public void broadcast_msg(int packetId,Message msg, List<Integer> peerid_list)
    {
        for( int i = 0; i < peerid_list.size();i++ )
        {
            GatePeer gatePeer = find_objr(peerid_list.get(i));
            if(  gatePeer != null)
            {
                gatePeer.send_msg(packetId,msg);
            }
        }
    }

    private void updateinfo(double elapsed)
    {
        m_updatetime += elapsed;

        //更新自已的信息给中心服务器
        if( m_updatetime > 10000 && m_oldcount != get_count()  )
        {
            m_oldcount = get_count();
            ServerPeer serverPeer =  BackstageManager.Instance.get_server_bytype(ServerBase.e_server_type.e_st_monitor_VALUE);
            if( serverPeer != null )
            {
                ServerProtocol.packet_updata_self_info.Builder builder = ServerProtocol.packet_updata_self_info.newBuilder();
                builder.getAttributesBuilder().setClientCount(m_oldcount);
                serverPeer.send_msg(builder.build());
            }

            m_updatetime = 0;
        }
    }

    //
    public boolean regedit_msg(int msgId,int servertype)
    {
        if( m_msg_route.containsKey(msgId) )
        {
            return false;
        }

        m_msg_route.put(msgId,servertype);

        return true;
    }

    public int route_msg(int msgId)
    {
        if( !m_msg_route.containsKey(msgId) )
        {
            return 0;
        }

        return m_msg_route.get(msgId);
    }

    public boolean route_handler(GatePeer peer, int msgid, ByteString byteString)
    {
        ServerProtocol.packet_transmit_msg.Builder builder2 = ServerProtocol.packet_transmit_msg.newBuilder();
        builder2.setSessionid(SessionHelper.get_sessionid(GateServer.Instance.get_serverid(),peer.get_id()));
        server_protocols.ServerBase.msg_packet.Builder pack = builder2.getMsgpakBuilder();
        pack.setMsgid(msgid);
        pack.setMsginfo( byteString );

        ServerPeer serverPeer = null;
        //接受分发处理
        if( msgid > Client2LogicMsgType.e_server_msg_type.e_mst_start_c2l.getNumber() &&
            msgid < Client2LogicMsgType.e_server_msg_type.e_mst_start_l2c.getNumber() )
        {
            //logic
             serverPeer =  BackstageManager.Instance.get_server_byid(peer.logic_id);
        }
        else if(  msgid > Client2WorldMsgType.e_server_msg_type.e_mst_start_c2w_VALUE && msgid < Client2WorldMsgType.e_server_msg_type.e_mst_start_w2c_VALUE )
        {
            serverPeer = BackstageManager.Instance.get_server_byid(peer.world_id);
            if( serverPeer != null )
            {
                peer.IsValid = true;
            }
        }
        else if( msgid >= 30000 &&  msgid <= 32000  )
        {
            //skip
            return true;
        }
        else if( msgid >= 40000 && msgid <= 45000 )
        {
            //skip
            return true;
        }
        else
        {
            logger.error("route_handler type error packetid:" + msgid + "  peerid:" + peer.get_id());
        }

        if( serverPeer != null )
        {
            peer.reset_checktime();
            serverPeer.send_msg(builder2.build());
            if( msgid > Client2LogicMsgType.e_server_msg_type.e_mst_start_c2l.getNumber() &&
                    msgid < Client2LogicMsgType.e_server_msg_type.e_mst_start_l2c.getNumber() )
            {
                peer.ErrorCount = 0;
            }
        }
        else
        {
            if( msgid > Client2LogicMsgType.e_server_msg_type.e_mst_start_c2l.getNumber() &&
                    msgid < Client2LogicMsgType.e_server_msg_type.e_mst_start_l2c.getNumber() )
            {
                peer.ErrorCount++;
                if( peer.ErrorCount <= 3 )
                {
                    logger.warn("warning route_handler  serverpeer is null!  packetid:" + msgid + " peerId:" + peer.get_id());
                    Client2GateProtocol.packetg2c_error_packet.Builder builder = Client2GateProtocol.packetg2c_error_packet.newBuilder();
                    peer.send_msg(builder.build());
                    return true;
                }
            }

            return  false;
        }

        return  true;
    }

    public void world_serverdown(int server_id)
    {
        for(GatePeer gatePeer : obj_map.values())
        {
            if( gatePeer.world_id == server_id )
            {
                gatePeer.discannect();
                break;
            }
        }
    }

    public void serverdown_client(int logicId)
    {
        for( GatePeer gatePeer : obj_map.values() )
        {
            if( logicId == 0 || logicId == gatePeer.logic_id )
            {
                gatePeer.discannect();
            }
        }
    }

    public int account_peer_count(String account)
    {
        int count = 0;

        for( GatePeer gatePeer : obj_map.values() )
        {
            if( gatePeer.get_account() == account )
            {
                count ++;
            }
        }

        return count;
    }


}
