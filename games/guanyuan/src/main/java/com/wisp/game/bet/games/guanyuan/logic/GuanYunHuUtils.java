package com.wisp.game.bet.games.guanyuan.logic;

import com.wisp.game.bet.games.share.HuStrategy.HuTypeUtils;
import com.wisp.game.bet.games.share.utils.IMaJiangPlayerData;

public class GuanYunHuUtils {

    public static boolean CheckCanHu(IMaJiangPlayerData seatData)
    {


        if(HuTypeUtils.getInstance().sevenPair(seatData))
        {
            return true;
        }

        return false;
    }
}
