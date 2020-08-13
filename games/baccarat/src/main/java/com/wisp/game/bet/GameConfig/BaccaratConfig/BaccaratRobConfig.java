package com.wisp.game.bet.GameConfig.BaccaratConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class BaccaratRobConfig {

    private static BaccaratRobConfig Instance;
    public static BaccaratRobConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new BaccaratRobConfig();
        }

        return Instance;
    }

    private Map<Integer,BaccaratRobConfigData> mMapData;

    public BaccaratRobConfig() {
        this.mMapData = new HashMap<Integer, BaccaratRobConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public BaccaratRobConfigData GetData(int mID)
    {
        return this.mMapData.get(mID);
    }

    public Map<Integer,BaccaratRobConfigData> GetMapData()
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
        return this.Load("./Config/BaccaratRobConfig.xml");
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
            BaccaratRobConfigData data = new BaccaratRobConfigData();
            
           data.mID = XMLUtils.getIntByElement(childElement,"ID");
           data.mName = XMLUtils.getStringByElement(childElement,"Name");
           data.mGoldMin = XMLUtils.getIntByElement(childElement,"GoldMin");
           data.mGoldMax = XMLUtils.getIntByElement(childElement,"GoldMax");
           data.mVipMin = XMLUtils.getIntByElement(childElement,"VipMin");
           data.mVipMax = XMLUtils.getIntByElement(childElement,"VipMax");


            if( mMapData.containsKey(data.mID) )
            {
                System.out.printf("data refind:" + data.mID);
                continue;
            }
            mMapData.put(data.mID,data);
        }

        return true;
    }

    public class BaccaratRobConfigData
    {
       
        private int mID; //Key

        private String mName; //玩家标签

        private int mGoldMin; //金币最小值

        private int mGoldMax; //金币最大值

        private int mVipMin; //VIP最小值

        private int mVipMax; //VIP最大值


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
        
        public int getmGoldMin() {
            return mGoldMin;
        }
        
        public void setmGoldMin(int mGoldMin) {
            this.mGoldMin = mGoldMin;
        }
        
        public int getmGoldMax() {
            return mGoldMax;
        }
        
        public void setmGoldMax(int mGoldMax) {
            this.mGoldMax = mGoldMax;
        }
        
        public int getmVipMin() {
            return mVipMin;
        }
        
        public void setmVipMin(int mVipMin) {
            this.mVipMin = mVipMin;
        }
        
        public int getmVipMax() {
            return mVipMax;
        }
        
        public void setmVipMax(int mVipMax) {
            this.mVipMax = mVipMax;
        }
        

    }
}
