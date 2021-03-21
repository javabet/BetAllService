package com.wisp.game.bet.games.guanyuan.logic;

import com.wisp.game.bet.games.guanyuan.logic.info.HuCircleInfo;
import com.wisp.game.bet.games.guanyuan.logic.info.HuCircleItemInfo;
import com.wisp.game.bet.games.guanyuan.logic.info.HuDetailInfo;
import com.wisp.game.bet.games.share.HuStrategy.HistoryActionInfo;
import com.wisp.game.bet.games.share.HuStrategy.HuTypeUtils;
import com.wisp.game.bet.games.share.common.MaJiangPlayerData;
import com.wisp.game.bet.games.share.enums.HuTypeEnum;
import msg_type_def.MsgTypeDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalcGameScroe {


    public static List<HuCircleItemInfo> calcScores(LogicCore logicCore)
    {
        List<HuCircleItemInfo> list = new ArrayList<>();
        HistoryActionInfo lastHuHistoryActionInfo = logicCore.getHistoryActionInfos().get( logicCore.getHistoryActionInfos().size() - 1 );

        MaJiangPlayerData huPlayer = logicCore.getGameSeats().get(lastHuHistoryActionInfo.getSeatPos());
        MaJiangPlayerData huedPlayer = logicCore.getGameSeats().get(lastHuHistoryActionInfo.getLinkedSeatPos());

        // 1:谁点炮,谁给钱,也就是1对1
        // 2:点炮的人付三家钱,
        // 3:每家给胡的付相同的钱
        int give_money_type = 1;
        if( lastHuHistoryActionInfo.getSeatPos() == lastHuHistoryActionInfo.getLinkedSeatPos() )
        {
            // 是自摸
            give_money_type = 3;
        }
        else
        {
            give_money_type = 2;
        }

        boolean sevenPairHuFlag =  HuTypeUtils.getInstance().sevenPair(huPlayer);
        boolean duiduiHuFlag = HuTypeUtils.getInstance().duiduiHu(huedPlayer);
        boolean qingYiSeHuFlag = HuTypeUtils.getInstance().qingYiSeDuiDuiHu(huPlayer);
        boolean jieZhangHuFlag = HuTypeUtils.getInstance().jieZhang(huedPlayer,logicCore.getHistoryActionInfos());

        boolean normalHuFlag = true;
        if( sevenPairHuFlag || duiduiHuFlag || qingYiSeHuFlag || jieZhangHuFlag )
        {
            normalHuFlag = true;
        }

        HuCircleItemInfo winCircleInfo = new HuCircleItemInfo();
        winCircleInfo.setSeatPos(huPlayer.getSeatIndex());
        list.add(winCircleInfo);

        int huaNum =  huPlayer.getFlowers().size();

        if( give_money_type == 1 )
        {
            HuCircleItemInfo loseCircleInfo = new HuCircleItemInfo();
            loseCircleInfo.setSeatPos(huedPlayer.getSeatIndex());
            list.add(loseCircleInfo);
            if( normalHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_NORMAL;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }

            if( sevenPairHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_SEVEN;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }
            if( duiduiHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_DUI_DUI_HU;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }

            if( qingYiSeHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_QI_YI_SE;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }

            if( jieZhangHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_JIE_ZHANG;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }

        }
        else if( give_money_type == 2 )
        {
            HuCircleItemInfo loseCircleInfo = new HuCircleItemInfo();
            loseCircleInfo.setSeatPos(huedPlayer.getSeatIndex());
            list.add(loseCircleInfo);
            if( normalHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_NORMAL;
                int score = 5 * 3;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }

            if( sevenPairHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_SEVEN;
                int score = 5 * 3;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }
            if( duiduiHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_DUI_DUI_HU;
                int score = 5 * 3;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }

            if( qingYiSeHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_QI_YI_SE;
                int score = 5 * 3;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }

            if( jieZhangHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_JIE_ZHANG;
                int score = 5 * 3;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,huedPlayer.getSeatIndex()));
                loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
            }
        }
        else
        {
            List< HuCircleItemInfo> loseHuCircleInfos = new ArrayList<>();
            for(int i = 0; i < 4;i ++)
            {
                if (i == huedPlayer.getSeatIndex())
                {
                    continue;
                }
                else
                {
                    HuCircleItemInfo loseCircleInfo = new HuCircleItemInfo();
                    loseCircleInfo.setSeatPos(huedPlayer.getSeatIndex());
                    loseHuCircleInfos.add(loseCircleInfo);
                }
            }

            if( normalHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_NORMAL;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,-1));    //-1:表示多个
                for(int i = 0;i  < loseHuCircleInfos.size(); i ++)
                {
                    HuCircleItemInfo loseCircleInfo = loseHuCircleInfos.get(i);
                    loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
                }
            }

            if( sevenPairHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_SEVEN;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,-1));
                for(int i = 0;i  < loseHuCircleInfos.size(); i ++)
                {
                    HuCircleItemInfo loseCircleInfo = loseHuCircleInfos.get(i);
                    loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
                }
            }
            if( duiduiHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_DUI_DUI_HU;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,-1));
                for(int i = 0;i  < loseHuCircleInfos.size(); i ++)
                {
                    HuCircleItemInfo loseCircleInfo = loseHuCircleInfos.get(i);
                    loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
                }
            }

            if( qingYiSeHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_QI_YI_SE;
                int score = 5;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,-1));
                for(int i = 0;i  < loseHuCircleInfos.size(); i ++)
                {
                    HuCircleItemInfo loseCircleInfo = loseHuCircleInfos.get(i);
                    loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
                }
            }

            if( jieZhangHuFlag )
            {
                HuTypeEnum type = HuTypeEnum.TYPE_JIE_ZHANG;
                int score = 5 * 3;
                winCircleInfo.addHuDetailInfo( new HuDetailInfo(type,score,-1));
                for(int i = 0;i  < loseHuCircleInfos.size(); i ++)
                {
                    HuCircleItemInfo loseCircleInfo = loseHuCircleInfos.get(i);
                    loseCircleInfo.addHuDetailInfo( new HuDetailInfo(type,-score,huPlayer.getSeatIndex()));
                }
            }
        }

        //收集当前各个情况的
        for(int i = 0; i < list.size();i++)
        {
            HuCircleItemInfo huCircleItemInfo = list.get(i);
            int score = 0;
            for(int j = 0; j < huCircleItemInfo.getHuDetailInfos().size();j++)
            {
                HuDetailInfo huDetailInfo = huCircleItemInfo.getHuDetailInfos().get(i);
                score += huDetailInfo.getScore();
            }
            huCircleItemInfo.setScore(score);
        }

        return list;
    }

    private void addLoseHuDetail(  HuTypeEnum type,int score,int hu_seat_pos )
    {
        HuDetailInfo huDetailInfo =  new HuDetailInfo(type,-score,hu_seat_pos);


    }
}
