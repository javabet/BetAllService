package com.wisp.game.bet.GameConfig.BaccaratConfig;

import com.wisp.game.bet.utils.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.*;


public final class BaccaratProbConfig {

    private static BaccaratProbConfig Instance;
    public static BaccaratProbConfig GetInstnace()
    {
        if( Instance == null )
        {
            Instance = new BaccaratProbConfig();
        }

        return Instance;
    }

    private Map<Integer,BaccaratProbConfigData> mMapData;

    public BaccaratProbConfig() {
        this.mMapData = new HashMap<Integer, BaccaratProbConfigData>();
    }

    public int GetCount(){
        return mMapData.size();
    }

    public BaccaratProbConfigData GetData(int mkey)
    {
        return this.mMapData.get(mkey);
    }

    public Map<Integer,BaccaratProbConfigData> GetMapData()
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
        this.Load("./Config/BaccaratProbConfig.xml");
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
            BaccaratProbConfigData data = new BaccaratProbConfigData();
            
           data.mkey = XMLUtils.getIntByElement(childElement,"key");
            {
               data.mValue = new ArrayList<>();
               String eleStr =  childElement.attribute("Value").getValue();
               if( eleStr != null && !eleStr.equals("") )
               {
                   String[] ValueStr = eleStr.split(",");
                   for(int i = 0; i < ValueStr.length;i++)
                   {
                       data.mValue.add( Integer.valueOf(ValueStr[i]) );
                   }
               }
            }


            if( mMapData.containsKey(data.mkey) )
            {
                System.out.printf("data refind:" + data.mkey);
                continue;
            }
            mMapData.put(data.mkey,data);
        }

    }

    public class BaccaratProbConfigData
    {
       
        private int mkey; //状态

        private List<Integer> mValue; //正常,通杀，通赔


        public int getmkey() {
            return mkey;
        }
        
        public void setmkey(int mkey) {
            this.mkey = mkey;
        }
        
        public List<Integer> getmValue() {
            return mValue;
        }
        
        public void setmValue(List<Integer> mValue) {
            this.mValue = mValue;
        }
        

    }
}
