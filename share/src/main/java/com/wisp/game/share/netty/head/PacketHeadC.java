package com.wisp.game.share.netty.head;

import io.netty.buffer.ByteBuf;

public class PacketHeadC extends PacketHeadS {

    private int keys_len = 12;
    private char[] ed_keys = new char[]{0x43,0x55,0xa1,0x5d,0x6a,0x1a,0x90,0x67,0xab,0xc7,0x9c,0xd1};

    public PacketHeadC() {
    }

    @Override
    public void to_array(ByteBuf buf, int offset) {
        super.to_array(buf, offset);
    }

    @Override
    public void parse_from(ByteBuf buf, int offset) {
        byte[] bytes =  buf.array();

        for(int i = 0; i < keys_len;i++)
        {
            bytes[i] = (byte)(bytes[i] ^ ed_keys[i]);
        }

        int j = 0;
        for(int i = 4; i < keys_len; i ++,j++)
        {
            if(j >  3) j = 0;
            bytes[i] = (byte)( bytes[i] ^ bytes[j] );
        }

        buf.readerIndex(0);
        buf.writerIndex(0);

        buf.writeBytes(bytes);

        buf.readerIndex(0);
        buf.writerIndex(0);
    }

    @Override
    public void buffer_decryption(ByteBuf buf, int len) {
        super.buffer_decryption(buf, len);
    }
}
