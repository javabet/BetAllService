package com.wisp.game.bet.world.db;

import com.wisp.game.bet.sshare.DbBase;
import org.springframework.stereotype.Component;

@Component
public class DbCloud extends DbBase {
    public static DbCloud Instance;

    public DbCloud() {
        Instance = this;
    }
}
