package com.wisp.game.bet.recharge.controller.test.demoTest;

import java.util.Arrays;
import java.util.Map;

public class MapDataUtil {
    /**
     * 对参数map进行升序排序用&拼接
     * @param map
     * @return
     */
    public static String createParam(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty())
                return null;
            Object[] key = map.keySet().toArray();
            Arrays.sort(key);
            StringBuffer res = new StringBuffer(128);
            for (int i = 0; i < key.length; i++)
            {
                Object obj = key[i];
                res.append(key[i] + "=" + map.get(key[i]) + "&");
            }


            String rStr = res.substring(0, res.length() - 1);
            System.out.println("rStr:"+rStr);
            return rStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
