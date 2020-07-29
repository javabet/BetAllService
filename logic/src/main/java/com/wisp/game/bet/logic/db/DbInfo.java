package com.wisp.game.bet.logic.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbInfo extends DbBase {

    public static DbInfo Instance;

    public DbInfo() {
        Instance = this;
    }
}
