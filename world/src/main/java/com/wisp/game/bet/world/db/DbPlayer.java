package com.wisp.game.bet.world.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbPlayer extends DbBase {

    public static final String DB_COMMON_CONFIG = "common_config";
    public static final String DB_PLAYER_INFO = "PlayerInfo";
    public static final String DB_RANDOM_ROOM_NUMBER = "RandomRoomNumber";//随机房号
    public static final String DB_ONLINE_ROOMCARD = "OnlineRoomCard";//当前房卡房间

    public static DbPlayer Instance;

    public DbPlayer() {
        Instance = this;
    }
}
