package com.wisp.game.bet.games.baccarat.logic;

import com.google.protobuf.Message;
import com.wisp.game.bet.GameConfig.BaccaratConfig.BaccaratBaseConfig;
import com.wisp.game.bet.games.baccarat.mgr.GameEngine;
import com.wisp.game.bet.games.share.config.RMConfig;
import com.wisp.game.bet.games.share.config.RMConfigData;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.IGamePHandler;
import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.core.random.RandomHandler;
import game_baccarat_protocols.GameBaccaratDef;
import msg_type_def.MsgTypeDef;
import pump_type.PumpType;

import java.util.ArrayList;
import java.util.List;

public class LogicPlayer implements IGamePHandler {
    public int MAX_BET_COUNT = 5;

    private GamePlayer gamePlayer;

    private LogicLobby m_lobby;
    private LogicRoom  m_room;

    private int bright_water;
    private int  m_change_water;

    private int first_bet_logicGold;

    private int m_logic_gold;					//当前拥有金币数量
    private int m_change_gold;					//单局变化金币
    private int m_once_real_win;	//本局赢钱流水,扣除下注额
    private int m_once_real_lose;	//本局输钱流水

    private int m_once_win_gold;				//单局赢得的金币数量
    private int m_once_beforeWater_win_gold;				//单局赢得的金币数量

    List<Integer> m_bet_list;			//当前下注链表
    List<Integer> m_award_list;			//获奖链表
    List<Integer> m_old_bet_list;		//上次下注链表
    private int m_bet_gold_count;				//本次下注总金额
    private boolean is_banker;							//是否是庄家
    private boolean is_ex_room;						//是否在测试场

    private int m_continue_bet_cnt;

    private double m_checksave;
    private double m_canBet;
    private boolean is_first_save;
    //Tfield<int32_t>::TFieldPtr m_player_id;

    private double m_rob_bet_cd;				//机器人下注CD
    private int m_rob_bet_max;					//单个机器人下注最大值
    private int m_rob_bet_remain;					//单个机器人剩余下注

    private int m_enter_time;
    private int m_playing_cnt;

    private List<Integer> m_win;

    public LogicPlayer() {
        m_bet_list = new ArrayList<>();
        m_award_list = new ArrayList<>();
        m_old_bet_list = new ArrayList<>();
        m_win = new ArrayList<>();
    }

    public int get_bet_win(){return m_once_win_gold;}

    public int get_playing_cnt() { return m_playing_cnt; }
    public void inc_playing_cnt() { m_playing_cnt++; }
    public void des_playing_cnt() { m_playing_cnt--; }

    public int get_once_bet_count(){return m_bet_gold_count;}
    public int get_once_win_count(){return m_once_win_gold;}

    public int send_msg_to_client(Message.Builder builder)
    {
        return gamePlayer.send_msg_to_client(builder);
    }

    public void heartbeat( double elapsed )
    {
        m_checksave += elapsed;
        if (m_checksave > 30)
        {
            if (is_first_save)
            {
                store_game_object(true);
                is_first_save = false;
            }
            else
                store_game_object(true);
            m_checksave = 0.0;
        }

        if (m_room == null)
            return;

        //int chip_min = m_room->get_room_cfg()->mWeightList[0];

        if ( m_room.get_room_state() == GameBaccaratDef.e_game_state.e_state_game_begin)
            first_bet_logicGold = m_logic_gold;

        if (is_robot() && !is_banker)
        {
            if (m_room.get_room_state() == GameBaccaratDef.e_game_state.e_state_game_begin)
            {
                if (is_banker_robot())
                    m_room.add_banker_list(get_pid());
                else
                    m_rob_bet_cd = gamePlayer.get_bet_interval(true);
            }
            else if (m_room.get_room_state() == GameBaccaratDef.e_game_state.e_state_game_bet)
            {
                m_rob_bet_cd -= elapsed;
                if (m_rob_bet_cd <= 0 && !is_banker_robot() && m_room.is_robot_can_bet())
                {
                    int bet_count = gamePlayer.get_bet_count(m_room.get_room_cfg().getmWeightList(), m_logic_gold);

                    int betkey = 0;
                    int random_index = RandomHandler.Instance.getRandomValue(1, 101);
                    if (random_index < 40)
                        betkey = 1;
                    else if (random_index < 80)
                        betkey = 4;
                    else
                        betkey = RandomHandler.Instance.getRandomValue(0, 5);



                    if (betkey == 1 && m_bet_list.get(4) > 0)
                        betkey = 4;
                    else if (betkey == 4 && m_bet_list.get(1) > 0)
                        betkey = 1;

                    if (add_bet(betkey, bet_count) == MsgTypeDef.e_msg_result_def.e_rmt_success)
                    {
                        if (m_room.is_real_banker())
                        {
                            //m_room.m_rob_outcome->add_value(bet_count);
                        }
                    }
                    m_rob_bet_cd = gamePlayer.get_bet_interval(false);
                }	///////////////////////
            }
        }
    }

    public int get_rob_bet()
    {
        int bet_count = 0;
        if (m_room.get_cd_time() > 15)
        {
            int random_base =  (int)( m_rob_bet_max*0.2/10);
            bet_count = RandomHandler.Instance.getRandomValue(random_base/2,random_base*3);
        }
        else if (m_room.get_cd_time() > 5)
        {
            int random_base = (int)(m_rob_bet_max*0.3/10);
            bet_count = RandomHandler.Instance.getRandomValue(random_base/2,random_base*3);
        }
        else
        {
            int random_base = (int)(m_rob_bet_max*0.5/5);
            bet_count = RandomHandler.Instance.getRandomValue(random_base,random_base*5);;
        }
        return bet_count;
    }

    public void enter_game(LogicLobby lobby)
    {
        m_lobby = lobby;
        //m_player_id->set_value(get_pid());
        load_player();

        m_logic_gold = gamePlayer.Gold;//m_player->get_attribute64(msg_type_def::e_itd_gold);

        //create_player();
    }

    public boolean enter_room(LogicRoom room)
    {
        if(m_room != null && room == null)
            return false;

        m_room = room;
        //m_player->roomLog(true, m_room->get_id(), get_gold());
        if (room.is_ex_room()) {
            if ( gamePlayer.is_banker_robot()) {
                //庄家机器人不改金币
                m_logic_gold = gamePlayer.Gold; // m_player->get_attribute64(msg_type_def::e_itd_gold);//m_player->get_attribute64(msg_type_def::e_itd_gold);
            }
            else {
                m_logic_gold =  room.get_free_gold();
            }
            is_ex_room = true;
        }
        else {
            is_ex_room = false;
            m_logic_gold =  gamePlayer.Gold  ;//m_player->get_attribute64(msg_type_def::e_itd_gold);//m_player->get_attribute64(msg_type_def::e_itd_gold);
        }

        m_bet_list.clear();;
        m_old_bet_list.clear();
        m_award_list.clear();

        first_bet_logicGold = m_logic_gold;

        return true;
    }

    public int get_pid()
    {
        return gamePlayer.get_playerid();
    }
    public String get_channelid()
    {
        return gamePlayer.get_channelid();
    }

    public  int get_viplvl()
    {
        return gamePlayer.VIPLevel;
    }

    public  int get_gold()
    {
        return m_logic_gold;
    }

    public int get_bet_max(int index)
    {
        return m_room.get_room_cfg().getmBetLimit().get(index);
    }

    public boolean change_gold(int/*int*/ v)
    {
        if(-v <= m_logic_gold)
        {
            m_logic_gold += v;
            m_change_gold += v;
            return true;
        }
        return false;
    }

    public void sycn_gold()
    {
        if ( is_ex_room ) {
            m_change_gold = 0;
            return;
        }

        if(get_bet_win() > 0)
        {
            //m_player->notify_share(1, get_bet_win());
        }


        if (m_room != null)
            save_bet_info();

        if (m_change_gold != 0)
        {
            //在百家乐中玩10局
            //m_player->quest_change(1006);
            //在百家乐任意场次累计赢得1000元
            //if(get_bet_win() > 0)
            //{
            //m_player->quest_change(1106, get_bet_win());
            //任意游戏赢钱
            //m_player->quest_change(3015, get_bet_win());
            //任意游戏输赢
            //m_player->quest_change(3016, get_bet_win());
            //}
            if(m_change_gold > 0)
            {
                //百家乐任意场累计赢得1000元
                //m_player->quest_change(1106, m_change_gold);
                //任意游戏赢钱
                //m_player->quest_change(3015, m_change_gold);
                //任意游戏赢钱
                //m_player->quest_change(3016, m_change_gold);
            }
            //在百家乐任意场次累计下注10000元
            //m_player->quest_change(1206, get_once_bet_count());

            gamePlayer.change_gold(m_change_gold);
            m_change_gold = 0;
        }
    }

    public boolean is_robot()
    {
        return gamePlayer.is_robot();
    }

    public boolean change_gold2(int v, PumpType.PropertyReasonType reason)
    {
        boolean ret =  gamePlayer.change_gold(v);// m_player->change_gold(v);
        if (ret)
        {
            m_logic_gold += v;
            //记录
            //db_log::instance().property_log(this, game_engine::instance().get_gameid(),
            //	1, v, reason);
            if(!is_robot())
            {
                //game_common_log::instance().player_gold_log(get_pid(), get_channelid(), m_room->get_room_id(), get_gold(), v, reason, "");
            }
        }
        return ret;
    }

    public boolean change_ticket(int count, PumpType.PropertyReasonType reason)
    {
        gamePlayer.change_ticket(count);
        if (reason.getNumber() != -1)
        {
            //db_log::instance().property_log(this, game_engine::instance().get_gameid(), 2,count,reason);
        }
        return true;
    }

    public int get_ticket()
    {
        return gamePlayer.Ticket;
    }

    public void set_bright_water(int water)
    {
        bright_water += water;
        //m_change_water += water;//业绩算抽水前的对应区域的纯输赢
    }

    public String  get_nickname()
    {
        return gamePlayer.get_nickname();
    }

    public String get_icon_custom()
    {
        return gamePlayer.get_icon_custom();
    }

    public int get_head_frame_id()
    {
        return 0;
    }

    public int get_player_sex()
    {
        return gamePlayer.Sex;
    }

    public int get_room_id()
    {
        if(m_room != null)
            return m_room.get_room_id();
        else
            return 0;
    }

    public void leave_room()
    {
        if(m_room != null)
        {
            //m_player->roomLog(false, m_room->get_id(), get_gold());
            if (m_room.get_room_state() == GameBaccaratDef.e_game_state.e_state_game_bet)
            {
                clear_bet();		//退出桌子押注数据无效
                m_room.set_is_have_bet(true);
            }

            m_room.leave_room(get_pid());
            m_room = null;
        }
        clear_table_data();

        sycn_gold();
        sync_water();

        if (is_first_save)		//保存数据
        {
            store_game_object(true);
            is_first_save = false;
        }
        else
            store_game_object(false);
    }

    public e_player_state get_game_state()
    {
        return gamePlayer.get_state();
    }

    public void release()
    {
        leave_room();
        gamePlayer.reset();
    }


    public void clear_once_data()
    {
        for (int i = 0;i < MAX_BET_COUNT; ++i)
        {
            m_old_bet_list.set(i,m_bet_list.get(i));
        }

        //memset(&m_bet_list[0],0,sizeof(GOLD_TYPE/*int*/) * MAX_BET_COUNT);
        //memset(&m_award_list[0],0,sizeof(GOLD_TYPE) * MAX_BET_COUNT);

        m_bet_list.clear();
        m_award_list.clear();

        m_continue_bet_cnt = 0;

        m_bet_gold_count = 0;
        m_once_win_gold = 0;
        m_once_beforeWater_win_gold = 0;
    }


    public void clear_table_data()
    {
       // memset(&m_bet_list[0],0,sizeof(GOLD_TYPE/*int*/) * MAX_BET_COUNT);
        //memset(&m_old_bet_list[0],0,sizeof(GOLD_TYPE/*int*/)*MAX_BET_COUNT);
        //memset(&m_award_list[0],0,sizeof(GOLD_TYPE)*MAX_BET_COUNT);

        m_bet_list.clear();;
        m_old_bet_list.clear();
        m_award_list.clear();

        is_banker = false;
    }
    public boolean rob_canot_bet(int index,int count)
    {
        if(!is_robot())
            return false;
        if(index!=1 && index != 4)
            return false;
        if(m_room.rob_canot_bet())
            return true;
        return false;
    }

    public MsgTypeDef.e_msg_result_def add_bet(int index,int count)
    {
        if (is_banker)
            return MsgTypeDef.e_msg_result_def.e_rmt_banker_not_bet;

        if (index < 0 || index >= MAX_BET_COUNT)
            return MsgTypeDef.e_msg_result_def.e_rmt_bet_index_error;			//押注序号不对

        if(m_room != null && m_room.get_betCondition() > first_bet_logicGold)
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_betgold_not_enough;	//可下注的金额判断
        }



        if (m_room == null)
            return MsgTypeDef.e_msg_result_def.e_rmt_no_find_table;

        if ((m_bet_list.get(index) + count) >  get_bet_max(index))
        {
            if(m_bet_list.get(index)>= get_bet_max(index))
                return MsgTypeDef.e_msg_result_def.e_rmt_other_betgold_is_full;			//超过单个押注上限
            else
                count = get_bet_max(index) - m_bet_list.get(index);
        }

        if(m_room.get_now_real_banker_id() != 0)//非系统小庄坐庄
        {
            int temp_count = m_room.get_can_bet_count(index);
            if (temp_count > 0)
            {
                if (count > temp_count)
                {
                    count = temp_count/100 *100;
                }
            }
        }
        if ( count <= 0 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_outof_bet_limit;
        }

        if (m_logic_gold < count )
            return MsgTypeDef.e_msg_result_def.e_rmt_gold_not_enough;		//金币不足

        //if(rob_canot_bet(index,count))//机器人超过单个机器人能下注的上限
        //    return msg_type_def::e_rmt_outof_bet_limit;

        //m_room->get_game_detail_log()->add_bet_gold(get_pid(), get_channelid(), is_robot(), index, count);

        m_logic_gold -= count;
        m_change_gold -= count;
        m_bet_gold_count += count;
        //m_bet_list[index] += count;
        m_bet_list.set(index,m_bet_list.get(index) + count);
        m_room.set_is_have_bet(true);

        m_room.set_can_bet_count();

        return MsgTypeDef.e_msg_result_def.e_rmt_success;			//成功
    }

    public MsgTypeDef.e_msg_result_def repeat_bet()
    {
        if (is_banker)
            return MsgTypeDef.e_msg_result_def.e_rmt_banker_not_bet;

        if (m_continue_bet_cnt == 0)
        {
            for (int i = 0;i < MAX_BET_COUNT; ++i)
            {
                if(m_bet_list.get(i)!=0)
                    return  MsgTypeDef.e_msg_result_def.e_rmt_can_not_bet_hasbet;
            }

            clear_bet();		//清零原有下注
        }
        m_continue_bet_cnt++;
        //if (m_continue_bet_cnt > 2)
        //	return  msg_type_def::e_rmt_can_not_bet_hasbet;

        if(m_room != null && m_room.get_betCondition() > first_bet_logicGold)
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_betgold_not_enough;	//可下注的金额判断
        }

        int temp_gold = 0;
        int temp_count  = 0;
        for (int i = 0;i < MAX_BET_COUNT; ++i)
        {
            temp_gold += m_old_bet_list.get(i);

            temp_count = m_room.get_can_bet_count(i);
            if (temp_count >= 0)
            {
                if (m_old_bet_list.get(i) * m_continue_bet_cnt > temp_count)
                    return MsgTypeDef.e_msg_result_def.e_rmt_outof_bet_limit;
            }
        }
        if (m_logic_gold < temp_gold)
            return MsgTypeDef.e_msg_result_def.e_rmt_gold_not_enough;

        if (m_room == null)
            return MsgTypeDef.e_msg_result_def.e_rmt_no_find_table;

        for (int j = 0;j < MAX_BET_COUNT; ++j)
        {
            if (m_old_bet_list.get(j) * m_continue_bet_cnt > get_bet_max(j))
                return MsgTypeDef.e_msg_result_def.e_rmt_other_betgold_is_full;			//超过单个押注上限
        }

        for (int j = 0; j < MAX_BET_COUNT; ++j)
        {
            //m_bet_list[j] += m_old_bet_list[j];
            //m_bet_gold_count += m_old_bet_list[j];
            //m_room->get_game_detail_log()->add_bet_gold(get_pid(), get_channelid(), is_robot(), j, m_old_bet_list[j]);
        }


        m_change_gold -=temp_gold;
        m_logic_gold -= temp_gold;
        m_room.set_is_have_bet(true);
        m_room.set_can_bet_count();
        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public MsgTypeDef.e_msg_result_def clear_bet()
    {
        //for (int i = 0; i < MAX_BET_COUNT; ++i)
        //{
        //	m_logic_gold += m_bet_list[i];
        //	m_change_gold += m_bet_list[i];
        //}
        //memset(&m_bet_list[0],0,sizeof(GOLD_TYPE/*int*/)*MAX_BET_COUNT);
        m_bet_list.clear();
        m_bet_gold_count = 0;

        //m_room->get_game_detail_log()->clear_bet_gold(get_pid());
        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public MsgTypeDef.e_msg_result_def leave_banker()
    {
        if (!is_banker)
            return MsgTypeDef.e_msg_result_def.e_rmt_success;

        int MinBankerCount = BaccaratBaseConfig.GetInstnace().GetData("MinBankerCount").getmValue();
        if (m_room.get_continue_banker_count() >= MinBankerCount)		//达到最小连庄次数
        {
            if (m_room.get_now_banker_id() == get_pid())
                m_room.set_now_banker_null(get_pid());
            else
                return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }
        else
        {
            if (get_ticket() >= m_room.get_room_cfg().getmLeaveBankerCost())	//扣除提前下庄的礼券
            {
                if (m_room.get_now_banker_id() == get_pid())
                {
                    change_ticket(-m_room.get_room_cfg().getmLeaveBankerCost(), PumpType.PropertyReasonType.type_reason_leave_banker);
                    m_room.set_now_banker_null(get_pid());
                }
                else
                    return MsgTypeDef.e_msg_result_def.e_rmt_fail;
            }
		else
            return MsgTypeDef.e_msg_result_def.e_rmt_ticket_not_enough;
        }

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }

    public void add_bet_win(int count)
    {
        m_once_win_gold += count;

        change_gold(count);
    }
    public void add_bet_beforeWater_win(int count)
    {
        m_once_beforeWater_win_gold += count;
    }

    public List<Integer> get_bet_list()
    {
        return m_bet_list;
    }

    public boolean set_is_banker(boolean isbanker)
    {
        if (m_room == null)
            return false;

        if (!isbanker)
        {
            if (is_banker)
            {
                double SystemRate = BaccaratBaseConfig.GetInstnace().GetData("SystemDrawWater").getmValue()/100.0;
                int banker_win = m_room.get_banker_win();
                int sys_win = 0;
                if (banker_win > 0)
                    sys_win = (int)(banker_win*SystemRate);

                change_gold(-sys_win);

                //if (!is_robot())
                 //   db_log::instance().playerBanker(this,m_room->get_continue_count(),(m_logic_gold - banker_win),m_logic_gold,banker_win,sys_win);
            }
        }

        if (isbanker)
        {
            if (m_logic_gold >= m_room.get_room_cfg().getmBankerCondition())
            {
                is_banker = true;
                return true;
            }
            return false;
        }
        else
            is_banker = false;

        return true;
    }

    public boolean load_player()
    {
        return true;
    }

    public void init_game_object()
    {
        //m_player_id = CONVERT_POINT(Tfield<int32_t>, regedit_tfield(e_got_int32, "PlayerId"));
    }

    public boolean store_game_object(boolean to_all)
    {

        return true;
    }

    public void bc_game_msg(int money, Object obj, int mtype)
    {
        if(m_room != null && !is_ex_room)
        {
            RMConfigData rcfg = m_room.get_room_cfg();
            //gamePlayer.game_broadcast(rcfg.getmRoomType(), rcfg.getmRoomIDTxt(), 2, sinfo, money, mtype);
        }
    }

    public int get_privilege()
    {
        return 0;
    }

    public void insert_control(int control_type, List<Integer> control_param)
    {
        //db_log::instance().gamecontrol_log(this, game_engine::instance().get_gameid(), control_type, control_param);
    }

    public void add_award_list(int index,int count)
    {
        m_award_list.set( index,m_award_list.get(index) + count );;
    }
    public void save_bet_info()
    {

        bright_water = 0;
    }

    public void sync_water()
    {
    }

    public  void activity_change(
            int count,
            int bet,
            int win,
            int lose,
            int performance,
            boolean sync,
            boolean updateprofit)
    {
        if (is_robot() || is_ex_room )
            return;
        if (win == 0 && lose == 0) {
            return;
        }

    }

    public void add_once_water(int value, boolean win)
    {
        if(is_robot())
            return;

        m_change_water += value;
        if(win)
            m_once_real_win += value;
        else
            m_once_real_lose += value;
    }

    public void add_once_water_banker(int value, boolean win, int performance)
    {
        if(is_robot())
            return;

        m_change_water += performance;
        if(win)
            m_once_real_win += value;
        else
            m_once_real_lose += value;
    }

    public void get_once_winlose_gold(int win, int lose)
    {
        win = m_once_real_win;
        lose = m_once_real_lose;

        m_once_real_win = 0;
        m_once_real_lose = 0;
    }

    public boolean robot_can_exit()
    {
        if (!is_robot())
            return false;

        if (m_room == null)
            return true;

        if ( gamePlayer.is_banker_robot())
        {
            if (get_is_banker())
                return false;

            if (m_logic_gold < m_room.get_room_cfg().getmBankerCondition())
            return true;
        }
        else if (m_logic_gold < gamePlayer.robot_exit_gold() && get_once_bet_count() == 0)
            return true;
        else if ( gamePlayer.need_release())
            return true;

        return false;

    }

    public boolean is_banker_robot()
    {
        return gamePlayer.is_banker_robot();
    }


    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public boolean get_is_banker()
    {
        return false;
    }

    public LogicRoom get_room()
    {
        return null;
    }
    public List<Integer> getWins()
    {
        return m_win;
    }
}
