package com.wisp.game.bet.games.share.config;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class RMStockConfig {
    public static RMStockConfig Instance;
    private Map<Integer, RMStockConfigData> mMapData;

    public RMStockConfig() {
        Instance = this;
        this.mMapData = new HashMap<Integer,RMStockConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public RMStockConfigData GetData(int mRoomID)
    {
        return this.mMapData.get(mRoomID);
    }

    public Map<Integer, RMStockConfigData> GetMapData()
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
        return this.Load("./Config/BaccaratStockConfig.xml");
    }

    public boolean Load(String path) {
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

        Element root = xmlDoc.getRootElement();

        if (root == null) {
            return false;
        }

        Iterator<Element> iterator = root.elementIterator();

        while (iterator.hasNext()) {
            Element childElement = iterator.next();
            RMStockConfigData data = new RMStockConfigData();

            data.setmRoomID(XMLUtils.getIntByElement(childElement, "RoomID"));
            data.setmRoomName(XMLUtils.getStringByElement(childElement, "RoomName"));
            data.setmBaseStock(XMLUtils.getIntByElement(childElement, "BaseStock"));
            data.setmInitStock(XMLUtils.getIntByElement(childElement, "InitStock"));
            data.setmStockRate(XMLUtils.getIntByElement(childElement, "StockRate"));
            {
                data.setmStockStage(new ArrayList<>());
                String eleStr = childElement.attribute("StockStage").getValue();
                if (eleStr != null && !eleStr.equals("")) {
                    String[] StockStageStr = eleStr.split(",");
                    for (int i = 0; i < StockStageStr.length; i++) {
                        data.getmStockStage().add(Float.valueOf(StockStageStr[i]));
                    }
                }
            }
            {
                data.setmStockBuff(new ArrayList<>());
                String eleStr = childElement.attribute("StockBuff").getValue();
                if (eleStr != null && !eleStr.equals("")) {
                    String[] StockBuffStr = eleStr.split(",");
                    for (int i = 0; i < StockBuffStr.length; i++) {
                        data.getmStockBuff().add(Integer.valueOf(StockBuffStr[i]));
                    }
                }
            }
            data.setmBrightWater(XMLUtils.getIntByElement(childElement, "BrightWater"));
            data.setmMaxStock(XMLUtils.getIntByElement(childElement, "MaxStock"));
            data.setmRelushTime(XMLUtils.getIntByElement(childElement, "RelushTime"));
            {
                data.setmStrongKill(new ArrayList<>());
                String eleStr = childElement.attribute("StrongKill").getValue();
                if (eleStr != null && !eleStr.equals("")) {
                    String[] StrongKillStr = eleStr.split(",");
                    for (int i = 0; i < StrongKillStr.length; i++) {
                        data.getmStrongKill().add(Integer.valueOf(StrongKillStr[i]));
                    }
                }
            }
            {
                data.setmWeakKill(new ArrayList<>());
                String eleStr = childElement.attribute("WeakKill").getValue();
                if (eleStr != null && !eleStr.equals("")) {
                    String[] WeakKillStr = eleStr.split(",");
                    for (int i = 0; i < WeakKillStr.length; i++) {
                        data.getmWeakKill().add(Integer.valueOf(WeakKillStr[i]));
                    }
                }
            }


            if (mMapData.containsKey(data.getmRoomID())) {
                System.out.printf("data refind:" + data.getmRoomID());
                continue;
            }
            mMapData.put(data.getmRoomID(), data);
        }

        return true;
    }
}
