package com.wisp.game.core.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLUtils {
	
	private static Logger log = LoggerFactory.getLogger("XMLUtils");
	
	private static final int str_type_str = 0;
	private static final int str_type_bool_true = 1;
	private static final int str_type_bool_false = 2;
	private static final int str_type_int = 3;
	private static final int str_type_uint = 4;
	private static final int str_type_double = 5;
	private static final int str_type_numeric_ary = 6;
	private static final int str_type_str_ary = 7;
	private static final int str_type_long = 8;
	
	public XMLUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static Map<Object, Object> parseXML(String path)
	{
		Map<Object,Object> hashMap = new HashMap<>();
		
		boolean flag =  parseXML(path, hashMap);
		if(!flag)
		{
			return null;
		}
		
		return hashMap;
	}
	
	
	public static boolean parseXML(String path,Map<Object, Object> container)
	{
		Document doc = XMLUtil4DOM.file2Document(path);
		if(doc == null)
		{
			System.out.println("the file_path is not exist:" + path);
			return false;
		}
		
		Element rootElement = doc.getRootElement();
		
		boolean flag = parseXML(rootElement, container);
		
		return flag;
	}
	
	public static boolean parseXML(Element element,Map<Object, Object> container)
	{
		List<Attribute> attributesList = element.attributes();
		for(int i =0;i < attributesList.size();i++)
		{
			Attribute attribute = attributesList.get(i);
			container.put(attribute.getName(), attribute.getText());
		}
		
		List<Element> elements =  element.elements();
		Iterator<Element> iterator = elements.iterator();
		for(;iterator.hasNext();)
		{
			Element node = iterator.next();
			Map<Object, Object> child = new HashMap<>();
			 boolean flag =  parseXML(node,child);
			if(!flag)
			{
				log.info("parseXML has error:" + node.getName());
				continue;
			}
			
			if( !container.containsKey(node.getName()))
			{
				List child_list = new LinkedList();
				container.put(node.getName(),child_list );
			}
			
			((List)container.get(node.getName())).add(child);
		}
		
		return true;
	}
	
	
	public static void formatXML(Object obj)
	{
		if(obj instanceof Map )
		{
			for(Object key : ((Map<Object,Object>)obj).keySet())
			{
				Object mapValue = ((Map<Object, Object>)obj).get(key);
				if(mapValue instanceof Map)
				{
					formatXML(mapValue);
				}
				else if(mapValue instanceof List)
				{
					formatXML(mapValue);
				}
				else if(mapValue.toString().equals("true"))
				{
					((Map<Object, Object>)obj).put(key, true);
				}
				else if(mapValue.toString().equals("false"))
				{
					((Map<Object, Object>)obj).put(key, false);
				}
				else
				{
					int str_type = getStrType(mapValue.toString());
					if(str_type == str_type_uint || str_type == str_type_int)
					{
						((Map<Object, Object>)obj).put(key, Integer.parseInt(mapValue.toString()));
					}
					else if(str_type == str_type_double)
					{
						((Map<Object, Object>)obj).put(key,Double.valueOf(mapValue.toString()));
					}
					else if(str_type == str_type_long)
					{
						((Map<Object, Object>)obj).put(key, Long.valueOf(mapValue.toString()));
					}
					else if(str_type == str_type_numeric_ary)
					{
						String[] str_list;
						if(mapValue.toString().charAt(mapValue.toString().length() - 1) == ',')
						{
							str_list = mapValue.toString().substring(0, mapValue.toString().length() -2).split(",");
						}
						else
						{
							str_list = mapValue.toString().split(",");
						}
						
						int[] int_list = new int[str_list.length];
						for(int i = 0; i < str_list.length;i++)
						{
							int_list[i] = Integer.valueOf( str_list[i] );
						}
						
						((Map<Object, Object>)obj).put(key,int_list);
					}
				}
			}
		}
		else if(obj instanceof List)
		{
			Iterator iterator = ((List)obj).iterator();
			for(;iterator.hasNext();)
			{
				formatXML(iterator.next());
			}
		}
		else
		{
			//do nothing
		}
	}
	
	
	
	/**
	 * 自动格式化，有两种方式，自动化格式必须有autoFormat字段
	 * 	一种是将对象转换为list转换为list的第一个对象
	 * 	一种是将list转换为list里的某个字段的为key值的集合
	 * @param obj
	 */
	public static void autoFormat(Object obj)
	{
		if( obj instanceof Map )
		{
			Map<String,Object> map = (Map)obj;
			for(Map.Entry<String, Object> entity : map.entrySet())
			{
				autoFormat(entity.getValue());
			}
			
			
			// 	<autoFormat>
    		//		<item name="biXiHu" type="0" />
    		//	</autoFormat>
			if(map.containsKey("autoFormat"))
			{
				List<Object> autoFormatList = (List)map.get("autoFormat");
				Map<String,Object> autoFormat = (Map<String,Object>)autoFormatList.get(0);
				if(autoFormat.containsKey("item"))
				{
					List<Map<String,Object>> itemList = (List<Map<String,Object>>)autoFormat.get("item");
					for(Map<String,Object> formatItem : itemList)
					{
						if(!formatItem.containsKey("name"))
						{
							continue;
						}
						
						String name = formatItem.get("name").toString();
						if( !map.containsKey(name) )
						{
							continue;
						}
						
						Object formatedObj = map.get(name);
						if(!(formatedObj instanceof List))
						{
							continue;
						}
						
						List<Object> list = (List<Object>)map.get(name);
						
						int type = 0; //0:使用第一个对象 1：使用指定的key
						if(formatItem.containsKey("type"))
						{
							type = (Integer)formatItem.get("type");
						}
						
						
						if(type == 0)
						{
							map.put(name, list.get(0));
						}
						else if(type == 1)
						{
							if(!formatItem.containsKey("key"))
							{
								continue;
							}
							
							String key = formatItem.get("key").toString();
							
							Map childMap = CommonUtils.ArrayToMap(list, key);
							if(childMap.size() == 0)
							{
								log.error("auto format has error,the key:" + key);
							}
							else
							{
								map.put(name, childMap);
							}
						}
						
					}
				}
				map.remove("autoFormat");
			}
		}
		else if(obj instanceof List)
		{
			List<Object> list = (List<Object>)obj;
			for(Object childObj : list)
			{
				autoFormat(childObj);
			}
		}
		else
		{
			//do nothing
		}
	}
	
	
	private static int getStrType(String str)
	{
		if(str == null || str.trim().equals(""))
		{
			return str_type_str;
		}
		if(str.equals("true"))
		{
			return str_type_bool_true;
		}
		else if(str.equals("false"))
		{
			return str_type_bool_false;
		}
		
		int str_type = str_type_uint;
		int period_count = 0;
		boolean first_char = true;
		boolean numeric_ary = false;
		
		
		for(int i = 0;i < str.length();i++)
		{
			char code = str.charAt(i);
			if(code >= '0' && code <= '9')
			{
				first_char = false;
				continue;
			}
			if(code == '.')
			{
				str_type = str_type_double;
				
				period_count ++;
				if(period_count > 1)
				{
					return str_type_str;
				}
				first_char = false;
			}
			else if(code == '-')
			{
				str_type = str_type_int;
				if(!first_char)
				{
					return str_type_str;
				}
				first_char = false;
			}
			else if(code == ',') //split
			{
				first_char = true;
				numeric_ary = true;
				period_count = 0;
			}
			else
			{
				return str_type_str;
			}
		}
		
		if(numeric_ary)
		{
			return str_type_numeric_ary;
		}
		
		if(str_type == str_type_uint)
		{
			if(Long.valueOf(str) > Math.pow(2, 31))
			{
				return str_type_long;
			}
		}
		else if(str_type == str_type_int)
		{
			if(Long.valueOf(str) < -Math.pow(2, 31))
			{
				return str_type_long;
			}
		}
		
		return str_type;
	}
}
