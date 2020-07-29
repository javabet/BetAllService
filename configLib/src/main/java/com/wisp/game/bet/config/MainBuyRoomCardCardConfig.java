package com.wisp.game.bet.config;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public final class MainBuyRoomCardCardConfig {

    private static MainBuyRoomCardCardConfig Instance;
    public static MainBuyRoomCardCardConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new MainBuyRoomCardCardConfig();
        }

        return Instance;
    }

    private Map<Integer,MainBuyRoomCardCardConfigData> mMapData;

    public MainBuyRoomCardCardConfig() {
        this.mMapData = new HashMap<Integer, MainBuyRoomCardCardConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public MainBuyRoomCardCardConfigData GetData(int id)
    {
        return this.mMapData.get(id);
    }

    public Map<Integer,MainBuyRoomCardCardConfigData> GetMapData()
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
        this.Load("Config/MainBuyRoomCardConfig.xml");
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
            MainBuyRoomCardCardConfigData data = new MainBuyRoomCardCardConfigData();
            data.mGold = Integer.valueOf( childElement.attribute("Gold").getValue() ) ;
            data.mId = Integer.valueOf(childElement.attribute("Id").getValue());

            if( mMapData.containsKey(data.mId) )
            {
                System.out.printf("data refind:" + data.mId);
                continue;
            }
            mMapData.put(data.mId,data);
        }

    }

    public class MainBuyRoomCardCardConfigData
    {
        private int mId;
        private int mGold;
        private int mRoomCard;

        public int getmId() {
            return mId;
        }

        public void setmId(int mId) {
            this.mId = mId;
        }

        public int getmGold() {
            return mGold;
        }

        public void setmGold(int mGold) {
            this.mGold = mGold;
        }

        public int getmRoomCard() {
            return mRoomCard;
        }

        public void setmRoomCard(int mRoomCard) {
            this.mRoomCard = mRoomCard;
        }
    }
}
