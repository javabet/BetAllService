package com.wisp.game.bet.games.share;

import com.wisp.game.bet.GameConfig.PlayerStockConfig;
import com.wisp.game.core.random.RandomHandler;

import java.util.Map;

public class PlayerStockCtrlCommon {

    private static int minId = -100000000;

    //百人类判断是否进行个人库存杀分
    public static boolean is_player_kill_mode(int recharged, int profit)
    {
        Map<Integer, PlayerStockConfig.PlayerStockConfigData> list =	PlayerStockConfig.GetInstnace().GetMapData();
        if (list.size() == 0)
            return false;

        int selId = -100000000;

        if (minId == -100000000)
        {
            for(int key : list.keySet())
            {
                if( key < minId )
                {
                    minId = key;
                }
            }
        }

        for (int key : list.keySet())
        {
            if (key <= recharged && key > selId)
            {
                selId = key;
            }
        }

        if (selId < minId)
            selId = minId;

        PlayerStockConfig.PlayerStockConfigData playerStock_cfg = PlayerStockConfig.GetInstnace().GetData(selId);
        int stage = -1;
        for (int i = 0; i < playerStock_cfg.getmPlayerProfit().size(); i++)
        {
            if (profit >= playerStock_cfg.getmPlayerProfit().get(i))
            {
                stage = i;
                break;
            }
        }

        if (stage < 0)
            return false;

        int StrongKill = playerStock_cfg.getmStrongKill().get(stage);//mStrongKill[stage];
        int r = RandomHandler.Instance.getRandomValue(100);
        if (r > StrongKill)
            return false;
        else
            return true;
    }

}
