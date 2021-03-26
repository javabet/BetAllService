package com.wisp.core.dao;

import java.util.List;
import java.util.Map;

/**
 * DAO支持接口
 * @version 2016-01-15
 * @param <T>
 */
interface CommonDao {
	/**
	 * 查找对象
	 * @param str
	 * @param obj
	 * @return
	 */
	public <E> E findForObject(String str, Object obj);

	/**
	 * 查找对象
	 * @param str
	 * @param obj
	 * @return
	 */
	public <E> List<E> findForList(String str, Object obj);
	
	/**
	 * 查找对象封装成Map
	 * @param str
	 * @param obj
	 * @return
	 */
	public <K, V> Map<K, V> findForMap(String str, Object obj, String key);
	
	/**
	 * 删除
	 * @param statement
	 * @param parameter
	 * @return
	 */
	public int delete(String statement, Object parameter);
	
	/**
	 * 更新
	 * @param statement
	 * @param parameter
	 * @return
	 */
	public int update(String statement, Object parameter);
}