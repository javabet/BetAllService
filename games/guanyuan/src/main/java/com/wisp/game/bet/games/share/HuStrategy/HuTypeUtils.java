package com.wisp.game.bet.games.share.HuStrategy;

import com.wisp.game.bet.games.share.enums.ActionTypeEnum;
import com.wisp.game.bet.games.share.enums.HistoryActionEnum;
import com.wisp.game.bet.games.share.common.MaJiangPlayerData;
import com.wisp.game.bet.games.share.utils.IMaJiangPlayerData;
import com.wisp.game.bet.games.share.utils.MaJianUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuTypeUtils
{
    private static HuTypeUtils instance;
    public static HuTypeUtils getInstance()
    {
        if( instance == null )
        {
            instance = new HuTypeUtils();
        }

        return instance;
    }

    public boolean yiTiaoLong(IMaJiangPlayerData seatData)
    {
        boolean isHuaXiaoLong = isYiTiaoLong(seatData,3);

        if(!isHuaXiaoLong)
        {
            return false;
        }
        return true;
    }

    // 混一色
    public boolean hunYiSe(IMaJiangPlayerData seatData)
    {
        int findFistType = -1;
        for (int i = 0; i < seatData.getHolds().size(); i++)
        {
            int cur_type = (seatData.getHolds().get(i) >> 4) & 0xF;

            if (cur_type >= 3)
            {
                continue;
            }

            if (findFistType == -1)
            {
                findFistType = cur_type;
            }
            else
            {
                if (cur_type != findFistType) { return false; }
            }
        }

        for (int i = 0; i < seatData.getOutHistoryList().size(); i++)
        {
            HistoryActionInfo outCardAction = seatData.getOutHistoryList().get(i);
            int card = outCardAction.getCard();
            int cur_type = (card >> 4) & 0xf;

            if (cur_type >= 3)
            {
                continue;
            }

            if (findFistType == -1)
            {
                findFistType = cur_type;
            }
            else
            {
                if (cur_type != findFistType) { return false; }
            }
        }

        //要有风牌
        boolean hasFeng = false;
        for (int i = 0; i < seatData.getHolds().size(); i++)
        {
            int cur_type = (seatData.getHolds().get(i) >> 4) & 0xF;
            if(cur_type == 3) {
                hasFeng = true;
                break;
            }
        }
        for (int i = 0; i < seatData.getOutHistoryList().size(); i++)
        {
            HistoryActionInfo outCardAction = seatData.getOutHistoryList().get(i);
            int card = outCardAction.getCard();
            int cur_type = (card >> 4) & 0xf;
            if(cur_type == 3) {
                hasFeng = true;
                break;
            }
        }

        if(hasFeng) {
            return true;
        } else {
            return false;
        }
    }


    public boolean hunYiSeHuaXiaoLong(IMaJiangPlayerData seatData)
    {
        boolean isHuaXiaoLong = isYiTiaoLong(seatData,2);
        return isHuaXiaoLong;
    }

    // 混一色，是只有一个色，其他是风牌
    public boolean hunYiSeDuiDuiHu(IMaJiangPlayerData seatData)
    {
        boolean duiduihuFlag = duiduiHu(seatData);
        if (!duiduihuFlag) { return false; }

        return isHunCards(seatData);
    }

    public boolean hunYiSeSevenPair(IMaJiangPlayerData seatData)
    {
        boolean isSevenPair = sevenPair(seatData);

        if (!isSevenPair) { return false; }

        boolean isHun =  isHunCards(seatData);

        if(!isHun)
        {
            return false;
        }

        return true;
    }

    public boolean qingYiSeDuiDuiHu(IMaJiangPlayerData seatData)
    {
        boolean isQingYiSe = isQingCards(seatData);
        if(!isQingYiSe)
        {
            return false;
        }


        boolean isDuiDuiHua = duiduiHu(seatData);
        if(!isDuiDuiHua)
        {
            return false;
        }

        return true;
    }

    public boolean qingYiSeSevenPair(IMaJiangPlayerData seatData)
    {
        boolean isQingYiSe = isQingCards(seatData);
        if(!isQingYiSe)
        {
            return false;
        }

        boolean isSevenPair = sevenPair(seatData);

        if(!isSevenPair)
        {
            return false;
        }

        return true;
    }

    public boolean qingYiSeHuaXiaoLong(IMaJiangPlayerData seatData)
    {
        boolean isQingYiSe = isQingCards(seatData);
        if(!isQingYiSe)
        {
            return false;
        }

        boolean isHuaXiaoLong = isYiTiaoLong(seatData,1);

        if(!isHuaXiaoLong)
        {
            return false;
        }

        return true;
    }

    protected int getMjType(Integer pai)
    {
        return (int) Math.floor(pai / 16);
    }

    protected boolean isHunCards(IMaJiangPlayerData seatData)
    {
        boolean hasFen = false;
        int firstType = -1;
        for (int i = 0; i < seatData.getHolds().size(); i++)
        {
            Integer card = seatData.getHolds().get(i);

            int cur_type = (card >> 4) & 0xf;

            if (cur_type >= 3)
            {
                hasFen = true;
            }
            else
            {
                if (firstType == -1)
                {
                    firstType = cur_type;
                }
                else
                {
                    if (firstType != cur_type) { return false; }
                }
            }
        }

        // 格式为前6位为标识位，中2位为被操作的玩家索引，后8位为牌位，总计16位，0x1:为碰 0x
        for (int i = 0; i < seatData.getOutHistoryList().size(); i++)
        {
            HistoryActionInfo outCardAction = seatData.getOutHistoryList().get(i);
            int card = outCardAction.getCard();
            int cur_type = (card >> 4) & 0xf;
            if (cur_type >= 3)
            {
                hasFen = true;
            }
            else
            {
                if (firstType == -1)
                {
                    firstType = cur_type;
                }
                else
                {
                    if (firstType != cur_type) { return false; }
                }
            }
        }

        if (!hasFen) { return false; }

        return true;
    }

    protected boolean isQingCards(IMaJiangPlayerData seatData)
    {
        int firstType = -1;
        for (int i = 0; i < seatData.getHolds().size(); i++)
        {
            Integer card = seatData.getHolds().get(i);

            int cur_type = (card >> 4) & 0xf;

            if(cur_type >= 3)
            {
                return false;
            }

            if (firstType == -1)
            {
                firstType = cur_type;
            }
            else
            {
                if (firstType != cur_type) { return false; }
            }
        }

        // 格式为前6位为标识位，中2位为被操作的玩家索引，后8位为牌位，总计16位，0x1:为碰 0x
        for (int i = 0; i < seatData.getOutHistoryList().size(); i++)
        {
            HistoryActionInfo outCardAction = seatData.getOutHistoryList().get(i);
            int card = outCardAction.getCard();
            int cur_type = (card >> 4) & 0xf;

            if(cur_type >= 3)
            {
                return false;
            }

            if (firstType == -1)
            {
                firstType = cur_type;
            }
            else
            {
                if (firstType != cur_type) { return false; }
            }
        }

        return true;
    }


    /**
     *
     * @param seatData
     * @param type 1:清一色  2：花小龙  3：一条龙
     * @return
     */
    public boolean isYiTiaoLong(IMaJiangPlayerData seatData, int type)
    {
        if(seatData.getHolds().size() < 9)
        {
            return false;
        }


        if(type == 1)
        {//清一色花小龙
            boolean isQing = isQingCards(seatData);
            if(!isQing)
            {
                return false;
            }

            for(int i = 1;i <= 9;i ++)
            {
                Integer card = (Integer) i;
                if(!seatData.getCountMap().containsKey(card))
                {
                    return false;
                }
                int cur_num = seatData.getCountMap().get(card);
                if(cur_num == 0)
                {
                    return false;
                }
            }
        }
        else if(type == 2)
        {//混一色花小龙
            boolean isHun = isHunCards(seatData);
            if(!isHun)
            {
                return false;
            }

            Map<Integer,Boolean> map = new HashMap<>();
            for(int i = 0;i < seatData.getHolds().size();i++)
            {
                Integer card = seatData.getHolds().get(i);
                int card_type = ( card >> 4 ) & 0xF;
                if(card_type >= 3)
                {
                    continue;
                }
                int card_val = card & 0xF;
                if(!map.containsKey(card_val))
                {
                    map.put(card_val, true);
                }
            }
            if(map.size() != 9)
            {
                return false;
            }
        }
        else
        {//一条龙
            //手牌同一门色中含有1到9
            Map<Integer,Integer> countSeMap = new HashMap<>();
            for(int i = 0;i < seatData.getHolds().size();i++)
            {
                int card = seatData.getHolds().get(i);
                int card_type = ( card >> 4 ) & 0xF;

                if(card_type >= 3)
                {
                    continue;
                }

                if(countSeMap.containsKey(card_type))
                {
                    countSeMap.put(card_type,countSeMap.get(card_type) + 1);
                }
                else
                {
                    countSeMap.put(card_type, 1);
                }
            }

            int findSeType = -1;
            for(Map.Entry<Integer, Integer> entity : countSeMap.entrySet())
            {
                if(entity.getValue() >= 9)
                {
                    findSeType = entity.getKey();
                }
            }

            if(findSeType == -1)
            {
                return false;
            }

            Map<Integer,Boolean> map = new HashMap<>();
            for(int i = 0;i < seatData.getHolds().size();i++)
            {
                int card = seatData.getHolds().get(i);
                int card_type = ( card >> 4 ) & 0xF;
                if(card_type != findSeType)
                {
                    continue;
                }

                int card_val = card & 0xF;
                if(!map.containsKey(card_val))
                {
                    map.put(card_val, true);
                }
            }

            return map.size() == 9;
        }

        return true;
    }

    /**
     * 全球对下来，或是全部杠下来，手上只余下一张牌
     * @param seatData
     * @return
     */
    public boolean quanQiuDuDiao(IMaJiangPlayerData seatData)
    {
        if(seatData.getHolds().size() > 2)
        {
            return false;
        }

        /**
        int actionFlag = 0;
        for( HistoryActionInfo outCardAction : seatData.getOutHistoryList() )
        {
            if(actionFlag == 0)
            {
                outCardAction = outCardAction.getAction();
            }
            else
            {

                if(outCardAction != (outCardAction & 0x3F))
                {
                    return false;
                }
            }
            if(outCardAction.getAction() == HistoryActionEnum.HISTORY_ACTION_CHUPAI)
            {
                return false;
            }
        }
        **/

        return true;
    }


    /**
     * 双七对 ,自己有三张同样的牌，听七对时，单听绝张（注：不计独吊），暗杠不算双七对
     * @param seatData
     * @return
     */
    public boolean doubleSevenPair(IMaJiangPlayerData seatData)
    {
        boolean isSevenPair = sevenPair(seatData);

        if(!isSevenPair)
        {
            return false;
        }

        int lastHuCard = seatData.getHolds().get(seatData.getHolds().size() - 1);

        int c_num = seatData.getCountMap().get(lastHuCard);

        return c_num == 4;
    }

    /**
     * 杠牌后胡牌
     * @param seatData
     * @return
     */
    public boolean gangBeforeHu(IMaJiangPlayerData seatData,List<HistoryActionInfo> historyList)
    {
        if(historyList.size() < 3)
        {
            return false;
        }

        //必须是摸牌
        HistoryActionInfo lastSecondAction = historyList.get(historyList.size() - 2);

        //必须是杠牌（弯杠，明杠，但不能是暗杠）
        HistoryActionInfo lastThreeAction = historyList.get(historyList.size() - 3);

        if( lastSecondAction.getAction() != HistoryActionEnum.HISTORY_ACTION_MOPAI)
        {
            return false;
        }

        HistoryActionEnum lastThreeActionFlag = lastThreeAction.getAction();
        if(lastThreeActionFlag != HistoryActionEnum.HISTORY_ACTION_DIAN_GANG && lastThreeActionFlag != HistoryActionEnum.HISTORY_ACTION_WANG_GANG)
        {
            return false;
        }

        return true;
    }


    /**
     * 暗杠后胡牌
     * @param seatData
     * @return
     */
    public boolean anGangBeforeHu(IMaJiangPlayerData seatData,List<Integer> mjActionList)
    {
        if(mjActionList.size() < 3)
        {
            return false;
        }

        //必须是摸牌
        int lastSecondAction = mjActionList.get(mjActionList.size() - 2);

        //必须是杠牌（弯杠，明杠，但不能是暗杠）
        int lastThreeAction = mjActionList.get(mjActionList.size() - 3);

        if( (lastSecondAction & 0x3F) !=  HistoryActionEnum.HISTORY_ACTION_MOPAI.getValue())
        {
            return false;
        }

        int lastThreeActionFlag = lastThreeAction & 0x3F;
        if(lastThreeActionFlag !=  HistoryActionEnum.HISTORY_ACTION_AN_GANG.getValue())
        {
            return false;
        }

        return true;
    }

    /**
     * 补花后胡牌
     * @param seatData
     * @return
     */
    public boolean huaBeforeHu(IMaJiangPlayerData seatData, List<HistoryActionInfo> historyList)
    {
        if(historyList.size() < 2)
        {
            return false;
        }

        //必须是摸牌,而且还必须是花牌
        HistoryActionInfo lastSecondAction = historyList.get(historyList.size() - 2);

        if( lastSecondAction.getAction() != HistoryActionEnum.HISTORY_ACTION_MOPAI )
        {
            return false;
        }

        boolean isHuaFlag = false; //(lastSecondAction >> 16 & 0x01) == 0x01;

        return isHuaFlag;
    }

    /**
     * 压绝 在某张牌（比如三万）已经被自己碰（别人碰或者打出三张的情况不算）的情况下，成压档（必须是压三万或是边三万）算做压绝，成牌后，另加40个花；（压绝成牌后压档的软花不计）
     无花果：在成牌时，如果没有一个硬花，则称为无花果，成牌后，另加40个花；（无花果小牌必须是门清）；
     * @param seatData
     * @return
     */
    public boolean yaJie(IMaJiangPlayerData seatData,  int mjType, int hunMj)
    {
        if(seatData.getPlayerHistoryList().size() < 0)
        {
            return false;
        }

        int lastHuCard = seatData.getHolds().get(seatData.getHolds().size() - 1);

        boolean findCardFlag = false;
        for(HistoryActionInfo cardAction : seatData.getPlayerHistoryList())
        {
            if(cardAction.getAction() != HistoryActionEnum.HISTORY_ACTION_PENG )
            {
                continue;
            }

            int card = cardAction.getCard();
            if(card != lastHuCard)
            {
                continue;
            }

            findCardFlag = true;
        }

        if(!findCardFlag)
        {
            return false;
        }

        //计算玩家是不是只能胡这一张牌的
        //去掉最后一张牌，看下是不是可以胡的
        seatData.getHolds().remove(seatData.getHolds().size() - 1);
        seatData.getCountMap().put(lastHuCard, 0); //因为已经碰下来了，所以手里有且只有一张

        MaJianUtils.checkAllTingPai(seatData, mjType, hunMj);

        int tingSize = seatData.getTingMap().size();

        seatData.getCountMap().put(lastHuCard, 1);
        seatData.getHolds().add(lastHuCard);


        return tingSize == 1;
    }

    /**
     * 无花果：在成牌时，如果没有一个硬花，则称为无花果，成牌后，另加40个花；（无花果小牌必须是门清）；
     * @param seatData
     * @return
     */
    public boolean wuhuaGu(IMaJiangPlayerData seatData)
    {
        if(seatData.getInitFlowerCards().size() > 0)
        {
            return false;
        }

        if(seatData.getFlowers().size() > 0)
        {
            return false;
        }

        return true;
    }

    public boolean duiduiHu(IMaJiangPlayerData seatData)
    {
        int singleCount = 0;
        int colCount = 0;
        int pairCount = 0;
        List<Integer> arr = new ArrayList<>();

        for(int k : seatData.getCountMap().keySet())
        {
            int c = seatData.getCountMap().get(k);
            if(c == 1)
            {
                singleCount ++;
                arr.add(k);
            }
            else if(c == 2)
            {
                pairCount ++;
                arr.add(k);
            }
            else if( c == 3)
            {
                colCount ++;
            }
            else if(c == 4)
            {
                singleCount ++;
                pairCount += 2;
            }

            if(singleCount > 1)
            {
                break;
            }
            if(pairCount > 2)
            {
                break;
            }
        }

        if(pairCount >= 2)
        {
            return false;
        }

        if(singleCount > 1)
        {
            return false;
        }

        return true;
    }

    //一组牌是否是一个类型
    protected boolean isSameType(int type, List<Integer> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            int t = getMjType(list.get(i));
            if (type != -1 && type != t) { return false; }
            type = t;
        }

        return true;
    }


    public boolean sevenPair(IMaJiangPlayerData playerData )
    {
        if( playerData.getHolds().size() != 14 )
        {
            return false;
        }

        for( Map.Entry<Integer,Integer>entry:  playerData.getCountMap().entrySet() )
        {
            if(entry.getValue() == 0)
            {
                continue;
            }
            if( entry.getValue() % 2 != 0 )
            {
                return false;
            }
        }


        return true;
    }


    public Object qingYiSe(MaJiangPlayerData playerData )
    {
        int type = getMjType(playerData.getHolds().get(0));

        //检查手上的牌
        if( isSameType(type, playerData.getHolds()) == false)
        {
            return false;
        }

        for(HistoryActionInfo mjAction : playerData.getOutHistoryList())
        {
            HistoryActionEnum mjAction_flag = mjAction.getAction();
            int card =  mjAction.getCard();

            int mjActionType = getMjType( card  );

            if( mjAction_flag == HistoryActionEnum.HISTORY_ACTION_AN_GANG || mjAction_flag == HistoryActionEnum.HISTORY_ACTION_WANG_GANG ||
                    mjAction_flag ==  HistoryActionEnum.HISTORY_ACTION_DIAN_GANG ||  mjAction_flag == HistoryActionEnum.HISTORY_ACTION_PENG)
            {
                if( mjActionType  != type)
                {
                    return false;
                }
            }
            else if( mjAction_flag == HistoryActionEnum.HISTORY_ACTION_CHI_PAI )
            {
                int otherCard = card;
                mjActionType = getMjType( otherCard  );

                if(mjActionType != type)
                {
                    return false;
                }

            }
        }

        return true;
    }


    public boolean mengQing(MaJiangPlayerData seatData)
    {
        return seatData.getOutHistoryList().size() == 0;
    }

    /*
     * 晃听，也叫放听 玩家手牌已经成牌，但还是没有听牌，这时玩家必须打掉一张牌才能听牌（即叫听打的牌即是玩家要听的牌）
     */
    public boolean huanTing(MaJiangPlayerData seatData)
    {
        //查找叫听的那张牌是不是玩家现在要胡的那张牌
        if(!seatData.isHued())
        {
            return false;
        }

        boolean find_last_hu = false;

        if( seatData.getPlayerHistoryList().size() == 0 )
        {
            return false;
        }

        HistoryActionInfo lastMjAction = seatData.getPlayerHistoryList().get( seatData.getPlayerHistoryList().size() - 1 );

        HistoryActionEnum lastMjFlag = lastMjAction.getAction();

        boolean huFlag = isHuFlag(lastMjFlag);
        if(!huFlag)
        {
            return false;
        }

        //查找胡的那张牌是否已经被碰过了
        int huPai = lastMjAction.getCard();

        for(HistoryActionInfo mjAction : seatData.getPlayerHistoryList())
        {
            HistoryActionEnum mjAction_flag = mjAction.getAction();
            int card = mjAction.getCard();

            if( mjAction_flag != HistoryActionEnum.HISTORY_ACTION_JIAO_TING)
            {
                continue;
            }

            return card == huPai;
        }


        return false;
    }



    public boolean isZimo(MaJiangPlayerData seatData)
    {
        if(seatData.getPlayerHistoryList().size() == 0)
        {
            return false;
        }

        HistoryActionInfo mjAction = seatData.getPlayerHistoryList().get( seatData.getPlayerHistoryList().size() - 1 );
        HistoryActionEnum lastMjFlag = mjAction.getAction();
        boolean huFlag = isHuFlag(lastMjFlag);
        if(!huFlag)
        {
            return false;
        }

        return mjAction.isZiMo();
    }

    //玩家只能胡一张牌，且这张牌已被自己或是其它的玩家碰过
    public boolean jieZhang(MaJiangPlayerData seatData,List<HistoryActionInfo> tableHistoryInfos)
    {
        if(!seatData.isHued())
        {
            return false;
        }

        if( seatData.getPlayerHistoryList().size() == 0 )
        {
            return false;
        }

        if( seatData.getJiaoTingCard() == 0 )
        {
            return false;
        }

        HistoryActionInfo lastMjAction = seatData.getPlayerHistoryList().get( seatData.getPlayerHistoryList().size() - 1 );
        if(!isHuFlag(lastMjAction.getAction()))
        {
            return false;
        }
        //查找胡的那张牌是否已经被碰过了
        int huPai = lastMjAction.getCard();
        boolean has_flag = false;
        for(HistoryActionInfo mjAction : tableHistoryInfos)
        {
            if( mjAction.getAction() != HistoryActionEnum.HISTORY_ACTION_PENG)
            {
                continue;
            }

            if( lastMjAction.getCard() != huPai)
            {
                continue;
            }
            has_flag = true;
            break;
        }

        if(!has_flag)
        {
            return false;
        }

        return true;
    }

    public static boolean isHuFlag(HistoryActionEnum mjAction)
    {
       boolean flag = mjAction == HistoryActionEnum.HISTORY_ACTION_HU ||
               mjAction == HistoryActionEnum.HISTORY_ACTION_HU_DIAN_GANG ||
               mjAction == HistoryActionEnum.HISTORY_ACTION_HU_FANG_PAO ||
               mjAction == HistoryActionEnum.HISTORY_ACTION_HU_GABNG_PAO ||
               mjAction == HistoryActionEnum.HISTORY_ACTION_HU_QIANG_GANG ||
               mjAction == HistoryActionEnum.HISTORY_ACTION_HU_ZIMO;

        return flag;
    }

}
