package com.wisp.game.bet.games.share.utils;

import com.wisp.game.bet.games.share.common.HuPattern;
import com.wisp.game.bet.games.share.common.MaJiangPlayerData;
import com.wisp.game.bet.games.share.enums.HuTypeEnum;

import java.util.*;

/*
高效的麻将胡牌算法
 */
public class MahjongUtils
{
	
	/**
	 * 获得可以听的数量
	 * @param seatData
	 * @param currentAllList
	 * @param hunMjs
	 */
	public static void checkAllTingPai(MaJiangPlayerData seatData, List<Integer> currentAllList, List<Integer> hunMjs)
	{
		seatData.getTingMap().clear();
		
		for( int card : currentAllList )
		{
			seatData.getHolds().add(card);
			boolean hasHu = checkCanHu(seatData,hunMjs);
			seatData.getHolds().remove(seatData.getHolds().size() - 1);
			
			if(hasHu)
			{
				HuPattern pattern = new HuPattern();
				pattern.setFan(0);
				pattern.setPattern(HuTypeEnum.TYPE_NORMAL);
				seatData.getTingMap().put(card, pattern);
			}
		}
	}
	
	/**
	**忽略刻后的胡
	*/
	public static boolean checkCanHuSkipKe(MaJiangPlayerData seatData,int keCard,List<Integer> hunMjs)
	{
		if(!seatData.getCountMap().containsKey(keCard))
		{
			return false;
		}
		
		if(seatData.getCountMap().get(keCard) < 3)
		{
			return false;
		}
		
		 // 移除的对象的索引位置，检测后还需要将将其还原的
		List<Integer> removeIndexList = new ArrayList<>();
		for (int i = 0; i < 3; i++)
		{
			int idx = seatData.getHolds().indexOf(keCard);
			removeIndexList.add(idx);
			seatData.getHolds().remove(idx);
		}
		
		boolean b_flag = checkCanHu(seatData,hunMjs);
		
		// 还原换牌以前的听牌结构
		seatData.getCountMap().put(keCard, 3);
		while (removeIndexList.size() > 0)
		{
			seatData.getHolds().add(removeIndexList.remove(removeIndexList.size() - 1), keCard);
		}
		
		return b_flag;
	}
	
	/**
	 * 忽略将牌的胡
	 * @param seatData
	 * @param jiangCard
	 * @param hunMjs
	 * @return
	 */
	public static boolean checkCanHuSkipJiang(MaJiangPlayerData seatData,byte jiangCard,List<Integer> hunMjs)
	{
		boolean hasHu = laiziHuPaiRecursion(seatData.getHolds(),new HashSet<>(),hunMjs,true);
		
		return hasHu;
	}
	
	
	/**
	 * 能否胡牌
	 * @param seatData
	 * @param hunMjs
	 * @return
	 */
	public static boolean checkCanHu(MaJiangPlayerData seatData,List<Integer> hunMjs)
	{
		boolean hasHu = false;
		if(seatData.getHolds().size() == 14)
		{
			hasHu = sevenPairHu(seatData, hunMjs);
		}
		
		if(!hasHu)
		{
			hasHu = laiziHuPaiRecursion(seatData.getHolds(),new HashSet<Integer>(),hunMjs,false);
		}
		
		return hasHu;
	}
	
	
	
	/**
	 * 判断是不是七对胡
	 * @param seatData
	 * @param hunMjs
	 * @return
	 */
	private static boolean sevenPairHu(MaJiangPlayerData seatData,List<Integer> hunMjs)
	{
		int hunNum = 0;
		int danNum = 0;
		for(Map.Entry<Integer, Integer> entity : seatData.getCountMap().entrySet())
		{
			if(entity.getValue() == 0)
			{
				continue;
			}
			
			if(hunMjs != null && hunMjs.size() > 0 &&  hunMjs.indexOf(entity.getKey()) >= 0 )
			{
				hunNum += entity.getValue();
			}
			else
			{
				if(entity.getValue() % 2 == 1)
				{
					danNum ++;
				}
			}
		}
		
		return hunNum >= danNum;
	}
	
	/*
	 * 赖子算法，传入的数组需为3n+2
	 */
	private static  boolean laiziHuPaiRecursion(List<Integer> list, Set<Integer> checkSet, List<Integer> laiziList, boolean skipJian )
	{
		//普通胡法
		int i,j,k;
		int i_max,i_min,i_mid;
		int i_mark1,i_mark2;
		int i_have3 = 0,laizi_num = 0;
		
		for(i = 0;i < list.size(); i++ )
		{
			if( checkSet.contains(i))
			{
				continue;
			}
			else
			{
				i_mark1 = i;
			}
			
			for( j = i + 1; j < list.size();j++)
			{
				if( i == j)
				{
					continue;
				}
				else if( checkSet.contains(j) )
				{
					continue;
				}
				else
				{
					i_mark2 = j;
				}
				
				for( k = j + 1; k < list.size();k++)
				{
					if(i == j || j == k)
					{
						continue;
					}
					else if( checkSet.contains(k) )
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
					
					i_mid = (byte)((i_max + i_min)/2);
					
					
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
							//风牌，花牌，不能成顺子
							if((list.get(j) >> 4) > 2  || (list.get(k) >> 4) > 2)
							{
								continue;
							}
							
							int dis = (int)(list.get(j) - list.get(k));
							if(Math.abs(dis) >= 2)
							{
								continue;
							}
						}
						else if( laiziList.indexOf(list.get(j)) >= 0 )
						{
							//风牌，花牌，不能成顺子
							if((list.get(i) >> 4) > 2  || (list.get(k) >> 4) > 2)
							{
								continue;
							}
							
							int dis = (int)(list.get(i) - list.get(k));
							if(Math.abs(dis) >= 2)
							{
								continue;
							}
						}
						else if(laiziList.indexOf(list.get(k)) >= 0)
						{
							//风牌，花牌，不能成顺子
							if((list.get(i) >> 4) > 2  || (list.get(j) >> 4) > 2)
							{
								continue;
							}
							
							int dis = (int)(list.get(i) - list.get(j));
							if(Math.abs(dis) >= 2)
							{
								continue;
							}
						}
					}
					else if( i_max - i_min == 2 && ( list.get(i) == i_mid || list.get(j) == i_mid || list.get(k) == i_mid ) )
					{
						//风牌，花牌，不能成顺子
						if(( list.get(i) >> 4) > 2 ||  (list.get(j) >> 4) > 2  || (list.get(k) >> 4) > 2  )
						{
							continue;
						}
						//表示能组成顺子
						//do something
					}
					else
					{
						continue;
					}


					checkSet.add(i);
					checkSet.add(j);
					checkSet.add(k);

					if(!laiziHuPaiRecursion(list,checkSet, laiziList,skipJian))
					{
						checkSet.remove(i);
						checkSet.remove(j);
						checkSet.remove(k);
						continue;
					}
					
					return true;
				}
				
				//忽略掉将，则直接判断可胡
				if(skipJian)
				{
					return true;
				}
				//只剩两张牌, 相等 或者 其中一张为癞子时，满足牌型  
				if(i_have3 == 0 && list.get(i_mark1) == list.get(i_mark2)  || laiziList.indexOf(list.get(i_mark1)) >= 0 || laiziList.indexOf(list.get(i_mark2)) >= 0)
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
