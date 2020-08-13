package com.wisp.game.bet.GameConfig.BaccaratConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class BaccaratBetMaxConfig {

    private static BaccaratBetMaxConfig Instance;
    public static BaccaratBetMaxConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new BaccaratBetMaxConfig();
        }

        return Instance;
    }

    private Map<Integer,BaccaratBetMaxConfigData> mMapData;

    public BaccaratBetMaxConfig() {
        this.mMapData = new HashMap<Integer, BaccaratBetMaxConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public BaccaratBetMaxConfigData GetData(int mID)
    {
        return this.mMapData.get(mID);
    }

    public Map<Integer,BaccaratBetMaxConfigData> GetMapData()
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
        return this.Load("./Config/BaccaratBetMaxConfig.xml");
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
            BaccaratBetMaxConfigData data = new BaccaratBetMaxConfigData();
            
           data.mID = XMLUtils.getIntByElement(childElement,"ID");
           data.mGoldCount = XMLUtils.getIntByElement(childElement,"GoldCount");
           data.mBetMax = XMLUtils.getIntByElement(childElement,"BetMax");
            {
               data.mCanUseWeight = new ArrayList<>();
               String eleStr =  childElement.attribute("CanUseWeight").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] CanUseWeightStr = eleStr.split(",");
                   for(int i = 0; i < CanUseWeightStr.length;i++)
                   {
                       data.mCanUseWeight.add( Integer.valueOf(CanUseWeightStr[i]) );
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

    public class BaccaratBetMaxConfigData
    {
       
        private int mID; //Key

        private int mGoldCount; //金币数量

        private int mBetMax; //单个投注上限

        private List<Integer> mCanUseWeight; //可用筹码序号


        public int getmID() {
            return mID;
        }
        
        public void setmID(int mID) {
            this.mID = mID;
        }
        
        public int getmGoldCount() {
            return mGoldCount;
        }
        
        public void setmGoldCount(int mGoldCount) {
            this.mGoldCount = mGoldCount;
        }
        
        public int getmBetMax() {
            return mBetMax;
        }
        
        public void setmBetMax(int mBetMax) {
            this.mBetMax = mBetMax;
        }
        
        public List<Integer> getmCanUseWeight() {
            return mCanUseWeight;
        }
        
        public void setmCanUseWeight(List<Integer> mCanUseWeight) {
            this.mCanUseWeight = mCanUseWeight;
        }
        

    }
}
