package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class MainMultiLanguageConfig {

    private static MainMultiLanguageConfig Instance;
    public static MainMultiLanguageConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new MainMultiLanguageConfig();
        }

        return Instance;
    }

    private Map<String,MainMultiLanguageConfigData> mMapData;

    public MainMultiLanguageConfig() {
        this.mMapData = new HashMap<String, MainMultiLanguageConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public MainMultiLanguageConfigData GetData(String mID)
    {
        return this.mMapData.get(mID);
    }

    public Map<String,MainMultiLanguageConfigData> GetMapData()
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
        return this.Load("./Config/MainMultiLanguageConfig.xml");
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
            MainMultiLanguageConfigData data = new MainMultiLanguageConfigData();
            
           data.mID = XMLUtils.getStringByElement(childElement,"ID");
           data.mName = XMLUtils.getStringByElement(childElement,"Name");


            if( mMapData.containsKey(data.mID) )
            {
                System.out.printf("data refind:" + data.mID);
                continue;
            }
            mMapData.put(data.mID,data);
        }

        return true;
    }

    public class MainMultiLanguageConfigData
    {
       
        private String mID; //Key

        private String mName; //中文名字


        public String getmID() {
            return mID;
        }
        
        public void setmID(String mID) {
            this.mID = mID;
        }
        
        public String getmName() {
            return mName;
        }
        
        public void setmName(String mName) {
            this.mName = mName;
        }
        

    }
}
