package com.wisp.game.bet.logic.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbLog extends DbBase {
    public static DbLog Instance;

    public DbLog() {
        Instance = this;
    }


}
