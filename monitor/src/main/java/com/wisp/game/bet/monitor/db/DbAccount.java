package com.wisp.game.bet.monitor.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbAccount extends DbBase {

    public static final String DB_SERVERINFO = "ServerInfo";

    public static DbAccount Instance;
    public DbAccount() {
        Instance = this;
    }
}
