package com.wisp.game.bet.http.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbConfig extends DbBase {

    public static final String DB_SERVERLIST = "ServerList";
    public static final String DB_AgentInfo = "AgentInfo";

    public static DbConfig Instance;

    public DbConfig() {
        Instance = this;
    }
}
