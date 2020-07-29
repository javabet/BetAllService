package com.wisp.game.bet.logic.sshare;

public enum e_player_state {
    e_ps_none(0),
    e_ps_loading(1),
    e_ps_playing(2),
    e_ps_disconnect(3);

    private int val;

    e_player_state(int val) {
        this.val = val;
    }

    public int getVal()
    {
        return this.val;
    }
}
