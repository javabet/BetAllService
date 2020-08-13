package com.wisp.game.bet.GameConfig.BaccaratConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.*;


public final class BaccaratBaseConfig {

    private static BaccaratBaseConfig Instance;
    public static BaccaratBaseConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new BaccaratBaseConfig();
        }

        return Instance;
    }

    private Map<String,BaccaratBaseConfigData> mMapData;

    public BaccaratBaseConfig() {
        this.mMapData = new HashMap<String, BaccaratBaseConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public BaccaratBaseConfigData GetData(String mKey)
    {
        return this.mMapData.get(mKey);
    }

    public Map<String,BaccaratBaseConfigData> GetMapData()
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
        this.Load("./Config/BaccaratBaseConfig.xml");
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
            BaccaratBaseConfigData data = new BaccaratBaseConfigData();
            
           data.mKey = XMLUtils.getStringByElement(childElement,"Key");
           data.mValue = XMLUtils.getIntByElement(childElement,"Value");


            if( mMapData.containsKey(data.mKey) )
            {
                System.out.printf("data refind:" + data.mKey);
                continue;
            }
            mMapData.put(data.mKey,data);
        }

    }

    public class BaccaratBaseConfigData
    {
       
        private String mKey; //Key值

        private int mValue; //数值


        public String getmKey() {
            return mKey;
        }
        
        public void setmKey(String mKey) {
            this.mKey = mKey;
        }
        
        public int getmValue() {
            return mValue;
        }
        
        public void setmValue(int mValue) {
            this.mValue = mValue;
        }
        

    }
}
