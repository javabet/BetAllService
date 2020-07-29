package com.wisp.game.bet.share.netty.infos;

public class PeerEvent {
    private int peed_id;
    private e_peer_event e_peer_event;

    public int getPeed_id() {
        return peed_id;
    }

    public void setPeed_id(int peed_id) {
        this.peed_id = peed_id;
    }

    public com.wisp.game.bet.share.netty.infos.e_peer_event getE_peer_event() {
        return e_peer_event;
    }

    public void setE_peer_event(com.wisp.game.bet.share.netty.infos.e_peer_event e_peer_event) {
        this.e_peer_event = e_peer_event;
    }
}
