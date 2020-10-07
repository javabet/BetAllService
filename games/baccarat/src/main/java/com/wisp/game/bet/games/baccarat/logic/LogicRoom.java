package com.wisp.game.bet.games.baccarat.logic;

import com.google.protobuf.Message;
import com.wisp.game.bet.GameConfig.BaccaratConfig.BaccaratBaseConfig;
import com.wisp.game.bet.db.mongo.games.doc.GameRoomDoc;
import com.wisp.game.bet.games.baccarat.db.DbGameName;
import com.wisp.game.bet.games.baccarat.mgr.GameEngine;
import com.wisp.game.bet.games.share.PlayerControl;
import com.wisp.game.bet.games.share.PlayerStockCtrlCommon;
import com.wisp.game.bet.games.share.StockCtrlXX;
import com.wisp.game.bet.games.share.config.RMConfig;
import com.wisp.game.bet.games.share.config.RMConfigData;
import com.wisp.game.bet.games.share.config.RMStockConfig;
import com.wisp.game.bet.games.share.config.RMStockConfigData;
import com.wisp.game.bet.logic.db.DbGame;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import com.wisp.game.core.random.RandomHandler;
import com.wisp.game.core.utils.CommonUtils;
import game_baccarat_protocols.GameBaccaratDef;
import game_baccarat_protocols.GameBaccaratProtocol;

import msg_type_def.MsgTypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pump_type.PumpType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LogicRoom {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static int MAX_BET_COUNT = 5;
    private static int SYNC_BET_TIME = 1;            //多少时间同步一次给客户端 单位为s
    private static int BANKER_MAX_COUNT = 10;

    private GameRoomDoc gameRoomDoc;
    private List<Integer> m_roomAll_betList;        ;//所有机器人或者真实玩家的下注情况
    private List<Integer> m_roomAll_winList;    //所有机器人或者真实玩家的下注获利
    private int m_roomAll_bet;      //所有机器人或者真实玩家的下注
    private RMConfigData m_cfg;
    private RMStockConfigData m_stock_cfg;
    private Map<Integer,WinnerList>  m_map_players;
    private int m_players_cnt;
    private int m_first_win_cnt;
    private LogicPlayer m_first_player;
    private List<Integer> m_master_id;
    private LogicLobby m_lobby;
    private Map<Integer,LogicPlayer> m_room_players;
    private LogicCore m_core_engine;    //发牌器
    private int m_control;
    private int m_kill_cnt;
    private GameBaccaratDef.e_game_state m_game_state;
    private double m_cd_time;
    private int m_cfg_time_bet_robot_sub;
    private boolean is_have_bet;
    private double m_elapse;
    private double m_checksave;
    private double m_rob_cd;            //机器人进入间隔
    private double m_rob_leave_cd;      //机器人离开间隔
    private  int m_rob_count;           //机器人数量
    private  int m_rob_banker;
    private int m_player_count;         //玩家数量
    private int m_no_banker_count;      //无庄间隔局数

    private int m_total_bet_count; //当前房价的押注总和
    private List<Integer> m_room_bet_list;
    private List<Integer> m_max_bet_list;   //每个桌子最大押注值
    private List<Double> m_room_odds_list;
    private List<Integer> m_room_award_list;
    private List<GameBaccaratProtocol.history_info> m_history_list;     //牌路

    private int m_once_income;                  //单局收到总押注
    private int m_once_outcome;                  //单局赔出的金币
    private int m_banker_once_win;              //单局庄家收益
    private int m_banker_total_win;              //庄家总收益
    private double m_system_water_rate;          //抽水比例
    private int m_system_draw_water;             //庄家下庄后系统抽水
    private List<Integer> m_banker_list;         //申请上庄列表
    private int m_continue_banker_count;        //连庄次数
    private int m_now_banker_id;                    //当前庄家ID
    private int m_old_banker_id;                //上次庄家ID
    private boolean is_change_banker;            //庄家是否有改变
    private boolean is_have_banker;             //当前是否有玩家坐庄
    private boolean banker_apply_leave;
    private  int apply_leave_bankerId;
    private boolean is_can_rob_banker;          //是否能抢庄
    private int m_rob_banker_id;                //当前抢庄玩家ID
    private int m_rob_banker_cost;               //当前抢庄花费
    private boolean is_have_rob_banker;      //是否需要广播抢庄信息

    private boolean is_gm = false;
    private int gm_index;
    private int gm_max;
    private boolean is_refresh_history;
    private double tempEarn;

    private GameBaccaratProtocol.packetl2c_bc_begin_award.Builder m_cache_msg;
    private GameBaccaratProtocol.packetl2c_playerlist_result.Builder m_msg_player_list_builder;


    private double m_rob_earn_rate;
    private int m_rob_min;
    private int m_rob_banker_min;
    private int m_player_store_control;     // 0不控，101杀分 102强
    private int m_players_control;          //玩家点控 0不控，301杀分 302送分 , 303 庄杀分 304 庄送分
    private int m_players_control_pid;      //玩家点控 pid

    private int m_first_bet_cnt = 0;
    private  int m_first_gold  = 0;

    private StockCtrlXX StockCtrlObj;

    public LogicRoom(RMConfigData cfg,LogicLobby logicLobby) {

        m_game_state = GameBaccaratDef.e_game_state.e_state_game_begin;

        m_master_id = new ArrayList<>();

        m_room_bet_list = new ArrayList<>();
        m_room_award_list = new ArrayList<>();
        m_max_bet_list = new ArrayList<>();
        m_roomAll_betList = new ArrayList<>();
        m_roomAll_winList = new ArrayList<>();

        m_room_odds_list = new LinkedList<>();
        m_banker_list = new ArrayList<>();
        m_room_players = new ConcurrentHashMap<>();
        m_history_list = new ArrayList<>();

        m_map_players = new HashMap<>();

        m_cfg = cfg;
        m_stock_cfg = RMStockConfig.Instance.GetData(cfg.getmRoomID());
        StockCtrlObj = new StockCtrlXX();


        this.m_lobby = logicLobby;
        for(int i = 0; i < MAX_BET_COUNT;i++)
        {
            int val =  BaccaratBaseConfig.GetInstnace().GetData( "Odds_" + i ).getmValue();
            m_room_odds_list.add( val/100.0 );
        }

        m_core_engine = new LogicCore();

        if( BaccaratBaseConfig.GetInstnace().GetData("RobotBetSubTime").getmValue() != 0 )
        {
            m_cfg_time_bet_robot_sub = BaccaratBaseConfig.GetInstnace().GetData("RobotBetSubTime").getmValue();
        }


        if(load_room() == null )
        {
            create_room();
        }

        StockCtrlObj.init(m_stock_cfg,gameRoomDoc,0,true);
        StockCtrlObj.set_control_param();
    }

    public int get_banker_list_size(){return m_banker_list.size();}

    public int get_banker_win(){return m_banker_total_win;}
    public int get_continue_count(){return m_continue_banker_count;}
    public int get_now_banker_id(){return m_now_banker_id;}
    public int get_continue_banker_count(){return m_continue_banker_count;}

    public GameBaccaratDef.e_game_state get_room_state()
    {
        return m_game_state;
    }

     public GameBaccaratProtocol.packetl2c_playerlist_result.Builder get_player_list()
     {
         return m_msg_player_list_builder;
    }

    public int get_id()
    {
        return m_cfg.getmRoomID();
    }

    public void heartbeat( double elapsed )
    {
        int IsOpenRob = BaccaratBaseConfig.GetInstnace().GetData("IsOpenRob").getmValue();
        if (IsOpenRob > 0)
        {
            int player_min = BaccaratBaseConfig.GetInstnace().GetData("PlayerMinCount").getmValue();
            //进入
            m_rob_cd -= elapsed;

            if(m_rob_cd <= 0)
            {
                int tag = GameEngine.Instance.get_gameid() * 100 + m_cfg.getmRoomID();
                boolean exroom = is_ex_room();
                if (m_rob_min <= 0)
                {
                    m_rob_min = GameManager.Instance.get_bet_robot_count(tag, exroom);
                }

                if (m_rob_banker_min <= 0)
                {
                    m_rob_banker_min = GameManager.Instance.get_banker_robot_count(tag, exroom);
                }


                if (m_rob_count < m_rob_min && m_cfg.getmIsOpen())
                {
                    GameEngine.Instance.request_robot(tag, false, exroom);
                }
                if (m_rob_banker < m_rob_banker_min && m_cfg.getmIsOpen())
                {
                    GameEngine.Instance.request_robot(tag, true, exroom);
                }

                m_rob_cd = RandomHandler.Instance.getRandomValue(1, 6);
            }
        }


        if( m_game_state == GameBaccaratDef.e_game_state.e_state_game_begin )
        {
            m_cd_time = BaccaratBaseConfig.GetInstnace().GetData("BetTime").getmValue();
            m_cd_time *= 1000;  //以ms 为单位
            m_cd_time += 1000;

            boolean isShutingDown = false;
            if( isShutingDown )
            {

            }
            else
            {
                //每局开始，检测机器人是否需要退
                for( LogicPlayer logicPlayer : m_room_players.values() )
                {
                    if( logicPlayer.is_robot() && logicPlayer.robot_can_exit() )
                    {
                        GameEngine.Instance.release_robot(logicPlayer.get_pid());
                    }
                }

                set_game_state( GameBaccaratDef.e_game_state.e_state_game_bet );

                check_canBankerList();
                bc_change_banker(refreshBanker());	//刷新庄家信息	//更改了上庄信息才发通知

                bc_begin_bet();			//广播开始下注
                m_room_bet_list = new ArrayList<>();
                m_room_award_list = new ArrayList<>();
                for(int i = 0; i < MAX_BET_COUNT;i++)
                {
                    m_room_bet_list.add(0);
                    m_room_award_list.add(0);
                }
                m_total_bet_count = 0;
            }
        }
        else if(  m_game_state == GameBaccaratDef.e_game_state.e_state_game_bet )
        {
            m_cd_time -= elapsed;

            if( m_cd_time <= 0 )
            {
                set_game_state(GameBaccaratDef.e_game_state.e_state_game_award);

                adjust_earn_rate();

                if( m_core_engine.get_send_card_count() <= 4 )
                {
                    m_cd_time = 15 * 1000;
                }
                else if( m_core_engine.get_send_card_count() == 5 )
                {
                    m_cd_time = 17 * 1000;
                }
                else if( m_core_engine.get_send_card_count() == 6 )
                {
                    m_cd_time = 19 * 1000;
                }



                sync_bet_to_room();
                bc_sync_bet_info();;
                if( is_have_rob_banker )
                {
                    bc_rob_banker_info();;
                    is_have_rob_banker = false;
                }

                bc_begin_reward();			//结算奖励以及广播开奖协议
                add_history_list();	//保存到牌路
            }
            else
            {
                m_elapse += elapsed;
                if( m_elapse > SYNC_BET_TIME * 1000 )
                {
                    m_elapse = 0.0;

                    if( sync_bet_to_room() )
                    {
                        bc_sync_bet_info();
                    }
                }

                if( is_have_rob_banker )
                {
                    bc_rob_banker_info();
                    is_have_rob_banker = false;
                }

            }
        }
        else if(  m_game_state == GameBaccaratDef.e_game_state.e_state_game_award ) //开奖期间
        {
            m_cd_time -= elapsed;

            if (m_cd_time <= 0)
            {
                m_cache_msg = null;
                set_game_state(GameBaccaratDef.e_game_state.e_state_game_begin);
            }
        }

        for( LogicPlayer logicPlayer : m_room_players.values() )
        {
            logicPlayer.heartbeat(elapsed);
        }

    }

    public void set_game_state(GameBaccaratDef.e_game_state state)
    {
        m_game_state = state;

        logger.info("current gameState:" + state);

        //同步信息到gstate
        //不需要此动作
    }

    public int get_gm_result()
    {
        return 0;
    }

    public void game_control( int control_type,int control_cnt,List<Integer>  control_param)
    {
        m_control = control_type;
        m_kill_cnt = control_cnt;
    }

    public int random_state()  //返回通杀 通吃 还是正常  // 11,通杀 12,通赔, 13,点杀
    {
        int max_win = 0;
        for (int i = 0; i < MAX_BET_COUNT; ++i)
        {
            if (m_roomAll_winList.get(i) > max_win)
            {
                max_win = m_roomAll_winList.get(i);
            }
        }
        if (StockCtrlObj.get_earn_type(max_win) < 0) {
            return 11;
        }

        m_players_control = check_players_control();
        if (m_players_control > 0)
        {
            return 13;
        }

        if (is_false_banker())
        {//非玩家庄时,所有玩家库存需要杀分时通杀
            int is_kill = 0;
            for (LogicPlayer logicPlayer : m_room_players.values())
            {
                if (logicPlayer.is_robot())
                    continue;

                int totalBets = 0;
                List<Integer> blist = logicPlayer.get_bet_list();
                for (int b : blist)
                {
                    totalBets += b;
                }

                if (totalBets <= 0)
                {
                    continue;
                }

                if (!PlayerStockCtrlCommon.is_player_kill_mode(
                        logicPlayer.getGamePlayer().get_recharged(),
                        logicPlayer.getGamePlayer().get_profit()))
                {
                    is_kill = 0;
                    break;
                }
                else {
                    is_kill = 1;
                }
            }
            if (is_kill > 0 && !is_ex_room()) {
                //m_game_detail_log->set_PSmodel("1");
                m_player_store_control = 102;
                return 11;
            }
        }
        else
        {//玩家庄
            LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);
            if ( !is_ex_room() &&
                    tempBanker != null && PlayerStockCtrlCommon.is_player_kill_mode(
                tempBanker.getGamePlayer().get_recharged(),
                tempBanker.getGamePlayer().get_profit()))
            {
                //m_game_detail_log->set_PSmodel("2");
                m_player_store_control = 103;
                return 11;
            }
        }
        return 0;
    }

    private void adjust_earn_rate()
    {
        int gm_result = 0;
        int leftCount = 0;
        int rightCount = 0;
        calculate_all_betRewordList();

        m_players_control = 0;
        m_players_control_pid = 0;
        m_player_store_control = 0;

        if (m_roomAll_bet > 0 && m_kill_cnt > 0 && m_control >= 2 && m_control <= 7)
        {//1:定制 2:通杀 3和 4闲 5闲对 6庄对 7庄 <=>1和 2闲 3闲对 4庄对 5庄
            gm_result = m_control;
            if (2 == gm_result)//通杀
                gm_result = kill_pay(leftCount, rightCount, true, is_false_banker());
            else
                gm_result = gm_result - 2;
            m_kill_cnt--;
        }
        else
        {
            int state = random_state();//库存控制的结果0  正常  11 通杀  12通赔
            if(11== state)  //通杀
                gm_result = kill_pay(leftCount,rightCount,true,is_false_banker());
            else if(12 == state)//通赔
                gm_result = kill_pay(leftCount,rightCount,false,is_false_banker());
            else if (13 == state)//点杀
            {
                gm_result = control_player_result(leftCount, rightCount, m_players_control);
            }
            else
                gm_result = state;
        }


        m_core_engine.send_gm_card(gm_result, leftCount, rightCount);
    }

    public int kill_pay(int leftCount,int rightCount,boolean tongsha,boolean falseBanker)//通杀通赔是针对系统的而不是针对庄家
    {
        List<Integer> room_bet_list_tmp = new ArrayList<>(5);
        for(int i = 0;i < 5;i ++)
        {
            room_bet_list_tmp.add(0);
        }

        //room_bet_list_tmp = room_bet_list;
        room_bet_list_tmp = m_roomAll_winList;
        if(tongsha == falseBanker)//针对系统通杀
        {
            //std::sort(room_bet_list_tmp.begin(),room_bet_list_tmp.end(),sort_func);//取最小的
            room_bet_list_tmp.sort(new SortAesc());
        }
        else
        {
            //std::sort(room_bet_list_tmp.begin(),room_bet_list_tmp.end(),sort_func1);//取最大的
            room_bet_list_tmp.sort(new SortDesc());
        }


        List<Integer> lfArry = new ArrayList<>();
        lfArry.clear();
        List<Integer> canGetArry = new ArrayList<>();
        canGetArry.clear();

	/*
		1) 和  每家只有两张牌
		2) 闲赢 每家只有两张牌   庄闲不许为对子
		3) 闲赢 闲对
		4) 庄赢 庄对
		5) 庄赢  庄闲不许为对子
	*/
        int res = -1;
        for (int j=0; j < MAX_BET_COUNT; ++j)
        {
            if(room_bet_list_tmp.get(0) == m_roomAll_winList.get(j))
            {
                canGetArry.add( j+1 );

            }
        }

        if (canGetArry.size() > 0) {
            int index1 = RandomHandler.Instance.getRandomValue(0, canGetArry.size() - 1);
            res = canGetArry.get(index1);
        }


        if (res == 3) {
            leftCount = 0;
            rightCount = 1;
        }
        else if (res == 4)
        {
            leftCount = 1;
            rightCount = 0;
        }

        return res;
    }

    private boolean is_false_banker()
    {
        if (is_have_banker)
        {
            LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);
            if(tempBanker != null && !tempBanker.is_robot())
                return false;
        }
        return true;
    }

    private void calculate_all_betRewordList()//计算所有机器人或者真实玩家的下注情况
    {
        boolean falseBanker = is_false_banker();
        //memset(&m_roomAll_betList[0],0,sizeof(GOLD_TYPE/*uint32_t*/)*MAX_BET_COUNT);
        //memset(&m_roomAll_winList[0],0,sizeof(GOLD_TYPE/*uint32_t*/)*MAX_BET_COUNT);

        m_roomAll_betList.clear();;
        m_roomAll_winList.clear();
        for(int i = 0; i < MAX_BET_COUNT;i++)
        {
            m_roomAll_betList.add(0);
            m_roomAll_winList.add(0);
        }

        m_roomAll_bet = 0;
        for (LogicPlayer logicPlayer : m_room_players.values())
        {
            if(falseBanker == logicPlayer.is_robot()) // 根据庄家是否为机器人 选择统计玩家或者机器人的总下注
                continue;
            List<Integer> temp_bet = logicPlayer.get_bet_list();
            for (int j=0; j < MAX_BET_COUNT && j < temp_bet.size(); ++j)
            {
                int addValue = temp_bet.get(j);
                int oldValue = m_roomAll_betList.get(j);
                m_roomAll_betList.set(j,addValue + oldValue );
                m_roomAll_bet += addValue;
            }
        }

        for (int j=0; j < MAX_BET_COUNT && j < m_room_odds_list.size(); ++j)
        {
            double val = m_room_odds_list.get(j) * m_roomAll_betList.get(j) + m_roomAll_betList.get(j);
            m_roomAll_winList.set(j,(int)val);
        }

        //m_roomAll_winList[0] += (m_roomAll_betList[1] + m_roomAll_betList[4]);
        //m_roomAll_winList[2] +=  m_roomAll_winList[1];
        //m_roomAll_winList[3] +=  m_roomAll_winList[4];

        int add_value = (m_roomAll_betList.get(1) + m_roomAll_betList.get(4));
        int old_value = m_roomAll_winList.get(0);
        m_roomAll_winList.set(0,add_value + old_value);

        add_value = m_roomAll_winList.get(1);
        old_value = m_roomAll_winList.get(2);
        m_roomAll_winList.set(2,add_value + old_value);

        add_value = m_roomAll_winList.get(4);
        old_value = m_roomAll_winList.get(3);
        m_roomAll_winList.set(3,add_value + old_value);
    }

    public void add_history_list()
    {
        //List<Boolean> resultList =  m_core_engine.get_result_list();

        //这个可以暂时不处理
    }

    public MsgTypeDef.e_msg_result_def set_rob_banker(int playerid)
    {
        LogicPlayer temp = m_room_players.get(playerid);
        if(temp == null )
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;

        if(playerid == m_now_banker_id)
            return MsgTypeDef.e_msg_result_def.e_rmt_now_is_banker;

        if (m_rob_banker_id == playerid)					//已经是你了
            return MsgTypeDef.e_msg_result_def.e_rmt_now_is_you;

        int MinBankerCount = BaccaratBaseConfig.GetInstnace().GetData("PminBankerCount").getmValue();
        if (m_now_banker_id != 0 &&m_continue_banker_count < MinBankerCount)
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;

        int needFee = 0;
        if (m_rob_banker_id == 0)
            needFee = m_cfg.getmFirstBankerCost();
        else
            needFee = m_rob_banker_cost + m_cfg.getmAddBankerCost();

        if (temp.get_gold() < m_cfg.getmBankerCondition() + needFee)	//金币不足
            return MsgTypeDef.e_msg_result_def.e_rmt_gold_not_enough;

        m_rob_banker_cost = needFee;
        m_rob_banker_id = playerid;

        is_have_rob_banker = true;

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    private boolean sync_bet_to_room() //当前房间的总押注
    {
        if( !is_have_bet )
        {
            return false;
        }

        m_room_bet_list.clear();
        for(int i = 0; i < 5; i ++)
        {
            m_room_bet_list.add(0);
        }

        m_total_bet_count = 0;
        for(LogicPlayer logicPlayer : m_room_players.values())
        {
            List<Integer> tmp_bet_list = logicPlayer.get_bet_list();
            for(int i = 0; i < MAX_BET_COUNT && i < tmp_bet_list.size();i++)
            {
                int oldValue = m_room_bet_list.get(i);
                m_room_bet_list.set(i,oldValue + tmp_bet_list.get(i));
                m_total_bet_count += tmp_bet_list.get(i);
            }
        }

        is_have_bet = false;

        return  true;
    }


    public int get_room_id()
    {
        return m_cfg.getmRoomID();
    }

    public int get_betCondition()
    {
        return m_cfg.getmBetCondition();
    }
    public void set_is_have_bet(boolean is_have)
    {
        is_have_bet = is_have;
    }

    public boolean room_is_full()
    {
        if (m_room_players.size() >= m_cfg.getmPlayerMaxCount())
            return true;
        return false;
    }

    public int enter_room(LogicPlayer player)
    {
        if (m_room_players.containsKey(player.get_pid()))
            return 2;

        if (m_room_players.size() >= m_cfg.getmPlayerMaxCount())		//房间已满
            return 12;

        if (!player.enter_room(this))
            return 2;

        if (!m_cfg.getmIsOpen())
            return 42; //房间未开放

        //m_room_players.insert(std::make_pair(player->get_pid(), player));
        m_room_players.put(player.get_pid(),player);

        //player->in_out_log(true);

        if (player.is_robot())
        {
            if (player.is_banker_robot())
            {
                m_rob_banker++;
                add_banker_list(player.get_pid());
            }
            else
            {
                m_rob_count++;
            }

            if (1 == m_rob_banker)
            {
                refreshBanker();
            }
        }
        else {
            m_player_count++;
        }

        //TODO wisp
        //m_enter_count.add_value(1);

        return 1;
    }

    public void leave_room(int playerid)
    {
        LogicPlayer logicPlayer = m_room_players.get(playerid);
        if( logicPlayer == null )
            return;

        PlayerControl.Instance.leave_game(playerid);

        if (logicPlayer.get_is_banker())
        {
            logicPlayer.set_is_banker(false);
            set_now_banker_null(playerid);
        }

        if (logicPlayer.is_robot())
        {
            if( logicPlayer.is_banker_robot() )
            {
                m_rob_banker -= 1;
            }
            else
            {
                m_rob_count -= 1;
            }

        }
        else {
            m_player_count--;
        }


        //it->second->in_out_log(false);
        m_room_players.remove(playerid);

        Iterator<Integer> it = m_banker_list.iterator();
        while (it.hasNext())
        {
            int bankerId = it.next();
            if( bankerId == playerid )
            {
                it.remove();
            }
        }

        if (playerid == m_rob_banker_id)
        {
            m_rob_banker_id = 0;
            bc_rob_banker_info();
        }
    }

    public RMConfigData get_room_cfg()
    {
        return m_cfg;
    }

    public int get_index_all_count( int index)
    {
        List<Integer> room_bet_list = new ArrayList<>(5);
        for(int i = 0; i < 5;i ++)
        {
            room_bet_list.add(0);
        }

        for (LogicPlayer logicPlayer : m_room_players.values())
        {
            if(!logicPlayer.is_robot())
                continue;
            List<Integer> temp_bet = logicPlayer.get_bet_list();
            for (int j=0; j < MAX_BET_COUNT; ++j)
            {
                int add_value = temp_bet.get(j);
                int old_value = room_bet_list.get(j);
                room_bet_list.set(j,add_value + old_value);
            }
        }
        return room_bet_list.get(index);
    }


    public int get_now_real_banker_id()
    {
        return m_now_banker_id;
    }

    private int get_can_bet_count_begin(int idx) //可下注的金额   算法重写
    {
        int real_now_banker_id = m_now_banker_id;
        m_max_bet_list.clear();
        for(int i = 0; i < MAX_BET_COUNT;i++)
        {
            m_max_bet_list.add(0);
        }

        if( real_now_banker_id != 0 )  //有人上庄
        {
            List<Integer> room_bet_list = new ArrayList<>(5);
            for(int i = 0; i < MAX_BET_COUNT;i++ )
            {
                room_bet_list.add(0);
            }
            int total_bet_count = 0;          //当前房价的押注总和 = 0;

            LogicPlayer tempBanker = m_room_players.get(real_now_banker_id);
            if(tempBanker != null )//找到当前的庄家
            {
                //下盘可赔的总钱/对应的赔率   3种情况   和   将  相
                int all_gold =  tempBanker.get_gold() ;
                int true_pay =  tempBanker.get_gold()  +  total_bet_count - (room_bet_list.get(2)+room_bet_list.get(3))*12;//最坏情况下，除去一定会赔出去的钱
                int jiang_pay = (int)(true_pay - room_bet_list.get(4)*1.95);//假如本局开奖是将
                int xiang_pay = true_pay - room_bet_list.get(1)*2;//假如本局开奖是相
                int he_pay = true_pay - room_bet_list.get(0)*9 - room_bet_list.get(1) - room_bet_list.get(4);//假如本局开奖是和

                int min_can_pay = (((jiang_pay<xiang_pay) ? jiang_pay : xiang_pay)<he_pay) ? ((jiang_pay<xiang_pay) ? jiang_pay : xiang_pay) : he_pay;
                m_max_bet_list.add( he_pay / 8 );
                m_max_bet_list.add( xiang_pay  );
                m_max_bet_list.add( min_can_pay / 11 );
                m_max_bet_list.add( min_can_pay / 11 );
                m_max_bet_list.add( (int)(jiang_pay / 0.95) );
            }
        }

        return -1;
    }


    public int set_can_bet_count()//可下注的金额   算法重写
    {
        int real_now_banker_id = m_now_banker_id;
        for(int i = 0; i <  MAX_BET_COUNT;i ++)
        {
            m_max_bet_list.add(0);
        }

        if (real_now_banker_id != 0)//有人上庄
        {
            //////////////////
            List<Integer> room_bet_list = new ArrayList<>();
            for(int i = 0; i < MAX_BET_COUNT;i++)
            {
                room_bet_list.add(0);
            }
            //memset(&room_bet_list[0],0,sizeof(GOLD_TYPE/*uint32_t*/)*MAX_BET_COUNT);

            int total_bet_count = 0;          //当前房价的押注总和 = 0;
            for (LogicPlayer logicPlayer : m_room_players.values() )
            {
                List<Integer> temp_bet = logicPlayer.get_bet_list();
                for (int j=0; j < MAX_BET_COUNT; ++j)
                {
                    //room_bet_list[j] += temp_bet[j];
                    room_bet_list.set(j,room_bet_list.get(j) + temp_bet.get(j));
                    total_bet_count += temp_bet.get(j);
                }
            }
            /////////////////////////////////////////

            LogicPlayer tempBanker = m_room_players.get(real_now_banker_id);
            if(tempBanker != null)//找到当前的庄家
            {
                //下盘可赔的总钱/对应的赔率   3种情况   和   庄  闲
                int all_gold = tempBanker.get_gold() ;
                int true_pay =  tempBanker.get_gold()  +  total_bet_count - (room_bet_list.get(2)+room_bet_list.get(3))*12;//最坏情况下，除去一定会赔出去的钱
                int jiang_pay = true_pay - room_bet_list.get(4)*2;//假如本局开奖是庄
                int xiang_pay = true_pay - room_bet_list.get(1)*2;//假如本局开奖是闲
                int he_pay = true_pay - room_bet_list.get(0)*9 - room_bet_list.get(1) - room_bet_list.get(4);//假如本局开奖是和

                int min_can_pay = (((jiang_pay<xiang_pay) ? jiang_pay : xiang_pay)<he_pay) ? ((jiang_pay<xiang_pay) ? jiang_pay : xiang_pay) : he_pay;
                m_max_bet_list.set(0,he_pay/8);
                m_max_bet_list.set(1,xiang_pay);
                m_max_bet_list.set(2,min_can_pay/11);
                m_max_bet_list.set(3,min_can_pay/11);
                m_max_bet_list.set(4,jiang_pay);
            }
        }

        return -1;
    }

    public int/*int32_t*/ get_can_bet_count(int index)//可下注的金额   算法重写
    {
        int real_now_banker_id = m_now_banker_id;
        //if(is_have_apply)
        //	real_now_banker_id = m_old_banker_id;

        if (real_now_banker_id != 0)
        {
            return m_max_bet_list.get(index);
        }
        return -1;
    }

    public void set_now_banker_null(int playerid)
    {
        banker_apply_leave = true;
        apply_leave_bankerId = playerid;
    }


    public MsgTypeDef.e_msg_result_def add_banker_list(int playerid)
    {
        LogicPlayer temp = m_room_players.get(playerid);
        if(temp == null)
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;

        if(playerid == m_now_banker_id)
            return MsgTypeDef.e_msg_result_def.e_rmt_now_is_banker;

        for (int bankerId :  m_banker_list)	//已在列表中
        {
            if (bankerId == playerid)
            {
                return MsgTypeDef.e_msg_result_def.e_rmt_has_in_banker_list;
            }
        }

        int golddd = temp.get_gold();
        if (temp.get_gold() < m_cfg.getmBankerCondition())	//金币不足
            return MsgTypeDef.e_msg_result_def.e_rmt_gold_not_enough;

        if (m_banker_list.size() >= BANKER_MAX_COUNT)		//上庄列表已满
            return MsgTypeDef.e_msg_result_def.e_rmt_banker_is_full;

        m_banker_list.add(playerid);				//加入申请上庄列表

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public void check_canBankerList()
    {
        //申请上庄列表
        for( int i = 0; i <  m_banker_list.size();i++ )
        {
            int banker_id = m_banker_list.get(i);
            if( !m_room_players.containsKey(banker_id) )
            {
                continue;
            }

            LogicPlayer logicPlayer = m_room_players.get(banker_id);

            if( logicPlayer.get_gold() < m_cfg.getmBankerCondition() )
            {
                m_banker_list.remove(i);
                i--;
            }
        }
    }

    public MsgTypeDef.e_msg_result_def remove_banker_list(int playerid)
    {
        LogicPlayer temp = m_room_players.get(playerid);
        if(temp == null)
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;

        if(playerid == m_now_banker_id)
            return MsgTypeDef.e_msg_result_def.e_rmt_now_is_banker;

        for(int i = m_banker_list.size()-1;i >= 0; i -- )
        {
            int bankerId = m_banker_list.get(i);
            if (bankerId == playerid)
            {
                m_banker_list.remove(i);
                return MsgTypeDef.e_msg_result_def.e_rmt_success;
            }
        }

        return MsgTypeDef.e_msg_result_def.e_rmt_fail;
    }

    public boolean refreshBanker()
    {
        if(banker_apply_leave)//上盘庄家申请下庄了
        {
            m_old_banker_id = apply_leave_bankerId;
            m_now_banker_id = 0;
            is_change_banker = true;

            banker_apply_leave = false;
            apply_leave_bankerId = 0;
        }

        if (m_now_banker_id == 0)
        {
            m_no_banker_count ++;
        }
        else
        {
            m_no_banker_count = 0;
        }

        int MinBankerCount = BaccaratBaseConfig.GetInstnace().GetData("MinBankerCount").getmValue();

        is_can_rob_banker = false;
        if (m_rob_banker_id > 0)
        {
            LogicPlayer temp_player = m_room_players.get(m_rob_banker_id);
            if(temp_player != null)
            {
                if (temp_player.set_is_banker(true))
                {
                    if (m_now_banker_id > 0)
                    {
                        LogicPlayer temp_player2 = m_room_players.get(m_now_banker_id);
                        if(temp_player2 != null)
                            temp_player2.set_is_banker(false);
                    }
                    m_old_banker_id = m_now_banker_id;
                    m_now_banker_id = m_rob_banker_id;
                    m_rob_banker_id = 0;

                    m_continue_banker_count = 1;		//成功上庄
                    m_system_draw_water = 0;
                    m_banker_total_win = 0;
                    is_change_banker = true;
                    is_have_banker = true;

                    if (is_ex_room() && !temp_player.is_robot()) {
                        //体验场
                        temp_player.change_gold(-m_rob_banker_cost);
                    }
                    else {
                        temp_player.change_gold2(-m_rob_banker_cost, PumpType.PropertyReasonType.type_reason_rob_banker);
                    }

                    for(int i = m_banker_list.size() - 1;i >= 0; i --)
                    {
                        int bankerId = m_banker_list.get(i);
                        if( m_now_banker_id == bankerId )
                        {
                            m_banker_list.remove(i);
                        }
                    }

                    return true;
                }
            }
        }
        m_rob_banker_id = 0;

        if (m_now_banker_id > 0)	//有庄家时判断是否能够继续坐庄
        {
            LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);
            if(tempBanker != null)
            {
                if (tempBanker.is_robot())//机器人处理
                {
                    if (tempBanker.get_game_state() == e_player_state.e_ps_disconnect || m_continue_banker_count > RandomHandler.Instance.getRandomValue(10,30))
                    {
                        m_old_banker_id = m_now_banker_id;
                        m_now_banker_id = 0;
                        tempBanker.set_is_banker(false);
                        is_change_banker = true;
                        is_have_banker = false;
                    }
                    //金钱不过
				else if (tempBanker.get_gold() <= m_cfg.getmAutoLeaveBanker())
                {
                    m_old_banker_id = m_now_banker_id;
                    m_now_banker_id = 0;
                    tempBanker.set_is_banker(false);
                    is_change_banker = true;
                    is_have_banker = false;
                }
                else
                {
                    m_continue_banker_count++;		//继续连庄
                    is_have_banker = true;
                }
                }
                else//玩家处理
                {
                    if (tempBanker.get_game_state() == e_player_state.e_ps_disconnect || m_continue_banker_count >= MinBankerCount)
                    {
                        //auto sendmsg = PACKET_CREATE(packetl2c_attention_needLeave, e_mst_attention_needLeave);

                        GameBaccaratProtocol.packetl2c_attention_needLeave.Builder builder = GameBaccaratProtocol.packetl2c_attention_needLeave.newBuilder();
                        tempBanker.send_msg_to_client(builder);
                        m_old_banker_id = m_now_banker_id;
                        m_now_banker_id = 0;
                        m_system_draw_water = 0;
                        m_banker_total_win = 0;
                        tempBanker.set_is_banker(false);
                        is_change_banker = true;
                        is_have_banker = false;
                    }
                    else if (tempBanker.get_gold() <= m_cfg.getmAutoLeaveBanker())//金币不足自动下庄
                    {
                        m_old_banker_id = m_now_banker_id;
                        m_now_banker_id = 0;
                        m_system_draw_water = 0;
                        m_banker_total_win = 0;
                        tempBanker.set_is_banker(false);
                        is_change_banker = true;
                        is_have_banker = false;
                    }
                    else
                    {
                        m_continue_banker_count++;		//继续连庄
                        is_have_banker = true;
                    }
                }
            }
            else
                m_now_banker_id = 0;
        }

        if (m_now_banker_id == 0)		//当前无庄家
        {
            is_have_banker = false;
            for ( LogicPlayer logicPlayer : m_room_players.values())
            {
                logicPlayer.set_is_banker(false);
            }

            if (m_banker_list.isEmpty())
            {
                m_continue_banker_count = 0;
                is_can_rob_banker = true;
                return false;
            }

            while (true)
            {
                if (m_banker_list.isEmpty())
                {
                    m_now_banker_id = 0;
                    m_continue_banker_count = 0;
                    is_change_banker = true;
                    is_have_banker = false;
                    break;
                }

                m_now_banker_id = m_banker_list.remove(0);

                LogicPlayer temp_player = m_room_players.get(m_now_banker_id);
                if(temp_player == null)
                    continue;

                int rob_banker_gold = BaccaratBaseConfig.GetInstnace().GetData("RobAskBankerGold").getmValue();
                if (temp_player.get_gold() < rob_banker_gold)
                    continue;

                if (temp_player.set_is_banker(true))
                {
                    m_continue_banker_count = 1;		//成功上庄
                    m_system_draw_water = 0;
                    m_banker_total_win = 0;
                    is_change_banker = true;
                    is_have_banker = true;
                    break;
                }
                else
                    continue;
            }
        }

        is_can_rob_banker = true;
        return false;
    }

    public void bc_begin_bet()
    {
        get_can_bet_count_begin(0);

        if (is_gm)
            return;

        for (LogicPlayer logicPlayer : m_room_players.values())
        {
            logicPlayer.clear_once_data();
        }

        GameBaccaratProtocol.packetl2c_bc_begin_bet.Builder builder = GameBaccaratProtocol.packetl2c_bc_begin_bet.newBuilder();
        builder.setIsCanRobBanker( is_can_rob_banker );

        broadcast_msg_to_client(builder);

        is_have_bet = true;
        sync_bet_to_room();
        bc_sync_bet_info();
    }


    public int get_true_userNum()
    {
        int num = 0;
        for (LogicPlayer logicPlayer : m_room_players.values())
        {
            if (logicPlayer != null && !logicPlayer.is_robot())
                num++;
        }
        return num;
    }

    //结算奖励以及广播开奖协议
    private void bc_begin_reward()
    {
        List<Integer> temp_room_bet = new ArrayList<>();
        temp_room_bet.add(0);
        temp_room_bet.add(0);
        temp_room_bet.add(0);
        temp_room_bet.add(0);
        temp_room_bet.add(0);

        //这里有问题
        if( m_cfg.getmRoomID() == 1 )
        {
            m_once_income = m_once_income;
        }

        for(LogicPlayer logicPlayer : m_room_players.values())
        {
            if( logicPlayer.is_robot() )
            {
                continue;
            }

            List<Integer> temp_bet_list = logicPlayer.get_bet_list();

            for(int i = 0; i < MAX_BET_COUNT && i < temp_bet_list.size();i++)
            {
                int old_value = temp_bet_list.get(i);
                temp_bet_list.set(i,old_value + temp_bet_list.get(i));
            }
        }


        int real_income = 0;    //玩家的押注
        int real_outcome = 0;//支出给玩家的

        m_once_income = 0;		//单局总押注
        m_once_outcome = 0;		//单局总支出

        int once_bet_win = 0;	//单局输的押注
        int once_bet_lose = 0;//单局赢的押注

        for (int i = 0;i < MAX_BET_COUNT;i++)
        {
            m_once_income += m_room_bet_list.get(i);
            real_income += temp_room_bet.get(i);
        }

/////////////////////////////////////////////
        LogicPlayer brcPlayer = null;//广播的玩家
        int       brcGold = 0;
        String   brcInfo = "";

        LogicPlayer brcPlayer_rob = null;//广播的机器人
        int       brcGold_rob = 0;
        String  brcInfo_rob = "";

        int gold = 0;
        String info = "";
/////////////////////////////////////////////

        m_players_cnt = 0;
        m_map_players.clear();

        m_first_win_cnt = 0;
        m_first_bet_cnt = 0;
        m_first_gold = 0;
        m_first_player = null;

        /////////计算玩家盈亏
        List<Boolean> result_list = m_core_engine.get_result_list();
        int win_count = 0;
        int real_system_win = 0;

        int player_ctrl_kill_gold = 0;
        int player_ctrl_send_gold = 0;

        for(int i = 0; i < result_list.size();i++)
        {
            if (result_list.get(i))
            {
                //m_game_detail_log.add_area_win(i);
            }
        }
        for (LogicPlayer logicPlayer : m_room_players.values())//玩家的情况
        {
            if (logicPlayer.get_is_banker())
                continue;

            boolean pc_flag = false;//点控影响
            int bright_water = 0, pure_win_gold = 0;
            win_count = 0;
            List<Integer> temp_bet = logicPlayer.get_bet_list();
            int total_bet = 0;
            int total_win_bet = 0;
            logicPlayer.getWins().clear();
            for (int k = 0; k < MAX_BET_COUNT ; k++)
            {
                logicPlayer.getWins().add(0);

                total_bet += temp_bet.get(k);

                //if(!it->second->is_robot())
                //	m_area_logs->add_outlay(k,temp_bet[k]);

                if (temp_bet.get(k) <= 0)	//没压
                    continue;
                if (!result_list.get(k))	//没中
                {
                    //logicPlayer.m_win[k] = -temp_bet.get(k);
                    logicPlayer.getWins().set(k,-temp_bet.get(k));
                    continue;
                }
                total_win_bet += temp_bet.get(k);
                int temp_count = (int)(temp_bet.get(k) + m_room_odds_list.get(k)*temp_bet.get(k));		//加上原押注

                //if(!it->second->is_robot())
                //	m_area_logs->add_incom(k, temp_count);

                int profit_gold = (int)((m_room_odds_list.get(k)*temp_bet.get(k))*StockCtrlObj.get_earnings_rate());
                //m_game_detail_log->add_win_gold(it->first, k, temp_count, profit_gold);
                bright_water += profit_gold;

                //logicPlayer.m_win[k] = m_room_odds_list.get(k)*temp_bet.get(k) * (1 - StockCtrlObj.get_earnings_rate());
                logicPlayer.getWins().set(k,(int)(m_room_odds_list.get(k)*temp_bet.get(k) * (1 - StockCtrlObj.get_earnings_rate())));

                pure_win_gold += temp_count - profit_gold;

                //广播
                int broadcast_gold = BaccaratBaseConfig.GetInstnace().GetData("BroadcastGold").getmValue();
                if (temp_count - temp_bet.get(k)>= broadcast_gold)
                {
                    //const std::string& sinfo = BaccaratRoomConfig::GetSingleton()->GetData(1)->mBetNames[k];
                    //it->second->bc_game_msg(temp_count - temp_bet[k], sinfo);
                    info = RMConfig.Instance.GetData(1).getmBetNames().get(k);
                    gold = temp_count - temp_bet.get(k);
                    if(logicPlayer.is_robot())//机器人
                    {
                        if(gold >brcGold_rob)
                        {
                            brcPlayer_rob = logicPlayer;
                            brcGold_rob = gold;
                            brcInfo_rob = info;
                        }
                    }
                    else
                    {
                        if(gold >brcGold)
                        {
                            brcPlayer = logicPlayer;
                            brcGold = gold;
                            brcInfo = info;
                        }
                    }
                }
                win_count += temp_count;
                logicPlayer.add_award_list(k,temp_count);
                m_room_award_list.set(k,m_room_award_list.get(k) + temp_count);//[k] += temp_count;
                logicPlayer.add_once_water(temp_count - temp_bet.get(k), true);
            }

            LogicPlayer player = logicPlayer;

            int total_win =0 , total_bets = 0;

            //it->second->add_log(pure_win_gold > 0, total_bet, total_win, total_bets);

            add_player_list(player, total_win, total_bets);

            boolean first_player = false;

            if (m_first_win_cnt < total_win)
                first_player = true;
            else if (m_first_win_cnt == total_win)
            {
                if (m_first_bet_cnt < total_bets)
                    first_player = true;
                else if (m_first_bet_cnt == total_bets)
                {
                    if (m_first_gold < player.get_gold())
                        first_player = true;
                }
            }

            if (first_player)
            {
                m_first_player = player;

                m_first_win_cnt = total_win;
                m_first_bet_cnt = total_bets;
                m_first_gold = m_first_player.get_gold();
            }

            if (result_list.get(0) && temp_bet.size() >= 5) //结果为“和”的话
            {
                win_count += (temp_bet.get(1) + temp_bet.get(4));
                total_win_bet += (temp_bet.get(1) + temp_bet.get(4));
                logicPlayer.add_award_list(1,temp_bet.get(1));
                logicPlayer.add_award_list(4,temp_bet.get(4));

                //m_room_award_list[1] += temp_bet.get(1);
                //m_room_award_list[4] += temp_bet.get(4);

                m_room_award_list.set(1,m_room_award_list.get(1) + temp_bet.get(1));
                m_room_award_list.set(4,m_room_award_list.get(4) + temp_bet.get(4));

                //logicPlayer.m_win[1] += temp_bet.get(1);
                //logicPlayer.m_win[4] += temp_bet.get(4);

                logicPlayer.getWins().set(1,logicPlayer.getWins().get(1) + temp_bet.get(1));
                logicPlayer.getWins().set(4,logicPlayer.getWins().get(4) + temp_bet.get(4));

                if(!logicPlayer.is_robot())
                {
                    //m_area_logs->add_outlay(1, -temp_bet[1]);
                    //m_area_logs->add_outlay(4, -temp_bet[1]);
                }

                //m_game_detail_log->add_win_gold(it->first, 1, temp_bet[1], 0);
                //m_game_detail_log->add_win_gold(it->first, 4, temp_bet[4], 0);
            }

            if (win_count > 0)
            {
                //int64_t bright_water = 0;

                //{
                //bright_water = (win_count - total_win_bet)*StockCtrlObj->get_earnings_rate();//明抽
                if(!logicPlayer.is_robot())
                {
                    StockCtrlObj.add_bright_water(bright_water);
                    logicPlayer.set_bright_water(bright_water);
                }
                //}

                //if(win_count > total_bet)  //表示赢了钱
                //{
                //    bright_water = (win_count - total_bet)*m_water_BrightRate->get_value();//明抽
                //    if(!it->second->is_robot())
                //    {
                //        m_room_BrightWater->add_value(bright_water);
                //        it->second->set_bright_water(bright_water);
                //    }
                //}
                logicPlayer.add_bet_win(win_count - bright_water);//待处理    玩家实际赢钱
                logicPlayer.add_bet_beforeWater_win(win_count);
                m_once_outcome += win_count;

                if (!logicPlayer.is_robot())
                    real_outcome += win_count;

                if (logicPlayer.is_robot() && is_real_banker())
                {
                    gameRoomDoc.setRobIncome( gameRoomDoc.getRobIncome() + win_count );
                }
            }
            logicPlayer.add_once_water(total_bet - total_win_bet, false);

            once_bet_win += total_win_bet;
            once_bet_lose += total_bet - total_win_bet;


            if (!logicPlayer.is_robot() && !is_ex_room()) {
                PlayerControl.Instance.control_result(
                        logicPlayer.get_pid(),
                        win_count - total_bet);
            }

            if (!pc_flag)
            {
                //更新活动数据
                player.activity_change(
                        1
                        , total_bet //所有盘口押注
                        , win_count - total_win_bet  //盘口纯利（返奖-本金，不抽水）
                        , total_bet - total_win_bet //输的盘口押注
                        , (win_count - total_win_bet) + (total_bet - total_win_bet) //业绩 (盘口纯利 + 输的盘口押注)
                        , false,false );
            }
        }

        //广播
        if(brcPlayer != null )
        {
            //brcPlayer.bc_game_msg(brcGold, brcInfo);
        }

        if(brcPlayer_rob != null )
        {
            //brcPlayer_rob.bc_game_msg(brcGold_rob, brcInfo_rob);
        }

///////////////////////////////////////////////////////////////////
        m_banker_once_win = m_once_income - m_once_outcome;//单局庄家总收益（机器人、玩家押注）

        if (is_have_banker)
        {
            m_banker_total_win += m_banker_once_win;
            m_system_draw_water = (int)(m_banker_total_win * m_system_water_rate);
        }

        //抽水只针对系统、机器人坐庄真实玩家的下注
        int stock_system_rob = real_income - real_outcome - player_ctrl_kill_gold + player_ctrl_send_gold;
        int rob_bet_result = m_once_income  - real_income  -(m_once_outcome -real_outcome);//机器人单局的输赢（机器人是拿库存的钱下注）

        if (!is_have_banker)	//系统为庄
        {
            gameRoomDoc.setRoomIncome( gameRoomDoc.getRobIncome() + real_income );
            gameRoomDoc.setRoomOutcome( gameRoomDoc.getRobOutcome() + real_outcome );
            StockCtrlObj.add_stock(stock_system_rob);

        }
        else//机器人或者正常庄家
        {
            LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);
            if(tempBanker != null )
            {
                //auto pc_flag = m_players_control == 303 || m_players_control == 304;
                boolean pc_flag = false;

                if(tempBanker.is_robot())//是机器人
                {
                    gameRoomDoc.setRoomIncome( gameRoomDoc.getRobIncome() + real_income );
                    gameRoomDoc.setRoomOutcome( gameRoomDoc.getRobOutcome() + real_outcome );
                    StockCtrlObj.add_stock(stock_system_rob);

                }
                else//庄家非机器人
                {
                    gameRoomDoc.setRoomIncome( gameRoomDoc.getRobIncome() + m_once_outcome - real_outcome );
                    gameRoomDoc.setRoomOutcome( gameRoomDoc.getRobOutcome() + real_income- m_once_income );

                    if (!pc_flag)
                    {
                        StockCtrlObj.add_stock(-rob_bet_result);
                    }

                }
            }
        }

        if (is_gm)
        {
            List<LogicCore.CardType> player_card = m_core_engine.get_player_card();
            List<LogicCore.CardType> banker_card = m_core_engine.get_banker_card();
            String resultStr = "";
            for (int k = 0; k < MAX_BET_COUNT ; k++)
            {
                if (result_list.get(k))	//没中
                {
                    switch (k)
                    {
                        case 0:
                            resultStr = resultStr + "和\t";
                            break;
                        case 1:
                            resultStr = resultStr + "闲胜\t";
                            break;
                        case 2:
                            resultStr = resultStr + "闲对\t";
                            break;
                        case 3:
                            resultStr = resultStr + "庄对\t";
                            break;
                        case 4:
                            resultStr = resultStr + "庄胜\t";
                            break;
                    }
                }
            }
            return;
        }

        int bright_water = 0;
        if(once_bet_lose >0)
            bright_water = (int)(once_bet_lose * StockCtrlObj.get_earnings_rate());

        if(is_have_banker)
        {
            LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);
            if(tempBanker != null)
            {
                if(!tempBanker.is_robot())
                {
                    StockCtrlObj.add_bright_water(bright_water);
                    tempBanker.set_bright_water(bright_water);
                }
            }
        }

        ///////结果协议
        GameBaccaratProtocol.packetl2c_bc_begin_award.Builder builder =  GameBaccaratProtocol.packetl2c_bc_begin_award.newBuilder();
        {
            for (int i = 0; i < result_list.size() ; ++i)
            {
                builder.addResultList(result_list.get(i));
            }



            List<LogicCore.CardType> player_card = m_core_engine.get_player_card();
            //sendmsg->mutable_player_card()->Reserve(player_card.size());
            List<LogicCore.CardType> banker_card = m_core_engine.get_banker_card();
            //sendmsg->mutable_banker_card()->Reserve(banker_card.size());
            builder.setCdTime((int)m_cd_time);
            for (int j = 0; j < player_card.size() ; ++j)
            {
                if(player_card.get(j).flower == null)
                {
                    continue;
                }
                game_baccarat_protocols.GameBaccaratProtocol.msg_card_info.Builder tempPlayerBuilder = game_baccarat_protocols.GameBaccaratProtocol.msg_card_info.newBuilder();
                tempPlayerBuilder.setCardFlower(player_card.get(j).flower);
                tempPlayerBuilder.setCardPoint(player_card.get(j).point);
                builder.addPlayerCard( tempPlayerBuilder );
            }

            for (int j = 0; j < banker_card.size() ; ++j)
            {
                if( banker_card.get(j).flower == null)
                {
                    continue;
                }
                game_baccarat_protocols.GameBaccaratProtocol.msg_card_info.Builder temp_banker_builder = game_baccarat_protocols.GameBaccaratProtocol.msg_card_info.newBuilder();
                temp_banker_builder.setCardFlower( banker_card.get(j).flower );
                temp_banker_builder.setCardPoint( banker_card.get(j).point );
                builder.addBankerCard(temp_banker_builder);
            }

            ////////////////////////////////////////////////////////明抽
            builder.setBankerWinGold(m_banker_once_win - bright_water);

            int HISTORY_MAX_LENGTH = BaccaratBaseConfig.GetInstnace().GetData("HistoryMaxCount").getmValue();
            if (m_history_list.size() >= HISTORY_MAX_LENGTH)
            {
                //m_history_list.pop_front();
                is_refresh_history = true;
                m_history_list.clear();

                //////////////////////////////////////////////////////////////////////////
                //同步信息到gstate
                //不处理
            }
            else
                is_refresh_history = false;

            builder.setIsRefreshHistory( is_refresh_history );
        }

        //玩家输赢金币
        for ( LogicPlayer logicPlayer : m_room_players.values())
        {
            game_baccarat_protocols.GameBaccaratProtocol.msg_player_award.Builder player_award_builder = game_baccarat_protocols.GameBaccaratProtocol.msg_player_award.newBuilder();
            player_award_builder.setPlayerId(logicPlayer.get_pid());
            player_award_builder.setGold(logicPlayer.get_gold());
            player_award_builder.setName(logicPlayer.get_nickname());
            builder.addPlayerList(player_award_builder);

            List<Long> otherWinList = new ArrayList<>();
            for (int i = 0; i < MAX_BET_COUNT; i++)
            {
                otherWinList.add( logicPlayer.getWins().get(i).longValue() );
            }
            player_award_builder.addAllOtherWin(otherWinList);

            if (logicPlayer.get_is_banker())
            {
                builder.setBankerGoldCount( logicPlayer.get_gold() );
            }

            if (logicPlayer.get_is_banker())	//庄家加金币
            {
                boolean ret = logicPlayer.change_gold(m_banker_once_win - bright_water);
                if (!ret)
                {
                    if (m_banker_once_win < 0)
                    {
                        if(!logicPlayer.is_robot())
                        {
                            //m_area_logs->add_banker_profit(-it->second->get_gold());
                            logicPlayer.add_once_water(logicPlayer.get_gold(), false);
                        }

                        logicPlayer.change_gold(-logicPlayer.get_gold());//庄家改金币
                    }
                }
                else if(!logicPlayer.is_robot())
                {
                    //m_area_logs->add_banker_profit(m_banker_once_win);
                    //it->second->add_once_water(m_banker_once_win, m_banker_once_win > 0);

                    //20190701应策划需求，庄家业绩设置为每个区域的纯盈利（或纯输钱）之和;
                    int temp_performance = m_once_outcome - once_bet_win + once_bet_lose;
                    logicPlayer.add_once_water_banker(m_banker_once_win, m_banker_once_win > 0,Math.abs(temp_performance));

                    if ( !is_ex_room()) {
                        PlayerControl.Instance.control_result(
                                logicPlayer.get_pid(),
                                once_bet_win + once_bet_lose - m_once_outcome );
                    }

                    //if (m_players_control != 303 && m_players_control != 304 /*点控时不算*/)
                    {
                        //更新活动数据
                        //it->second->activity_change(
                        //	1
                        //	, 0
                        //	, once_bet_lose //庄家纯盈利
                        //	, m_once_outcome - once_bet_win //庄家纯输钱
                        //	, m_once_outcome - once_bet_win + once_bet_lose //业绩 (庄家纯输钱 + 庄家纯盈利 )
                        //	, false);

                        logicPlayer.activity_change(
                                1
                                , once_bet_lose + once_bet_win
                                , m_once_outcome - once_bet_win
                                , once_bet_lose
                                , m_once_outcome - once_bet_win + once_bet_lose //业绩 (庄家纯输钱 + 庄家纯盈利 )
                                , false,false);
                    }
                }
            }
            logicPlayer.sycn_gold();//玩家改金币
        }


        m_cache_msg = builder;
        broadcast_msg_to_client(builder);

        m_master_id.clear();
        int cnt = 0;

         m_msg_player_list_builder = GameBaccaratProtocol.packetl2c_playerlist_result.newBuilder();

        if (m_first_player != null )
        {
            game_baccarat_protocols.GameBaccaratProtocol.msg_player_info.Builder  player_info_builder = game_baccarat_protocols.GameBaccaratProtocol.msg_player_info.newBuilder();

            cnt++;
            m_master_id.add(m_first_player.get_pid());

            player_info_builder.setPlayerId(m_first_player.get_pid());
            player_info_builder.setPlayerName(m_first_player.get_nickname());
            player_info_builder.setHeadFrame(m_first_player.get_head_frame_id());
            player_info_builder.setHeadCustom(m_first_player.get_icon_custom());
            player_info_builder.setPlayerGold(m_first_player.get_gold());
            player_info_builder.setPlayerSex(m_first_player.get_player_sex());
            player_info_builder.setVipLevel(m_first_player.get_viplvl());

            player_info_builder.setWinCount(m_first_win_cnt);
            player_info_builder.setBets(m_first_bet_cnt);
            player_info_builder.setPlayCnt(m_first_player.get_playing_cnt());

            List<Integer> bets = m_first_player.get_bet_list();
            for (int i = 0; i < MAX_BET_COUNT && i < bets.size(); i++)
            {
                player_info_builder.addOtherBets( bets.get(i) );
            }

            for (int i = 0; i < MAX_BET_COUNT && i < m_first_player.getWins().size(); i++)
            {
                player_info_builder.addOtherWin( m_first_player.getWins().get(i) );
            }

            m_msg_player_list_builder.addPlayerInfos(player_info_builder);
        }

        for ( int winner_list_key : m_map_players.keySet())
        {
            LogicRoom.WinnerList winner_list = m_map_players.get(winner_list_key);
            for (int bets_list_key : winner_list.m_map_winner.keySet())
            {
                BetsList bets_list = winner_list.m_map_winner.get(bets_list_key);
                for (LogicPlayer player : bets_list.m_map_bets.values())
                {
                    if (player != m_first_player)
                    {
                        game_baccarat_protocols.GameBaccaratProtocol.msg_player_info.Builder player_info_builder =  game_baccarat_protocols.GameBaccaratProtocol.msg_player_info.newBuilder();

                        if (cnt++ <= 5)
                        {
                            m_master_id.add(player.get_pid());
                        }

                        player_info_builder.setPlayerId( player.get_pid() );
                        player_info_builder.setPlayerName( player.get_nickname() );
                        player_info_builder.setHeadFrame( player.get_head_frame_id() );
                        player_info_builder.setHeadCustom( player.get_icon_custom() );
                        player_info_builder.setPlayerGold( player.get_gold() );
                        player_info_builder.setPlayerSex( player.get_player_sex() );
                        player_info_builder.setVipLevel( player.get_viplvl() );

                        player_info_builder.setWinCount( bets_list_key );
                        player_info_builder.setBets(winner_list_key);
                        player_info_builder.setPlayCnt(player.get_playing_cnt());


                        for (int i = 0; i < MAX_BET_COUNT; i++)
                        {
                            player_info_builder.addOtherBets(player.get_bet_list().get(i));
                        }

                        for (int i = 0; i < MAX_BET_COUNT; i++)
                        {
                            player_info_builder.addOtherWin(player.getWins().get(i));
                        }
                        m_msg_player_list_builder.addPlayerInfos(player_info_builder);
                    }
                }
            }
        }

        broadcast_msg_to_client(m_msg_player_list_builder);
    }

    public int card_flower(GameBaccaratDef.e_card_flower fl)
    {
        if( fl ==  GameBaccaratDef.e_card_flower.e_flower_spade)
        {
            return 0;
        }
        else if( fl ==  GameBaccaratDef.e_card_flower.e_flower_heart)
        {
            return 1;
        }
        else if( fl ==  GameBaccaratDef.e_card_flower.e_flower_club)
        {
            return 2;
        }
        else if( fl ==  GameBaccaratDef.e_card_flower.e_flower_diamond)
        {
            return 3;
        }
        else
        {
            return 0;
        }
    }


    public String get_result()
    {
        return "";
    }

    private void bc_sync_bet_info()
    {
        if(is_gm)
        {
            return;
        }

        GameBaccaratProtocol.packetl2c_bc_total_bet_info.Builder builder = GameBaccaratProtocol.packetl2c_bc_total_bet_info.newBuilder();
        for(int i = 0; i < MAX_BET_COUNT;i++)
        {
            GameBaccaratProtocol.msg_betinfo.Builder msgBetInfoBuilder = GameBaccaratProtocol.msg_betinfo.newBuilder();
            msgBetInfoBuilder.setBetGolds(m_room_bet_list.get(i));
            msgBetInfoBuilder.setMaxBetCount(m_max_bet_list.get(i));

            for(int masterId : m_master_id)
            {
                LogicPlayer logicPlayer = m_room_players.get(masterId);
                if( logicPlayer == null )
                {
                    continue;
                }

                if( i >= logicPlayer.get_bet_list().size() )
                {
                    continue;
                }

                int betGold = logicPlayer.get_bet_list().get(i);
                if( betGold == 0 )
                {
                    continue;
                }

                GameBaccaratProtocol.msg_master_bets.Builder msgMasterbetsBuilder = GameBaccaratProtocol.msg_master_bets.newBuilder();
                msgMasterbetsBuilder.setPlayerId(logicPlayer.get_pid());
                msgMasterbetsBuilder.setPlayerBets(betGold);
                msgBetInfoBuilder.addMasterBets(msgMasterbetsBuilder);
            }
            builder.addBets(msgBetInfoBuilder);
        }

        broadcast_msg_to_client(builder);


        GameBaccaratProtocol.packetl2c_notify_sceneinfo.Builder notifySceneInfoBuilder =  build_sceneinfo();
        notifySceneInfoBuilder.setEarnGold(StockCtrlObj.get_bright_water());
        notifySceneInfoBuilder.setStockGold( StockCtrlObj.get_stock() );
        for( LogicPlayer logicPlayer : m_room_players.values() )
        {
            if( logicPlayer.get_privilege() > 0 )
            {
                logicPlayer.getGamePlayer().send_msg_to_client(notifySceneInfoBuilder);
            }
        }
    }

    public GameBaccaratProtocol.packetl2c_notify_sceneinfo.Builder  build_sceneinfo()
    {
        GameBaccaratProtocol.packetl2c_notify_sceneinfo.Builder builder = GameBaccaratProtocol.packetl2c_notify_sceneinfo.newBuilder();

        if (m_game_state == GameBaccaratDef.e_game_state.e_state_game_bet)
        {
            builder.setCountDown( (int)(get_cd_time() * 1000) );
        }
        builder.setRetainCnt(m_kill_cnt);
        builder.setBankerType(1);

        for (LogicPlayer logicPlayer : m_room_players.values())
        {
            if (logicPlayer.get_is_banker())
            {
                builder.setBankerType( logicPlayer.is_robot() ? 2 : 3 );
                builder.setBankerName(logicPlayer.get_nickname());
                builder.setBankerGold(logicPlayer.get_gold());

                break;
            }
        }

        for (LogicPlayer player  : m_room_players.values())
        {
            if (player.is_robot() == false)
            {
                game_baccarat_protocols.GameBaccaratProtocol.msg_player_betinfo.Builder bet_info_builder = game_baccarat_protocols.GameBaccaratProtocol.msg_player_betinfo.newBuilder();
                builder.addPlayerBetinfos(bet_info_builder);

                bet_info_builder.setPlayerId( player.get_pid() );
                bet_info_builder.setPlayerName(player.get_nickname());
                bet_info_builder.setPlayerGold(player.get_gold());


                for (int i = 0; i < MAX_BET_COUNT && i < player.get_bet_list().size(); i++)
                {
                    bet_info_builder.addBetGold(player.get_bet_list().get(i));
                }
            }
        }
        return builder;
    }


    public void bc_rob_banker_info()
    {
        if (is_gm)
            return;

        GameBaccaratProtocol.packetl2c_bc_rob_banker_info.Builder builder = GameBaccaratProtocol.packetl2c_bc_rob_banker_info.newBuilder();
        builder.setPlayerId( m_rob_banker_id );
        builder.setPayCount( m_rob_banker_cost );

        broadcast_msg_to_client(builder);
    }


    private void bc_change_banker( boolean is_rob )
    {
        if(is_gm)
        {
            return;
        }

        if(!is_change_banker)
        {
            return;
        }

        GameBaccaratProtocol.packetl2c_bc_change_banker.Builder builder = GameBaccaratProtocol.packetl2c_bc_change_banker.newBuilder();
        GameBaccaratProtocol.player_info.Builder playerInfoBuilder =  builder.getBankerInfoBuilder();
        boolean is_system_banker = true;

        if( m_now_banker_id > 0 )
        {
            LogicPlayer tmp_banker = m_room_players.get(m_now_banker_id);
            if( tmp_banker != null )
            {
                playerInfoBuilder.setPlayerId( m_now_banker_id );
                playerInfoBuilder.setPlayerNickname( tmp_banker.get_nickname() );
                playerInfoBuilder.setPlayerHeadFrame( tmp_banker.get_head_frame_id() );
                playerInfoBuilder.setPlayerHeadCustom(tmp_banker.get_icon_custom());
                playerInfoBuilder.setPlayerGold( tmp_banker.get_gold() );
                playerInfoBuilder.setPlayerSex(tmp_banker.get_player_sex());
                playerInfoBuilder.setPlayerVipLv(tmp_banker.get_viplvl());

                is_system_banker = false;
            }
        }

        if( is_system_banker )
        {
            playerInfoBuilder.setPlayerId(0);
            playerInfoBuilder.setPlayerNickname("");
            playerInfoBuilder.setPlayerHeadFrame(0);
            playerInfoBuilder.setPlayerHeadCustom("");
            playerInfoBuilder.setPlayerGold(0);
            playerInfoBuilder.setPlayerSex(1);
            playerInfoBuilder.setPlayerVipLv(0);
        }

        builder.setIsRob(is_rob);

        if( m_system_draw_water > 0 )
        {

        }
        else
        {
            builder.setSystemDrawWater(0);
        }

        m_system_draw_water = 0;

        if( m_old_banker_id > 0 )
        {
            builder.setOldBankerId(m_old_banker_id);
            m_old_banker_id = 0;
        }

        broadcast_msg_to_client(builder);
        is_change_banker = true;
    }

    private  int broadcast_msg_to_client(Message.Builder builder)
    {
        return broadcast_msg_to_client( builder,0 );
    }

    private  int broadcast_msg_to_client(Message.Builder builder,int except_id)
    {
        Message msg = builder.build();
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());

        return broadcast_msg_to_client( packetId,msg,except_id );
    }

    private  int broadcast_msg_to_client(int packetId, Message msg,int except_id)
    {
        if( m_room_players.size() <= 0 )
        {
            return -1;
        }

        List<Integer> pids = new ArrayList<>();
        for(LogicPlayer logicPlayer : m_room_players.values())
        {
            pids.add(logicPlayer.get_pid());
        }

        return GameManager.Instance.broadcast_msg_to_client(pids,packetId,msg);
    }

    private GameRoomDoc load_room()
    {
        Criteria criteria = Criteria.where("RoomId").is(m_cfg.getmRoomID());
        gameRoomDoc =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria), GameRoomDoc.class, DbGameName.DB_BACCARAT_ROOM);
        return gameRoomDoc;
    }

    private void create_room()
    {
        StockCtrlObj.reset();
        gameRoomDoc = new GameRoomDoc();
        gameRoomDoc.setAwardPool(0.01);
        gameRoomDoc.setAwardTwoPair(0.005);
        gameRoomDoc.setAwardThree(0.01);
        gameRoomDoc.setAwardFullHouse(0.015);
        gameRoomDoc.setAwardFour(0.09);

        gameRoomDoc.setResetStock(false);
        gameRoomDoc.setRoomStock(m_stock_cfg.getmInitStock());
        gameRoomDoc.setOtherStock(0);
        gameRoomDoc.setBrightRate( m_stock_cfg.getmBrightWater() / 100.0 );
        gameRoomDoc.setBrightWater(0);
    }


    public double get_cd_time()
    {
        return  m_cd_time;
    }

    public  GameBaccaratProtocol.packetl2c_get_room_scene_info_result.Builder get_room_scene_info(LogicPlayer player)
    {
        GameBaccaratProtocol.packetl2c_get_room_scene_info_result.Builder builder = GameBaccaratProtocol.packetl2c_get_room_scene_info_result.newBuilder();
        builder.setRoomId(m_cfg.getmRoomID());
        builder.setRoomState(m_game_state);
        builder.setCdTime((int)(m_cd_time/1000));

        if (player != null )
        {
            builder.setSelfGolds( player.get_gold() );
        }

        for (int i = 0; i < MAX_BET_COUNT; i++)//m_max_bet_list
        {
            game_baccarat_protocols.GameBaccaratProtocol.msg_betinfo.Builder msg_betinfo_builder = game_baccarat_protocols.GameBaccaratProtocol.msg_betinfo.newBuilder();
            msg_betinfo_builder.setBetGolds(m_room_bet_list.get(i));
            msg_betinfo_builder.setMaxBetCount(m_max_bet_list.get(i));

            for (int masterId: m_master_id)
            {
                LogicPlayer it_player = m_room_players.get(masterId);

                if (it_player != null)
                {
                    if( i >= it_player.get_bet_list().size()  )
                    {
                        continue;
                    }

                    int bet_golds = it_player.get_bet_list().get(i);
                    if (bet_golds > 0)
                    {
                        game_baccarat_protocols.GameBaccaratProtocol.msg_master_bets.Builder master_builder = game_baccarat_protocols.GameBaccaratProtocol.msg_master_bets.newBuilder();
                        master_builder.setPlayerId(masterId);
                        master_builder.setPlayerBets(bet_golds);

                        msg_betinfo_builder.addMasterBets( master_builder );
                    }
                }
            }

            builder.addBetInfoList( msg_betinfo_builder );
        }

        if (player != null )
        {
            for (int i = 0; i < MAX_BET_COUNT &&  i < player.get_bet_list().size(); i++)//m_max_bet_list
            {
                builder.addSelfBetGolds( player.get_bet_list().get(i) );
            }
        }

        builder.setRemainCardCount( m_core_engine.get_remain_card_count() );
        builder.setBankerContinueCount( m_continue_banker_count );

        for ( GameBaccaratProtocol.history_info history_info : m_history_list)
        {
            game_baccarat_protocols.GameBaccaratProtocol.history_info.Builder history_info_builder = game_baccarat_protocols.GameBaccaratProtocol.history_info.newBuilder();
            history_info_builder.setIsTie(history_info.getIsTie());
            history_info_builder.setIsPlayerWin(history_info.getIsPlayerWin());
            history_info_builder.setIsPlayerPair(history_info.getIsPlayerPair());
            history_info_builder.setIsBankerPair(history_info.getIsPlayerPair());
            history_info_builder.setIsBankerWin(history_info.getIsBankerWin());
            history_info_builder.setWinPoint(history_info.getWinPoint());
            builder.addHistoryList(history_info_builder);
        }

        builder.setBankerWinGold(m_banker_total_win);
        builder.setIsCanRobBanker(is_can_rob_banker);

        game_baccarat_protocols.GameBaccaratProtocol.player_info.Builder banker_info_builder = game_baccarat_protocols.GameBaccaratProtocol.player_info.newBuilder();


        boolean is_system_banker = true;
        if (m_now_banker_id > 0)
        {
            LogicPlayer temp_banker = m_room_players.get(m_now_banker_id);
            if (temp_banker != null)
            {
                banker_info_builder.setPlayerId(m_now_banker_id);
                banker_info_builder.setPlayerNickname(temp_banker.get_nickname());
                banker_info_builder.setPlayerHeadFrame(temp_banker.get_head_frame_id());
                banker_info_builder.setPlayerHeadCustom(temp_banker.get_icon_custom());
                banker_info_builder.setPlayerGold(temp_banker.get_gold());
                banker_info_builder.setPlayerSex(temp_banker.get_player_sex());
                banker_info_builder.setPlayerVipLv(temp_banker.get_viplvl());

                is_system_banker = false;
            }
        }
        if (is_system_banker)
        {
            banker_info_builder.setPlayerId(0);
            banker_info_builder.setPlayerNickname("");
            banker_info_builder.setPlayerHeadFrame(0);
            banker_info_builder.setPlayerHeadCustom("");
            banker_info_builder.setPlayerGold(0);
            banker_info_builder.setPlayerSex(1);
            banker_info_builder.setPlayerVipLv(0);
        }
        builder.setBankerInfo(banker_info_builder);

        return builder;
    }

    public GameBaccaratProtocol.packetl2c_ask_for_player_list_result.Builder get_room_player_list()
    {
        GameBaccaratProtocol.packetl2c_ask_for_player_list_result.Builder builder =  GameBaccaratProtocol.packetl2c_ask_for_player_list_result.newBuilder();
        for (LogicPlayer logicPlayer : m_room_players.values())
        {
            game_baccarat_protocols.GameBaccaratProtocol.player_info.Builder player_info_builder = game_baccarat_protocols.GameBaccaratProtocol.player_info.newBuilder();
            builder.addPlayerList( player_info_builder );
            player_info_builder.setPlayerId( logicPlayer.get_pid() );
            player_info_builder.setPlayerNickname(logicPlayer.get_nickname());
            player_info_builder.setPlayerHeadFrame(logicPlayer.get_head_frame_id());
            player_info_builder.setPlayerHeadCustom(logicPlayer.get_icon_custom());
            player_info_builder.setPlayerGold(logicPlayer.get_gold());
            player_info_builder.setPlayerVipLv(logicPlayer.get_viplvl());
        }

        return builder;
    }

    public GameBaccaratProtocol.packetl2c_ask_for_banker_list_result.Builder get_room_banker_list()
    {
        GameBaccaratProtocol.packetl2c_ask_for_banker_list_result.Builder builder = GameBaccaratProtocol.packetl2c_ask_for_banker_list_result.newBuilder();
        for (int bankerId : m_banker_list)
        {
            LogicPlayer tempPlayer = m_room_players.get(bankerId);
            if (tempPlayer != null)
            {
                game_baccarat_protocols.GameBaccaratProtocol.player_info.Builder banker_list_builder = game_baccarat_protocols.GameBaccaratProtocol.player_info.newBuilder();
                banker_list_builder.setPlayerId( tempPlayer.get_pid() );
                banker_list_builder.setPlayerNickname(tempPlayer.get_nickname());
                banker_list_builder.setPlayerHeadFrame(tempPlayer.get_head_frame_id());
                banker_list_builder.setPlayerHeadCustom(tempPlayer.get_icon_custom());
                banker_list_builder.setPlayerGold(tempPlayer.get_gold());
                banker_list_builder.setPlayerSex(tempPlayer.get_player_sex());
                banker_list_builder.setPlayerVipLv(tempPlayer.get_viplvl());

                builder.addBankerList(banker_list_builder);

            }
        }

        return builder;
    }

    public GameBaccaratProtocol.packetl2c_ask_for_history_list_result.Builder get_room_history_list()
    {
        GameBaccaratProtocol.packetl2c_ask_for_history_list_result.Builder builder = GameBaccaratProtocol.packetl2c_ask_for_history_list_result.newBuilder();
        for ( GameBaccaratProtocol.history_info history_info :m_history_list)
        {
            game_baccarat_protocols.GameBaccaratProtocol.history_info.Builder history_info_builder = game_baccarat_protocols.GameBaccaratProtocol.history_info.newBuilder();
            history_info_builder.setIsTie(history_info.getIsTie());
            history_info_builder.setIsPlayerWin( history_info.getIsPlayerWin() );
            history_info_builder.setIsPlayerPair(history_info.getIsPlayerPair());
            history_info_builder.setIsBankerPair(history_info.getIsBankerPair());
            history_info_builder.setIsBankerWin(history_info.getIsBankerWin());
            history_info_builder.setWinPoint(history_info.getWinPoint());
            builder.addHistoryList(history_info_builder);
        }
        return builder;
    }

    public void send_cache_msg(LogicPlayer player)
    {
        if(m_game_state != GameBaccaratDef.e_game_state.e_state_game_award)
            return;

        if(player != null && m_cache_msg != null)
        {
            m_cache_msg.setCdTime((int)m_cd_time);
            player.send_msg_to_client(m_cache_msg);

        }
    }

    public void set_gm(int count)
    {
        is_gm = true;
        gm_index = 0;
        gm_max = count;
    }

    public boolean is_real_banker()
    {
        if (m_now_banker_id == 0)
            return false;
        LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);
        if (tempBanker != null)
        {
            if(!tempBanker.is_robot())
                return true;
        }
        return false;
    }

    public double rob_bet_count()
    {
        double temp_count = 0;
        if (m_now_banker_id == 0)
        {
            temp_count = RandomHandler.Instance.getRandomValue(0,20000);
        }
        else
        {
            int BetLimitRate = BaccaratBaseConfig.GetInstnace().GetData("BetLimitRate").getmValue();
            LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);
            if(tempBanker != null)
            {
                temp_count = (tempBanker.get_gold()/BetLimitRate)*RandomHandler.Instance.getRandomValue(0.001,0.015);
                if (temp_count > 20000)
                {
                    temp_count = RandomHandler.Instance.getRandomValue(0,20000);
                }
            }
        }

        return temp_count;
    }

    public int get_win_index()
    {
        int index = -1;
        List<Boolean> result_list = m_core_engine.get_result_list();
        if (result_list.get(1))
        {
            index = 1;
        }
        else if (result_list.get(4))
        {
            index = 4;
        }

        return index;
    }

    public int get_no_banker_count()
    {
        return m_no_banker_count;
    }

    public int get_duration()
    {
        return (int)m_cd_time;
    }


    public int get_player_count()
    {
        return m_room_players.size();
    }

    public List<LogicCore.CardType> get_player_card()
    {
        return m_core_engine.get_player_card();
    }

    public List<LogicCore.CardType> get_banker_card()
    {
        return m_core_engine.get_banker_card();
    }

    public void deduct_stock(int gold)
    {
        StockCtrlObj.deduct_stock(gold);
    }

    public void add_player_list(LogicPlayer player, int win, int bets)
    {
        WinnerList winner_list = m_map_players.get(bets);

        if (winner_list == null)
        {
            m_map_players.put( bets,new WinnerList() );
            winner_list = m_map_players.get(bets);
        }

        BetsList bets_list = winner_list.m_map_winner.get(win);

        if (bets_list == null )
        {
            winner_list.m_map_winner.put(win,new BetsList());
            bets_list = winner_list.m_map_winner.get(win);
        }


        bets_list.m_map_bets.put( player.get_gold(),player);

        player.inc_playing_cnt();

        if (m_players_cnt >= 50)
        {
            /**
            it_winner = m_map_players.begin();
            it_bets = it_winner->second.m_map_winner.begin();

            WinnerList winner_list = it_winner->second;
            BetsList bets_list = it_bets->second;

            auto it = bets_list.m_map_bets.begin();
            bets_list.m_map_bets.erase(it);

            if (bets_list.m_map_bets.empty())
                winner_list.m_map_winner.erase(it_bets);

            if (winner_list.m_map_winner.empty())
                m_map_players.erase(it_winner);
             **/
        }
        else
        {
            m_players_cnt++;
        }
    }


    public boolean is_ex_room()
    {
        return get_room_cfg().getmFreeGold() > 0;
        //return false;
    }
    public int get_free_gold()
    {
        return get_room_cfg().getmFreeGold();
    }


    public int check_players_control()
    {
        int pid = 0;

        if (is_ex_room()) {
            return 0;
        }

        int res = -1;
        int bankres = -1;
        LogicPlayer tempBanker = m_room_players.get(m_now_banker_id);

        if (tempBanker != null && !tempBanker.is_robot())
        {//玩家是庄家时
            pid = tempBanker.get_pid();
            bankres = PlayerControl.Instance.check_control(pid);
        }

        List<Integer> pids = new ArrayList<>();
        for ( LogicPlayer logicPlayer :  m_room_players.values() )
        {
            if (logicPlayer.is_robot())
            {
                continue;
            }
            boolean isbet = logicPlayer.get_once_bet_count() > 0;
            if (!isbet)
            {
                continue;
            }

            int id = logicPlayer.get_pid();
            pids.add(id);

            int tres = PlayerControl.Instance.check_control(id);
            if (res < 0) {
                res = tres;
            }

            if (tres != res) {
                //不是纯杀纯送
                res = -1;
                break;
            }
        }


        if (bankres > 0) {
            if (bankres == 1)
            {//庄家点控杀分，走通杀
                return 303;
            }
            else if (bankres == 2) {
                //庄家点控送分,下方无真人玩家,则通赔
                if (pids.size() <= 0) {
                    return 304;
                }
            }
        }
        else if (res > 0)
        {
            if (bankres == 0) {
                //有玩家庄,并且庄未点控时,闲家也不控
                return 0;
            }
            //玩家纯杀纯送
            int idx = RandomHandler.Instance.getRandomValue(0, pids.size() - 1);
            pid = pids.get(idx);
            return res == 1 ? 301 : 302;
        }
        return 0;
    }


    public int control_player_result(int leftCount, int rightCount, int ctype)
    {
        int res = 0;
        if (ctype == 301) {
            //杀分
            res = kill_pay(leftCount, rightCount, true, is_false_banker());
        }
        else if (ctype == 303) {
            //庄杀分

            res = kill_pay(leftCount, rightCount, true, is_false_banker());
        }
        else if (ctype == 302 || ctype == 304)
        {//送分
            if (ctype == 302) {
                LogicPlayer p = m_room_players.get(m_players_control_pid);
                List<Integer> temp_bet = p.get_bet_list();
                int bet_count = p.get_once_bet_count();

                List<rd_result> rd_resultArry = new ArrayList<>();
                rd_resultArry.clear();
                rd_result rdIndex = new rd_result();

                for (int i = 0; i < temp_bet.size(); i++) {
                    rdIndex.stock = (int)(m_room_odds_list.get(i) * temp_bet.get(i) - bet_count);
                    rdIndex.index = i;
                    rdIndex.weight = 10;
                    rd_resultArry.add(rdIndex);
                }
                rd_resultArry.sort(new sort_rd2());

                int allNum = 0;
                List<Integer> weightArry = new ArrayList<>();
                weightArry.clear();
                int indexEnd = -1;
                for (int i = 0; i < rd_resultArry.size(); ++i)
                {
                    if (rd_resultArry.get(i).stock > 0)
                    {
                        indexEnd = i;
                    }
                }

                if (-1 == indexEnd) {
                    return rd_resultArry.get(0).index + 1;
                }

                for (int i = 0; i <= indexEnd; ++i)
                {
                    allNum += rd_resultArry.get(i).weight;
                    weightArry.add(allNum);
                }
                int tem_index = 0;
                int rand_num = RandomHandler.Instance.getRandomValue(0, allNum);
                for (int i = 0; i < weightArry.size(); ++i)
                {
                    if (rand_num <= weightArry.get(i))
                    {
                        tem_index = i;
                        break;
                    }
                }

                res = rd_resultArry.get(tem_index).index + 1;
            }
            else
            {//庄送分
                res = kill_pay(leftCount, rightCount, false, is_false_banker());
            }

        }
        return res;
    }

    public boolean is_robot_can_bet()
    {
        if (m_game_state != GameBaccaratDef.e_game_state.e_state_game_bet)
        {
            return false;
        }
        if (m_cd_time < m_cfg_time_bet_robot_sub)
        {
            return false;
        }
        return true;
    }

    public boolean rob_canot_bet()
    {
        return true;
    }

    public class rd_result
    {
        int index;
        int stock;
        int weight;//权重
    };

    public class BetsList
    {
        private Map<Integer,LogicPlayer> m_map_bets;

        public BetsList() {
            m_map_bets = new HashMap<>();
        }
    }

    public  class WinnerList
    {
        private Map<Integer,BetsList> m_map_winner;

        public WinnerList() {
            m_map_winner = new HashMap<>();
        }
    }

    public class SortDesc implements Comparator<Integer>
    {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 -o1;
        }
    }

    public class SortAesc implements Comparator<Integer>
    {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    public class sort_rd2 implements Comparator<rd_result>
    {
        @Override
        public int compare(rd_result o1, rd_result o2) {
            return o2.stock - o1.stock;
        }
    }
}
