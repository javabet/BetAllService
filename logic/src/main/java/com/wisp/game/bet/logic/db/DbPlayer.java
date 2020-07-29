package com.wisp.game.bet.logic.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbPlayer extends DbBase {

    public static final String DB_COMMON_CONFIG = "common_config";
    public static final String DB_PLAYER_INFO = "PlayerInfo";

    public static DbPlayer Instance;

    public DbPlayer() {
        Instance = this;
    }
}
