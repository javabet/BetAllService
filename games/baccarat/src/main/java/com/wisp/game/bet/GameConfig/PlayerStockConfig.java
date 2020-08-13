package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.*;


public final class PlayerStockConfig {

    private static PlayerStockConfig Instance;
    public static PlayerStockConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new PlayerStockConfig();
        }

        return Instance;
    }

    private Map<Integer,PlayerStockConfigData> mMapData;

    public PlayerStockConfig() {
        this.mMapData = new HashMap<Integer, PlayerStockConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public PlayerStockConfigData GetData(int mRecharge)
    {
        return this.mMapData.get(mRecharge);
    }

    public Map<Integer,PlayerStockConfigData> GetMapData()
    {
        return this.mMapData;
    }

    public void Reload()
    {
        mMapData.clear();
        this.Load();
    }

    public void Load()
    {
        this.Load("./Config/PlayerStockConfig.xml");
    }

    public void Load(String path)
    {
        Document xmlDoc = XMLUtils.file2Document(path);

        if( xmlDoc == null )
        {
            return;
        }

        Element root = xmlDoc.getRootElement();

        if( root == null )
        {
            return;
        }

        Iterator<Element> iterator =  root.elementIterator();

        while (iterator.hasNext())
        {
            Element childElement = iterator.next();
            PlayerStockConfigData data = new PlayerStockConfigData();
            
           data.mRecharge = XMLUtils.getIntByElement(childElement,"Recharge");
            {
               data.mPlayerProfit = new ArrayList<>();
               String eleStr =  childElement.attribute("PlayerProfit").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] PlayerProfitStr = eleStr.split(",");
                   for(int i = 0; i < PlayerProfitStr.length;i++)
                   {
                       data.mPlayerProfit.add( Integer.valueOf(PlayerProfitStr[i]) );
                   }
               }
            }
            {
               data.mStrongKill = new ArrayList<>();
               String eleStr =  childElement.attribute("StrongKill").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] StrongKillStr = eleStr.split(",");
                   for(int i = 0; i < StrongKillStr.length;i++)
                   {
                       data.mStrongKill.add( Integer.valueOf(StrongKillStr[i]) );
                   }
               }
            }
            {
               data.mWeakKill = new ArrayList<>();
               String eleStr =  childElement.attribute("WeakKill").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] WeakKillStr = eleStr.split(",");
                   for(int i = 0; i < WeakKillStr.length;i++)
                   {
                       data.mWeakKill.add( Integer.valueOf(WeakKillStr[i]) );
                   }
               }
            }
           data.mMinRelushTime = XMLUtils.getIntByElement(childElement,"MinRelushTime");
           data.mMaxRelushTime = XMLUtils.getIntByElement(childElement,"MaxRelushTime");


            if( mMapData.containsKey(data.mRecharge) )
            {
                System.out.printf("data refind:" + data.mRecharge);
                continue;
            }
            mMapData.put(data.mRecharge,data);
        }

    }

    public class PlayerStockConfigData
    {
       
        private int mRecharge; //充值(充值额挨得最近的取其值,单位元)

        private List<Integer> mPlayerProfit; //盈利（盈利在某个阶段对于不同收放分，单位分）

        private List<Integer> mStrongKill; //强杀概率

        private List<Integer> mWeakKill; //弱杀概率

        private int mMinRelushTime; //最短刷新时间(分钟)

        private int mMaxRelushTime; //最大刷新时间


        public int getmRecharge() {
            return mRecharge;
        }
        
        public void setmRecharge(int mRecharge) {
            this.mRecharge = mRecharge;
        }
        
        public List<Integer> getmPlayerProfit() {
            return mPlayerProfit;
        }
        
        public void setmPlayerProfit(List<Integer> mPlayerProfit) {
            this.mPlayerProfit = mPlayerProfit;
        }
        
        public List<Integer> getmStrongKill() {
            return mStrongKill;
        }
        
        public void setmStrongKill(List<Integer> mStrongKill) {
            this.mStrongKill = mStrongKill;
        }
        
        public List<Integer> getmWeakKill() {
            return mWeakKill;
        }
        
        public void setmWeakKill(List<Integer> mWeakKill) {
            this.mWeakKill = mWeakKill;
        }
        
        public int getmMinRelushTime() {
            return mMinRelushTime;
        }
        
        public void setmMinRelushTime(int mMinRelushTime) {
            this.mMinRelushTime = mMinRelushTime;
        }
        
        public int getmMaxRelushTime() {
            return mMaxRelushTime;
        }
        
        public void setmMaxRelushTime(int mMaxRelushTime) {
            this.mMaxRelushTime = mMaxRelushTime;
        }
        

    }
}
