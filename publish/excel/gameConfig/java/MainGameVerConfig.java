package com.wisp.game.bet.world.config;

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

public final class MainGameVerConfig {

	private static Logger logger = LoggerFactory.getLogger("XmlConfig");

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
           data.mBindGameId = XMLUtils.getIntByElement(childElement,"BindGameId");
           data.mWalletType = XMLUtils.getIntByElement(childElement,"WalletType");
           data.mGoldCondition = XMLUtils.getIntByElement(childElement,"GoldCondition");


            if( mMapData.containsKey(data.mID) )
            {
                logger.error("data refind:" + data.mID);
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

        private int mBindGameId; //实际上的服务器GameId

        private int mWalletType; //钱包类型 0无 1中心钱包，2转账钱包

        private int mGoldCondition; //API钱包游戏入场条件


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
        
        public int getmBindGameId() {
            return mBindGameId;
        }
        
        public void setmBindGameId(int mBindGameId) {
            this.mBindGameId = mBindGameId;
        }
        
        public int getmWalletType() {
            return mWalletType;
        }
        
        public void setmWalletType(int mWalletType) {
            this.mWalletType = mWalletType;
        }
        
        public int getmGoldCondition() {
            return mGoldCondition;
        }
        
        public void setmGoldCondition(int mGoldCondition) {
            this.mGoldCondition = mGoldCondition;
        }
        

    }
}
