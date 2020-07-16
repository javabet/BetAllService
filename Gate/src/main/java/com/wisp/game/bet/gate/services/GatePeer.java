package com.wisp.game.bet.gate.services;

import client2gate_protocols.Client2GateProtocol;
import com.wisp.game.bet.gate.db.DbAccount;
import com.wisp.game.db.account.info.ServerInfo;
import com.wisp.game.share.component.TimeHelper;
import com.wisp.game.share.netty.PeerTcp;
import com.wisp.game.share.netty.infos.e_peer_state;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.Date;

public class GatePeer extends PeerTcp {

    private static int inc = 0;
    public int logic_id;
    public int world_id;
    private double m_checktime = 0;
    public boolean IsValid = false;
    private int m_net_param = 0;
    private String m_account = "";

    public GatePeer() {
        set_check_time();
    }

    public int get_type() {
        return ServerBase.e_server_type.e_st_gate_VALUE;
    }


    public void heartbeat(double elapsed) {
        int packet_id = packet_service(160);         //每帧执行的包的数量

        if (packet_id != 0) {
            logger.error("gate_peer packet_service error id:" + get_id() + " packetid:" + packet_id);
            discannect();
            return;
        }

        if (get_state() == e_peer_state.e_ps_accepting) {
            return;
        }

        m_checktime += elapsed;

        if (m_checktime > 180000) {
            logger.error("gate_peer check timeout id:" + get_id());
            //discannect();
            m_checktime = 0;
        }
    }

    public void reset_checktime() {
        if (IsValid) {
            m_checktime = 0;
        }
    }

    public void set_net_param() {
        if (ClientManager.Instance.is_shutdowning()) {
            Client2GateProtocol.packetg2c_net_param.Builder builder = Client2GateProtocol.packetg2c_net_param.newBuilder();
            builder.setShutdown(true);
            send_msg(builder.build());
        } else {
            int t = 3;
            if (m_is_websocket) {
                t = 0;
            }

            if (t == 0) {
                init_net_param0();
            } else if (t == 1) {
                init_net_param1();
            } else if (t == 2) {
                init_net_param2();
            } else if (t == 3) {
                init_net_param3();
            }
        }

    }

    public int get_net_param() {
        return m_net_param;
    }

    private void init_net_param0() {
        m_net_param = 0;

    }

    private void init_net_param1() {

    }

    private void init_net_param2() {

    }

    private void init_net_param3() {

    }

    public void discannect() {

    }

    public boolean check_gate( String account )
    {
        if( account == null || account == "" )
        {
            return false;
        }

        m_account = account;

        Query query = new Query(Criteria.where("Account").is(account).and("GateId").is(GateServer.Instance.get_serverid()));
         ServerInfo serverInfo =   DbAccount.Instance.getMongoTemplate().findOne(query, ServerInfo.class);
        if( serverInfo == null )
        {
            return  false;
        }

        world_id = serverInfo.getWorldId();

        //如果还没有分配worldId
        if( world_id <= 0 )
        {
            world_id = BackstageManager.Instance.alloc_world_server();
            if(  world_id > 0)
            {
                Update update = new Update();
                update.set("WorldId",world_id);
                update.set("IsConnect",1);
                FindAndModifyOptions options = new FindAndModifyOptions();
                options.upsert(true);
                options.returnNew(true);
                ServerInfo serverInfo1 = DbAccount.Instance.getMongoTemplate().findAndModify(query,update, options,ServerInfo.class);
                if( serverInfo1 == null )
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void on_logout()
    {
        if( m_account == null || m_account == "" )
        {
            return;
        }

        long cur_tm_ms = TimeHelper.Instance.get_cur_time() * 1000;

        Query query = new Query(Criteria.where("Account").is(m_account).and("GateId").is( GateServer.Instance.get_serverid() ));
        Update update = new Update();
        update.set("IsConnect",0);
        update.set("LastTime",new Date(cur_tm_ms));

        if( ClientManager.Instance.account_peer_count(m_account) <= 1 )
        {
            DbAccount.Instance.getMongoTemplate().updateFirst(query,update,DbAccount.DB_SERVERINFO);
        }

        m_account = null;
    }

    public void check_gate_fail()
    {
        IsValid = false;
        m_checktime = 50;
    }
}
