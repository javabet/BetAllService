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

public final class MainRobotTypeConfig {

    private static MainRobotTypeConfig Instance;
    public static MainRobotTypeConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new MainRobotTypeConfig();
        }

        return Instance;
    }

    private Map<Integer,MainRobotTypeConfigData> mMapData;

    public MainRobotTypeConfig() {
        this.mMapData = new HashMap<Integer, MainRobotTypeConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public MainRobotTypeConfigData GetData(int mID)
    {
        return this.mMapData.get(mID);
    }

    public Map<Integer,MainRobotTypeConfigData> GetMapData()
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
        return this.Load("./Config/MainRobotTypeConfig.xml");
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
            MainRobotTypeConfigData data = new MainRobotTypeConfigData();
            
           data.mID = XMLUtils.getIntByElement(childElement,"ID");
           data.mWeight = XMLUtils.getIntByElement(childElement,"Weight");
            {
               data.mBeginTime = new ArrayList<>();
               Attribute attr =  childElement.attribute("BeginTime");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] BeginTimeStr = eleStr.split(",");
                       for(int i = 0; i < BeginTimeStr.length;i++)
                       {
                           data.mBeginTime.add( Integer.valueOf(BeginTimeStr[i]) );
                       }
                   }
               }
            }
            {
               data.mInterval = new ArrayList<>();
               Attribute attr =  childElement.attribute("Interval");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] IntervalStr = eleStr.split(",");
                       for(int i = 0; i < IntervalStr.length;i++)
                       {
                           data.mInterval.add( Integer.valueOf(IntervalStr[i]) );
                       }
                   }
               }
            }
            {
               data.mBetRate = new ArrayList<>();
               Attribute attr =  childElement.attribute("BetRate");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] BetRateStr = eleStr.split(",");
                       for(int i = 0; i < BetRateStr.length;i++)
                       {
                           data.mBetRate.add( Float.valueOf(BetRateStr[i]) );
                       }
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

    public class MainRobotTypeConfigData
    {
       
        private int mID; //ID

        private int mWeight; //权重

        private List<Integer> mBeginTime; //初始下注时间范围

        private List<Integer> mInterval; //再次下注间隔

        private List<Float> mBetRate; //下注比例

        private int mLeaveGold; //离开金币


        public int getmID() {
            return mID;
        }
        
        public void setmID(int mID) {
            this.mID = mID;
        }
        
        public int getmWeight() {
            return mWeight;
        }
        
        public void setmWeight(int mWeight) {
            this.mWeight = mWeight;
        }
        
        public List<Integer> getmBeginTime() {
            return mBeginTime;
        }
        
        public void setmBeginTime(List<Integer> mBeginTime) {
            this.mBeginTime = mBeginTime;
        }
        
        public List<Integer> getmInterval() {
            return mInterval;
        }
        
        public void setmInterval(List<Integer> mInterval) {
            this.mInterval = mInterval;
        }
        
        public List<Float> getmBetRate() {
            return mBetRate;
        }
        
        public void setmBetRate(List<Float> mBetRate) {
            this.mBetRate = mBetRate;
        }
        
        public int getmLeaveGold() {
            return mLeaveGold;
        }
        
        public void setmLeaveGold(int mLeaveGold) {
            this.mLeaveGold = mLeaveGold;
        }
        

    }
}
