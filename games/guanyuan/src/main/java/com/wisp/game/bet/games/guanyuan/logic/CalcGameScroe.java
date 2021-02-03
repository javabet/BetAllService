package com.wisp.game.bet.games.guanyuan.logic;

import com.wisp.game.bet.games.share.HuStrategy.HistoryActionInfo;
import com.wisp.game.bet.games.share.HuStrategy.HuTypeUtils;
import com.wisp.game.bet.games.share.common.MaJiangPlayerData;
import msg_type_def.MsgTypeDef;

import java.util.List;

public class CalcGameScroe {

    public static MsgTypeDef.e_msg_result_def calcScore(LogicCore logicCore)
    {
        List<HistoryActionInfo> historyActionInfos = logicCore.getHistoryActionInfos();
        if( historyActionInfos.size() == 0 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail ;
        }

        HistoryActionInfo historyActionInfo = historyActionInfos.get( historyActionInfos.size() - 1 );

        if( !historyActionInfo.isHu() )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_fail;
        }

        MaJiangPlayerData huPlayer = logicCore.getGameSeats().get(historyActionInfo.getSeatPos());
        MaJiangPlayerData huedPlayer = logicCore.getGameSeats().get(historyActionInfo.getLinkedSeatPos());


        //检查各种类型的胡法
        boolean severPairHuFlag =  HuTypeUtils.getInstance().sevenPair(huPlayer);
        boolean duiduiHuFlag = HuTypeUtils.getInstance().duiduiHu(huedPlayer);
        boolean qingYiSeHuFlag = HuTypeUtils.getInstance().qingYiSeDuiDuiHu(huPlayer);
        boolean jieZhangHuFlag = HuTypeUtils.getInstance().jieZhang(huedPlayer,historyActionInfos);

        // 1:谁点炮,谁给钱,也就是1对1
        // 2:点炮的人付三家钱,
        // 3:每家给胡的付相同的钱
        int give_money_type = 1;
        if( historyActionInfo.isZiMo() )
        {
            give_money_type = 3;
        }
        else
        {
            give_money_type = 2;

        }

        if( give_money_type == 1 )
        {

        }
        else if( give_money_type == 2 )
        {

        }
        else
        {

        }

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }
}
