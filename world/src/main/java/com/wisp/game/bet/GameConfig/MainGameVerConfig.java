package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.share.utils.MD5Util;
import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

public final class MainGameVerConfig {

    private static MainGameVerConfig Instance;
    public static MainGameVerConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new MainGameVerConfig();
        }

        return Instance;
    }

    private Map<Integer,MainGameVerConfigData> mMapData;

    public MainGameVerConfig() {
        this.mMapData = new HashMap<Integer, MainGameVerConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public MainGameVerConfigData GetData(int mID)
    {
        return this.mMapData.get(mID);
    }

    public Map<Integer,MainGameVerConfigData> GetMapData()
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
        return this.Load("./Config/MainGameVerConfig.xml");
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
            MainGameVerConfigData data = new MainGameVerConfigData();
            
           data.mID = XMLUtils.getIntByElement(childElement,"ID");
           data.mGameCnName = XMLUtils.getStringByElement(childElement,"GameCnName");
           data.mGameEnName = XMLUtils.getStringByElement(childElement,"GameEnName");
           data.mIsOpen = XMLUtils.getBooleanByElement(childElement,"IsOpen");
           data.mGameVer = XMLUtils.getIntByElement(childElement,"GameVer");
           data.mMinVer = XMLUtils.getIntByElement(childElement,"MinVer");
            {
               data.mH5GameVer = new ArrayList<>();
               Attribute attr =  childElement.attribute("H5GameVer");
               if(attr != null)
               {
                   String eleStr =  attr.getValue();
                   if( eleStr != null && !eleStr.equals("") )
                   {
                       String[] H5GameVerStr = eleStr.split(",");
                       for(int i = 0; i < H5GameVerStr.length;i++)
                       {
                           data.mH5GameVer.add( H5GameVerStr[i] );
                       }
                   }
               }
            }
           data.mGameType = XMLUtils.getIntByElement(childElement,"GameType");


            if( mMapData.containsKey(data.mID) )
            {
                System.out.printf("data refind:" + data.mID);
                continue;
            }
            mMapData.put(data.mID,data);
        }

        return true;
    }

    public class MainGameVerConfigData
    {
       
        private int mID; //key

        private String mGameCnName; //游戏名字

        private String mGameEnName; //游戏路径

        private boolean mIsOpen; //是否开放

        private int mGameVer; //游戏版本

        private int mMinVer; //最低版本

        private List<String> mH5GameVer; //游戏版本

        private int mGameType; //游戏类型:1自研 2API


        public int getmID() {
            return mID;
        }
        
        public void setmID(int mID) {
            this.mID = mID;
        }
        
        public String getmGameCnName() {
            return mGameCnName;
        }
        
        public void setmGameCnName(String mGameCnName) {
            this.mGameCnName = mGameCnName;
        }
        
        public String getmGameEnName() {
            return mGameEnName;
        }
        
        public void setmGameEnName(String mGameEnName) {
            this.mGameEnName = mGameEnName;
        }
        
        public boolean getmIsOpen() {
            return mIsOpen;
        }
        
        public void setmIsOpen(boolean mIsOpen) {
            this.mIsOpen = mIsOpen;
        }
        
        public int getmGameVer() {
            return mGameVer;
        }
        
        public void setmGameVer(int mGameVer) {
            this.mGameVer = mGameVer;
        }
        
        public int getmMinVer() {
            return mMinVer;
        }
        
        public void setmMinVer(int mMinVer) {
            this.mMinVer = mMinVer;
        }
        
        public List<String> getmH5GameVer() {
            return mH5GameVer;
        }
        
        public void setmH5GameVer(List<String> mH5GameVer) {
            this.mH5GameVer = mH5GameVer;
        }
        
        public int getmGameType() {
            return mGameType;
        }
        
        public void setmGameType(int mGameType) {
            this.mGameType = mGameType;
        }
        

    }
}
