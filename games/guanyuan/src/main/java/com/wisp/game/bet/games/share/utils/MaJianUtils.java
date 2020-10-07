package com.wisp.game.bet.games.share.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wisp.game.bet.games.share.common.HuPattern;
import com.wisp.game.bet.games.share.common.MaJiangPlayerData;


//0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09, //万1 - 9
//0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19, //条 1- 9
//0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29, //饼1 - K
//0x31,0x32,0x33,0x34,0x35,0x36,0x37 //风 东南西北
//0x51,0x52,0x53,0x54,0x55,0x56,0x57,0x58,//8张花牌
//0xF1,0xF2,0xF3,0xF4,0xF5,0xF6,0xF7,0xF8,0xF9,0xFA,0xFB // 吃 碰 杠 听 补 胡 自摸 准备 抢杠 杠开 用于声音播放


public class MaJianUtils {


	/**
	 *
	 * @param seatData	玩家
	 * @param nunType
	 * @param hunMj		需要检查的牌，加上此牌是否可以胡
	 */
	public static void checkAllTingPai(MaJiangPlayerData seatData, int nunType, int hunMj)
	{
		if(!seatData.getCountMap().containsKey(hunMj))
		{
			checkTingPaiRecursion(seatData, nunType, 0);
			return;
		}

		int c_old = seatData.getCountMap().get(hunMj);
		 //先置空
		seatData.getCountMap().put(hunMj, 0);

		int rmv_num = 0;
		for(int i = seatData.getHolds().size() - 1; i >= 0;i --)
		{
			if(seatData.getHolds().get(i) == hunMj)
			{
				seatData.getHolds().remove(i);
				rmv_num ++;

				if(rmv_num >= c_old)
				{
					break;
				}
			}
		}


		checkTingPaiRecursion(seatData, nunType, c_old);

		seatData.getCountMap().put(hunMj, c_old);
		 //需要将前面删除的数据给还原
		for(int i = 0; i < rmv_num;i++)
		{
			seatData.getHolds().add(hunMj);
		}
	}
	
	/**
	 * 当忽略了将的时候，其是否也能够组成3n规则
	 * @param seatData
	 * @param jiangCard
	 * @return
	 */
	public static boolean checkCanHuSkipJiang(MaJiangPlayerData seatData,int jiangCard)
	{
		changeMapValue( seatData,jiangCard,-2);
		boolean b_flag = checkSingle(seatData);
		changeMapValue( seatData,jiangCard,2);
		
		return b_flag;
	}
	
	
	/*
	当忽略了ke的时候，其是否也能够hu
	 * @param seatData
	 * @param jiangCard
	 */
	public static boolean checkCanHuSkipKe(MaJiangPlayerData seatData,int keCard)
	{
		changeMapValue( seatData,keCard,-3);
		boolean b_flag = checkCanHu(seatData);
		changeMapValue( seatData,keCard,3);

		return b_flag;
	}

	/**
	 *
	 * @param seatData
	 * @param mjNum
	 * @param hunNum  混牌的数量
	 */
	private static void checkTingPaiRecursion(MaJiangPlayerData seatData,int mjNum,int hunNum)
	{
		if( hunNum <= 0 )
		{
			checkTingPai(seatData,mjNum);
		}
		else
		{
			hunNum -- ;

			/**
			List<Integer> currentList = ConfigDataPool.getInstance().gameConfig.getTingCardByMjNum(mjNum);
			
			for(int i = 0; i < currentList.size();i ++)
			{
				int mj = currentList.get(i);
				
				if(seatData.getCountMap().containsKey(mj))
				{
					int old_c = seatData.getCountMap().get(mj);
					seatData.getCountMap().put(mj, old_c + 1);
				}
				else
				{
					seatData.getCountMap().put(mj, 1);
				}
				
				seatData.getHolds().add(mj);
				
				checkTingPaiRecursion(seatData, mjNum, hunNum);
				
				if(seatData.getCountMap().get(mj) > 1)
				{
					changeMapValue(seatData,mj,-1);					
				}
				else
				{
					seatData.getCountMap().remove(mj);
				}
					
				seatData.getHolds().remove(seatData.getHolds().size() -1);
			}
			 **/
		}
	}
	
	/**
	 * 
	 * @param seatData
	 * @param numType 0:108,1:136
	 */
	private static void checkTingPai(MaJiangPlayerData seatData,int numType)
	{
		//first check the seven pairs
		if(seatData.getHolds().size() == 13)
		{
			boolean hu = false;
			int danpai = -1;
			int pairCount = 0;
			
			for(int k : seatData.getCountMap().keySet())
			{
				int c = seatData.getCountMap().get(k);
				if(c == 2 || c == 3)
				{
					pairCount ++;
				}
				else if(c == 4)
				{
					pairCount += 2;
				}
				
				if(c == 1 || c == 3)
				{
					//如果已经有单牌了，表示不止一张单牌，并没有下叫。直接闪
					if(danpai >= 0)
					{
						break;
					}
					danpai = k;
				}
					
			}
			
			if(pairCount == 6 && danpai > 0)
			{
				HuPattern pattern = new HuPattern();
				pattern.setFan( 2 );
				pattern.setPattern(HuPattern.SEVENN);
				seatData.getTingMap().put(danpai, pattern);
			}
		}
		
		
	    //再检查对对胡，
	    //检查是否是对对胡  由于四川麻将没有吃，所以只需要检查手上的牌
	    //对对胡叫牌有两种情况
	    //1、N坎 + 1张单牌
	    //2、N-1坎 + 两对牌
		int singleCount = 0;
		int colCount = 0;
		int pairCount = 0;
		List<Integer> arr = new ArrayList<>();
		
		for(int k : seatData.getCountMap().keySet())
		{
			int c = seatData.getCountMap().get(k);
			if(c == 1)
			{
				singleCount ++;
				arr.add(k);
			}
			else if(c == 2)
			{
				pairCount ++;
				arr.add(k);
			}
			else if( c == 3)
			{
				colCount ++;
			}
			else if(c == 4)
			{
				singleCount ++;
				pairCount += 2;
			}	
			
			if(singleCount > 1)
			{
				break;
			}
			if(pairCount > 2)
			{
				break;
			}
		}
		
		if( ( pairCount == 2 && singleCount == 0 ) || ( pairCount == 0 && singleCount == 1 ) )
		{
			for(int i = 0;i < arr.size();i++)
			{
				int p = arr.get(i);
				
				if(!seatData.getTingMap().containsKey(p))
				{
					HuPattern pattern = new HuPattern();
					pattern.setFan( 2 );
					pattern.setPattern(HuPattern.DUIDUIHU);
					seatData.getTingMap().put(p, pattern);
				}
					
			}
		}
		
		
		//使用新的方式，使其更加的灵活
//		List<MJTingItemInfo> tingList  = ConfigDataPool.getInstance().gameConfig.getMJTingListByMjNum(numType);
//		for(MJTingItemInfo tingItemInfo : tingList)
//		{
//			checkBaseTingPai(seatData,tingItemInfo.getStart(),tingItemInfo.getEnd());
//		}
		
		checkBaseTingPai(seatData, 0x01, 0x09);
		checkBaseTingPai(seatData, 0x11, 0x19);
		checkBaseTingPai(seatData, 0x21, 0x29);
	}
	
	/**
	 * find the base canhu
	 * @param seatData
	 * @param begin
	 * @param end
	 */
	public static void checkBaseTingPai(MaJiangPlayerData seatData,int begin,int end)
	{
		synchronized (seatData) {
			for(int i = begin;i <= end;i++ )
			{
				//如果这牌已经在和了，就不用检查了
				boolean has_ting =  seatData.getTingMap().containsKey(i);
				if(has_ting)
				{
					continue;
				}
				
				//将牌加入到计数中
				int old = 0 ;
				if(seatData.getCountMap().containsKey(i))
				{
					old = seatData.getCountMap().get(i);
					seatData.getCountMap().put(i, old + 1);
				}
				else
				{
					seatData.getCountMap().put(i, 1);
				}
				
				seatData.getHolds().add(i);
				
				//逐个判定手上的牌
				boolean canHu = checkCanHu(seatData);
				if(canHu)
				{
					HuPattern pattern = new HuPattern();
					pattern.setFan(0);
					pattern.setPattern(HuPattern.NORMAL);
					seatData.getTingMap().put(i, pattern);
				}
				
				//搞完以后，撤消刚刚加的牌
				seatData.getCountMap().put(i, old);
				seatData.getHolds().remove(seatData.getHolds().size() -1 ); //delete the last item
			}
		}
	}
	
	
	public static boolean checkCanHu(MaJiangPlayerData seatData)
	{
		if(seatData.getHolds().size() == 14)
		{
			boolean huFlag = true;
			for(int k : seatData.getCountMap().keySet())
			{
				int c = seatData.getCountMap().get(k);
				if(c % 2 == 1 )
				{
					huFlag = false;
					break;
				}
				
			}
			if( huFlag)
			{
				return true;
			}
		}
		
		for(int k : seatData.getCountMap().keySet())
		{
			int c = seatData.getCountMap().get(k);
			if(c < 2)
			{
				continue;
			}
			
			//如果当前牌大于等于２，则将它选为将牌
			//逐个判定剩下的牌是否满足　３Ｎ规则,一个牌会有以下几种情况
			//1、0张，则不做任何处理
			//2、2张，则只可能是与其它牌形成匹配关系
			//3、3张，则可能是单张形成 A-2,A-1,A  A-1,A,A+1  A,A+1,A+2，也可能是直接成为一坎
			//4、4张，则只可能是一坎+单张
			
			changeMapValue( seatData,k,-2);
			boolean b_flag = checkSingle(seatData);
			changeMapValue( seatData,k,2);
			if(b_flag)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean checkSingle(MaJiangPlayerData seatData)
	{
		List<Integer> holds = seatData.getHolds();
		int selected = -1;
		int c = 0;
		boolean b_flag = false;
		
		for( int i = 0; i < holds.size();i++)
		{
			int pai = holds.get(i);
			
			if(seatData.getCountMap().containsKey(pai))
			{
				c = seatData.getCountMap().get(pai);
				if(c != 0)
				{
					selected = pai;
					break;
				}
			}	
		}
		
		//如果没有找到剩余牌，则表示匹配成功了
		if(selected == -1)
		{
			return true;
		}
		
		//否则，进行匹配
		if(c == 3)
		{	//直接作为一坎
			seatData.getCountMap().put(selected, 0);
			b_flag = checkSingle(seatData);
			seatData.getCountMap().put(selected, c);
			if(b_flag)
			{
				return true;
			}
		}
		else if(c == 4)
		{//直接作为一坎
			seatData.getCountMap().put(selected, 1);
			b_flag = checkSingle(seatData);
			seatData.getCountMap().put(selected, c);//立即恢复对数据的修改
			//如果作为一坎能够把牌匹配完，直接返回TRUE。
			if(b_flag)
			{
				return true;
			}
		}
		
		
		return matchSingle(seatData, selected);
	}
	
	
	
	private static boolean matchSingle(MaJiangPlayerData seatData,int selected)
	{
		int mj_type = selected / 16;
		if(mj_type >= 3)
		{
			return false;
		}
		
		//分开匹配 A-2,A-1,A
		
		boolean matched = true;
		boolean b_flag = false;
		int tmp_val;
		
		int v = selected % 16;
		if(v < 3)
		{
			matched = false;
		}
		else
		{
			for(int i = 0;i < 3; i ++)
			{
				int t = selected -2 + i;
				
				b_flag = seatData.getCountMap().containsKey(t);
				if(!b_flag)
				{
					matched = false;
					break;
				}
				
				
				if( seatData.getCountMap().get(t) == 0 )
				{
					matched = false;
					break;
				}
			}
		}
		
		
		//匹配成功，扣除相应数值
		if(matched){
			changeMapValue(seatData,selected - 2,-1);
			changeMapValue(seatData,selected - 1,-1);
			changeMapValue(seatData,selected - 0,-1);
			b_flag = checkSingle(seatData);
			changeMapValue(seatData,selected - 2,1);
			changeMapValue(seatData,selected - 1,1);
			changeMapValue(seatData,selected - 0,1);
			if(b_flag)
			{
				return true;
			}
		}
		
		//分开匹配 A-1,A,A + 1
		matched = true;
		if( v < 2 || v > 8)
		{
			matched = false;
		}
		else
		{
			for(int i = 0;i < 3;i ++)
			{
				int t = selected - 1 + i;
				
				b_flag = seatData.getCountMap().containsKey(t);
				if(!b_flag)
				{
					matched = false;
					break;
				}
				
				
				if( seatData.getCountMap().get(t) == 0 )
				{
					matched = false;
					break;
				}
			}
		}
		
		//匹配成功，扣除相应数值
		if(matched){
			changeMapValue(seatData,selected - 1,-1);
			changeMapValue(seatData,selected,-1);
			changeMapValue(seatData,selected + 1,-1);
			b_flag = checkSingle(seatData);
			changeMapValue(seatData,selected - 1,1);
			changeMapValue(seatData,selected,1);
			changeMapValue(seatData,selected + 1,1);
			if(b_flag)
			{
				return true;
			}
		}
		
		//分开匹配 A,A+1,A + 2
		matched = true;
		
		if(v > 7)
		{
			matched = false;
		}
		else
		{
			for(int i = 0;i < 3;i ++)
			{
				int t = selected + i;
				
				b_flag = seatData.getCountMap().containsKey(t);
				if(!b_flag)
				{
					matched = false;
					break;
				}
				
				
				if( seatData.getCountMap().get(t) == 0 )
				{
					matched = false;
					break;
				}
			}
		}
		
		
		//匹配成功，扣除相应数值
		if(matched){
			changeMapValue(seatData,selected,-1);
			changeMapValue(seatData,selected + 1,-1);
			changeMapValue(seatData,selected + 2,-1);
			b_flag = checkSingle(seatData);
			changeMapValue(seatData,selected ,1);
			changeMapValue(seatData,selected + 1,1);
			changeMapValue(seatData,selected + 2,1);
			if(b_flag)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static void changeMapValue(MaJiangPlayerData seatData,int selected,int changeNum)
	{
		int old_value = seatData.getCountMap().get(selected);
		seatData.getCountMap().put(selected, old_value + changeNum);
	}
	

	
	private static  boolean laiziHuPaiRecursion(List<Integer> list,int cardNum,Map<Integer,Boolean> checkMap,List<Integer> laiziList )
	{
		int i,j,k;
		Integer i_max,i_min,i_mid;
		int i_mark1,i_mark2;
		int i_have3 = 0,laizi_num = 0;
		
		for(i = 0;i < cardNum; i++ )
		{
			if( checkMap.containsKey(i) && checkMap.get(i) )
			{
				continue;
			}
			else
			{
				i_mark1 = i;
			}
			
			for( j = i + 1; j < cardNum;j++)
			{
				if( i == j)
				{
					continue;
				}
				else if( checkMap.containsKey(j) && checkMap.get(j) )
				{
					continue;
				}
				else
				{
					i_mark2 = j;
				}
				
				for( k = j + 1; k < cardNum;k++)
				{
					if(i == j || j == k)
					{
						continue;
					}
					else if( checkMap.containsKey(k) && checkMap.get(k) )
					{
						continue;
					}
					
					i_have3 = 1;
					laizi_num = 0;
					
					i_max = list.get(i);
					i_min = list.get(i);
					
					if( i_min > list.get(j))
					{
						i_min = list.get(j);
					}
					else
					{
						i_max = list.get(j);
					}
					
					if( i_min > list.get(k))
					{
						i_min = list.get(k);
					}
					else
					{
						i_max = list.get(k);
					}
					
					i_mid = ((i_max + i_min)/2);
					
					if(laiziList != null && laiziList.size() > 0)
					{
						if( laiziList.indexOf(list.get(i)) >= 0 )
						{
							laizi_num ++;
						}
						if( laiziList.indexOf(list.get(j)) >= 0 )
						{
							laizi_num ++;
						}
						if( laiziList.indexOf(list.get(k)) >= 0 )
						{
							laizi_num ++;
						}
					}
					
					
					
					if(laizi_num >= 2)
					{
						//do someting
					}
					else if( list.get(i) == list.get(j) && list.get(j) == list.get(k) )
					{
						//能成刻子
					}
					else if( laizi_num == 1 && ( list.get(i) == list.get(j) || list.get(i) == list.get(k) || list.get(j) == list.get(k) ) )
					{
						//能成刻子
					}
					else if(laizi_num == 1)
					{
						if(laiziList.indexOf(list.get(i)) >= 0)
						{
							int dis = (int)(list.get(j) - list.get(k));
							if(Math.abs(dis) > 2)
							{
								continue;
							}
							
						}
						else if( laiziList.indexOf(list.get(j)) >= 0 )
						{
							int dis = (int)(list.get(i) - list.get(k));
							if(Math.abs(dis) >= 2)
							{
								continue;
							}
						}
						else if(laiziList.indexOf(list.get(k)) >= 0)
						{
							int dis = (int)(list.get(i) - list.get(j));
							if(Math.abs(dis) >= 2)
							{
								continue;
							}
						}
					}
					else if( i_max - i_min == 2 && ( list.get(i) == i_mid || list.get(j) == i_mid || list.get(k) == i_mid ) )
					{
						//表示能组成顺子
						//do something
					}
					else
					{
						continue;
					}
					
					checkMap.put(i, true);
					checkMap.put(j, true);
					checkMap.put(k, true);
					
					if(!laiziHuPaiRecursion(list, cardNum, checkMap, laiziList))
					{
						checkMap.remove(i);
						checkMap.remove(j);
						checkMap.remove(k);
						continue;
					}
					
					return true;
				}
				
				
				//只剩两张牌, 相等 或者 其中一张为癞子时，满足牌型  
				if(i_have3 == 0)
				{
					if(list.get(i_mark1) == list.get(i_mark2))
					{
						return true;
					}
					else if(laiziList != null && laiziList.size() > 0)
					{
						if(laiziList.indexOf(list.get(i_mark1)) >= 0 || laiziList.indexOf(list.get(i_mark2)) >= 0)
						{
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	
	
	
}
