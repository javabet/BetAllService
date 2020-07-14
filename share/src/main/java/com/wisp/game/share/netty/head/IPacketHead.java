package com.wisp.game.share.netty.head;

import io.netty.buffer.ByteBuf;

public interface IPacketHead {

    void init(int pid,int psize);

    void to_array(ByteBuf buf);

    void to_array(ByteBuf buf,int offset);

    void parse_from(ByteBuf buf);

    void parse_from(ByteBuf buf,int offset);

    void buffer_decryption(ByteBuf buf,int len);

    boolean check_head();

    int get_id();

    int get_size();

}
