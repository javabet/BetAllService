package com.wisp.game.bet.games.share;

import com.wisp.game.bet.db.mongo.games.doc.GameRoomDoc;
import com.wisp.game.bet.games.share.config.RMStockConfigData;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.core.random.RandomHandler;

import java.util.ArrayList;
import java.util.List;

public class StockCtrlXX {
    private GameRoomDoc gameRoomDoc;
    private RMStockConfigData m_stock_cfg;

    private boolean control_game;   //是否开启控制
    private int control_times;      //控制次数
    private int control_min_param;  //控制参数1
    private int control_max_param;  //控制参数2
    private int m_control_end;      //杀分结束时间
    private int control_param;
    private int m_cycle_num;
    private int m_cur_day;


    private int m_game_room_stock_base_stock;
    private int m_game_room_stock_init_stock;
    private int m_game_room_stock_max_stock;
    private int m_game_room_stock_relush_time;
    private int m_game_room_stock_ext_kill_per;

    private List<Integer> m_vec_game_room_stock_buff;
    private List<Boolean> m_vec_game_room_stock_stage;
    private List<Integer> m_vec_game_room_stock_strong_kill;

    private List<Integer> m_vec_game_room_stock_weak_kill;
    private boolean m_vec_game_room_stock_flag;

    public StockCtrlXX() {
        m_cycle_num = 0;
        m_cur_day = TimeHelper.Instance.get_cur_time();

        control_times = 0;
        control_min_param = 0;
        control_max_param = 1;
        control_param = 0;
        control_game = false;
        m_control_end = 0;

        m_vec_game_room_stock_flag = false;
        m_game_room_stock_base_stock = 0;
        m_game_room_stock_init_stock = 0;
        m_game_room_stock_max_stock = 0;
        m_game_room_stock_relush_time = 0;
        m_game_room_stock_ext_kill_per = 0;
    }

    public void init(RMStockConfigData stock_cfg, GameRoomDoc doc, int agentId, boolean stock_obj  )
    {
        m_stock_cfg = stock_cfg;
        this.gameRoomDoc = doc;
    }

    //qxzl修复agentid为0的 bug, 后继版本移除
    public void setAgentId(int agentid)
    {
        gameRoomDoc.setAgentId(agentid);
    }

    public  void reset()
    {
        {
            control_times = 0;
            control_min_param = 0;
            control_max_param = 1;
            control_param = 0;
            control_game = false;
            m_control_end = 0;
        }
    }

    public void add_other_stock( int v)
    {
        gameRoomDoc.setOtherStock( gameRoomDoc.getOtherStock() + v );
    }

    public void add_stock(int v)
    {
        gameRoomDoc.setRoomStock( gameRoomDoc.getRoomStock() + v );
    }

    public  long  get_stock()
    {
        return gameRoomDoc.getRoomStock();
    }

    public void add_bright_water(int v)
    {
        gameRoomDoc.setBrightWater(gameRoomDoc.getBrightWater() + v);
    }

    public long get_bright_water()
    {
        return gameRoomDoc.getBrightWater();
    }

    public void deduct_stock(int v)
    {
        gameRoomDoc.setRoomStock(  gameRoomDoc.getRoomStock() + v );

        if( gameRoomDoc.getRoomStock() < 0 )
        {
            gameRoomDoc.setRoomStock(0);
        }
    }

    public double get_earnings_rate()
    {
        return gameRoomDoc.getBrightRate();
    }

    public double getAwardPool()
    {
        return gameRoomDoc.getAwardPool();
    }

    public double getAwardTwoPair()
    {
        return gameRoomDoc.getAwardTwoPair();
    }

    public double getAwardThree()
    {
        return gameRoomDoc.getAwardThree();
    }

    public double getAwardFullHouse()
    {
        return gameRoomDoc.getAwardFullHouse();
    }

    public double getAwardFour()
    {
        return gameRoomDoc.getAwardFour();
    }

    public void reflush_stock()
    {
        int relushTime = m_stock_cfg.getmRelushTime();
        int maxStock = m_stock_cfg.getmMaxStock();

        int now = TimeHelper.Instance.get_cur_time();
        if (now - m_cur_day > relushTime)
        {
            m_cur_day = now;

            long stock = gameRoomDoc.getRoomStock();

            if (stock > maxStock)
            {
                long temp = stock - maxStock;
                gameRoomDoc.setOtherStock( gameRoomDoc.getRoomStock() + temp );
                gameRoomDoc.setRoomStock(maxStock);
            }
        }
    }


    //百人游戏杀分检测
    public int get_earn_type(int win_gold , int other_stock)
    {
        return check_stock(win_gold, other_stock);
    }

    //强杀,弱杀
    public EarnType get_earn_type2()
    {
        int baseStock = m_stock_cfg.getmBaseStock();
        List<Float> vecStockStage =  m_stock_cfg.getmStockStage();// get_stock_stage();
        List<Integer> vecStrongKill = m_stock_cfg.getmStrongKill();//get_strong_kill();
        List<Integer> vecWeakKill =  m_stock_cfg.getmWeakKill();// get_weak_kill();

        long stock = gameRoomDoc.getRoomStock();// RoomStock->get_value();
        double stock_stage = (double)stock / baseStock;

        int stage = 0;
        for (int i = 0; i < vecStockStage.size(); i++)
        {
            if (stock_stage >= vecStockStage.get(i))
                stage = i;
        }

        //int allWeight = 100;
        int leftWeight =  100;
        //std::vector<std::pair<bool, int>> weight_vec;

        List<WeightItem> weight_vec = new ArrayList<>();


        int strengWeight = vecStrongKill.get(stage);
        strengWeight = leftWeight > strengWeight ? strengWeight : leftWeight;
        leftWeight -= strengWeight;

        int weakWeight = vecWeakKill.get(stage);
        weakWeight = leftWeight > weakWeight ? weakWeight : leftWeight;
        leftWeight -= weakWeight;

        int allWeight = leftWeight;
        weight_vec.add(  new WeightItem(leftWeight != 0, allWeight) );
        allWeight += weakWeight;
        weight_vec.add(new WeightItem(weakWeight != 0, allWeight));
        allWeight += strengWeight;
        weight_vec.add(new WeightItem(strengWeight != 0, allWeight));


        int r = RandomHandler.Instance.getRandomValue(0, allWeight);
        int index = 0;
        for (int i = 0; i < weight_vec.size(); i++)
        {
            if (r <= weight_vec.get(i).weight && weight_vec.get(i).key)
            {
                index = i;
                break;
            }
        }

        EarnType earnType = new EarnType();
        earnType.stageIndex = stage;
        earnType.earnType = -index;
        return earnType;
    }

    public int is_broken(int win_gold )
    {
        int baseStock = m_stock_cfg.getmBaseStock();// get_base_stock();
        List<Float> vecStockStage = m_stock_cfg.getmStockStage(); //get_stock_stage();
        //破底保护
        long stock =  gameRoomDoc.getRoomStock() - win_gold;
        double stock_stage = (double)stock / baseStock;

        if (stock_stage < vecStockStage.get(0))
        {
            //SLOG_ALERT << "==================== " << m_stock_cfg->mRoomID << " is kill!";
            return  earn_type.banker_win_poker.getNumber();
        }

        return earn_type.rand_poker.getNumber();
    }

    public  int check_stock(int win_gold, int other_stock)
    {
        int baseStock = m_stock_cfg.getmBaseStock();// get_base_stock();
        List<Float> vecStockStage = m_stock_cfg.getmStockStage();// get_stock_stage();
        List<Integer> vecStockBuff = m_stock_cfg.getmStockBuff();//  get_stock_buff();

        //破底保护
        long stock = gameRoomDoc.getRoomStock() - win_gold;
        if (other_stock != 0)
            stock = other_stock - win_gold;

        double stock_stage = (double)stock / baseStock;

        if (stock_stage < vecStockStage.get(0))
        {
            return earn_type.banker_win_poker.getNumber();
        }

        //当前库存,概率杀分
        stock = gameRoomDoc.getRoomStock();// RoomStock->get_value();
        if (other_stock != 0)
            stock = other_stock;

        stock_stage = (double)stock / baseStock;
        for (int i = 1; i < vecStockStage.size(); i++)
        {
            if (stock_stage < vecStockStage.get(i))
            {
                int min_rate = vecStockBuff.get(i - 1);
                int max_rate = vecStockBuff.get(i);

                if (min_rate == 0 && max_rate == 0)
                {
                    return earn_type.rand_poker.getNumber();
                }

                if (max_rate <= 0)
                {
                    double rate = (stock_stage - vecStockStage.get(i-1)) / (vecStockStage.get(i) - vecStockStage.get(i-1));
                    double rand_value = -(min_rate + (max_rate - min_rate) * rate) / 100.0;
                    if (RandomHandler.Instance.getRandomValue(0,1) <= rand_value)
                    {
                        return earn_type.banker_win_poker.getNumber();
                    }
					else
                    {
                        return earn_type.rand_poker.getNumber();
                    }
                }
                break;
            }
        }

        //20200422 无天包要求，库存阶段 > 配置的库存阶段时，杀分概率按最后一个配置走
        if (!vecStockStage.isEmpty() && stock_stage >= vecStockStage.get(vecStockStage.size() - 1))
        {
            double rand_value = vecStockBuff.get( vecStockStage.size() - 1 ) / 100;
            rand_value = Math.abs(rand_value);
            if (RandomHandler.Instance.getRandomValue(0,1) <= rand_value)
            {
                return earn_type.banker_win_poker.getNumber();
            }
			else
            {
                return earn_type.rand_poker.getNumber();
            }
        }

        return earn_type.rand_poker.getNumber();
    }

    public boolean get_control_game()
    {
        if (TimeHelper.Instance.get_cur_time() > m_control_end)
        return false;

        return control_game;
    }

    public void add_control_times() { control_times++; }
    public int  get_control_param() { return control_param; }
    public void set_control_param()
    {
        if (control_min_param <= control_max_param)
        {
            control_param = RandomHandler.Instance.getRandomValue(control_min_param, control_max_param);
        }
        else
        {
            control_param = RandomHandler.Instance.getRandomValue(control_max_param, control_min_param);
        }
    }

    public int random_control_param()
    {
        int r = 0;
        if (control_min_param <= control_max_param)
        {
            r = RandomHandler.Instance.getRandomValue(control_min_param, control_max_param);
        }
        else
        {
            r = RandomHandler.Instance.getRandomValue(control_max_param, control_min_param);
        }

        return r;
    }

    public int get_earn_type(int winGold)
    {
        return 0;
    }

    public class WeightItem
    {
        public boolean key;
        public int weight;

        public WeightItem(boolean key, int weight) {
            this.key = key;
            this.weight = weight;
        }
    }

    public class EarnType
    {
        public  int earnType;
        public int stageIndex;
    }

    public enum  earn_type
    {
        banker_win_poker(-2),		//大地狱
        banker_power_poker(-1),	//小地狱
        rand_poker(0);			//正常

        private  int type;
        earn_type( int num ) {
            type = num;
        }

        public int getNumber()
        {
            return type;
        }
    }
}
