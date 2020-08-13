package com.wisp.game.bet.games.baccarat.logic;

import com.wisp.game.bet.GameConfig.BaccaratConfig.BaccaratBaseConfig;
import com.wisp.game.core.random.RandomHandler;
import game_baccarat_protocols.GameBaccaratDef;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogicCore {

    public static int SEND_MAX_COUNT = 3;
    public static int MAX_BET_COUNT = 5;

    private List<CardType> m_cards;
    private List<CardType> m_banker_card;
    private List<CardType> m_player_card;
    private int m_player_point = 0;
    private int m_banker_point = 0;

    private List<Boolean> m_result_list;
    private boolean is_player_third = false;
    private boolean is_banker_third = false;

    public LogicCore() {
        m_banker_card = new ArrayList<>(SEND_MAX_COUNT);
        m_player_card = new ArrayList<>(SEND_MAX_COUNT);
        m_result_list = new ArrayList<>(SEND_MAX_COUNT);
    }

    List<CardType> get_banker_card(){return m_banker_card;}
    List<CardType> get_player_card(){return m_player_card;}

    List<Boolean> get_result_list(){return m_result_list;}
    int get_remain_card_count(){return m_cards.size();}

    public void init_card()
    {
        int CARD_MAX_COUNT = BaccaratBaseConfig.GetInstnace().GetData("CardMaxCount").getmValue();
        m_cards.clear();

        int index = 0;
        int temp = 0;
        for(int i = 0; i < CARD_MAX_COUNT;i++)
        {
            index ++;
            if( index > 13 )
            {
                index = 1;
                temp ++;
            }

            CardType cardType = new CardType();
            cardType.point = index;
            int tmpVal = temp % 4;
            if( tmpVal == 0 )
            {
                cardType.flower = GameBaccaratDef.e_card_flower.e_flower_diamond;
            }
            else  if( tmpVal == 1 )
            {
                cardType.flower = GameBaccaratDef.e_card_flower.e_flower_club;
            }
            else  if( tmpVal == 2 )
            {
                cardType.flower = GameBaccaratDef.e_card_flower.e_flower_spade;
            }
            else  if( tmpVal == 3 )
            {
                cardType.flower = GameBaccaratDef.e_card_flower.e_flower_heart;
            }
            m_cards.add(cardType);
        }

        //将牌打乱
        for(int i = 0; i < CARD_MAX_COUNT;i++ )
        {
            int randIdx = Double.valueOf( Math.random() * CARD_MAX_COUNT).intValue() ;

            CardType randValue = m_cards.remove(randIdx);
            CardType curValue = m_cards.get(i);
            m_cards.set(randIdx,curValue);
            m_cards.set(i,randValue);
        }
    }

    public void send_card(int win_index)
    {
        m_banker_card.clear();;
        m_player_card.clear();
        m_result_list.clear();


        m_player_point = 0;
        m_banker_point = 0;
        for (int i = 0;i<MAX_BET_COUNT;i++)
            m_result_list.add(false) ;
        is_player_third = false;
//--------------------------
        random_result(true,0);
        random_result(false,0);
        random_result(true,1);
        random_result(false,1);

        m_player_point = (to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point))%10;	//闲家点数
        m_banker_point = (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point))%10;	//庄家点数

        if (m_player_point < 8 && m_banker_point < 8)	//非天牌
        {
            is_player_third = playerIsSendThird();
            if (is_player_third)		//闲家补牌
            {
                random_result(true,2);
                m_player_point = (to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point) + to_point(m_player_card.get(2).point))%10;	//闲家点数
            }

            if (bankerIsSendThird())	//庄家补牌
            {
                random_result(false,2);
                m_banker_point = (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point) + to_point(m_banker_card.get(2).point))%10;	//庄家点数
            }
        }
//--------------------------------结果   win_index  2  闲赢   4 庄赢
        if (m_player_point > m_banker_point)		//闲胜
        {
            if (win_index != 5)
            {
                m_result_list.set(1,true);
            }

            else
            {
                exchange_card();
                m_result_list.set(4,true);
            }
        }
        else if (m_banker_point > m_player_point)	//庄胜
        {
            if (win_index != 2)
                m_result_list.set(4,true);
            else
            {
                exchange_card();
                m_result_list.set(1,true);
            }
        }
        else if (m_banker_point == m_player_point)
            m_result_list.set(0,true);				//和

        if (m_player_card.get(0).point == m_player_card.get(1).point)	//闲对
            m_result_list.set(2,true);

        if (m_banker_card.get(0).point == m_banker_card.get(1).point)	//庄对
            m_result_list.set(3,true);
    }

    public void random_result(boolean is_player,int index)
    {
        int SHUFFLE_LIMIT = BaccaratBaseConfig.GetInstnace().GetData("ShuffleLimit").getmValue();
        if (m_cards.size() < SHUFFLE_LIMIT)	//小于4副牌就洗牌
            init_card();
        while(m_cards.get(m_cards.size()-1).point > 13 || m_cards.get(m_cards.size()-1).point <= 0
                || m_cards.get(m_cards.size()-1).flower.getNumber() >3 || m_cards.get(m_cards.size()-1).flower.getNumber() <0)
        {
            m_cards.remove(m_cards.size() - 1);
        }

        if (is_player)
        {
            m_player_card.set(index,m_cards.get(m_cards.size()-1)) ;
        }
        else
        {
            m_banker_card.set(index,m_cards.get(m_cards.size()-1));
        }

        m_cards.remove(m_cards.size() - 1);
    }

    public boolean playerIsSendThird()
    {
        int point = (to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point))%10;
        if (point <= 5)
            return true;
        return false;
    }

    private boolean bankerIsSendThird()
    {
        boolean is_send = false;
        int point = (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point))%10;
        switch (point)
        {
            case 0:
            case 1:
            case 2:
            {
                is_send = true;
            }
            break;
            case 3://如果闲家补得第三张牌（非三张牌点数相加，下同）是8点，不须补牌，其他则需补牌
            {
                if (is_player_third && to_point(m_player_card.get(2).point) == 8)
                    is_send = false;
                else
                    is_send = true;
            }
            break;
            case 4://如果闲家补得第三张牌是0,1,8,9点，不须补牌，其他则需补牌
            {
                if (is_player_third && (to_point(m_player_card.get(2).point) == 0 || to_point(m_player_card.get(2).point) == 1 || to_point(m_player_card.get(2).point) == 8 || to_point(m_player_card.get(2).point) == 9))
                    is_send = false;
                else
                    is_send = true;
            }
            break;
            case 5://如果闲家补得第三张牌是0,1,2,3,8,9点，不须补牌，其他则需补牌
            {
                if (is_player_third && (to_point(m_player_card.get(2).point) <= 3 || to_point(m_player_card.get(2).point) == 8 || to_point(m_player_card.get(2).point) == 9))
                    is_send = false;
                else
                    is_send = true;
            }
            break;
            case 6://如果闲家需补牌（即前提是闲家为1至5点）而补得第三张牌是6或7点，补一张牌，其他则不需补牌
            {
                if (is_player_third && (to_point(m_player_card.get(2).point) == 6 || to_point(m_player_card.get(2).point) == 7))
                    is_send = true;
                else
                    is_send = false;
            }
            break;
            case 7:
            case 8:
            case 9:
            {
                is_send = false;
            }
            break;
        }
        return is_send;
    }

    public int to_point(int count)
    {
        int point = 0;
        if (count < 10)
            point = count;
        else
            point = 0;
        return point;
    }

    private void exchange_card()
    {
        List<CardType> temp_card = m_banker_card;
        int temp_point = m_banker_point;

        m_banker_card = m_player_card;
        m_player_card = temp_card;
        m_banker_point = m_player_point;
        m_player_point = temp_point;
    }

    public int get_send_card_count()
    {
        int temp_count = 0;
        for (int i = 0; i < 3 ; i++)
        {
            if (m_banker_card.get(i).point > 0)
                temp_count ++ ;
            if (m_player_card.get(i).point > 0)
                temp_count ++ ;
        }
        return temp_count;
    }

    private int get_win_point()
    {
        if (m_player_point >= m_banker_point)
            return m_player_point;

        return m_banker_point;
    }

    public void send_gm_card(int which_win,int leftCount,int rightCount)//1和 2闲 3闲对 4庄对 5庄
    {
        int SHUFFLE_LIMIT = BaccaratBaseConfig.GetInstnace().GetData("ShuffleLimit").getmValue();
        if (m_cards.size() < SHUFFLE_LIMIT)	//小于4副牌就洗牌
            init_card();

        if (which_win < 1 || which_win > 5)
        {
            send_card(which_win);
            return;
        }

        m_result_list.clear();

        m_player_point = 0;
        m_banker_point = 0;
        for (int i = 0;i<MAX_BET_COUNT;i++)
            m_result_list.add(false);
        is_player_third = false;
        //memset(&m_banker_card[0],0,sizeof(CardType)*SEND_MAX_COUNT);
        //memset(&m_player_card[0],0,sizeof(CardType)*SEND_MAX_COUNT);

        m_banker_card.clear();
        m_player_card.clear();

        if (which_win == 1)	//和  每家只有两张牌
        {
            int temp_point = RandomHandler.Instance.getRandomValue(6,10);

            while(true)
            {
                m_player_card.set(0,point_to_card(-1,temp_point));
                m_player_card.set(1,point_to_card(m_player_card.get(0).point,temp_point));

                m_banker_card.set(0,point_to_card(-1,temp_point));
                m_banker_card.set(1,point_to_card(m_banker_card.get(0).point,temp_point));
                if(m_player_card.get(0).point != m_player_card.get(1).point && m_banker_card.get(0).point != m_banker_card.get(1).point)
                    break;
            }
            //m_player_card[0] = point_to_card(-1,temp_point);
            //m_player_card[1] = point_to_card(m_player_card[0].point,temp_point);
            //m_banker_card[0] = point_to_card(-1,temp_point);
            //m_banker_card[1] = point_to_card(m_banker_card[0].point,temp_point);
        }
        else if (which_win == 2) //闲赢 每家只有两张牌   庄闲不许为对子
        {
            int temp_point1 = 0;
            int temp_point2 = 0;

            while(true)
            {
                temp_point1 = RandomHandler.Instance.getRandomValue(7,10);					//闲家点数
                temp_point2 = RandomHandler.Instance.getRandomValue(6,temp_point1);	//庄家点数
                m_player_card.set(0,point_to_card(-1,temp_point1));//随机一个点数
                m_player_card.set(1,point_to_card(m_player_card.get(0).point,temp_point1));

                m_banker_card.set(0,point_to_card(-1,temp_point2));
                m_banker_card.set(1,point_to_card(m_banker_card.get(0).point,temp_point2));
                if(m_player_card.get(0).point != m_player_card.get(1).point && m_banker_card.get(0).point != m_banker_card.get(1).point)
                    break;
            }
        }
        else if (which_win == 3) //闲对
        {
            int temp_point1 = 0;		//闲家点数
            int temp_point2 = 0;	//庄家点数
            int player_point = 0;
            int banker_point = 0;

            while(true)
            {
                //memset(&m_banker_card[0], 0, sizeof(CardType) * SEND_MAX_COUNT);
                //memset(&m_player_card[0], 0, sizeof(CardType) * SEND_MAX_COUNT);

                m_banker_card.clear();
                m_player_card.clear();

                temp_point1 =  RandomHandler.Instance.getRandomValue(0,10);
                temp_point2 =  RandomHandler.Instance.getRandomValue(0,10);
                m_player_card.set(0,m_player_card.get(0));
                m_player_card.set(1,m_player_card.get(0));
                m_banker_card.set(0,point_to_card(-1,temp_point2)) ;
                m_banker_card.set(1,point_to_card(m_banker_card.get(0).point,temp_point2)) ;
                if ((to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point))%10  < 8 && (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point))%10  < 8)	//非天牌
                {
                    is_player_third = playerIsSendThird();
                    if (is_player_third)		//闲家补牌
                        random_result(true,2);
                    is_banker_third = bankerIsSendThird();
                    if (is_banker_third)	//庄家补牌
                        random_result(false,2);
                }
                player_point = (to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point) + to_point(m_player_card.get(2).point))%10;
                banker_point = (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point) + to_point(m_banker_card.get(2).point))%10;
                if(((player_point>banker_point) == (leftCount < rightCount))&&player_point != banker_point)
                    break;
            }
        }
        else if (which_win == 4) //庄对
        {
            int temp_point1 =0;				//闲家点数
            int temp_point2 = 0;	//庄家点数
            int player_point = 0;
            int banker_point = 0;
            while(true)
            {
                //memset(&m_banker_card[0], 0, sizeof(CardType) * SEND_MAX_COUNT);
                //memset(&m_player_card[0], 0, sizeof(CardType) * SEND_MAX_COUNT);

                m_banker_card.clear();;
                m_player_card.clear();

                temp_point1 =  RandomHandler.Instance.getRandomValue(0,10);					//闲家点数
                temp_point2 =  RandomHandler.Instance.getRandomValue(0,10);	//庄家点数
                m_player_card.set(0,point_to_card(-1,temp_point1));
                m_player_card.set(1,point_to_card(m_player_card.get(0).point,temp_point1));
                m_banker_card.set(0,point_to_card(-1,temp_point2));
                m_banker_card.set(1,m_banker_card.get(0)) ;
                if ((to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point))%10  < 8 && (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point))%10  < 8)	//非天牌
                {
                    is_player_third = playerIsSendThird();
                    if (is_player_third)		//闲家补牌
                        random_result(true,2);
                    is_banker_third = bankerIsSendThird();
                    if (is_banker_third)	//庄家补牌
                        random_result(false,2);
                }
                player_point = (to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point) + to_point(m_player_card.get(2).point))%10;
                banker_point = (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point) + to_point(m_banker_card.get(2).point))%10;
                if(((player_point>banker_point) == (leftCount < rightCount))&&player_point != banker_point)
                    break;
            }

        }
        else if (which_win == 5) //庄赢  庄闲不许为对子
        {
            int temp_point1 = 0;
            int temp_point2 =0;
            while(true)
            {
                temp_point1 =  RandomHandler.Instance.getRandomValue(6,9);					//闲家点数
                temp_point2 =  RandomHandler.Instance.getRandomValue((temp_point1 + 1),10);	//庄家点数
                m_player_card.set(0,point_to_card(-1,temp_point1));
                m_player_card.set(1,point_to_card(m_player_card.get(0).point,temp_point1));

                m_banker_card.set(0,point_to_card(-1,temp_point2));
                m_banker_card.set(1,point_to_card(m_banker_card.get(0).point,temp_point2));
                if(m_player_card.get(0).point != m_player_card.get(1).point && m_banker_card.get(0).point != m_banker_card.get(1).point)
                    break;
            }
        }
        //--------------------------------点数
        if (is_player_third)
            m_player_point = (to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point) + to_point(m_player_card.get(2).point))%10;	//闲家点数
        else
            m_player_point = (to_point(m_player_card.get(0).point) + to_point(m_player_card.get(1).point))%10;	//闲家点数

        if (is_banker_third)
            m_banker_point = (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point) + to_point(m_banker_card.get(2).point))%10;	//庄家点数
        else
            m_banker_point = (to_point(m_banker_card.get(0).point) + to_point(m_banker_card.get(1).point))%10;	//庄家点数

        //--------------------------------结果
        if (m_player_point > m_banker_point)		//闲胜
        {
            m_result_list.set(1,true);
        }
        else if (m_banker_point > m_player_point)	//庄胜
        {
            m_result_list.set(4,true);
        }
        else if (m_banker_point == m_player_point)
            m_result_list.set(0,true);				//和

        if (m_player_card.get(0).point == m_player_card.get(1).point)	//闲对
            m_result_list.set(2,true);

        if (m_banker_card.get(0).point == m_banker_card.get(1).point)	//庄对
            m_result_list.set(3,true);
    }

    public CardType point_to_card_()
    {
        List<Integer> range_point = new ArrayList<>();
        range_point.add(3);
        range_point.add(4);
        range_point.add(8);
        range_point.add(9);
        int tmp_point = Double.valueOf(Math.random()*4).intValue();

        CardType cardType = new CardType();
        cardType.point = range_point.get(tmp_point);
        int flower =  Double.valueOf(Math.random()*4).intValue();
        cardType.flower = GameBaccaratDef.e_card_flower.valueOf(flower);

        return cardType;
    }

    public CardType point_to_card(int point,int point_all)
    {
        if( point <= -1 )
        {
            CardType cardType = new CardType();
            cardType.point = 1 + Double.valueOf(Math.random() * 13).intValue();
            int flowerValue = Double.valueOf(Math.random() * 4).intValue();
            cardType.flower = GameBaccaratDef.e_card_flower.valueOf(flowerValue);
            return cardType;
        }

        CardType temp2 = new CardType();
        int flowerValue = Double.valueOf(Math.random() * 4).intValue();
        temp2.flower = GameBaccaratDef.e_card_flower.valueOf(flowerValue);

        int gap = point_all - to_point(point);
        if(  gap > 0 )
        {
            temp2.point = gap;
        }
        else if( gap < 0 )
        {
            temp2.point = point_all + 10 - to_point(point);
        }
        else
        {
            temp2.point = 10 + ( Double.valueOf(Math.random()*4).intValue() );
        }
        return  temp2;
    }

    public class CardType
    {
        int point;
        GameBaccaratDef.e_card_flower flower;
    }
}
