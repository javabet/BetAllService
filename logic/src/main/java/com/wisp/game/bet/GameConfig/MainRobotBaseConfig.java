package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class MainRobotBaseConfig {

    private static MainRobotBaseConfig Instance;
    public static MainRobotBaseConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new MainRobotBaseConfig();
        }

        return Instance;
    }

    private Map<Integer,MainRobotBaseConfigData> mMapData;

    public MainRobotBaseConfig() {
        this.mMapData = new HashMap<Integer, MainRobotBaseConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public MainRobotBaseConfigData GetData(int mID)
    {
        return this.mMapData.get(mID);
    }

    public Map<Integer,MainRobotBaseConfigData> GetMapData()
    {
        return this.mMapData;
    }

    public boolean Reload()
    {
        mMapData.clear();
        return this.Load();
    }

    public boolean Load()
    {
        return this.Load("./Config/MainRobotBaseConfig.xml");
    }

    public boolean Load(String path)
    {
        Document xmlDoc = null;

        ClassPathResource classPathResource = new ClassPathResource(path);
        if (classPathResource.exists())
        {
            try
            {
                xmlDoc = XMLUtils.file2Document(classPathResource.getInputStream());
            }
            catch (IOException ioexception)
            {
                //do nothing
            }
        }

        if( xmlDoc == null )
        {

        }

        if( xmlDoc == null )
        {
            return false;
        }

        Element root = xmlDoc.getRootElement();

        if( root == null )
        {
            return false;
        }


        Iterator<Element> iterator =  root.elementIterator();

        while (iterator.hasNext())
        {
            Element childElement = iterator.next();
            MainRobotBaseConfigData data = new MainRobotBaseConfigData();
            
           data.mID = XMLUtils.getIntByElement(childElement,"ID");
           data.mName = XMLUtils.getStringByElement(childElement,"Name");
            {
               data.mRobotGold = new ArrayList<>();
               String eleStr =  childElement.attribute("RobotGold").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] RobotGoldStr = eleStr.split(",");
                   for(int i = 0; i < RobotGoldStr.length;i++)
                   {
                       data.mRobotGold.add( Integer.valueOf(RobotGoldStr[i]) );
                   }
               }
            }
            {
               data.mGoldWeight = new ArrayList<>();
               String eleStr =  childElement.attribute("GoldWeight").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] GoldWeightStr = eleStr.split(",");
                   for(int i = 0; i < GoldWeightStr.length;i++)
                   {
                       data.mGoldWeight.add( Integer.valueOf(GoldWeightStr[i]) );
                   }
               }
            }
           data.mLifeTime = XMLUtils.getIntByElement(childElement,"LifeTime");
            {
               data.mBetRobotCount = new ArrayList<>();
               String eleStr =  childElement.attribute("BetRobotCount").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] BetRobotCountStr = eleStr.split(",");
                   for(int i = 0; i < BetRobotCountStr.length;i++)
                   {
                       data.mBetRobotCount.add( Integer.valueOf(BetRobotCountStr[i]) );
                   }
               }
            }
            {
               data.mBankerRobotCount = new ArrayList<>();
               String eleStr =  childElement.attribute("BankerRobotCount").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] BankerRobotCountStr = eleStr.split(",");
                   for(int i = 0; i < BankerRobotCountStr.length;i++)
                   {
                       data.mBankerRobotCount.add( Integer.valueOf(BankerRobotCountStr[i]) );
                   }
               }
            }
            {
               data.mBankerRobotGold = new ArrayList<>();
               String eleStr =  childElement.attribute("BankerRobotGold").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] BankerRobotGoldStr = eleStr.split(",");
                   for(int i = 0; i < BankerRobotGoldStr.length;i++)
                   {
                       data.mBankerRobotGold.add( Integer.valueOf(BankerRobotGoldStr[i]) );
                   }
               }
            }
           data.mLeaveGold = XMLUtils.getIntByElement(childElement,"LeaveGold");


            if( mMapData.containsKey(data.mID) )
            {
                System.out.printf("data refind:" + data.mID);
                continue;
            }
            mMapData.put(data.mID,data);
        }

        return true;
    }

    public class MainRobotBaseConfigData
    {
       
        private int mID; //tagID 游戏ID

        private String mName; //游戏名字

        private List<Integer> mRobotGold; //机器人金币

        private List<Integer> mGoldWeight; //权重

        private int mLifeTime; //生存时间

        private List<Integer> mBetRobotCount; //下注机器人人数

        private List<Integer> mBankerRobotCount; //上庄机器人人数

        private List<Integer> mBankerRobotGold; //上庄机器人金币

        private int mLeaveGold; //机器人离开金币


        public int getmID() {
            return mID;
        }
        
        public void setmID(int mID) {
            this.mID = mID;
        }
        
        public String getmName() {
            return mName;
        }
        
        public void setmName(String mName) {
            this.mName = mName;
        }
        
        public List<Integer> getmRobotGold() {
            return mRobotGold;
        }
        
        public void setmRobotGold(List<Integer> mRobotGold) {
            this.mRobotGold = mRobotGold;
        }
        
        public List<Integer> getmGoldWeight() {
            return mGoldWeight;
        }
        
        public void setmGoldWeight(List<Integer> mGoldWeight) {
            this.mGoldWeight = mGoldWeight;
        }
        
        public int getmLifeTime() {
            return mLifeTime;
        }
        
        public void setmLifeTime(int mLifeTime) {
            this.mLifeTime = mLifeTime;
        }
        
        public List<Integer> getmBetRobotCount() {
            return mBetRobotCount;
        }
        
        public void setmBetRobotCount(List<Integer> mBetRobotCount) {
            this.mBetRobotCount = mBetRobotCount;
        }
        
        public List<Integer> getmBankerRobotCount() {
            return mBankerRobotCount;
        }
        
        public void setmBankerRobotCount(List<Integer> mBankerRobotCount) {
            this.mBankerRobotCount = mBankerRobotCount;
        }
        
        public List<Integer> getmBankerRobotGold() {
            return mBankerRobotGold;
        }
        
        public void setmBankerRobotGold(List<Integer> mBankerRobotGold) {
            this.mBankerRobotGold = mBankerRobotGold;
        }
        
        public int getmLeaveGold() {
            return mLeaveGold;
        }
        
        public void setmLeaveGold(int mLeaveGold) {
            this.mLeaveGold = mLeaveGold;
        }
        

    }
}