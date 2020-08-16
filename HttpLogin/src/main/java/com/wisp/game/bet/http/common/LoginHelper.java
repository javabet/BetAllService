package com.wisp.game.bet.http.common;

import org.bson.types.ObjectId;

public class LoginHelper {

    public static String buildWebAccount(String agentId)
    {
        return "web_" + agentId + "_" + new ObjectId().toHexString();
    }

    public  LoginResult buildWebLoginMsg(String info)
    {
        return buildWebLoginMsg(info,false,"","","",false,"");
    }

    public  LoginResult buildWebLoginMsg(String info, boolean ret , String acc, String gameip , String gameWebIp , boolean needip, String urlConfig )
    {
        LoginResult result = new LoginResult();
        result.ret = ret;
        result.info = info;
        result.acc = acc;
        result.gameip = gameip;
        result.needip = needip;
        result.urlConfig = urlConfig;
        result.gameWebIp = gameWebIp;

        return result;
    }

    public class LoginResult
    {
        private boolean ret = false;
        private String acc;
        private String info;
        private String gameip;
        private boolean needip;
        private String urlConfig;
        private String gameWebIp;

        public LoginResult() {
        }

        public boolean isRet() {
            return ret;
        }

        public void setRet(boolean ret) {
            this.ret = ret;
        }

        public String getAcc() {
            return acc;
        }

        public void setAcc(String acc) {
            this.acc = acc;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getGameip() {
            return gameip;
        }

        public void setGameip(String gameip) {
            this.gameip = gameip;
        }

        public boolean isNeedip() {
            return needip;
        }

        public void setNeedip(boolean needip) {
            this.needip = needip;
        }

        public String getUrlConfig() {
            return urlConfig;
        }

        public void setUrlConfig(String urlConfig) {
            this.urlConfig = urlConfig;
        }

        public String getGameWebIp() {
            return gameWebIp;
        }

        public void setGameWebIp(String gameWebIp) {
            this.gameWebIp = gameWebIp;
        }
    }
}
