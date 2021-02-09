package com.wisp.game.bet.games.guanyun.config;

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

public final class GuanYunConfig {

	private static Logger logger = LoggerFactory.getLogger("XmlConfig");

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

    public GuanYunConfigData GetData(int mKey)
    {
        return this.mMapData.get(mKey);
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
            GuanYunConfigData data = new GuanYunConfigData();
            
           data.mKey = XMLUtils.getIntByElement(childElement,"Key");
           data.mType = XMLUtils.getIntByElement(childElement,"Type");
            {
               data.mCount = new ArrayList<>();
               Attribute attr =  childElement.attribute("Count");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] CountStr = eleStr.split(",");
                       for(int i = 0; i < CountStr.length;i++)
                       {
                           data.mCount.add( Integer.valueOf(CountStr[i]) );
                       }
                   }
               }
            }
            {
               data.mCost = new ArrayList<>();
               Attribute attr =  childElement.attribute("Cost");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] CostStr = eleStr.split(",");
                       for(int i = 0; i < CostStr.length;i++)
                       {
                           data.mCost.add( Integer.valueOf(CostStr[i]) );
                       }
                   }
               }
            }


            if( mMapData.containsKey(data.mKey) )
            {
                logger.error("data refind:" + data.mKey);
                continue;
            }
            mMapData.put(data.mKey,data);
        }

        return true;
    }

    public class GuanYunConfigData
    {
       
        private int mKey; //键值

        private int mType; //房间类型

        private List<Integer> mCount; //局数

        private List<Integer> mCost; //花费的房卡


        public int getmKey() {
            return mKey;
        }
        
        public void setmKey(int mKey) {
            this.mKey = mKey;
        }
        
        public int getmType() {
            return mType;
        }
        
        public void setmType(int mType) {
            this.mType = mType;
        }
        
        public List<Integer> getmCount() {
            return mCount;
        }
        
        public void setmCount(List<Integer> mCount) {
            this.mCount = mCount;
        }
        
        public List<Integer> getmCost() {
            return mCost;
        }
        
        public void setmCost(List<Integer> mCost) {
            this.mCost = mCost;
        }
        

    }
}
