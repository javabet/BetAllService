package com.wisp.game.share.common;

import java.util.concurrent.ConcurrentHashMap;

public class EnableObjectManager<K,T>
{
    protected ConcurrentHashMap<K,T> obj_map;
    public EnableObjectManager() {
        obj_map = new ConcurrentHashMap<>();
    }

    public boolean add_obj(K obj_id,T obj)
    {
        obj_map.put(obj_id,obj);
        return  true;
    }

    public T find_objr(K obj_id)
    {
        return obj_map.get(obj_id);
    }

    public boolean remove_obj( K obj_id )
    {
        T removeObj = obj_map.remove(obj_id);

        return removeObj  != null;
    }

    public void clear()
    {
        obj_map.clear();
    }

    public ConcurrentHashMap<K,T> get_map()
    {
        return obj_map;
    }

    public int get_count()
    {
        return obj_map.size();
    }
}
