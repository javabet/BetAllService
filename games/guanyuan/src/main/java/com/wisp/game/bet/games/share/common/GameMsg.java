package com.wisp.game.bet.games.share.common;

public class GameMsg {

	public static final int ACTION_TYPE_CHU_PAI = 0x02;
	public static final int ACTION_TYPE_MO_PAI = 0x0c;
	public static final int ACTION_TYPE_LIANGSHEN = 0x06;
	public static final int ACTION_TYPE_MING_GANG = 0xf3;
	public static final int ACTION_TYPE_PENG = 0xf2;
	public static final int ACTION_TYPE_AN_GANG = 0x04;
	public static final int ACTION_TYPE_ZI_MO_GANG = 0x03;
	public static final int ACTION_TYPE_HU = 0xf6;
	public static final int ACTION_TYPE_ZIMO_HU = 0xf7;
	public static final int ACTION_TYPE_HUANG_PAI = 0x12;
	public static final int ACTION_TYPE_JIAO_TING = 0x13;		//叫听
	
	
	//牌的类型,0:普通牌,1:风牌,2:花牌
	public static final int CARD_TYPE_NORMAL = 0;
	public static final int CARD_TYPE_FEN = 1;
	public static final int CARD_TYPE_HUA = 2;
	
	
	
	// 用来保存的时候来标识动作为的
	public static final int FLAG_GUO = 0;		//过
	public static final int FLAG_CHUPAI = 1;
	public static final int FLAG_MOPAI = 2;
	public static final int FLAG_CHI_PAI = 3;
	public static final int FLAG_PENG = 4;
	public static final int FLAG_DIAN_GANG = 5;
	public static final int FLAG_WANG_GANG = 6;
	public static final int FLAG_AN_GANG = 7;
	//public static final int FLAG_FANG_GANG = 8;
	public static final int FLAG_JIAO_TING = 9; // 加听
	public static final int FLAG_HU = 10;
	public static final int FLAG_QIANG_GANG_HU = 11;
	public static final int FLAG_ZIMO_HU = 13;
	public static final int FLAG_GANG_HU = 14;		//杠胡 
	public static final int FLAG_DIAN_GANG_HU = 15;		//点杠胡
	public static final int FLAG_GANG_PAO_HU = 16; // 放炮胡
	public static final int FLAG_FANG_PAO_HU = 17; // 放炮胡
	
	//多个动作同时存在时的定义
	public static final int ACTION_PENG = 0x01;
	public static final int ACTION_HU  = 0x02;
	public static final int ACTION_ANGANG =  0x04;
	public static final int ACTION_WANGANG = 0x08;
	public static final int ACTION_DIANGGANG = 0x10;
	public static final int ACTION_CHI = 0x20;
	
	//使用二进制数据来表示当前可以操作的类型  
	public static final int AT_AN_GANG 			= 			0x01;
	public static final int AT_DIAN_GANG 		= 			0x02;
	public static final int AT_WANG_GANG 		=			0x04;
	public static final int AT_PENG				=			0x08;
	public static final int AT_HU				=			0x10;
	public static final int AT_CHI_PAI			=			0x20;
	public static final int AT_CHU_PAI			=			0x40;
	public static final int AT_JIAO_TING		=			0x80;

}
