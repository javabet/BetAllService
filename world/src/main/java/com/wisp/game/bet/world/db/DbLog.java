package com.wisp.game.bet.world.db;

import com.wisp.game.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbLog extends DbBase {
    public static DbLog Instance;

    public DbLog() {
        Instance = this;
    }


}
