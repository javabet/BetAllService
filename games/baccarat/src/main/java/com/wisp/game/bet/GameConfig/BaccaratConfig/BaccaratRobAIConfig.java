package com.wisp.game.bet.GameConfig.BaccaratConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class BaccaratRobAIConfig {

    private static BaccaratRobAIConfig Instance;
    public static BaccaratRobAIConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new BaccaratRobAIConfig();
        }

        return Instance;
    }

    private Map<Integer,BaccaratRobAIConfigData> mMapData;

    public BaccaratRobAIConfig() {
        this.mMapData = new HashMap<Integer, BaccaratRobAIConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public BaccaratRobAIConfigData GetData(int mID)
    {
        return this.mMapData.get(mID);
    }

    public Map<Integer,BaccaratRobAIConfigData> GetMapData()
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
        return this.Load("./Config/BaccaratRobAIConfig.xml");
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
            BaccaratRobAIConfigData data = new BaccaratRobAIConfigData();
            
           data.mID = XMLUtils.getIntByElement(childElement,"ID");
           data.mBetName = XMLUtils.getStringByElement(childElement,"BetName");
            {
               data.mBetRate = new ArrayList<>();
               String eleStr =  childElement.attribute("BetRate").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] BetRateStr = eleStr.split(",");
                   for(int i = 0; i < BetRateStr.length;i++)
                   {
                       data.mBetRate.add( Integer.valueOf(BetRateStr[i]) );
                   }
               }
            }


            if( mMapData.containsKey(data.mID) )
            {
                System.out.printf("data refind:" + data.mID);
                continue;
            }
            mMapData.put(data.mID,data);
        }

        return true;
    }

    public class BaccaratRobAIConfigData
    {
       
        private int mID; //Key

        private String mBetName; //下注偏好

        private List<Integer> mBetRate; //下注比例(顺序:和、闲、闲对、庄对、庄)(百分比)


        public int getmID() {
            return mID;
        }
        
        public void setmID(int mID) {
            this.mID = mID;
        }
        
        public String getmBetName() {
            return mBetName;
        }
        
        public void setmBetName(String mBetName) {
            this.mBetName = mBetName;
        }
        
        public List<Integer> getmBetRate() {
            return mBetRate;
        }
        
        public void setmBetRate(List<Integer> mBetRate) {
            this.mBetRate = mBetRate;
        }
        

    }
}
