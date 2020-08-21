package com.wisp.game.bet.games.share;

import com.wisp.game.bet.db.mongo.games.doc.PlayerCtrlDoc;
import com.wisp.game.bet.games.baccarat.mgr.GameEngine;
import com.wisp.game.bet.logic.db.DbGame;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.core.random.RandomHandler;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerControl {
    public static PlayerControl Instance;

    private Map<Integer, PlayerCtrlDoc> m_ctrl_map;
    public PlayerControl() {
        Instance = this;
        m_ctrl_map = new HashMap<>();
    }


    /**************************************************
     @brief   : 每局游戏开始前调用，检查点控杀分还是送分
     @param   : player_id 玩家ID
     @return  ：0随机，1杀分 2送分
     **************************************************/
    public int check_control(int player_id)
    {
        int cur_time = TimeHelper.Instance.get_cur_time();
        PlayerCtrlDoc it = m_ctrl_map.get(player_id);
        if (it == null || cur_time - it.getCheck_time()> 20)
        {
            //auto filter = document{} << "PlayerId" << player_id <<finalize;
            //auto doc = db_game::instance().findone(DB_PLAYER_CTRL, filter);

            Criteria criteria = Criteria.where("PlayerId").is(player_id);
            PlayerCtrlDoc playerCtrlDoc = DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),PlayerCtrlDoc.class);

            if (playerCtrlDoc != null)
            {
                PlayerCtrlDoc ptr = playerCtrlDoc;
                if (it == null)
                {
                    m_ctrl_map.put(player_id,ptr);
                    it = m_ctrl_map.get(player_id);
                }
                else
                {
                    it.setRate(ptr.getRate());
                    it.setType(ptr.getType());
                    it.setCtrlGold(ptr.getCtrlGold());
                    it.setStartTime(ptr.getStartTime());
                    it.setEndTime(ptr.getEndTime());
                    if( it.getKey() != ptr.getKey() )
                    {
                        it.setKey(ptr.getKey());
                        it.setCurrentGold(0);
                    }
                    it.setGames(new ArrayList<>());
                    for(int i = 0; i < ptr.getGames().size();i++)
                    {
                        it.getGames().add(ptr.getGames().get(i));
                    }

                }

                ptr.setCheck_time(TimeHelper.Instance.get_cur_time());
            }
            else
            {
                //防止频繁查询数据库
                if (it == null)
                {
                    PlayerCtrlDoc ptr = new PlayerCtrlDoc();
                    ptr.setPlayerId(player_id);
                    ptr.setType(0);
                    ptr.setCheck_time(TimeHelper.Instance.get_cur_time());

                    m_ctrl_map.put(player_id,ptr);
                    it = m_ctrl_map.get(player_id);
                }
                else
                {
                    //it->second->m_check_time = time_helper::instance().get_cur_time();
                    it.setCheck_time(TimeHelper.Instance.get_cur_time());
                }


            }
        }

        if (it == null || it.getType() == 0)
            return 0;


        PlayerCtrlDoc ctrl_ptr = it;
        if (ctrl_ptr.getGames() != null && ctrl_ptr.getGames().size() > 0 )
        {
            //若已指定点控游戏列表，判断当前游戏是否需要控制
            int gameid = GameEngine.Instance.get_gameid();
            List<Integer> vec = ctrl_ptr.getGames();
            if( !vec.contains(gameid) )
            {
                return 0;
            }

        }

        //点控时间
        if ( TimeHelper.Instance.get_cur_time() > ctrl_ptr.getEndTime().getSecondTime()
            || TimeHelper.Instance.get_cur_time() < ctrl_ptr.getEndTime().getSecondTime())
        {
            return 0;
        }

        if (ctrl_ptr.getType() == 1)
        {
            if (ctrl_ptr.getCurrentGold() <=   ctrl_ptr.getCtrlGold())
                return 0;

            double r = RandomHandler.Instance.getRandomValue(0,1);
            if (r <= ctrl_ptr.getRate())
            {
                return 1;
            }
        }
        else if (ctrl_ptr.getType() == 2)
        {
            if (ctrl_ptr.getCurrentGold() >= ctrl_ptr.getCtrlGold())
                return 0;

            double r =  RandomHandler.Instance.getRandomValue(0,1);// global_random::instance().rand_01();
            if (r <= ctrl_ptr.getRate())
            {
                return 2;
            }
        }

        return 0;
    }

    /**************************************************
     @brief   : 返回点控类型，点控概率
     @param   : player_id 玩家ID
     @param   : rate 点控概率
     @return  ：0随机，1杀分 2送分
     **************************************************/
    public int check_control_rate(int player_id, double rate)
    {
        int gameid = GameEngine.Instance.get_gameid();
        int cur_time =TimeHelper.Instance.get_cur_time();//  time_helper::instance().get_cur_time();
        PlayerCtrlDoc it = m_ctrl_map.get(player_id);
        if (it == null || cur_time - it.getCheck_time() > 20)
        {
            Criteria criteria = Criteria.where("PlayerId").is(player_id);
            PlayerCtrlDoc ptr =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),PlayerCtrlDoc.class);

            if (ptr != null )
            {
                if (it == null )
                {
                    m_ctrl_map.put( player_id,ptr );
                    it = m_ctrl_map.get(player_id);
                }
                else
                {
                    it.setRate(ptr.getRate());
                    it.setType(ptr.getType());
                    it.setCtrlGold(ptr.getCtrlGold());
                    it.getStartTime().setSecondTime(ptr.getStartTime().getSecondTime());
                    it.getEndTime().setSecondTime(ptr.getEndTime().getSecondTime());
                    if( it.getKey() != ptr.getKey() )
                    {
                        it.setKey(ptr.getKey());
                        it.setCurrentGold(ptr.getCurrentGold());
                    }
                    it.getGames().clear();
                    for(int i = 0; i < ptr.getGames().size();i++)
                    {
                        it.getGames().add(ptr.getGames().get(i));
                    }
                }

                ptr.setCheck_time(TimeHelper.Instance.get_cur_time());
            }
            else
            {
                //防止频繁查询数据库
                if (it == null)
                {
                    ptr = new PlayerCtrlDoc();
                    ptr.setPlayerId(player_id);
                    ptr.setType(0);
                    ptr.setCheck_time(TimeHelper.Instance.get_cur_time());

                    m_ctrl_map.put(player_id,ptr);
                    it = m_ctrl_map.get(player_id);

                }
                else
                {
                    it.setCheck_time(TimeHelper.Instance.get_cur_time());
                }
            }
        }

        if (it == null|| it.getType() == 0)
            return 0;


        PlayerCtrlDoc ctrl_ptr = it;
        if (ctrl_ptr.getGames().size() > 0)
        {
            //若已指定点控游戏列表，判断当前游戏是否需要控制
            if(!ctrl_ptr.getGames().contains(gameid))
            {
                return 0;
            }
        }

        //点控时间
        if (TimeHelper.Instance.get_cur_time() >    ctrl_ptr.getEndTime().getSecondTime()
            || TimeHelper.Instance.get_cur_time() < ctrl_ptr.getStartTime().getSecondTime())
        {
            return 0;
        }

        if (ctrl_ptr.getType() == 1)
        {
            if (ctrl_ptr.getCurrentGold() <= ctrl_ptr.getCtrlGold())
                return 0;

            rate = ctrl_ptr.getRate();
            return 1;
        }
        else if (ctrl_ptr.getType() == 2)
        {
            if (ctrl_ptr.getCurrentGold() >= ctrl_ptr.getCtrlGold())
                return 0;

            rate = ctrl_ptr.getRate();
            return 2;
        }

        return 0;
    }

    /**************************************************
     @brief   : 点控结果
     @param   : player_id 玩家ID
     @param   : change_gold 输赢金额
     @return  ：none
     **************************************************/
    public void control_result(int player_id, int change_gold)
    {
        if (change_gold == 0)
            return;

        int gameid = GameEngine.Instance.get_gameid();// game_engine::instance().get_gameid();
        int cur_time = TimeHelper.Instance.get_cur_time();// time_helper::instance().get_cur_time();
        PlayerCtrlDoc it = m_ctrl_map.get(player_id);
        if (it == null)
        {
            Criteria criteria = Criteria.where("PlayerId").is(player_id);
            PlayerCtrlDoc ptr =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),PlayerCtrlDoc.class);

            if (ptr != null )
            {
                ptr.setCheck_time( TimeHelper.Instance.get_cur_time() );
                m_ctrl_map.put(player_id,ptr);
                it = m_ctrl_map.get(player_id);

                if (ptr.getGames().size() > 0)
                {
                    //若已指定点控游戏列表，判断当前游戏是否需要控制
                    if( !ptr.getGames().contains(gameid) )
                    {
                        return;
                    }
                }

                if (cur_time > ptr.getStartTime().getSecondTime() && cur_time < ptr.getEndTime().getSecondTime())
                {
                    if (ptr.getType() != 0)
                    {
                        ptr.setCurrentGold( ptr.getCtrlGold() + change_gold );
                        ptr.store_game_object( DbGame.Instance.getMongoTemplate() );
                    }
                }
            }
        }
        else
        {
            if (it.getType() == 0)
                return;

            if (it.getGames().size() > 0)
            {
                //若已指定点控游戏列表，判断当前游戏是否需要控制
                if( it.getGames().contains(player_id) )
                {
                    return;
                }
            }

            it.setCurrentGold( it.getCurrentGold() + change_gold );
            it.store_game_object(DbGame.Instance.getMongoTemplate());
        }
    }


    /**************************************************
     @brief   : 玩家离开游戏
     @param   : player_id 玩家ID
     @return  ：none
     **************************************************/
    public void leave_game(int player_id)
    {
        PlayerCtrlDoc it = m_ctrl_map.get(player_id);
        if (it != null && it.getType() != 0)
        {
            it.store_game_object(DbGame.Instance.getMongoTemplate());
            m_ctrl_map.remove(player_id);
        }
    }

    /**************************************************
     @brief   : 获取当前已点控金额,目标金额
     @param   : player_id 玩家ID ctrl_gold 点控目标金额 current_gold 当前已点控金额
     @return  ：none
     **************************************************/
    public CtrlGoldReturn get_ctrl_gold(int player_id)
    {
        PlayerCtrlDoc it = m_ctrl_map.get(player_id);
        if (it != null)
        {
            int ctrl_gold = it.getCtrlGold(); //it->second->m_ctrl_gold->get_value();
            int current_gold = it.getCurrentGold() ;//it->second->m_current_gold->get_value();

            CtrlGoldReturn ctrlGoldReturn = new CtrlGoldReturn();
            ctrlGoldReturn.ctrl_gold = ctrl_gold;
            ctrlGoldReturn.current_gold = current_gold;

            return ctrlGoldReturn;
        }

        return null;
    }

    public class CtrlGoldReturn
    {
        public int ctrl_gold;
        public int current_gold;
    }
}
