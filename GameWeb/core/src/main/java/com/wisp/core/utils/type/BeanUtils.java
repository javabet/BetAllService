package com.wisp.core.utils.type;


import com.wisp.core.utils.exception.BusinessCommonException;

import java.util.ArrayList;
import java.util.List;

public class BeanUtils {

    public static <T> T toResp(Object data, Class<T> clazz) {
        if (data == null)
            return null;
        try {
            T t = clazz.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(data, t);
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BusinessCommonException("创建实例异常", e);
        }
    }

    public static <T> List<T> toResp(List<?> list, Class<T> clazz) {
        if (list == null)
            return null;
        if (list.isEmpty())
            return new ArrayList<T>();
        List<T> ts = new ArrayList<T>(list.size());
        for (Object data : list)
            ts.add(toResp(data, clazz));
        return ts;
    }
}
