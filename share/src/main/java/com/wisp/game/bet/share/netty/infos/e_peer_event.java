package com.wisp.game.bet.share.netty.infos;

public enum e_peer_event {

    e_pe_disconnected(0),
    e_pe_connected(1),
    e_pe_connectfail(2),
    e_pe_accepted(3),
    e_pe_acceptfail(4),
    e_pe_recved(5),
    e_pe_only_remove(6);

    private final int value;
    e_peer_event(int val) {
        this.value = val;
    }

    public final int getNumber() { return value; }
}
