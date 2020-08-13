package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;

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

    public void Reload()
    {
        mMapData.clear();
        this.Load();
    }

    public void Load()
    {
        this.Load("./Config/MainRobotNameConfig.xml");
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
