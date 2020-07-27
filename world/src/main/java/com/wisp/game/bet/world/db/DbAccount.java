package com.wisp.game.bet.world.db;

import com.wisp.game.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbAccount extends DbBase {

    public static final String DB_SERVERINFO = "ServerInfo";

    public static DbAccount Instance;
    public DbAccount() {
        Instance = this;
    }
}
