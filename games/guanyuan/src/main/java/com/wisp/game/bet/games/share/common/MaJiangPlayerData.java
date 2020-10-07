package com.wisp.game.bet.games.share.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//打牌时玩家数据
public class MaJiangPlayerData {
	
	private int seatIndex; //玩家的坐位信息0,1,2,3
	
	private long UID;			//玩家的uid
	
	private List<Integer> holds;	//玩家持有的牌
	
	private List<Integer> folds;		//打出的牌  也就是桌面上自己跟前的牌
	
	private List<Integer> flowers;		//花的数量
	
	private List<Integer> initHolds;			//初化时的玩家数据
	
	private List<Integer> initFlowerCards;		//初始化时花牌的数据
	
	private List<Integer> outCardActionList;		//已经推倒的牌  //包括,碰,杠(明杠,暗杠,点杠),吃 //wispAdd 用于替换上面的outCards里的数据,里面的数据mjActionList里的数据是一致的
	
	private List<Integer> mjActionList;		//所有操作的集合		//数据类拟于 最低4位是标识位，向上4位为位置，再上8位为牌的数据，再上以4位表示一个位置
	
	private Map<Integer,Integer> countMap;//玩家手上的牌的数目，用于快速判定碰杠

	private Map<Integer,HuPattern> tingMap;  //玩家听牌，用于快速判定胡了的番数
	
	private List<Integer> wangGangList;		//可以弯杠的列表
	private List<Integer> angGangList;		//可以暗杠的列表
   
	private boolean canAnGang = false;
	
	private boolean canDianGang = false;
	
	private boolean canWanGang = false;
	
	private boolean canPeng = false;
	
	private boolean canHu = false;
	
	private boolean canChi = false;
	
	private boolean canChuPai = false;
	
	private boolean canJiaoTing = false;
	
	private boolean hued = false;    //是否胡了

	private boolean iszimo = false;
	
	private boolean isGangHu = false;
	
	private int fan = 0;
	
	private int score = 0;

	private int lastFangGangSeat = -1;
	
	private int jiaoTingCard = 0;		//叫听的牌
	
	private Map<String,Boolean> huList;			//当前玩家胡牌型 
	
	private List<MjEndDetailEntity> detailList; //玩家此局牌胡牌的具体信息
	
	private int cardTypeMask = 0;			//玩家的类型的掩码,手里的牌 + 碰杠吃的牌的牌类型掩码
	
	private List<Integer> luoList;			//漏胡漏碰的列表
	
	public MaJiangPlayerData() {
		// TODO Auto-generated constructor stub
		
		holds = new ArrayList<>();
		folds = new ArrayList<>();
		flowers = new ArrayList<>();
		
		outCardActionList  = new ArrayList<>();
		
		mjActionList = new ArrayList<>();
		
		countMap = new HashMap<>();
		
		tingMap = new HashMap<>();
		
		this.huList = new HashMap<>();
		
		this.initFlowerCards = new ArrayList<>();
		
		this.detailList = new ArrayList<>();
		
		this.wangGangList = new ArrayList<>();
		this.angGangList = new ArrayList<>();
		this.luoList = new ArrayList<>();
	}
	
	
	public void clearAllOptions()
	{
		this.setCanPeng(false);
		this.setCanAnGang(false);
		this.setCanDianGang(false);
		this.setCanWanGang(false);
		this.setCanHu(false);
		this.setCanChi(false);
		this.setCanJiaoTing(false);
		this.lastFangGangSeat = -1;
		this.wangGangList.clear();
		this.angGangList.clear();
	}
	

	public int getSeatIndex() {
		return seatIndex;
	}


	public void setSeatIndex(int seatIndex) {
		this.seatIndex = seatIndex;
	}


	public long getUID() {
		return UID;
	}


	public void setUID(long uID) {
		UID = uID;
	}


	public List<Integer> getHolds() {
		return holds;
	}


	public void setHolds(List<Integer> holds) {
		this.holds = holds;
	}


	public List<Integer> getFolds() {
		return folds;
	}


	public void setFolds(List<Integer> folds) {
		this.folds = folds;
	}
	
	public List<Integer> getFlowers() {
		return flowers;
	}


	public void setFlowers(List<Integer> flowers) {
		this.flowers = flowers;
	}

	public Map<Integer, Integer> getCountMap() {
		return countMap;
	}


	public void setCountMap(Map<Integer, Integer> countMap) {
		this.countMap = countMap;
	}


	public Map<Integer, HuPattern> getTingMap() {
		return tingMap;
	}


	public void setTingMap(Map<Integer, HuPattern> tingMap) {
		this.tingMap = tingMap;
	}

	public boolean isCanAnGang() {
		return canAnGang;
	}


	public void setCanAnGang(boolean canAnGang) {
		this.canAnGang = canAnGang;
	}


	public boolean isCanDianGang() {
		return canDianGang;
	}


	public void setCanDianGang(boolean canDianGang) {
		this.canDianGang = canDianGang;
	}


	public boolean isCanWanGang() {
		return canWanGang;
	}


	public void setCanWanGang(boolean canWanGang) {
		this.canWanGang = canWanGang;
	}

	public boolean isCanPeng() {
		return canPeng;
	}


	public void setCanPeng(boolean canPeng) {
		this.canPeng = canPeng;
	}


	public boolean isCanHu() {
		return canHu;
	}


	public void setCanHu(boolean canHu) {
		this.canHu = canHu;
	}

	public boolean isCanChi() {
		return canChi;
	}


	public void setCanChi(boolean canChi) {
		this.canChi = canChi;
	}


	public boolean isCanChuPai() {
		return canChuPai;
	}


	public void setCanChuPai(boolean canChuPai) {
		this.canChuPai = canChuPai;
	}

	public boolean isCanJiaoTing() {
		return canJiaoTing;
	}


	public void setCanJiaoTing(boolean canJiaoTing) {
		this.canJiaoTing = canJiaoTing;
	}

	public boolean isHued() {
		return hued;
	}


	public void setHued(boolean hued) {
		this.hued = hued;
	}


	public boolean isIszimo() {
		return iszimo;
	}


	public void setIszimo(boolean iszimo) {
		this.iszimo = iszimo;
	}


	public boolean isGangHu() {
		return isGangHu;
	}


	public void setGangHu(boolean isGangHu) {
		this.isGangHu = isGangHu;
	}


	public int getFan() {
		return fan;
	}


	public void setFan(int fan) {
		this.fan = fan;
	}


	public int getScore() {
		return score;
	}

	//....
	public void setScore(int score) {
		this.score = score;
	}


	public int getLastFangGangSeat() {
		return lastFangGangSeat;
	}


	public void setLastFangGangSeat(int lastFangGangSeat) {
		this.lastFangGangSeat = lastFangGangSeat;
	}

	public List<Integer> getMjActionList() {
		return mjActionList;
	}


	public void setMjActionList(List<Integer> mjActionList) {
		this.mjActionList = mjActionList;
	}



	public Map<String, Boolean> getHuList() {
		return huList;
	}


	public void setHuList(Map<String, Boolean> huList) {
		this.huList = huList;
	}

	public List<Integer> getInitHolds() {
		return initHolds;
	}


	public void setInitHolds(List<Integer> initHolds) {
		this.initHolds = initHolds;
	}


	public List<Integer> getInitFlowerCards() {
		return initFlowerCards;
	}


	public void setInitFlowerCards(List<Integer> initFlowerCards) {
		this.initFlowerCards = initFlowerCards;
	}


	public List<Integer> getOutCardActionList() {
		return outCardActionList;
	}


	public void setOutCardActionList(List<Integer> outCardActionList) {
		this.outCardActionList = outCardActionList;
	}

	public int getJiaoTingCard() {
		return jiaoTingCard;
	}


	public void setJiaoTingCard(int jiaoTingCard) {
		this.jiaoTingCard = jiaoTingCard;
	}


	public List<MjEndDetailEntity> getDetailList()
	{
		return detailList;
	}


	public void setDetailList(List<MjEndDetailEntity> detailList)
	{
		this.detailList = detailList;
	}


	public List<Integer> getWangGangList() {
		return wangGangList;
	}


	public void setWangGangList(List<Integer> wangGangList) {
		this.wangGangList = wangGangList;
	}


	public List<Integer> getAngGangList() {
		return angGangList;
	}


	public void setAngGangList(List<Integer> angGangList) {
		this.angGangList = angGangList;
	}
	
	
	
	
	public int getCardTypeMask()
	{
		return cardTypeMask;
	}
	

	public void setCardTypeMask(int cardTypeMask)
	{
		this.cardTypeMask = cardTypeMask;
	}
	

	public List<Integer> getLuoList()
	{
		return luoList;
	}


	public void setLuoList(List<Integer> luoList)
	{
		this.luoList = luoList;
	}


	/**
	 * @return
	 */
	public int getActionFlag()
	{
		int flag = 0;
		if(isCanPeng())
		{
			flag |= GameMsg.ACTION_PENG;
		}
		
		if(isCanHu())
		{
			flag |= GameMsg.ACTION_HU;
		}
		
		if(isCanAnGang())
		{
			flag |= GameMsg.ACTION_ANGANG;
		}
		
		if( isCanWanGang() )
		{
			flag |= GameMsg.ACTION_WANGANG;
		}
		
		if(isCanDianGang())
		{
			flag |= GameMsg.ACTION_DIANGGANG;
		}
		
		if(isCanChi())
		{
			flag |= GameMsg.ACTION_CHI;
		}
		
		return flag;
	}
	
	
	public void setActionFlag(int flag )
	{
		if( (flag & GameMsg.ACTION_PENG ) == GameMsg.ACTION_PENG )
		{
			canPeng = true;
		}
		
		if( (flag & GameMsg.ACTION_HU ) == GameMsg.ACTION_HU )
		{
			canHu = true;
		}
		
		if( (flag & GameMsg.ACTION_ANGANG ) == GameMsg.ACTION_ANGANG )
		{
			canAnGang = true;
		}
		
		if( (flag & GameMsg.ACTION_WANGANG ) == GameMsg.ACTION_WANGANG )
		{
			canWanGang = true;
		}
		
		if( (flag & GameMsg.ACTION_DIANGGANG ) == GameMsg.ACTION_DIANGGANG )
		{
			canDianGang = true;
		}
		
		if( (flag & GameMsg.ACTION_CHI ) == GameMsg.ACTION_CHI )
		{
			canChi = true;
		}
	}
	
	
	public void calcCardMask()
	{
		cardTypeMask = 0;
		for(int holdCard:  holds)
		{
			cardTypeMask |= (0x01 << (holdCard >> 4 ));
		}
		
		for(int outCardAction:  outCardActionList)
		{
			int outCard = (outCardAction) >> 8 & 0x0FF;
			cardTypeMask |= (0x01 << (outCard >> 4 ));
		}
	}
	
	
	/**
	 * 去掉某张牌后的二进制掩码
	 * @param card
	 */
	public int calcCardMaskExcept(int card)
	{
		int mask = 0;
		for(int holdCard:  holds)
		{
			if(card == holdCard)
			{
				continue;
			}
			cardTypeMask |= (0x01 << (holdCard >> 4 ));
		}
		
		for(int outCardAction:  outCardActionList)
		{
			int outCard = (outCardAction) >> 8 & 0x0FF;
			cardTypeMask |= (0x01 << (outCard >> 4 ));
		}
		
		return mask;
	}
	
}
