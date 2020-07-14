package com.wisp.game.share.netty.infos;

public enum e_peer_state {
    e_ps_disconnected(0,0),
    e_ps_disconnecting(1,1),
    e_ps_connecting(2,2),
    e_ps_connected(3,3),
    e_ps_accepting(4,4);

    private final int index;
    private final int value;

    private e_peer_state(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public final int getNumber() { return value; }
}
