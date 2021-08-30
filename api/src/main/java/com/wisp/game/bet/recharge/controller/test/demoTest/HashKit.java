package com.wisp.game.bet.recharge.controller.test.demoTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashKit {
    /**
     * 将参数MD5加密处理
     * @param strSrc
     * @return
     */
    public static String md5(String strSrc ) {
        String key = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));
            String result = "";
            byte[] temp;
            temp = md5.digest(key.getBytes("UTF8"));
            for (int i = 0; i < temp.length; i++)
                result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
