package com.wisp.game.bet.logic.config;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class MainMultiLanguageConfig {

	private static Logger logger = LoggerFactory.getLogger("XmlConfig");

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
        InputStream inputStream = null;

        ClassPathResource classPathResource = new ClassPathResource(path);
        try
        {
            if (classPathResource.exists())
            {
                inputStream = classPathResource.getInputStream();
            }
            else
            {
                FileSystemResource fileSystemResource = new FileSystemResource(path);
                inputStream = fileSystemResource.getInputStream();
            }
        }
        catch (IOException ex)
        {
            logger.error("read xml has error %s",path);
            return false;
        }

        if( inputStream == null )
        {
            logger.error("read xml has error {0}",path);
            return false;
        }

        xmlDoc = XMLUtils.file2Document(inputStream);

        if( xmlDoc == null )
        {
            logger.error("read xml has error {0}",path);
            return false;
        }

        Element root = xmlDoc.getRootElement();

        if( root == null )
        {
            logger.error("read xml has error {0}",path);
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
                logger.error("data refind:" + data.mID);
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
