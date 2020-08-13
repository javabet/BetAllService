package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class MainRobotNameConfig {

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
            MainRobotNameConfigData data = new MainRobotNameConfigData();
            
           data.mIndex = XMLUtils.getIntByElement(childElement,"Index");
           data.mNickName = XMLUtils.getStringByElement(childElement,"NickName");


            if( mMapData.containsKey(data.mIndex) )
            {
                System.out.printf("data refind:" + data.mIndex);
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
