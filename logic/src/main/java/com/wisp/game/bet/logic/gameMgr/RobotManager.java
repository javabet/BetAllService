package com.wisp.game.bet.logic.gameMgr;

import com.wisp.game.bet.GameConfig.MainMultiLanguageConfig;
import com.wisp.game.bet.GameConfig.MainRobotBaseConfig;
import com.wisp.game.bet.GameConfig.MainRobotNameConfig;
import com.wisp.game.bet.db.mongo.player.doc.OrderPlayerIdDoc;
import com.wisp.game.bet.db.mongo.player.doc.PlayerInfoDoc;
import com.wisp.game.bet.logic.db.DbPlayer;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.logic.unit.ServersManager;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.core.random.RandomHandler;
import logic2world_protocols.Logic2WorldProtocol;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RobotManager {
    public static int MAX_INDEX = 1000000;
    public static RobotManager Instance;

    private static int next_player_index = 0;

    private List<Integer> m_reqlist;// enable_queue<int> m_reqlist;		//请求队列
    private int m_gameid;
    private List<Integer> m_dellist;
    private int m_count;
    private double m_elapsed;
    private Set<String> m_robot_names;
    private Criteria criteria;


    public RobotManager() {
        Instance = this;
        m_robot_names = new HashSet<>();
        m_reqlist = new ArrayList<>();
        m_dellist = new ArrayList<>();
    }

    public int request_robot(int tag, int needgold, boolean exroom)
    {
        int player_id = generic_playerid();

        MainMultiLanguageConfig.MainMultiLanguageConfigData data = MainMultiLanguageConfig.GetInstnace().GetData("NewAccountName");

        String nickName = "";
        if ((RandomHandler.Instance.getRandomValue(0,100) % 2) == 0)
        {
            int maxcount= MainRobotNameConfig.GetInstnace().GetCount();
            int i = 0;
            do
            {
                int index = RandomHandler.Instance.getRandomValue(0, maxcount);
                nickName = MainRobotNameConfig.GetInstnace().GetData(index).getmNickName();

                if (i++ > 15)
                {
                    if(data == null)
                        nickName = String.valueOf(player_id) ;
                    else
                    {
                        //char buf[32] = {0};
                        //sprintf_s(buf, data->mName.c_str(), player_id);
                        nickName = data.getmName();
                    }
                    break;
                }
            }
            while (m_robot_names.contains(nickName));

            m_robot_names.add(nickName);
        }
        else
        {
            if(data == null)
                nickName = String.valueOf(player_id);
            else
            {
                //char buf[32] = {0};
                //sprintf_s(buf, data->mName.c_str(), player_id);
                nickName =data.getmName();
            }
        }

        int sex = (RandomHandler.Instance.getRandomValue(0,100) % 2) + 1;
        int default_head = 0;
        int index = RandomHandler.Instance.getRandomValue(1, 6);
        String icon = (sex == 1 ? "head_nan_" : "head_nv_") + index + ".png";

        GamePlayer p = new GamePlayer();
        p.PlayerID = player_id;
        p.Gold = needgold;
        p.NickName = nickName;
        p.VIPLevel = calc_robot_vip(needgold, exroom);
        p.Sex = sex;
        p.PhotoFrame = default_head;
        p.IsRobot = true;
        p.IconCustom = icon;

        IGameEngine eng = GameManager.Instance.get_game_engine();
        if(eng != null)
        {
            if(eng.player_enter_game(p, -1))
            {
                p.set_state( e_player_state.e_ps_playing);
                GamePlayerMgr.Instance.add_player(p);
                //机器人需要另外通知
                p.reset_robot_life();

            }
        }

        return player_id;
    }


    public int request_robot(int tag, boolean banker, int bet_rate, boolean exroom)
    {
        int player_id = generic_playerid();

        MainMultiLanguageConfig.MainMultiLanguageConfigData data = MainMultiLanguageConfig.GetInstnace().GetData("NewAccountName");

        String nickName;
        if ((RandomHandler.Instance.getRandomValue(0,100) % 2) == 0)
        {
            int maxcount= MainRobotNameConfig.GetInstnace().GetCount();
            int i = 0;
            do
            {
                int index = RandomHandler.Instance.getRandomValue(0, maxcount);
                nickName = MainRobotNameConfig.GetInstnace().GetData(index).getmNickName();

                if (i++ > 15)
                {
                    if(data == null)
                        nickName =String.valueOf(player_id);
                    else
                    {
                        //char buf[32] = {0};
                        //sprintf_s(buf, data->mName.c_str(), player_id);
                        nickName = data.getmName();
                    }
                    break;
                }
            }
            while (m_robot_names.contains(nickName));

            m_robot_names.add(nickName);
        }
        else
        {
            if(data == null)
                nickName = String.valueOf(player_id);
            else
            {
                //char buf[32] = {0};
                //sprintf_s(buf, data->mName.c_str(), player_id);
                nickName = data.getmName();
            }
        }

        //机器人VIP根据身上的钱决定
        int gold = random_robot_gold(tag, banker);
        if(bet_rate > 0 && !banker)
            gold *= bet_rate;

        int sex = (RandomHandler.Instance.getRandomValue(100) % 2) + 1;
        int default_head = 0;
        int index = RandomHandler.Instance.getRandomValue(1, 6);
        String icon = (sex == 1 ? "head_nan_" : "head_nv_") + String.valueOf(index) + ".png";

        GamePlayer p = new GamePlayer();
        p.PlayerID = player_id;
        p.Gold = gold;
        p.NickName = nickName;
        p.VIPLevel = calc_robot_vip(gold, exroom);
        p.Sex = sex;
        p.PhotoFrame = default_head;
        p.IconCustom = icon;
        p.IsRobot = true;
        p.tag = tag;
        //桌子tag处理
        if (tag > 100000)
        {
            p.tag = tag / 1000;
        }
        else
        {//只用游戏id
            p.tag = tag / 100;
        }


        //随机机器人性格
        p.m_robot_type = random_robot_type();
        p.m_banker_robot = banker;

        IGameEngine eng = GameManager.Instance.get_game_engine();
        if(eng != null)
        {
            if(eng.player_enter_game(p, -1))
            {
                p.set_state(e_player_state.e_ps_playing);
                GamePlayerMgr.Instance.add_player(p);

                //机器人需要另外通知
                p.reset_robot_life();
            }
        }

        return player_id;
    }

    public int request_bot(int tag, int needgold, int needvip)
    {
        if(GamePlayerMgr.Instance.is_closing())
        return -2;//关闭中不能申请机器人

        //没找到合适的向world请求
        LogicPeer peer = ServersManager.Instance.get_bot();
        if(peer != null)
        {

            /**
            auto sendmsg = PACKET_CREATE(packetl2w_request_robot, e_mst_l2w_robot_request);

            Logic2WorldProtocol.packetl2w_req

            sendmsg->set_needvip(needvip);
            sendmsg->set_needgold(needgold);
            sendmsg->set_gameid(m_gameid);
            sendmsg->set_roomid(tag);
            sendmsg->set_playerid(generic_playerid());
            peer->send_msg(sendmsg);

            next_player_index += 1;
            if (next_player_index >= 1000000)
                next_player_index -= 1000000;

            m_reqlist.push(tag);
             **/
        }

        return -1;
    }

    //释放机器人
    public void release_robot(int playerid)
    {
        boolean flag =  m_dellist.contains(playerid);
        if( !flag )
        {
            return;
        }

        m_dellist.add(playerid);
    }



    public void heartbeat( double elapsed )
    {
        m_elapsed += elapsed;
        if (m_elapsed > 60)
        {
            m_elapsed = 0;

        }

        if(m_dellist.isEmpty())
            return;

        for (int i = 0;i<m_dellist.size(); i++)
        {
            leave_robot(m_dellist.get(i));
        }
        m_dellist.clear();
    }

    public void leave_robot(int playerid)
    {
        GamePlayer p = GamePlayerMgr.Instance.find_playerbyid(playerid);
        if(p!=null)
        {
            m_robot_names.remove(p.NickName);
            p.leave_game(false);
            GamePlayerMgr.Instance.remove_player(p);
            m_count--;
        }
    }

    public int pop_tag()
    {
        int tag = m_reqlist.remove( m_reqlist.size() - 1 );
        return tag;
    }

    public void set_gameid(int gameid)
    {
        m_gameid = gameid;
    }

    public int get_bet_robot_count(int tag, boolean exroom)
    {
        if (exroom)
        {
            return RandomHandler.Instance.getRandomValue(10,21);
        }
        else {
            tag = tag / 100;//只用游戏id
            MainRobotBaseConfig.MainRobotBaseConfigData data = MainRobotBaseConfig.GetInstnace().GetData(GameManager.Instance.get_gameid());
            if (data != null )
            {
                return RandomHandler.Instance.getRandomValue(data.getmBetRobotCount().get(0), data.getmBetRobotCount().get(1));
            }
        }
        return 0;
    }

    public int get_banker_robot_count(int tag, boolean exroom)
    {
        if (exroom)
        {
            return RandomHandler.Instance.getRandomValue(3, 7);
        }
        else {
            tag = tag / 100;//只用游戏id
            MainRobotBaseConfig.MainRobotBaseConfigData data = MainRobotBaseConfig.GetInstnace().GetData(GameManager.Instance.get_gameid());
            if (data != null)
            {
                return RandomHandler.Instance.getRandomValue(data.getmBankerRobotCount().get(0),data.getmBankerRobotCount().get(1));
            }
        }
        return 0;
    }

    public int get_count()
    {
        return m_count;
    }

    public void inc_robot()
    {
        m_count++;
    }


    public int generic_playerid()
    {
        if(next_player_index > MAX_INDEX)
            next_player_index = 0;

        int index = ++next_player_index;


        Criteria criteria = Criteria.where("Index").is(index);

        Query query = new Query( criteria );
        query.fields().include("Pid");

        OrderPlayerIdDoc doc =  DbPlayer.Instance.getMongoTemplate().findOne(query, OrderPlayerIdDoc.class);
        if (doc == null)
        {
            //SLOG_ERROR << "game_player_mgr::generic_playerid DB_ORDER_PID error index:" << index;
            //BOOST_ASSERT_MSG(false, "pid enough!!!\n");
            System.exit(1);
            return 0;
        }

        return  doc.getPid();
    }

    public int random_robot_type()
    {
        int weight = 0, result = 0;
        List<Integer> weight_array = new ArrayList<>();
        int count = MainRobotTypeConfig.GetInstnace().GetCount();
        for(int i = 0; i < count; i++)
        {
            weight += MainRobotTypeConfig.GetInstnace().GetData(i+1).getmWeight();
            weight_array.add(weight);
        }
        int r = RandomHandler.Instance.getRandomValue(0, weight+1);
        for(int i = 0; i < weight_array.size(); i++)
        {
            if(r <= weight_array.get(i))
            {
                result = i+1;
                break;
            }
        }

        return result;
    }



    public int random_robot_gold(int tag, boolean banker)
    {
        //桌子tag处理
        if (tag > 100000)
        {
            tag = tag / 1000;
        }
        else {
            tag = tag / 100;
        }


        int result = 0;
        MainRobotBaseConfig.MainRobotBaseConfigData cfg = MainRobotBaseConfig.GetInstnace().GetData(GameManager.Instance.get_gameid());
        if(cfg == null)
            return result;

        if(banker)
        {
            return RandomHandler.Instance.getRandomValue(cfg.getmBankerRobotCount().get(0),cfg.getmBankerRobotCount().get(1));
        }

        int weight = 0;
        List<Integer> weight_array = new ArrayList<>();
        int count = cfg.getmRobotGold().size();
        for(int i = 0; i < count; i++)
        {
            weight += cfg.getmGoldWeight().get(i);
            weight_array.add(weight);
        }
        int r = RandomHandler.Instance.getRandomValue(0, weight+1);
        for(int i = 0; i < weight_array.size(); i++)
        {
            if(r <= weight_array.get(i))
            {
                result = cfg.getmRobotGold().get(i);
                break;
            }
        }

        if(result < 0 )
        {
            result = 0;
        }

        return RandomHandler.Instance.getRandomValue(result * 0.8, result * 1.2);
    }


    public int calc_robot_vip(int gold, boolean exroom)
    {
        if (exroom) {
            return RandomHandler.Instance.getRandomValue(0, 3);
        }

        /**
        int idx = MainRobotVipConfig.GetSingleton()->GetCount();

        for (; idx > 0; idx--)
        {
            auto row = MainRobotVipConfig::GetSingleton()->GetData(idx);

            if (row && gold >= row->mGold && row->mVipLevel.size() > 0)
            {
                idx = rand() % row->mVipLevel.size();

                return row->mVipLevel[idx];
            }
        }
        **/
        return 0;
    }
}
