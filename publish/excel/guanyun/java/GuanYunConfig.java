package com.wisp.game.guanyun;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class GuanYunConfig {

    private static GuanYunConfig Instance;
    public static GuanYunConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new GuanYunConfig();
        }

        return Instance;
    }

    private Map<Integer,GuanYunConfigData> mMapData;

    public GuanYunConfig() {
        this.mMapData = new HashMap<Integer, GuanYunConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public GuanYunConfigData GetData(int mType)
    {
        return this.mMapData.get(mType);
    }

    public Map<Integer,GuanYunConfigData> GetMapData()
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
        return this.Load("./Config/GuanYunConfig.xml");
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
            GuanYunConfigData data = new GuanYunConfigData();
            
           data.mType = XMLUtils.getIntByElement(childElement,"Type");
           data.mCount = XMLUtils.getIntByElement(childElement,"Count");
           data.mCost = XMLUtils.getIntByElement(childElement,"Cost");


            if( mMapData.containsKey(data.mType) )
            {
                System.out.printf("data refind:" + data.mType);
                continue;
            }
            mMapData.put(data.mType,data);
        }

        return true;
    }

    public class GuanYunConfigData
    {
       
        private int mType; //房间类型

        private int mCount; //局数

        private int mCost; //花费的房卡


        public int getmType() {
            return mType;
        }
        
        public void setmType(int mType) {
            this.mType = mType;
        }
        
        public int getmCount() {
            return mCount;
        }
        
        public void setmCount(int mCount) {
            this.mCount = mCount;
        }
        
        public int getmCost() {
            return mCost;
        }
        
        public void setmCost(int mCost) {
            this.mCost = mCost;
        }
        

    }
}
