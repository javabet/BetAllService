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

public final class MainRobotNameConfig {

	private static Logger logger = LoggerFactory.getLogger("XmlConfig");

    private static MainRobotNameConfig Instance;
    public static MainRobotNameConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new MainRobotNameConfig();
        }

        return Instance;
    }

    private Map<Integer,MainRobotNameConfigData> mMapData;

    public MainRobotNameConfig() {
        this.mMapData = new HashMap<Integer, MainRobotNameConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public MainRobotNameConfigData GetData(int mIndex)
    {
        return this.mMapData.get(mIndex);
    }

    public Map<Integer,MainRobotNameConfigData> GetMapData()
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
        return this.Load("./Config/MainRobotNameConfig.xml");
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
            MainRobotNameConfigData data = new MainRobotNameConfigData();
            
           data.mIndex = XMLUtils.getIntByElement(childElement,"Index");
           data.mNickName = XMLUtils.getStringByElement(childElement,"NickName");


            if( mMapData.containsKey(data.mIndex) )
            {
                logger.error("data refind:" + data.mIndex);
                continue;
            }
            mMapData.put(data.mIndex,data);
        }

        return true;
    }

    public class MainRobotNameConfigData
    {
       
        private int mIndex; //索引

        private String mNickName; //昵称


        public int getmIndex() {
            return mIndex;
        }
        
        public void setmIndex(int mIndex) {
            this.mIndex = mIndex;
        }
        
        public String getmNickName() {
            return mNickName;
        }
        
        public void setmNickName(String mNickName) {
            this.mNickName = mNickName;
        }
        

    }
}
