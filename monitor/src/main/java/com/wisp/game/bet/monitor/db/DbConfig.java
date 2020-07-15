package com.wisp.game.bet.monitor.db;

import com.wisp.game.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbConfig extends DbBase {

    public static final String DB_SERVERLIST = "ServerList";

    public static DbConfig Instance;

    public DbConfig() {
        Instance = this;
    }
}