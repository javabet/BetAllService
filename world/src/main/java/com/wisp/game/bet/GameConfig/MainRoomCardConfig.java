package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class MainRoomCardConfig {

    private static MainRoomCardConfig Instance;
    public static MainRoomCardConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new MainRoomCardConfig();
        }

        return Instance;
    }

    private Map<Integer,MainRoomCardConfigData> mMapData;

    public MainRoomCardConfig() {
        this.mMapData = new HashMap<Integer, MainRoomCardConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public MainRoomCardConfigData GetData(int mKey)
    {
        return this.mMapData.get(mKey);
    }

    public Map<Integer,MainRoomCardConfigData> GetMapData()
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
        return this.Load("./Config/MainRoomCardConfig.xml");
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
            MainRoomCardConfigData data = new MainRoomCardConfigData();
            
           data.mKey = XMLUtils.getIntByElement(childElement,"Key");
           data.mName = XMLUtils.getStringByElement(childElement,"Name");
            {
               data.mBaseGold = new ArrayList<>();
               Attribute attr =  childElement.attribute("BaseGold");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] BaseGoldStr = eleStr.split(",");
                       for(int i = 0; i < BaseGoldStr.length;i++)
                       {
                           data.mBaseGold.add( Integer.valueOf(BaseGoldStr[i]) );
                       }
                   }
               }
            }
            {
               data.mGoldCondition = new ArrayList<>();
               Attribute attr =  childElement.attribute("GoldCondition");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] GoldConditionStr = eleStr.split(",");
                       for(int i = 0; i < GoldConditionStr.length;i++)
                       {
                           data.mGoldCondition.add( Integer.valueOf(GoldConditionStr[i]) );
                       }
                   }
               }
            }
            {
               data.mPlayerCount = new ArrayList<>();
               Attribute attr =  childElement.attribute("PlayerCount");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] PlayerCountStr = eleStr.split(",");
                       for(int i = 0; i < PlayerCountStr.length;i++)
                       {
                           data.mPlayerCount.add( Integer.valueOf(PlayerCountStr[i]) );
                       }
                   }
               }
            }
            {
               data.mSmallBlind = new ArrayList<>();
               Attribute attr =  childElement.attribute("SmallBlind");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] SmallBlindStr = eleStr.split(",");
                       for(int i = 0; i < SmallBlindStr.length;i++)
                       {
                           data.mSmallBlind.add( Integer.valueOf(SmallBlindStr[i]) );
                       }
                   }
               }
            }
            {
               data.mBigBlind = new ArrayList<>();
               Attribute attr =  childElement.attribute("BigBlind");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] BigBlindStr = eleStr.split(",");
                       for(int i = 0; i < BigBlindStr.length;i++)
                       {
                           data.mBigBlind.add( Integer.valueOf(BigBlindStr[i]) );
                       }
                   }
               }
            }
            {
               data.mHuaGold = new ArrayList<>();
               Attribute attr =  childElement.attribute("HuaGold");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] HuaGoldStr = eleStr.split(",");
                       for(int i = 0; i < HuaGoldStr.length;i++)
                       {
                           data.mHuaGold.add( Integer.valueOf(HuaGoldStr[i]) );
                       }
                   }
               }
            }
            {
               data.mDuration = new ArrayList<>();
               Attribute attr =  childElement.attribute("Duration");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] DurationStr = eleStr.split(",");
                       for(int i = 0; i < DurationStr.length;i++)
                       {
                           data.mDuration.add( Integer.valueOf(DurationStr[i]) );
                       }
                   }
               }
            }
            {
               data.mModel = new ArrayList<>();
               Attribute attr =  childElement.attribute("Model");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] ModelStr = eleStr.split(",");
                       for(int i = 0; i < ModelStr.length;i++)
                       {
                           data.mModel.add( Integer.valueOf(ModelStr[i]) );
                       }
                   }
               }
            }
            {
               data.mType = new ArrayList<>();
               Attribute attr =  childElement.attribute("Type");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] TypeStr = eleStr.split(",");
                       for(int i = 0; i < TypeStr.length;i++)
                       {
                           data.mType.add( Integer.valueOf(TypeStr[i]) );
                       }
                   }
               }
            }
            {
               data.mRateLimit = new ArrayList<>();
               Attribute attr =  childElement.attribute("RateLimit");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] RateLimitStr = eleStr.split(",");
                       for(int i = 0; i < RateLimitStr.length;i++)
                       {
                           data.mRateLimit.add( Integer.valueOf(RateLimitStr[i]) );
                       }
                   }
               }
            }
            {
               data.mRounds = new ArrayList<>();
               Attribute attr =  childElement.attribute("Rounds");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] RoundsStr = eleStr.split(",");
                       for(int i = 0; i < RoundsStr.length;i++)
                       {
                           data.mRounds.add( Integer.valueOf(RoundsStr[i]) );
                       }
                   }
               }
            }
            {
               data.mCostCount = new ArrayList<>();
               Attribute attr =  childElement.attribute("CostCount");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] CostCountStr = eleStr.split(",");
                       for(int i = 0; i < CostCountStr.length;i++)
                       {
                           data.mCostCount.add( Integer.valueOf(CostCountStr[i]) );
                       }
                   }
               }
            }
           data.mRoomType = XMLUtils.getIntByElement(childElement,"RoomType");


            if( mMapData.containsKey(data.mKey) )
            {
                System.out.printf("data refind:" + data.mKey);
                continue;
            }
            mMapData.put(data.mKey,data);
        }

        return true;
    }

    public class MainRoomCardConfigData
    {
       
        private int mKey; //游戏ID

        private String mName; //游戏名

        private List<Integer> mBaseGold; //底分

        private List<Integer> mGoldCondition; //入场条件

        private List<Integer> mPlayerCount; //人数

        private List<Integer> mSmallBlind; //小盲/二麻借用下

        private List<Integer> mBigBlind; //大盲

        private List<Integer> mHuaGold; //花分

        private List<Integer> mDuration; //倒计时

        private List<Integer> mModel; //模式

        private List<Integer> mType; //玩法

        private List<Integer> mRateLimit; //封顶

        private List<Integer> mRounds; //局数

        private List<Integer> mCostCount; //开房间消耗数量

        private int mRoomType; //房卡游戏类型


        public int getmKey() {
            return mKey;
        }
        
        public void setmKey(int mKey) {
            this.mKey = mKey;
        }
        
        public String getmName() {
            return mName;
        }
        
        public void setmName(String mName) {
            this.mName = mName;
        }
        
        public List<Integer> getmBaseGold() {
            return mBaseGold;
        }
        
        public void setmBaseGold(List<Integer> mBaseGold) {
            this.mBaseGold = mBaseGold;
        }
        
        public List<Integer> getmGoldCondition() {
            return mGoldCondition;
        }
        
        public void setmGoldCondition(List<Integer> mGoldCondition) {
            this.mGoldCondition = mGoldCondition;
        }
        
        public List<Integer> getmPlayerCount() {
            return mPlayerCount;
        }
        
        public void setmPlayerCount(List<Integer> mPlayerCount) {
            this.mPlayerCount = mPlayerCount;
        }
        
        public List<Integer> getmSmallBlind() {
            return mSmallBlind;
        }
        
        public void setmSmallBlind(List<Integer> mSmallBlind) {
            this.mSmallBlind = mSmallBlind;
        }
        
        public List<Integer> getmBigBlind() {
            return mBigBlind;
        }
        
        public void setmBigBlind(List<Integer> mBigBlind) {
            this.mBigBlind = mBigBlind;
        }
        
        public List<Integer> getmHuaGold() {
            return mHuaGold;
        }
        
        public void setmHuaGold(List<Integer> mHuaGold) {
            this.mHuaGold = mHuaGold;
        }
        
        public List<Integer> getmDuration() {
            return mDuration;
        }
        
        public void setmDuration(List<Integer> mDuration) {
            this.mDuration = mDuration;
        }
        
        public List<Integer> getmModel() {
            return mModel;
        }
        
        public void setmModel(List<Integer> mModel) {
            this.mModel = mModel;
        }
        
        public List<Integer> getmType() {
            return mType;
        }
        
        public void setmType(List<Integer> mType) {
            this.mType = mType;
        }
        
        public List<Integer> getmRateLimit() {
            return mRateLimit;
        }
        
        public void setmRateLimit(List<Integer> mRateLimit) {
            this.mRateLimit = mRateLimit;
        }
        
        public List<Integer> getmRounds() {
            return mRounds;
        }
        
        public void setmRounds(List<Integer> mRounds) {
            this.mRounds = mRounds;
        }
        
        public List<Integer> getmCostCount() {
            return mCostCount;
        }
        
        public void setmCostCount(List<Integer> mCostCount) {
            this.mCostCount = mCostCount;
        }
        
        public int getmRoomType() {
            return mRoomType;
        }
        
        public void setmRoomType(int mRoomType) {
            this.mRoomType = mRoomType;
        }
        

    }
}
