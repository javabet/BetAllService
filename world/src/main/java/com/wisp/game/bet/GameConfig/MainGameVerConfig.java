package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;

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

    public void Reload()
    {
        mMapData.clear();
        this.Load();
    }

    public void Load()
    {
        //this.Load("Config/MainGameVerConfig.xml");
        this.Load("D:\\E\\myBet\\service\\CopyService\\world\\src\\main\\resources\\Config\\MainGameVerConfig.xml");
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
            MainGameVerConfigData data = new MainGameVerConfigData();
            
           data.mID = Integer.valueOf(childElement.attribute("ID").getValue());
           data.mGameCnName = childElement.attribute("GameCnName").getValue();
           data.mGameEnName = childElement.attribute("GameEnName").getValue();
           data.mIsOpen = Boolean.valueOf(childElement.attribute("IsOpen").getValue());
           data.mGameVer = Integer.valueOf(childElement.attribute("GameVer").getValue());
           data.mMinVer = Integer.valueOf(childElement.attribute("MinVer").getValue());
            {
               data.mH5GameVer = new ArrayList<>();
               String[] H5GameVerStr = childElement.attribute("H5GameVer").getValue().split(",");
               for(int i = 0; i < H5GameVerStr.length;i++)
               {
                   data.mH5GameVer.add( H5GameVerStr[i] );
               }
            }
           data.mGameType = Integer.valueOf(childElement.attribute("GameType").getValue());


            if( mMapData.containsKey(data.mID) )
            {
                System.out.printf("data refind:" + data.mID);
                continue;
            }
            mMapData.put(data.mID,data);
        }

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
