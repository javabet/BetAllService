package com.wisp.game.bet.world.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbConfig extends DbBase {

    public static final String DB_SERVERLIST = "ServerList";

    public static DbConfig Instance;

    public DbConfig() {
        Instance = this;
    }
}
