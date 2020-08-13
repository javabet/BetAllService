package com.wisp.game.bet.games.share.config;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Attribute;
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
public class RMConfig {

    public static RMConfig Instance;
    private Map<Integer, RMConfigData> mMapData;

    public RMConfig() {
        this.mMapData = new HashMap<Integer, RMConfigData>();
        Instance = this;
    }

    public int GetCount(){
        return mMapData.size();
    }

    public RMConfigData GetData(int mRoomID)
    {
        return this.mMapData.get(mRoomID);
    }

    public Map<Integer, RMConfigData> GetMapData()
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
        return this.Load("./Config/BaccaratRoomConfig.xml");
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
            RMConfigData data = new RMConfigData();

            data.setmRoomID(XMLUtils.getIntByElement(childElement, "RoomID"));
            data.setmRoomName(XMLUtils.getStringByElement(childElement, "RoomName"));
            data.setmGoldCondition(XMLUtils.getIntByElement(childElement, "GoldCondition"));
            data.setmBetCondition(XMLUtils.getIntByElement(childElement, "BetCondition"));
            data.setmBaseGold(XMLUtils.getIntByElement(childElement, "BaseGold"));
            data.setmVipCondition(XMLUtils.getIntByElement(childElement, "VipCondition"));
            data.setmTableCount(XMLUtils.getIntByElement(childElement, "TableCount"));
            data.setmIsOpen(XMLUtils.getBooleanByElement(childElement, "IsOpen"));
            {
                data.setmWeightList(new ArrayList<>());
                Attribute weightListAtt =  childElement.attribute("WeightList");
                if( weightListAtt != null )
                {
                    String eleStr = weightListAtt.getValue();
                    if (eleStr != null && !eleStr.equals("")) {
                        String[] WeightListStr = eleStr.split(",");
                        for (int i = 0; i < WeightListStr.length; i++) {
                            data.getmWeightList().add(Integer.valueOf(WeightListStr[i]));
                        }
                    }
                }
            }
            data.setmBankerCondition(XMLUtils.getIntByElement(childElement, "BankerCondition"));
            data.setmAutoLeaveBanker(XMLUtils.getIntByElement(childElement, "AutoLeaveBanker"));
            data.setmLeaveBankerCost(XMLUtils.getIntByElement(childElement, "LeaveBankerCost"));
            data.setmFirstBankerCost(XMLUtils.getIntByElement(childElement, "FirstBankerCost"));
            data.setmAddBankerCost(XMLUtils.getIntByElement(childElement, "AddBankerCost"));
            {
                data.setmBetLimit(new ArrayList<>());
                Attribute attribute = childElement.attribute("BetLimit");
                if( attribute != null )
                {
                    String eleStr = attribute.getValue();
                    if (eleStr != null && !eleStr.equals("")) {
                        String[] BetLimitStr = eleStr.split(",");
                        for (int i = 0; i < BetLimitStr.length; i++) {
                            data.getmBetLimit().add(Integer.valueOf(BetLimitStr[i]));
                        }
                    }
                }
            }
            {
                data.setmBetRange(new ArrayList<>());
                Attribute attribute = childElement.attribute("BetRange");
                if( attribute != null )
                {
                    String eleStr = attribute.getValue();
                    if (eleStr != null && !eleStr.equals("")) {
                        String[] BetRangeStr = eleStr.split(",");
                        for (int i = 0; i < BetRangeStr.length; i++) {
                            data.getmBetRange().add(Integer.valueOf(BetRangeStr[i]));
                        }
                    }
                }
            }
            {
                data.setmCustomList(new ArrayList<>());
                Attribute attribute = childElement.attribute("CustomList");
                if( attribute != null )
                {
                    String eleStr = attribute.getValue();
                    if (eleStr != null && !eleStr.equals("")) {
                        String[] CustomListStr = eleStr.split(",");
                        for (int i = 0; i < CustomListStr.length; i++) {
                            data.getmCustomList().add(Integer.valueOf(CustomListStr[i]));
                        }
                    }
                }
            }
            {
                data.setmPlatList(new ArrayList<>());
                Attribute attribute = childElement.attribute("PlatList");
                if(  attribute != null)
                {
                    String eleStr = attribute.getValue();
                    if (eleStr != null && !eleStr.equals("")) {
                        String[] PlatListStr = eleStr.split(",");
                        for (int i = 0; i < PlatListStr.length; i++) {
                            data.getmPlatList().add(Integer.valueOf(PlatListStr[i]));
                        }
                    }
                }

            }
            data.setmFreeGold(XMLUtils.getIntByElement(childElement, "FreeGold"));
            {
                data.setmBetNames(new ArrayList<>());
                Attribute attr =  childElement.attribute("BetNames");
                if(attr != null)
                {
                    String eleStr =  attr.getValue();
                    if( eleStr != null && !eleStr.equals("") )
                    {
                        String[] BetNamesStr = eleStr.split(",");
                        for(int i = 0; i < BetNamesStr.length;i++)
                        {
                            data.getmBetNames().add( BetNamesStr[i] );
                        }
                    }
                }
            }
            data.setmPlayerMaxCount(XMLUtils.getIntByElement(childElement, "PlayerMaxCount"));
            data.setmCarryRestriction(XMLUtils.getIntByElement(childElement, "CarryRestriction"));
            data.setmSmallBlind( XMLUtils.getIntByElement(childElement, "SmallBlind"));
            data.setmBigBlind( XMLUtils.getIntByElement(childElement, "BigBlind"));
            data.setmRoomIDTxt(XMLUtils.getStringByElement(childElement, "RoomIDTxt")) ;
            data.setmForceLeaveGold(XMLUtils.getIntByElement(childElement, "ForceLeaveGold"));
            data.setmBotMinGold(XMLUtils.getIntByElement(childElement, "BotMinGold"));
            data.setmBotMaxGold(XMLUtils.getIntByElement(childElement, "BotMaxGold")) ;
            data.setmRoomType(XMLUtils.getIntByElement(childElement, "RoomType"));
            data.setmMaxAnte(XMLUtils.getIntByElement(childElement, "MaxAnte")) ;
            data.setmLevelCondition(XMLUtils.getIntByElement(childElement, "LevelCondition"));
            data.setmPowerParam(XMLUtils.getIntByElement(childElement, "PowerParam"));
            data.setmBuyPowerCost( XMLUtils.getIntByElement(childElement, "BuyPowerCost"));
            data.setmMissileCost(XMLUtils.getIntByElement(childElement, "MissileCost"));
            data.setmCanGetExp(XMLUtils.getBooleanByElement(childElement, "CanGetExp"));
            data.setmCheckOpenRate(XMLUtils.getBooleanByElement(childElement, "CheckOpenRate"));
            data.setmFreeItem( XMLUtils.getBooleanByElement(childElement, "FreeItem"));
            data.setmEveryLeopardLimit(XMLUtils.getIntByElement(childElement, "EveryLeopardLimit")) ;
            data.setmLeopardLimit( XMLUtils.getIntByElement(childElement, "LeopardLimit"));
            {
                data.setmOdds(new ArrayList<>());
                Attribute attribute = childElement.attribute("Odds");
                if( attribute != null )
                {
                    String eleStr = attribute.getValue();
                    if (eleStr != null && !eleStr.equals("")) {
                        String[] OddsStr = eleStr.split(",");
                        for (int i = 0; i < OddsStr.length; i++) {
                            data.getmOdds().add(Integer.valueOf(OddsStr[i]));
                        }
                    }
                }

            }
            data.setmProfitRate(XMLUtils.getIntByElement(childElement, "ProfitRate"));
            data.setmProfitRateCheckIntervaltime(XMLUtils.getIntByElement(childElement, "ProfitRateCheckIntervaltime"));
            {
                data.setmRatePoolList(new ArrayList<>());
                Attribute attribute = childElement.attribute("RatePoolList");
                if( attribute != null )
                {
                    String eleStr = attribute.getValue();
                    if (eleStr != null && !eleStr.equals("")) {
                        String[] RatePoolListStr = eleStr.split(",");
                        for (int i = 0; i < RatePoolListStr.length; i++) {
                            data.getmRatePoolList().add(Integer.valueOf(RatePoolListStr[i]));
                        }
                    }
                }
            }
            data.setmRoomNameType(XMLUtils.getIntByElement(childElement, "RoomNameType"));
            data.setmHuaGold(XMLUtils.getIntByElement(childElement, "HuaGold"));
            data.setmGameModel(XMLUtils.getStringByElement(childElement, "GameModel"));
            data.setmRoomParam1(XMLUtils.getIntByElement(childElement, "RoomParam1"));


            if (mMapData.containsKey(data.getmRoomID()))
            {
                System.out.printf("data refind:" + data.getmRoomID());
                continue;
            }
            mMapData.put(data.getmRoomID(), data);
        }

        return true;
    }
}
