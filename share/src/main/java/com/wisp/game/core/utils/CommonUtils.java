package com.wisp.game.core.utils;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {
	private static Logger log = LoggerFactory.getLogger("wisp");


	
	public static String jsonStr(Object obj)
	{
		String str = "";
		
		if (obj instanceof HashMap) {
			HashMap<String, Object> hashMap = (HashMap<String, Object>)obj;
			if(hashMap.size() > 0)
			{
				str += "{";
				int idx = hashMap.keySet().size();
				for(String key : hashMap.keySet())
				{
					String childStr =  jsonStr(hashMap.get(key));
					
					str += "\"" +  key + "\"" + ":" + childStr;
					idx --;
					if(idx > 0)
					{
						str += ",";
					}
				}
				
				str += "}";
			}
		}
		else if(obj instanceof List)
		{
			List list = (List)obj;
			if(list.size() > 0)
			{
				str += "[";
				for(int j = 0; j < list.size();j++)
				{
					 str += jsonStr(list.get(j));
					 if(j != list.size() - 1)
					 {
						 str += ",";
					 }
				}
					
				
				str += "]";
			}
		}
		else if(obj instanceof Integer)
		{
			return obj.toString();
		}
		else if(obj instanceof Long)
		{
			return obj.toString();
		}
		else if(obj instanceof Float)
		{
			return obj.toString();
		}
		else if(obj instanceof String)
		{
			return "\"" +  obj.toString() + "\"";
		}
		else if(obj instanceof String[])
		{
			return obj.toString();
		}
		else if(obj instanceof int[])
		{
			String int_list_str = "[";
			
			int len = ((int[])obj).length;
			for(int i = 0;i <len ;i++)
			{
				int_list_str +=  ((int[])obj)[i];
				if(i != len - 1)
				{
					int_list_str += ",";
				}
			}
			
			int_list_str += "]";
			return int_list_str;
		}
		else
		{
			return obj.toString();
		}
		
		return str;
	}
	
	public static void mapChildListToMap(Map map,String listName,String listId)
	{
		List list = (List)map.get(listName);
		if(list == null)
		{
			log.error("format MapChildListToMap has error,the listName:" + listName);
			return;
		}
		
		Map childMap = ArrayToMap(list, listId);
		map.put(listName, childMap);
	}
	
	public static Map ArrayToMap(List list,String id)
	{
		LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
		Iterator iterator = list.iterator();
		while(iterator.hasNext())
		{
			Map map = (Map)iterator.next();
			if(map == null)
			{
				continue;
			}
			
			if( !map.containsKey(id) )
			{
				continue;
			}
			
			Object key = map.get(id);
			linkedHashMap.put(key, map);
		}
		
		return linkedHashMap;
	}
	
	
	public static void firstListToMap(Map map,String nodeName)
	{
		List list = (List)map.get(nodeName);
		if(list == null)
		{
			log.error("firstListToMap has error,the node:" + nodeName);
			return;
		}
		
		map.put(nodeName, list.get(0));
	}
	
	
	public static String byteToString(byte[] bytes)
	{
		String str = "0x   ";
		for( byte sByte: bytes )
		{
			String bStr = Integer.toHexString( sByte );
			if(bStr.length() == 1)
			{
				bStr = "0" + bStr;
			}
			str += bStr + " ";
		}
		return str;
	}
	
	public static byte[] StringToByte( String str ) throws IOException
	{
		byte[] bytes= str.getBytes("utf-8");
		
		
		return bytes;
	}
	
	
	public static String dbByteToString(byte[] bytes)
	{
		if( bytes == null )
		{
			return "";
		}
		
		String str = "0x";
		
		for(byte sByte : bytes)
		{
			str += Integer.toHexString( (sByte >> 4) & 0xF ) + Integer.toHexString( sByte & 0xF );
		}
		
		return str;
	}
	
	public static String dumpStackInfo()
	{
		String message = "";
		StackTraceElement[] stackElements =Thread.currentThread().getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
            	message += stackElements[i].getClassName() + " " + stackElements[i].getFileName();
            	message += " line:" + stackElements[i].getLineNumber();
            	message += " method:" + stackElements[i].getMethodName() + "\n";
            }
        }
        
        return message;
	}



	public static <T>  T deepClone(T object)
	{
		Object o=null;
		try{
			if (object != null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				oos.close();
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				ObjectInputStream ois = new ObjectInputStream(bais);
				o = ois.readObject();
				ois.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if( o == null )
		{
			return null;
		}

		return (T)o;
	}

	/**
	 * 简单对象的复制，深度复制不要使用此
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> deepSimpleList(List<T> list)
	{
		List<T> cloneList = new ArrayList<>();
		for( int i = 0; i < list.size();i++ )
		{
			cloneList.add((T)list.get(i));
		}

		return cloneList;
	}
	
}
