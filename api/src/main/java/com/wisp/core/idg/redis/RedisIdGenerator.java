package com.wisp.core.idg.redis;

import com.wisp.core.cache.CacheHander;
import com.wisp.core.idg.IDGeneratorException;
import com.wisp.core.idg.api.IdGeneratorApi;
import com.wisp.core.idg.util.IdgParam;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

public class RedisIdGenerator implements IdGeneratorApi<Number> {

    private static Logger logger = LoggerFactory.getLogger(RedisIdGenerator.class);

    private CacheHander cacheHander;

    public void setCacheHander(CacheHander cacheHander) {
        this.cacheHander = cacheHander;
    }

    private Map<Class<?>, IdgParam> clazzIdgParams = new HashMap<Class<?>, IdgParam>();
    private Map<String, IdgParam> stringIdgParams = new HashMap<String, IdgParam>();

    @Override
    public Number nextId(Object entity) throws IDGeneratorException {
        if (entity == null)
            throw new IDGeneratorException("entity对象为空，无法生成id");
        IdgParam param;
        if (entity instanceof String) {
            String strEntity = entity.toString();
            param = stringIdgParams.get(strEntity);
            if (param == null) {
                synchronized (this) {
                    param = clazzIdgParams.get(strEntity);
                    if (param == null) {
                        String name = "generate_id_" + strEntity.toLowerCase();
                        boolean[] notCheck = {false};
                        param = new IdgParam(name, false, "", notCheck);
                        stringIdgParams.put(strEntity, param);
                    }
                }
            }
        } else {
            Class<?> clazz = entity.getClass();
            param = clazzIdgParams.get(clazz);
            if (param == null) {
                synchronized (this) {
                    param = clazzIdgParams.get(clazz);
                    if (param == null) {
                        Table table = clazz.getAnnotation(Table.class);
                        if (table == null)
                            throw new IDGeneratorException(clazz.getName() + "不存在javax.persistence.Table注解，无法生成id");
                        String name = "generate_id_" + table.name().replace('.', '_').toLowerCase();
                        boolean[] notCheck = {false};
                        param = new IdgParam(name, false, "", notCheck);
                        clazzIdgParams.put(clazz, param);
                    }
                }
            }
        }
        return this.generateId(param);
    }

    private Number generateId(IdgParam param) {
        Number number = cacheHander.incrBy(param.getName(), 1, null);
        logger.info("[{}][{}]-[{}]","redis_generate_id",param.getName(),number);
        return number;
    }

    @Override
    public Number entityCorrectId(Object entity, Exception e) throws IDGeneratorException {

        if (entity == null)
            throw new IDGeneratorException("entity对象为空，无法生成id");
        IdgParam param;
        if (entity instanceof String) {
            String strEntity = entity.toString();
            param = stringIdgParams.get(strEntity);
            if (param == null) {
                synchronized (this) {
                    param = clazzIdgParams.get(strEntity);
                    if (param == null) {
                        String name = "generate_id_" + strEntity.toLowerCase();
                        boolean[] notCheck = {false};
                        param = new IdgParam(name, false, "", notCheck);
                        stringIdgParams.put(strEntity, param);
                    }
                }
            }
        } else {
            Class<?> clazz = entity.getClass();
            param = clazzIdgParams.get(clazz);
            if (param == null) {
                synchronized (this) {
                    param = clazzIdgParams.get(clazz);
                    if (param == null) {
                        Table table = clazz.getAnnotation(Table.class);
                        if (table == null)
                            throw new IDGeneratorException(clazz.getName() + "不存在javax.persistence.Table注解，无法生成id");
                        String name = "generate_id_" + table.name().replace('.', '_').toLowerCase();
                        boolean[] notCheck = {false};
                        param = new IdgParam(name, false, "", notCheck);
                        clazzIdgParams.put(clazz, param);
                    }
                }
            }
        }

        if (ExceptionUtils.getStackTrace(e).contains("MySQLIntegrityConstraintViolationException")) {
            //这个异常将 redis 中的
            Long count = cacheHander.incrBy(param.getName(), 0, null);
            if (count != null) {
                if (count > 10000000L) {
                    cacheHander.incrBy(param.getName(), 10000L, null);
                } else {
                    cacheHander.incrBy(param.getName(), 20000L, null);
                }
            }
        }
        return this.generateId(param);
    }

}
